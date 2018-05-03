package org.drools.compiler.integrationtests.incrementalcompilation;

public class StringPermutation {
    private final String[] permutation;

    public StringPermutation(final String[] permutation) {
        this.permutation = permutation;
    }

    public String[] getPermutation() {
        return permutation;
    }
}
