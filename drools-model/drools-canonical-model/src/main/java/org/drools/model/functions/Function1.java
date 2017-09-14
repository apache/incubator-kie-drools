package org.drools.model.functions;

import java.io.Serializable;

public interface Function1<T, R> extends Serializable {
    R apply(T t);
}
