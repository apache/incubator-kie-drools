package org.kie.api.internal.utils;

import java.util.ServiceLoader;

public interface KieService extends Comparable<KieService> {
    default int servicePriority() {
        return 0;
    }

    @Override
    default int compareTo(KieService other) {
        if (servicePriority() == other.servicePriority()) {
            throw new IllegalStateException("Found 2 services with same priority (" + servicePriority() + "): " + this.getClass().getCanonicalName() + " and " + other.getClass().getCanonicalName());
        }
        return servicePriority() - other.servicePriority();
    }

    static <T extends KieService> T load(Class<T> serviceClass) {
        ServiceLoader<T> loader = ServiceLoader.load(serviceClass, serviceClass.getClassLoader());
        T service = null;
        for (T impl : loader) {
            if (service == null || impl.compareTo(service) > 0) {
                service = impl;
            }
        }
        return service;
    }
}
