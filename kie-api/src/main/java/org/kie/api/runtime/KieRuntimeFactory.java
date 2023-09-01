package org.kie.api.runtime;

import java.util.NoSuchElementException;
import java.util.concurrent.ConcurrentHashMap;

import org.kie.api.KieBase;
import org.kie.api.internal.runtime.KieRuntimeService;
import org.kie.api.internal.runtime.KieRuntimes;
import org.kie.api.internal.utils.KieService;

/**
 * Maintains a collection of Knowledge Runtimes
 * that is bound to the given KieBase.
 */
public class KieRuntimeFactory {

    private final KieBase kieBase;
    private final ConcurrentHashMap<Class<?>, Object> runtimeServices = new ConcurrentHashMap<>();

    /**
     * Creates an instance of this factory for the given KieBase
     */
    public static KieRuntimeFactory of(KieBase kieBase) {
        return new KieRuntimeFactory(kieBase);
    }

    private KieRuntimeFactory(KieBase kieBase) {
        this.kieBase = kieBase;
    }

    /**
     * Returns a singleton instance of the given class (if any)
     * @throws NoSuchElementException if it is not possible to find a service for the given class
     */
    @SuppressWarnings("unchecked")
    public <T> T get(Class<T> cls) {
        return (T) runtimeServices.computeIfAbsent(cls, this::createRuntimeInstance);
    }

    /**
     * Create a runtime instance, and return nulls if it fails.
     * Respects the Map#computeIfAbsent contract
     * @param c
     * @return
     */
    private Object createRuntimeInstance(Class<?> c) {
        KieRuntimeService kieRuntimeService = KieService.load(KieRuntimes.class).getRuntime(c);
        if (kieRuntimeService == null) {
            throw new NoSuchElementException(c.getName());
        }
        return kieRuntimeService.newKieRuntime(kieBase);
    }
}
