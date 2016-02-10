package org.drools.testcoverage.domain;

import java.io.Serializable;

/**
 * A customer in a bar.
 */
public class Customer implements Serializable {

    private final String name;

    private final int ageInYears;

    public Customer(final String name, final int ageInYears) {
        this.name = name;
        this.ageInYears = ageInYears;
    }

    public String getName() {
        return this.name;
    }

    public int getAgeInYears() {
        return this.ageInYears;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Customer customer = (Customer) o;

        if (ageInYears != customer.ageInYears) return false;
        return !(name != null ? !name.equals(customer.name) : customer.name != null);

    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + ageInYears;
        return result;
    }

    @Override
    public String toString() {
        return "Customer{" +
                "name='" + name + '\'' +
                ", ageInYears=" + ageInYears +
                '}';
    }
}
