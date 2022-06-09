package org.optaplanner.examples.machinereassignment.domain;

import org.optaplanner.examples.common.domain.AbstractPersistable;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("MrLocation")
public class MrLocation extends AbstractPersistable {

    public MrLocation() {
    }

    public MrLocation(long id) {
        super(id);
    }
}
