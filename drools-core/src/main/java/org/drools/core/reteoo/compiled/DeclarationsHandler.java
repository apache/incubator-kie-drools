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
import org.drools.core.reteoo.Sink;
import org.drools.core.rule.ContextEntry;

import java.util.*;

/**
 * This handler is used to create the member declarations section of a generated subclass of a {@link CompiledNetwork}.
 * Currently we only create member variables for the following types of nodes:
 * <p/>
 * <li>Non-hashed {@link AlphaNode}s</li>
 * <li>{@link LeftInputAdapterNode}s</li>
 * <li>{@link BetaNode}s</li>
 * <li>A {@link Map} for each set of hashed {@link AlphaNode}s. The keys are the hashed values, and the values are
 * the IDs of the alphas</li>
 */
public class DeclarationsHandler extends AbstractCompilerHandler {
    private static final String PRIVATE_MODIFIER = "private";

    /**
     * This field keeps track of the current set of hashed AlphaNodes for a ClassReader.
     *
     * @see #startHashedAlphaNodes(org.kie.base.ClassFieldReader)
     * @see #startHashedAlphaNode(org.kie.reteoo.AlphaNode, Object)
     */
    private HashedAlphasDeclaration currentHashedAlpha;

    private final StringBuilder builder;

    /**
     * Keeps track of all the ClassFieldReaders for hashed alphas, and the maps that contain hashed values/node ids
     * for said alphas.
     */
    private final Collection<HashedAlphasDeclaration> hashedAlphaDeclarations;

    public DeclarationsHandler(StringBuilder builder) {
        this.builder = builder;
        this.hashedAlphaDeclarations = new LinkedList<HashedAlphasDeclaration>();
    }

    private String getVariableDeclaration(AlphaNode alphaNode) {
        Class<?> variableType = getVariableType(alphaNode);
        String variableName = getVariableName(alphaNode);
        // comment for variable declaration is just the toString of the node
        String comment = alphaNode.toString();

        return PRIVATE_MODIFIER + " " + variableType.getName() + " " + variableName + "; // " + comment;
    }

    private String getContextVariableDeclaration(AlphaNode alphaNode){
        Class<?> variableType = ContextEntry.class;
        String variableName = getContextVariableName(alphaNode);

        return PRIVATE_MODIFIER + " " + variableType.getName() + " " + variableName + ";";
    }

    private String getVariableDeclaration(Sink sink) {
        Class<?> declarationType = getVariableType(sink);
        String variableName = getVariableName(sink);
        // comment for variable declaration is just the toString of the node
        String comment = sink.toString();

        return PRIVATE_MODIFIER + " " + declarationType.getName() + " " + variableName + "; // " + comment;
    }

    private String getVariableDeclaration(ClassFieldReader fieldReader) {
        Class<?> declarationType = Map.class;
        Class<?> createType = HashMap.class;
        String variableName = getVariableName(fieldReader);

        // todo JANINO doesn't support generics
        // return "private java.util.Map<Object,Integer> " + variableName + " = new java.util.HashMap<Object,Integer>();";
        return PRIVATE_MODIFIER + " " + declarationType.getName() + " " + variableName
                + " = new " + createType.getName() + "();";
    }

    public Collection<HashedAlphasDeclaration> getHashedAlphaDeclarations() {
        return Collections.unmodifiableCollection(hashedAlphaDeclarations);
    }

    @Override
    public void startNonHashedAlphaNode(AlphaNode alphaNode) {
        builder.append(getVariableDeclaration(alphaNode)).append(NEWLINE);
        builder.append(getContextVariableDeclaration(alphaNode)).append(NEWLINE);
    }

    @Override
    public void startBetaNode(BetaNode betaNode) {
        builder.append(getVariableDeclaration(betaNode)).append(NEWLINE);
    }

    @Override
    public void startLeftInputAdapterNode(LeftInputAdapterNode leftInputAdapterNode) {
        builder.append(getVariableDeclaration(leftInputAdapterNode)).append(NEWLINE);
    }

    @Override
    public void startHashedAlphaNodes(ClassFieldReader hashedFieldReader) {
        // we create a new hashed alpha that will be used to keep track of the hashes values to node ID for each
        // class field reader.
        currentHashedAlpha = new HashedAlphasDeclaration(getVariableName(hashedFieldReader),
                hashedFieldReader.getValueType());

        // add the new declaration
        hashedAlphaDeclarations.add(currentHashedAlpha);

        builder.append(getVariableDeclaration(hashedFieldReader)).append(NEWLINE);
    }

    @Override
    public void startHashedAlphaNode(AlphaNode hashedAlpha, Object hashedValue) {
        currentHashedAlpha.add(hashedValue, String.valueOf(hashedAlpha.getId()));
    }
}
