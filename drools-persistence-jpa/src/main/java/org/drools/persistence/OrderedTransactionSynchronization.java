package org.drools.persistence;

public abstract class OrderedTransactionSynchronization implements TransactionSynchronization, Comparable<OrderedTransactionSynchronization> {

    private Integer order;
    private String identifier;

    public OrderedTransactionSynchronization(Integer order, String identifier) {
        this.order = order;
        this.identifier = identifier;
    }

    public Integer getOrder() {
        return order;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    @Override
    public int compareTo(OrderedTransactionSynchronization o) {
        if (this.getClass() != o.getClass()) {
            return this.getOrder().compareTo(o.getOrder()+1);
        }
        int result = this.getOrder().compareTo(o.getOrder());
        if (result == 0) {
            return this.getIdentifier().compareTo(o.getIdentifier());
        }

        return result;
    }
}
