package org.drools.runtime.pipeline.impl;

import org.drools.runtime.pipeline.ResultHandler;

public interface Pipeline {
    void insert(Object object, ResultHandler resultHandler);
}
