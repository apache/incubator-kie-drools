package org.optaplanner.examples.taskassigning.domain;

import org.optaplanner.examples.common.domain.AbstractPersistable;
import org.optaplanner.examples.common.swingui.components.Labeled;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("TaCustomer")
public class Customer extends AbstractPersistable implements Labeled {

    private String name;

    public Customer() {
    }

    public Customer(long id, String name) {
        super(id);
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    // ************************************************************************
    // Complex methods
    // ************************************************************************

    @Override
    public String getLabel() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }

}
