package org.drools.model.codegen.execmodel.domain;

public class MysteriousMan extends Person {

    public MysteriousMan() {
        super();
    }

    public MysteriousMan(String name, int age) {
        super(name, age);
    }

    @Override
    public Address getAddress() {
        throw new UnsupportedOperationException("Address is not supported");
    }

    @Override
    public void setAddress(Address address) {
        throw new UnsupportedOperationException("Address is not supported");
    }
}
