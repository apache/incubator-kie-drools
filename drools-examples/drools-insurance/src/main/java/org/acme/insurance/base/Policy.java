package org.acme.insurance.base;

/**
 * This represents a policy that a driver is applying for.
 * <p/>
 * Obviously in the real world, there are actuaries to mess things up, but lets just pretend there is
 * some simple base price and discount that we can calculate with relatively simple rules !
 */
public class Policy {

    private int driverId;

    private boolean approved = false;
    private int discountPercent = 0;
    private double basePrice;
    private double insurancePrice;

    public double getInsurancePrice() {
        return insurancePrice;
    }

    public void setInsurancePrice(double insurancePrice) {
        this.insurancePrice = insurancePrice;
    }

    public boolean isApproved() {
        return approved;
    }

    public void setApproved(boolean approved) {
        this.approved = approved;
    }

    public double getBasePrice() {
        return basePrice;
    }

    public void setBasePrice(double basePrice) {
        this.basePrice = basePrice;
    }

    public int getDiscountPercent() {
        return discountPercent;
    }

    public void setDiscountPercent(int discountPercent) {
        this.discountPercent = discountPercent;
    }

    public int getDriverId() {
        return driverId;
    }

    public void setDriverId(int driverId) {
        this.driverId = driverId;
    }
}
