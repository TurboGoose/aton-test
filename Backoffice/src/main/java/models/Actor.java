package models;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class Actor {
    private final String name;
    private final List<Replica> replicas;

    public Actor(String name) {
        this.name = name;
        this.replicas = new LinkedList<>();
    }

    public void addReplica(String line, int order) {
        replicas.add(new Replica(line, order));
    }

    public List<Replica> getReplicas() {
        return replicas;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Actor actor = (Actor) o;
        return Objects.equals(name, actor.name) && Objects.equals(replicas, actor.replicas);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, replicas);
    }

    @Override
    public String toString() {
        return "model.Actor{" +
                "name='" + name + '\'' +
                ", replicas=" + replicas +
                '}';
    }
}

