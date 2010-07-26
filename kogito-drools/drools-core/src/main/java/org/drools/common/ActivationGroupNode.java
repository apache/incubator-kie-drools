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

package org.drools.common;

import org.drools.core.util.AbstractBaseLinkedListNode;
import org.drools.spi.Activation;
import org.drools.spi.ActivationGroup;

public class ActivationGroupNode extends AbstractBaseLinkedListNode {

    private Activation      activation;

    private ActivationGroup activationGroup;

    public ActivationGroupNode(final Activation activation,
                               final ActivationGroup activationGroup) {
        super();
        this.activation = activation;
        this.activationGroup = activationGroup;
    }

    public Activation getActivation() {
        return this.activation;
    }

    public ActivationGroup getActivationGroup() {
        return this.activationGroup;
    }

}
