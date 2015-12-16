/*
 * Copyright 2005 Red Hat, Inc. and/or its affiliates.
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

import org.drools.core.util.AbstractBaseLinkedListNode;
import org.drools.core.spi.Activation;

public class ActivationNode extends AbstractBaseLinkedListNode<ActivationNode> {

    private static final long     serialVersionUID = 510l;

    private final Activation activation;
    private final Object parentContainer;

    public ActivationNode(final Activation activation,
                          final Object parentContainer) {
        super();
        this.activation = activation;
        this.activation.setActivationNode( this );
        this.parentContainer = parentContainer;
    }

    public Activation getActivation() {
        return this.activation;
    }

    public Object getParentContainer() {
        return this.parentContainer;
    }

}
