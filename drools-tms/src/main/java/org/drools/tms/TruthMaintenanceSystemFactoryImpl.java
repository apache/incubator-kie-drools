package org.drools.tms;

import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.function.Predicate;

import org.drools.core.common.InternalWorkingMemoryEntryPoint;
import org.drools.core.common.ReteEvaluator;
import org.drools.core.common.TruthMaintenanceSystem;
import org.drools.core.common.TruthMaintenanceSystemFactory;
import org.drools.base.definitions.rule.impl.QueryImpl;
import org.drools.tms.beliefsystem.abductive.Abductive;

public class TruthMaintenanceSystemFactoryImpl implements TruthMaintenanceSystemFactory {

    private final Map<InternalWorkingMemoryEntryPoint, TruthMaintenanceSystem> tmsForEntryPoints = Collections.synchronizedMap(new IdentityHashMap<>());

    @Override
    public TruthMaintenanceSystem getOrCreateTruthMaintenanceSystem(ReteEvaluator reteEvaluator) {
        return getOrCreateTruthMaintenanceSystem((InternalWorkingMemoryEntryPoint) reteEvaluator.getDefaultEntryPoint());
    }

    @Override
    public TruthMaintenanceSystem getOrCreateTruthMaintenanceSystem(InternalWorkingMemoryEntryPoint entryPoint) {
        return tmsForEntryPoints.computeIfAbsent(entryPoint, TruthMaintenanceSystemImpl::new);
    }

    @Override
    public void clearTruthMaintenanceSystem(InternalWorkingMemoryEntryPoint entryPoint) {
        TruthMaintenanceSystem tms = tmsForEntryPoints.remove(entryPoint);
        if (tms != null) {
            tms.clear();
        }
    }

    @Override
    public QueryImpl createTmsQuery(String name, Predicate<Class<? extends Annotation>> hasAnnotation) {
        return hasAnnotation.test(Abductive.class) ? new AbductiveQuery(name) : new QueryImpl(name);
    }

    public int getEntryPointsMapSize() {
        // only for testing purposes
        return tmsForEntryPoints.size();
    }

    public void clearEntryPointsMap() {
        // only for testing purposes
        tmsForEntryPoints.clear();
    }
}
