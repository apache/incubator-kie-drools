/*
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

package org.drools.core.common;

import org.drools.core.spi.InternalActivationGroup;
import org.drools.core.util.AbstractBaseLinkedListNode;
import org.drools.core.spi.Activation;

public class ActivationGroupNode extends AbstractBaseLinkedListNode<ActivationGroupNode> {

    private final Activation      activation;

    private final InternalActivationGroup activationGroup;

    public ActivationGroupNode(final Activation activation,
                               final InternalActivationGroup activationGroup) {
        super();
        this.activation = activation;
        this.activationGroup = activationGroup;
    }

    public Activation getActivation() {
        return this.activation;
    }

    public InternalActivationGroup getActivationGroup() {
        return this.activationGroup;
    }

}
