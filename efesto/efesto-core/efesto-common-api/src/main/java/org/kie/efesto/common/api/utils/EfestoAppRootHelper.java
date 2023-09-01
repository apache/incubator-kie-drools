package org.kie.efesto.common.api.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;

import org.kie.efesto.common.api.identifiers.ComponentRoot;
import org.kie.efesto.common.api.identifiers.EfestoComponentRoot;

public class EfestoAppRootHelper {

    private EfestoAppRootHelper() {
    }

    public static Map<Class<? extends EfestoComponentRoot>, EfestoComponentRoot> getEfestoComponentRootBySPI(Class<?
            extends EfestoComponentRoot> toLoad) {
        Map<Class<? extends EfestoComponentRoot>, EfestoComponentRoot> toReturn = new HashMap<>();
        ServiceLoader<? extends EfestoComponentRoot> componentRootLoader = ServiceLoader.load(toLoad);
        componentRootLoader.iterator()
                .forEachRemaining(efestoComponentRoot -> toReturn.put(efestoComponentRoot.getClass(), efestoComponentRoot));
        return toReturn;
    }

    public static Map<Class<? extends ComponentRoot>, ComponentRoot> getComponentRootBySPI(Class<?
            extends ComponentRoot> toLoad) {
        Map<Class<? extends ComponentRoot>, ComponentRoot> toReturn = new HashMap<>();
        ServiceLoader<? extends ComponentRoot> componentRootLoader = ServiceLoader.load(toLoad);
        componentRootLoader.iterator()
                .forEachRemaining(componentRoot -> toReturn.put(componentRoot.getClass(), componentRoot));
        return toReturn;
    }
}
