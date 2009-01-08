package org.drools.runtime.pipeline;

public interface ResultHandlerFactory {    
    ResultHandler newResult(ResultHandlerContext context);
}
