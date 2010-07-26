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

package org.drools.reteoo.compiled;

import org.drools.common.NetworkNode;
import org.drools.common.RuleBasePartitionId;
import org.drools.reteoo.*;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

/**
 * This is the base type for all generated classes that that represent a "compiled" portion of the RETE network.
 * By compiled we mean IF statements, switch statements, etc. as opposed to nodes, propagators, etc.
 *
 * @author <a href="mailto:stampy88@yahoo.com">dave sinclair</a>
 */
public abstract class CompiledNetwork implements ObjectSink {

    private ObjectTypeNode objectTypeNode;

    /**
     * Returns the unique id that represents the node in the Rete network
     *
     * @return unique int value
     */
    public int getId() {
        return objectTypeNode.getId();
    }

    /**
     * Returns the partition ID to which this node belongs to
     *
     * @return partition id
     */
    public RuleBasePartitionId getPartitionId() {
        return objectTypeNode.getPartitionId();
    }

    /**
     * The object implements the writeExternal method to save its contents
     * by calling the methods of DataOutput for its primitive values or
     * calling the writeObject method of ObjectOutput for objects, strings,
     * and arrays.
     *
     * @param out the stream to write the object to
     * @throws java.io.IOException Includes any I/O exceptions that may occur
     * @serialData Overriding methods should use this tag to describe
     * the data layout of this Externalizable object.
     * List the sequence of element types and, if possible,
     * relate the element to a public/protected field and/or
     * method of this Externalizable class.
     */
    public void writeExternal(ObjectOutput out) throws IOException {
        // todo is this needed??
    }

    /**
     * The object implements the readExternal method to restore its
     * contents by calling the methods of DataInput for primitive
     * types and readObject for objects, strings and arrays.  The
     * readExternal method must read the values in the same sequence
     * and with the same types as were written by writeExternal.
     *
     * @param in the stream to read data from in order to restore the object
     * @throws java.io.IOException    if I/O errors occur
     * @throws ClassNotFoundException If the class for an object being
     *                                restored cannot be found.
     */
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        // todo is this needed??
    }

    /**
     * Sets the OTN for this network. Calling this method will set all variables in the generated subclasses
     * by walking the {@link org.drools.reteoo.ObjectTypeNode} using a {@link ObjectTypeNodeParser}.
     * @param objectTypeNode node for whom this network was created
     */
    public final void setObjectTypeNode(final ObjectTypeNode objectTypeNode) {
        this.objectTypeNode = objectTypeNode;

        NodeReferenceSetter setter= new NodeReferenceSetter();
        ObjectTypeNodeParser parser = new ObjectTypeNodeParser(objectTypeNode);
        parser.accept(setter);
    }

    /**
     * Generated subclasses need to implement this method to set member variables based on the specified
     * NetworkNode.
     *
     * @param networkNode node to set to set
     */
    protected abstract void setNetworkNodeReference(NetworkNode networkNode);

    /**
     * Handler implementation to call {@link CompiledNetwork#setNetworkNodeReference} for each node
     * encountered in the network.
     */
    private class NodeReferenceSetter extends NetworkHandlerAdaptor {

        public void startNonHashedAlphaNode(AlphaNode alphaNode) {
            setNetworkNodeReference(alphaNode);
        }

        public void startBetaNode(BetaNode betaNode) {
            setNetworkNodeReference(betaNode);
        }

        public void startLeftInputAdapterNode(LeftInputAdapterNode leftInputAdapterNode) {
            setNetworkNodeReference(leftInputAdapterNode);
        }

        public void startHashedAlphaNode(AlphaNode hashedAlpha, Object hashedValue) {
            setNetworkNodeReference(hashedAlpha);
        }
    }
}
