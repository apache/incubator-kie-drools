package org.drools.examples.birdsfly;

public class Rocket {
    private Bird bird;

    public Rocket(Bird bird) {
        this.bird = bird;
    }

    public Bird getBird() {
        return bird;
    }

    public void setBird(Bird bird) {
        this.bird = bird;
    }

    @Override
    public String toString() {
        return "Rocket{" +
               "bird=" + bird +
               '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }

        Rocket rocket = (Rocket) o;

        if (!bird.equals(rocket.bird)) { return false; }

        return true;
    }

    @Override
    public int hashCode() {
        return bird.hashCode();
    }
}
