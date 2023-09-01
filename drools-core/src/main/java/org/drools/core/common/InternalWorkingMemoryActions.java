package org.drools.core.common;

import java.util.Collection;

import org.drools.core.WorkingMemoryEntryPoint;
import org.drools.base.definitions.rule.impl.RuleImpl;
import org.drools.base.factmodel.traits.Thing;
import org.drools.base.factmodel.traits.TraitableBean;
import org.drools.core.reteoo.TerminalNode;
import org.drools.core.rule.consequence.InternalMatch;
import org.drools.core.util.bitmask.BitMask;
import org.kie.api.runtime.rule.FactHandle;
import org.drools.base.beliefsystem.Mode;

public interface InternalWorkingMemoryActions
        extends
        InternalWorkingMemory,
        WorkingMemoryEntryPoint {

    void update(FactHandle handle,
                Object object,
                BitMask mask,
                Class<?> modifiedClass,
                InternalMatch internalMatch);

    FactHandle insert(Object object,
                      boolean dynamic,
                      RuleImpl rule,
                      TerminalNode terminalNode);

    FactHandle insertAsync(Object object);

    void updateTraits( InternalFactHandle h, BitMask mask, Class<?> modifiedClass, InternalMatch internalMatch);

    <T, K, X extends TraitableBean> Thing<K> shed(InternalMatch internalMatch, TraitableBean<K,X> core, Class<T> trait);

    <T, K> T don(InternalMatch internalMatch, K core, Collection<Class<? extends Thing>> traits, boolean b, Mode[] modes);

    <T, K> T don(InternalMatch internalMatch, K core, Class<T> trait, boolean b, Mode[] modes);
}
