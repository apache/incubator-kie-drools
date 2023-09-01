package org.drools.examples.buspass;

public class AdultBusPass extends BusPass {

    public AdultBusPass(Person person) {
        super(person);
    }

    @Override
    public String toString() {
        return "AdultBusPass{" +
               "person=" + getPerson() +
               '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }

        AdultBusPass that = (AdultBusPass) o;

        if (!getPerson().equals(that.getPerson())) { return false; }

        return true;
    }

    @Override
    public int hashCode() {
        return getPerson().hashCode();
    }
}
