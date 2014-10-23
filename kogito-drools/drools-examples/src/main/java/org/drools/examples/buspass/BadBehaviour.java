package org.drools.examples.buspass;

public class BadBehaviour {
    private Person person;

    public BadBehaviour(Person person) {
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
        return "BadBehaviour{" +
               "person=" + person +
               '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }

        BadBehaviour that = (BadBehaviour) o;

        if (!person.equals(that.person)) { return false; }

        return true;
    }

    @Override
    public int hashCode() {
        return person.hashCode();
    }
}
