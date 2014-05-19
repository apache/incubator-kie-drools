package org.drools.beliefs.bayes;

public class CliqueBitSet {
    private OpenBitSet OpenBitSet;

    private int weight;

    public CliqueBitSet(OpenBitSet OpenBitSet, int weight) {
        this.OpenBitSet = OpenBitSet;
        this.weight = weight;
    }

    public OpenBitSet getOpenBitSet() {
        return OpenBitSet;
    }

    public int getWeight() {
        return weight;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }

        CliqueBitSet clique = (CliqueBitSet) o;

        if (weight != clique.weight) { return false; }
        if (!OpenBitSet.equals(clique.OpenBitSet)) { return false; }

        return true;
    }

    @Override
    public int hashCode() {
        int result = OpenBitSet.hashCode();
        result = 31 * result + weight;
        return result;
    }

    @Override
    public String toString() {
        return "Clique{" +
               "OpenBitSet=" + OpenBitSet +
               ", weight=" + weight +
               '}';
    }
}
