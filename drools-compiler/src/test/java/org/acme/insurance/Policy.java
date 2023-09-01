package org.acme.insurance;

/**
 * This represents a policy that a driver is applying for. 
 * 
 * Obviously in the real world, there are actuaries to mess things up, but lets just pretend there is
 * some simple base price and discount that we can calculate with relatively simple rules !
 */
public class Policy {

    private String  type            = "COMPREHENSIVE";
    private boolean approved        = false;
    private int     discountPercent = 0;
    private int     basePrice;

    public boolean isApproved() {
        return this.approved;
    }

    public void setApproved(final boolean approved) {
        this.approved = approved;
    }

    public int getDiscountPercent() {
        return this.discountPercent;
    }

    public void setDiscountPercent(final int discountPercent) {
        this.discountPercent = discountPercent;
    }

    public String getType() {
        return this.type;
    }

    public void setType(final String type) {
        this.type = type;
    }

    public void applyDiscount(final int discount) {
        this.discountPercent += discount;
    }

    public int getBasePrice() {
        return this.basePrice;
    }

    public void setBasePrice(final int basePrice) {
        this.basePrice = basePrice;
    }

}
