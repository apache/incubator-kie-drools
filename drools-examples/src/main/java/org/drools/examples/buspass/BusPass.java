package org.drools.examples.buspass;

public class BusPass {
    private Person person;

    public BusPass(Person person) {
        this.person = person;
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    @Override
    public String toString() {
        return "AdultBusPass{" +
               "person=" + person +
               '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }

        BusPass that = (BusPass) o;

        if (!person.equals(that.person)) { return false; }

        return true;
    }

    @Override
    public int hashCode() {
        return person.hashCode();
    }
}
