package org.drools.core.base;

import java.util.Collection;

import org.drools.core.common.InternalFactHandle;
import org.drools.base.definitions.rule.impl.RuleImpl;
import org.drools.base.factmodel.traits.Thing;
import org.drools.base.factmodel.traits.TraitableBean;
import org.drools.core.reteoo.TerminalNode;
import org.drools.core.rule.consequence.InternalMatch;
import org.drools.core.util.bitmask.BitMask;
import org.drools.base.beliefsystem.Mode;

public interface TraitHelper {

    <K> K extractTrait(InternalFactHandle defaultFactHandle, Class<K> klass);

    <T, K> T don(InternalMatch internalMatch, K core, Collection<Class<? extends Thing>> traits, boolean logical, Mode... modes);

    <T, K> T don(InternalMatch internalMatch, K core, Class<T> trait, boolean logical, Mode... modes);

    <T, K, X extends TraitableBean> Thing<K> shed(TraitableBean<K, X> core, Class<T> trait, InternalMatch internalMatch);

    void replaceCore(InternalFactHandle handle, Object object, Object originalObject, BitMask modificationMask, Class<? extends Object> aClass, InternalMatch internalMatch);

    void deleteWMAssertedTraitProxies(InternalFactHandle handle, RuleImpl rule, TerminalNode terminalNode);

    void updateTraits(final InternalFactHandle handle, BitMask mask, Class<?> modifiedClass, InternalMatch internalMatch);
}
