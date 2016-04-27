package org.drools.testcoverage.domain;

import java.io.Serializable;

/**
 * A non-alcoholic or alcoholic drink offered in a bar.
 */
public class Drink implements Serializable {

    private final String name;

    private final boolean containsAlcohol;

    public Drink(final String name, final boolean containsAlcohol) {
        this.name = name;
        this.containsAlcohol = containsAlcohol;
    }

    public String getName() {
        return this.name;
    }

    public boolean containsAlcohol() {
        return this.containsAlcohol;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Drink drink = (Drink) o;

        if (containsAlcohol != drink.containsAlcohol) return false;
        return !(name != null ? !name.equals(drink.name) : drink.name != null);

    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (containsAlcohol ? 1 : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Drink{" +
                "name='" + name + '\'' +
                ", containsAlcohol=" + containsAlcohol +
                '}';
    }
}
