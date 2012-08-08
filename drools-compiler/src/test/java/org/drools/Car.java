package org.drools;


public class Car {

    private String brand;
    private boolean expensive;

    public Car() {
        brand = "ferrari";
        expensive = true;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public boolean isExpensive() {
        return expensive;
    }

    public void setExpensive(boolean expensive) {
        this.expensive = expensive;
    }

    @Override
    public String toString() {
        return "Car{" +
                "brand='" + brand + '\'' +
                ", expensive=" + expensive +
                '}';
    }
}
