import models.Actor;
import models.Replica;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class MultithreadingDialog {
    private static final String SCRIPT_FILE = "script.txt";
    public static void main(String[] args) throws FileNotFoundException, InterruptedException {
        Collection<Actor> actors = readScriptFromFile(SCRIPT_FILE);
        DialogSynchronizer synchronizer = new DialogSynchronizer();
        try (ExecutorService executorService = Executors.newFixedThreadPool(actors.size())) {
            for (Actor actor : actors) {
                executorService.submit(new RunnableActor(actor, synchronizer));
            }
            executorService.shutdown();
            executorService.awaitTermination(1, TimeUnit.HOURS);
        }
    }

    private static Collection<Actor> readScriptFromFile(String filename) throws FileNotFoundException {
        Map<String, Actor> ActorNameToActor = new HashMap<>();
        try (Scanner sc = new Scanner(new FileReader(filename))) {
            int replicaCounter = 0;
            while (sc.hasNext()) {
                String line = sc.nextLine();
                int separatorIndex = line.indexOf(": ");
                String actorName = line.substring(0, separatorIndex);
                String replicaLine = line.substring(separatorIndex + 2);
                ActorNameToActor.putIfAbsent(actorName, new Actor(actorName));
                Actor actor = ActorNameToActor.get(actorName);
                actor.addReplica(replicaLine, replicaCounter++);
            }
        }
        return ActorNameToActor.values();
    }
}

class DialogSynchronizer {
    private volatile int counter = 0;

    public int getCurrentReplicaOrder() {
        return counter;
    }

    synchronized public void proceed() {
        counter++;
    }
}


class RunnableActor implements Runnable {
    private final Actor actor;
    private final DialogSynchronizer synchronizer;

    public RunnableActor(Actor actor, DialogSynchronizer synchronizer) {
        this.actor = actor;
        this.synchronizer = synchronizer;
    }

    @Override
    public void run() {
        for (Replica replica : actor.getReplicas()) {
            synchronized (synchronizer) {
                while (synchronizer.getCurrentReplicaOrder() != replica.getOrder()) {
                    try {
                        synchronizer.wait();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                System.out.println(actor.getName() + ": " + replica.getLine());
                synchronizer.proceed();
                synchronizer.notifyAll();
            }
        }
    }
}




