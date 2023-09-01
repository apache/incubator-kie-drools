package org.drools.testcoverage.common.model;

import java.io.Serializable;

public class Subject implements Serializable {

    private static final long serialVersionUID = 5115167051047419861L;

    private int id = 0;
    private int age = 0;
    private String name = "";
    private int dummy = 0;
    private String sex = "";
    private int value = 0;

    public Subject() {
    }

    public Subject(final String name) {
        this.name = name;
    }

    public Subject(final int age, final String name, final int dummy, final String sex) {
        this.age = age;
        this.name = name;
        this.dummy = dummy;
        this.sex = sex;
    }

    public void setAge(final int age) {
        this.age = age;
    }

    public int getAge() {
        return age;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setDummy(final int dummy) {
        this.dummy = dummy;
    }

    public int getDummy() {
        return dummy;
    }

    public void setSex(final String sex) {
        this.sex = sex;
    }

    public String getSex() {
        return sex;
    }

    public void setId(final int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setValue(final int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

}
