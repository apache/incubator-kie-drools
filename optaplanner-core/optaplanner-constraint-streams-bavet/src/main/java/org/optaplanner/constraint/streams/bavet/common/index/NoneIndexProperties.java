package org.optaplanner.constraint.streams.bavet.common.index;

final class NoneIndexProperties implements IndexProperties {

    static final NoneIndexProperties INSTANCE = new NoneIndexProperties();

    private NoneIndexProperties() {
    }

    @Override
    public <Type_> Type_ getProperty(int index) {
        throw new IllegalArgumentException("Impossible state: none index property requested");
    }

    @Override
    public int maxLength() {
        throw new IllegalStateException("Impossible state: none index property requested");
    }

    @Override
    public <Type_> Type_ getIndexerKey(int fromInclusive, int toExclusive) {
        throw new IllegalArgumentException("Impossible state: none indexer key requested");
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof NoneIndexProperties;
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public String toString() {
        return "[]";
    }

}
