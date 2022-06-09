package org.optaplanner.examples.machinereassignment.domain;

import org.optaplanner.examples.common.domain.AbstractPersistable;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("MrNeighborhood")
public class MrNeighborhood extends AbstractPersistable {

    public MrNeighborhood() {
    }

    public MrNeighborhood(long id) {
        super(id);
    }
}
