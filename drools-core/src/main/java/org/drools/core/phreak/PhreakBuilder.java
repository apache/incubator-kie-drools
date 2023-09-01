package org.drools.core.phreak;

import java.util.Collection;

import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.impl.InternalRuleBase;
import org.drools.core.reteoo.TerminalNode;

import static org.drools.util.Config.getConfig;

public interface PhreakBuilder {

    static PhreakBuilder get() {
        return Holder.PHREAK_BUILDER;
    }

    static boolean isEagerSegmentCreation() {
        return Holder.EAGER_SEGMENT_CREATION;
    }

    void addRule(TerminalNode tn, Collection<InternalWorkingMemory> wms, InternalRuleBase kBase);
    void removeRule(TerminalNode tn, Collection<InternalWorkingMemory> wms, InternalRuleBase kBase);

    class Holder {
        private static final boolean EAGER_SEGMENT_CREATION = Boolean.parseBoolean(getConfig("drools.useEagerSegmentCreation", "false"));
        private static final PhreakBuilder PHREAK_BUILDER = EAGER_SEGMENT_CREATION ? new EagerPhreakBuilder() : new LazyPhreakBuilder();
    }
}
