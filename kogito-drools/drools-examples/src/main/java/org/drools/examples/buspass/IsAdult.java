package org.drools.examples.buspass;

public class IsAdult {
    private Person person;

    public IsAdult(Person person) {
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

        IsAdult isAdult = (IsAdult) o;

        if (!person.equals(isAdult.person)) { return false; }

        return true;
    }

    @Override
    public int hashCode() {
        return person.hashCode();
    }
}
