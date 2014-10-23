package org.drools.examples.birdsfly;

public class Fly {
    private Bird bird;

    public Fly(Bird bird) {
        this.bird = bird;
    }

    public Bird getBird() {
        return bird;
    }

    public void setBird(Bird bird) {
        this.bird = bird;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }

        Fly fly = (Fly) o;

        if (!bird.equals(fly.bird)) { return false; }

        return true;
    }

    @Override
    public int hashCode() {
        return bird.hashCode();
    }
}
