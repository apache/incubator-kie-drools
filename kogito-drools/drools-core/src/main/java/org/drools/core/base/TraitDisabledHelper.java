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

/*
    Noop implementation used when drools-traits is not in the dependencies.
    Shouldn't never be called, if it happens it means we're trying to use traits without a feasible implementation
 */
public class TraitDisabledHelper implements TraitHelper {

    @Override
    public <K> K extractTrait(InternalFactHandle defaultFactHandle, Class<K> klass) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T, K> T don(Activation activation, K core, Collection<Class<? extends Thing>> traits, boolean logical, Mode... modes) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T, K> T don(Activation activation, K core, Class<T> trait, boolean logical, Mode... modes) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T, K, X extends TraitableBean> Thing<K> shed(TraitableBean<K, X> core, Class<T> trait, Activation activation) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void replaceCore(InternalFactHandle handle, Object object, Object originalObject, BitMask modificationMask, Class<?> aClass, Activation activation) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void deleteWMAssertedTraitProxies(InternalFactHandle handle, RuleImpl rule, TerminalNode terminalNode) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void updateTraits(InternalFactHandle handle, BitMask mask, Class<?> modifiedClass, Activation activation) {
        throw new UnsupportedOperationException();
    }
}
