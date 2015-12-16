/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

package org.drools.core.common;

import org.drools.core.BeliefSystemType;
import org.drools.core.beliefsystem.BeliefSystem;
import org.drools.core.beliefsystem.defeasible.DefeasibleBeliefSystem;
import org.drools.core.beliefsystem.jtms.JTMSBeliefSystem;
import org.drools.core.beliefsystem.simple.SimpleBeliefSystem;

import java.io.Serializable;

public class PhreakBeliefSystemFactory implements BeliefSystemFactory, Serializable {

    public BeliefSystem createBeliefSystem(BeliefSystemType type, NamedEntryPoint ep,
                                           TruthMaintenanceSystem tms) {
        switch(type) {
            case SIMPLE:
                return new SimpleBeliefSystem(ep, tms);
            case JTMS:
                return new JTMSBeliefSystem( ep, tms );
            case DEFEASIBLE:
                return new DefeasibleBeliefSystem( ep, tms );
        }
        throw new UnsupportedOperationException();
    }
}
