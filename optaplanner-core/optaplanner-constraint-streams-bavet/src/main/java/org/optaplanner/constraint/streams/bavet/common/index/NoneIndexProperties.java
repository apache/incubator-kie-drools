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
    public String toString() {
        return "[]";
    }

}
