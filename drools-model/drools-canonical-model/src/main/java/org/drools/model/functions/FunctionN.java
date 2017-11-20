package org.drools.model.functions;

import java.io.Serializable;

public interface FunctionN<R> extends Serializable {
    R apply(Object... objs);
}
