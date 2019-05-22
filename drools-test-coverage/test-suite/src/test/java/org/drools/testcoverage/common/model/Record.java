package org.drools.testcoverage.common.model;

import java.util.Objects;

public class Record {
    String category;
    String phoneNumber;

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Record record = (Record) o;
        return Objects.equals(category, record.category) &&
                Objects.equals(phoneNumber, record.phoneNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(category, phoneNumber);
    }

    @Override
    public String toString() {
        return "Record{" +
                "category='" + category + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                '}';
    }
}

