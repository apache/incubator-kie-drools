package org.drools;

public enum Gender {

    FEMALE(0), MALE(1), OTHER(2), NOT_AVAILABLE(3);

    private int key;

    Gender(int key) {
        this.key = key;
    }

    public int getKey() {
        return key;
    }
}
