package org.drools.process.core.context;

import java.io.Serializable;

import org.drools.process.core.Context;

public abstract class AbstractContext implements Context, Serializable {

    private long id;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
    
}
