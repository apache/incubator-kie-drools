/**
 * Copyright 2010 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.planner.config.localsearch.decider.forager;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import org.drools.planner.core.localsearch.decider.forager.AcceptedForager;
import org.drools.planner.core.localsearch.decider.forager.Forager;
import org.drools.planner.core.localsearch.decider.forager.PickEarlyType;

/**
 * @author Geoffrey De Smet
 */
@XStreamAlias("forager")
public class ForagerConfig {

    private Forager forager = null; // TODO remove this and document extending ForagerConfig
    private Class<Forager> foragerClass = null;
    private PickEarlyType pickEarlyType = null;

    protected Integer minimalAcceptedSelection = null;

    public Forager getForager() {
        return forager;
    }

    public void setForager(Forager forager) {
        this.forager = forager;
    }

    public Class<Forager> getForagerClass() {
        return foragerClass;
    }

    public void setForagerClass(Class<Forager> foragerClass) {
        this.foragerClass = foragerClass;
    }

    public PickEarlyType getPickEarlyType() {
        return pickEarlyType;
    }

    public void setPickEarlyType(PickEarlyType pickEarlyType) {
        this.pickEarlyType = pickEarlyType;
    }

    public Integer getMinimalAcceptedSelection() {
        return minimalAcceptedSelection;
    }

    public void setMinimalAcceptedSelection(Integer minimalAcceptedSelection) {
        this.minimalAcceptedSelection = minimalAcceptedSelection;
    }

    // ************************************************************************
    // Builder methods
    // ************************************************************************

    public Forager buildForager() {
        if (forager != null) {
            return forager;
        } else if (foragerClass != null) {
            try {
                return foragerClass.newInstance();
            } catch (InstantiationException e) {
                throw new IllegalArgumentException("foragerClass (" + foragerClass.getName()
                        + ") does not have a public no-arg constructor", e);
            } catch (IllegalAccessException e) {
                throw new IllegalArgumentException("foragerClass (" + foragerClass.getName()
                        + ") does not have a public no-arg constructor", e);
            }
        }
        PickEarlyType pickEarlyType = (this.pickEarlyType == null) ? PickEarlyType.NEVER : this.pickEarlyType;
        int minimalAcceptedSelection = (this.minimalAcceptedSelection == null)
                ? Integer.MAX_VALUE : this.minimalAcceptedSelection;

        return new AcceptedForager(pickEarlyType, minimalAcceptedSelection);
    }

    public void inherit(ForagerConfig inheritedConfig) {
        if (forager == null && foragerClass == null && pickEarlyType == null && minimalAcceptedSelection == null) {
            forager = inheritedConfig.getForager();
            foragerClass = inheritedConfig.getForagerClass();
            pickEarlyType = inheritedConfig.getPickEarlyType();
            minimalAcceptedSelection = inheritedConfig.getMinimalAcceptedSelection();
        }
    }

}
