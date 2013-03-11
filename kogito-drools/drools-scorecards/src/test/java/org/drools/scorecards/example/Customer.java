package org.drools.scorecards.example;

/**
 * Created with IntelliJ IDEA.
 * User: vinod kiran
 * Date: 14/10/12
 * Time: 11:00 AM
 */
public class Customer {
    double customerScore;
    int customerAge;
    String placeOfResidence;

    public Customer() {
    }

    public double getCustomerScore() {
        return customerScore;
    }

    public void setCustomerScore(double customerScore) {
        this.customerScore = customerScore;
    }

    public int getCustomerAge() {
        return customerAge;
    }

    public void setCustomerAge(int customerAge) {
        this.customerAge = customerAge;
    }

    public String getPlaceOfResidence() {
        return placeOfResidence;
    }

    public void setPlaceOfResidence(String placeOfResidence) {
        this.placeOfResidence = placeOfResidence;
    }
}
