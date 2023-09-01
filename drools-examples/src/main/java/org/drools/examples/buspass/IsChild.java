package org.drools.examples.buspass;

public class IsChild {
    private Person person;

    public IsChild(Person person) {
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
        return "IsAdult{" +
               "person=" + person +
               '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }

        IsChild isChild = (IsChild) o;

        if (!person.equals(isChild.person)) { return false; }

        return true;
    }

    @Override
    public int hashCode() {
        return person.hashCode();
    }
}
