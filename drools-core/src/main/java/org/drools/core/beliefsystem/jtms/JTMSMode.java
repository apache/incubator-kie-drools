/*
 * Copyright 2015 JBoss Inc
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

package org.drools.core.beliefsystem.jtms;

import org.drools.core.beliefsystem.BeliefSystem;
import org.drools.core.beliefsystem.ModedAssertion;
import org.drools.core.beliefsystem.defeasible.DefeasibleLogicalDependency;
import org.drools.core.common.LogicalDependency;
import org.drools.core.spi.Activation;
import org.drools.core.util.AbstractBaseLinkedListNode;
import org.drools.core.util.LinkedListEntry;
import org.kie.internal.runtime.beliefs.Mode;

public class JTMSMode<M extends JTMSMode<M>> extends AbstractBaseLinkedListNode<M> implements ModedAssertion<M> {
    private BeliefSystem<M> bs;
    private String value;
    private LogicalDependency<M> dep;
    private Mode nextMode;

    public JTMSMode(String value, BeliefSystem bs) {
        this.value = value;
        this.bs = bs;
    }

    public JTMSMode(String value, BeliefSystem bs,  Mode nextMode) {
        this.value = value;
        this.bs = bs;
        this.nextMode = nextMode;
    }

    @Override
    public Object getBeliefSystem() {
        return bs;
    }

    public String getValue() {
        return value;
    }

    public LogicalDependency<M> getLogicalDependency() {
        return dep;
    }

    public void setLogicalDependency(LogicalDependency<M> dep) {
        this.dep = dep;
    }

    public Mode getNextMode() {
        return nextMode;
    }
}
