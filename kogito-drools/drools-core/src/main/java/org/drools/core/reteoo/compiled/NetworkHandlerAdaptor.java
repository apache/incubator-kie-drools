/*
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
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

package org.drools.core.reteoo.compiled;

import org.drools.core.base.ClassFieldReader;
import org.drools.core.reteoo.AlphaNode;
import org.drools.core.reteoo.BetaNode;
import org.drools.core.reteoo.LeftInputAdapterNode;
import org.drools.core.reteoo.ObjectTypeNode;

/**
 * An abstract adapter class for receiving network node events from the {@link org.kie.reteoo.compiled.ObjectTypeNodeParser}.
 * The methods in this class are empty. This class exists as convenience for creating handler objects.
 * <P>
 * Extend this class to create a <code>NetworkHandler</code> and override the methods for the nodes of interest.
 * (If you implement the {@link NetworkHandler} interface, you have to define all of the methods in it. This
 * abstract class defines null methods for them all, so you can only have to define methods for events you care about.)
 * <P>
 * @see org.kie.reteoo.compiled.NetworkHandler
 * @see org.kie.reteoo.compiled.ObjectTypeNodeParser
 */
public class NetworkHandlerAdaptor implements NetworkHandler {
    public void startObjectTypeNode(ObjectTypeNode objectTypeNode) {

    }

    public void startNonHashedAlphaNode(AlphaNode alphaNode) {

    }

    public void endNonHashedAlphaNode(AlphaNode alphaNode) {

    }

    public void startBetaNode(BetaNode betaNode) {

    }

    public void endBetaNode(BetaNode betaNode) {

    }

    public void startLeftInputAdapterNode(LeftInputAdapterNode leftInputAdapterNode) {

    }

    public void endLeftInputAdapterNode(LeftInputAdapterNode leftInputAdapterNode) {

    }

    public void startHashedAlphaNodes(ClassFieldReader hashedFieldReader) {

    }

    public void endHashedAlphaNodes(ClassFieldReader hashedFieldReader) {

    }

    public void startHashedAlphaNode(AlphaNode hashedAlpha, Object hashedValue) {

    }

    public void endHashedAlphaNode(AlphaNode hashedAlpha, Object hashedValue) {

    }

    public void endObjectTypeNode(ObjectTypeNode objectTypeNode) {

    }
}
