package org.kie.efesto.runtimemanager.api.model;

import java.util.ServiceLoader;

import org.kie.efesto.common.api.listener.EfestoListener;
import org.kie.efesto.common.api.model.EfestoContext;
import org.kie.efesto.runtimemanager.api.service.KieRuntimeService;

public interface EfestoRuntimeContext<T extends EfestoListener> extends EfestoContext<T> {

    Class<?> loadClass(String className) throws ClassNotFoundException;

    ServiceLoader<KieRuntimeService> getKieRuntimeService();
}
