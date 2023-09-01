package org.kie.internal.services;

import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;

public abstract class AbstractMultiService<K, V> {

    private volatile Map<K, V> servicesMap;

    protected V getService(K key) {
        if (servicesMap == null) {
            init();
        }
        return servicesMap.get(key);
    }

    private synchronized void init() {
        if (servicesMap == null) {
            servicesMap = new HashMap<>();
            ServiceLoader<V> loader = ServiceLoader.load(serviceClass());
            for (V service : loader) {
                servicesMap.put(serviceKey(service), service);
            }
        }
    }

    protected abstract Class<V> serviceClass();

    protected abstract K serviceKey(V service);
}
