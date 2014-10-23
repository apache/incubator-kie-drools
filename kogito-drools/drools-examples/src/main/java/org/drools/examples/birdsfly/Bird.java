package org.drools.examples.birdsfly;

public class Bird  {
    private String name;
    private String species;

    public Bird(String name, String species) {
        this.name = name;
        this.species = species;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSpecies() {
        return species;
    }

    public void setSpecies(String species) {
        this.species = species;
    }

    @Override
    public String toString() {
        return "Bird{" +
               "name='" + name + '\'' +
               ", species='" + species + '\'' +
               '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }

        Bird bird = (Bird) o;

        if (!name.equals(bird.name)) { return false; }
        if (!species.equals(bird.species)) { return false; }

        return true;
    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + species.hashCode();
        return result;
    }
}
