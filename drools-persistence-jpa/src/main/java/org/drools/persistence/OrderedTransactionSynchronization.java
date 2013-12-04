package org.drools.persistence;

public abstract class OrderedTransactionSynchronization implements TransactionSynchronization, Comparable<OrderedTransactionSynchronization> {

    private Integer order;

    public OrderedTransactionSynchronization(Integer order) {
        this.order = order;
    }

    public Integer getOrder() {
        return order;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }

    @Override
    public int compareTo(OrderedTransactionSynchronization o) {
        if (this.getClass() != o.getClass()) {
            return this.getOrder().compareTo(o.getOrder()+1);
        }
        return this.getOrder().compareTo(o.getOrder());
    }
}
