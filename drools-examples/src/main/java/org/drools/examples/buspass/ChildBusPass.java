package org.drools.examples.buspass;

public class ChildBusPass extends BusPass {

    public ChildBusPass(Person person) {
        super(person);
    }

    @Override
    public String toString() {
        return "ChildBusPass{" +
               "person=" + getPerson() +
               '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }

        ChildBusPass that = (ChildBusPass) o;

        if (!getPerson().equals(that.getPerson())) { return false; }

        return true;
    }

    @Override
    public int hashCode() {
        return getPerson().hashCode();
    }
}
