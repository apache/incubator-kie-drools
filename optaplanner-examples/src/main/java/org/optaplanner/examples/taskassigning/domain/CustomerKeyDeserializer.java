package org.optaplanner.examples.taskassigning.domain;

import org.optaplanner.examples.common.persistence.jackson.AbstractKeyDeserializer;

final class CustomerKeyDeserializer extends AbstractKeyDeserializer<Customer> {

    public CustomerKeyDeserializer() {
        super(Customer.class);
    }

    @Override
    protected Customer createInstance(long id) {
        return new Customer(id);
    }
}
