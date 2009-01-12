package org.drools.runtime.pipeline;


public interface Pipeline extends Stage, Emitter{
    void insert(Object object, ResultHandler resultHandler);
}
