package org.optaplanner.examples.pas.domain;

public enum Gender {
    MALE("M"),
    FEMALE("F");

    public static Gender valueOfCode(String code) {
        for (Gender gender : values()) {
            if (code.equalsIgnoreCase(gender.getCode())) {
                return gender;
            }
        }
        return null;
    }

    private final String code;

    Gender(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

}
