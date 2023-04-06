/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.core.phreak;

import java.util.Collection;

import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.impl.RuleBase;
import org.drools.core.reteoo.TerminalNode;

import static org.drools.util.Config.getConfig;

public interface PhreakBuilder {

    static PhreakBuilder get() {
        return Holder.PHREAK_BUILDER;
    }

    static boolean isEagerSegmentCreation() {
        return Holder.EAGER_SEGMENT_CREATION;
    }

    void addRule(TerminalNode tn, Collection<InternalWorkingMemory> wms, RuleBase kBase);
    void removeRule(TerminalNode tn, Collection<InternalWorkingMemory> wms, RuleBase kBase);

    class Holder {
        private static final boolean EAGER_SEGMENT_CREATION = Boolean.parseBoolean(getConfig("drools.useEagerSegmentCreation", "false"));
        private static final PhreakBuilder PHREAK_BUILDER = EAGER_SEGMENT_CREATION ? new EagerPhreakBuilder() : new LazyPhreakBuilder();
    }
}
