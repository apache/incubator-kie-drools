package org.drools.compiler.xpath;

public class BabyGirl extends Child {

    private final String favoriteDollName;

    public BabyGirl(String name, int age) {
        this(name, age, null);
    }

    public BabyGirl(String name, int age, String favoriteDollName) {
        super(name, age);
        this.favoriteDollName = favoriteDollName;
    }

    public String getFavoriteDollName() {
        return favoriteDollName;
    }
}
