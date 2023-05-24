package models;

import java.util.Objects;

public class Replica {
    private final String line;
    private final int order;

    public Replica(String line, int order) {
        this.line = line;
        this.order = order;
    }

    public String getLine() {
        return line;
    }

    public int getOrder() {
        return order;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Replica replica = (Replica) o;
        return order == replica.order && Objects.equals(line, replica.line);
    }

    @Override
    public int hashCode() {
        return Objects.hash(line, order);
    }

    @Override
    public String toString() {
        return "model.Replica{" +
                "line='" + line + '\'' +
                ", order=" + order +
                '}';
    }
}
