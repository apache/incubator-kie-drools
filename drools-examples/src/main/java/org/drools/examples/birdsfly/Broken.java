package org.drools.examples.birdsfly;

public class Broken {
    private Bird bird;
    private String part;

    public Broken(Bird bird, String part) {
        this.bird = bird;
        this.part = part;
    }

    public Bird getBird() {
        return bird;
    }

    public void setBird(Bird bird) {
        this.bird = bird;
    }

    public String getPart() {
        return part;
    }

    public void setPart(String part) {
        this.part = part;
    }

    @Override
    public String toString() {
        return "Broken{" +
               "bird=" + bird +
               ", part='" + part + '\'' +
               '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }

        Broken broken = (Broken) o;

        if (!bird.equals(broken.bird)) { return false; }
        if (!part.equals(broken.part)) { return false; }

        return true;
    }

    @Override
    public int hashCode() {
        int result = bird.hashCode();
        result = 31 * result + part.hashCode();
        return result;
    }
}
