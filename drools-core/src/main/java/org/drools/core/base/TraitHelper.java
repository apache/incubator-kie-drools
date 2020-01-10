package org.drools.core.base;

import java.util.Collection;

import org.drools.core.common.InternalFactHandle;
import org.drools.core.definitions.rule.impl.RuleImpl;
import org.drools.core.factmodel.traits.Thing;
import org.drools.core.factmodel.traits.TraitableBean;
import org.drools.core.reteoo.TerminalNode;
import org.drools.core.spi.Activation;
import org.drools.core.util.bitmask.BitMask;
import org.kie.api.internal.runtime.beliefs.Mode;

public interface TraitHelper {

    <T, K> T don(Activation activation, K core, Collection<Class<? extends Thing>> traits, boolean logical, Mode... modes);

    <T, K> T don(Activation activation, K core, Class<T> trait, boolean logical, Mode... modes);

    <T, K, X extends TraitableBean> Thing<K> shed(TraitableBean<K, X> core, Class<T> trait, Activation activation);

    void replaceCore(InternalFactHandle handle, Object object, Object originalObject, BitMask modificationMask, Class<? extends Object> aClass, Activation activation);

    void deleteWMAssertedTraitProxies(InternalFactHandle handle, RuleImpl rule, TerminalNode terminalNode);

    void updateTraits(final InternalFactHandle handle, BitMask mask, Class<?> modifiedClass, Activation activation);
}
