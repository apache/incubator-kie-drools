package org.optaplanner.examples.pas.domain;

import org.optaplanner.examples.common.domain.AbstractPersistable;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("Night")
public class Night extends AbstractPersistable {

    private int index;

    public Night() {
    }

    public Night(int index) {
        this.index = index;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getLabel() {
        return (index + 1) + "-JAN";
    }

    @Override
    public String toString() {
        return Integer.toString(index);
    }

}
