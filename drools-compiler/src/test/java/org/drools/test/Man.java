package org.drools.test;

import org.drools.definition.type.Position;

public class Man extends Person {
    
    @Position(1)
    private int age;
 
    @Position(2)
    private double weight;

    public Man() {
        super();    //To change body of overridden methods use File | Settings | File Templates.
    }
 
    public Man(String name, int age, double weight) {
        super(name);
        this.age = age;
        this.weight = weight;
    }
 
    public int getAge() {
        return age;
    }
 
    public void setAge(int age) {
        this.age = age;
    }
 
    public double getWeight() {
        return weight;
    }
 
    public void setWeight(double weight) {
        this.weight = weight;
    }
    
    @Override
    public String toString() {
        return "Man{" +
                "name='" + getName() + '\'' +
                ", age=" + age +
                ", weight=" + weight +
                '}';
    }
 
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
 
        Man man = (Man) o;
 
        if (age != man.age) return false;
        if (Double.compare(man.weight, weight) != 0) return false;
        if (getName() != null ? !getName().equals(man.getName()) : man.getName() != null) return false;
 
        return true;
    }
 
    @Override
    public int hashCode() {
        int result;
        long temp;
        result = getName() != null ? getName().hashCode() : 0;
        result = 31 * result + age;
        temp = weight != +0.0d ? Double.doubleToLongBits(weight) : 0L;
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }    
}
