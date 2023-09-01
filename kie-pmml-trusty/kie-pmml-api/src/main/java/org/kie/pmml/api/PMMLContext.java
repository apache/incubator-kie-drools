package org.kie.pmml.api;

import org.kie.api.runtime.Context;
import org.kie.efesto.common.api.listener.EfestoListener;
import org.kie.efesto.common.api.model.EfestoContext;

public interface PMMLContext<T extends EfestoListener> extends Context,
                                                               EfestoContext<T> {

    Class<?> loadClass(String className) throws ClassNotFoundException;

}
