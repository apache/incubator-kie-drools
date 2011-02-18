package org.drools.testframework;

public class DumbFact {


    private String name;
    private int age;
    private Long number;
    private boolean enabled;

    public boolean isEnabled() {
        return enabled;
    }
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
    public int getAge() {
        return age;
    }
    public void setAge(int age) {
        this.age = age;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public Long getNumber() {
        return number;
    }
    public void setNumber(Long number) {
        this.number = number;
    }

    public String toString() {
        return "Name:" + name + " age:" + age + " number:" + number + " enabled:" + enabled;
    }

}
