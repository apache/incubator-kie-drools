package org.kie.pmml.api.iinterfaces;

import java.io.Serializable;
import java.util.function.Function;

public interface SerializableFunction<T, R> extends Function<T, R>,
                                              Serializable {

}
