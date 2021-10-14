/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.drools.ancompiler;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.drools.core.InitialFact;
import org.drools.core.base.ClassObjectType;
import org.drools.core.reteoo.ObjectTypeNode;
import org.drools.core.reteoo.Rete;
import org.drools.core.util.index.AlphaRangeIndex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ObjectTypeNodeCompiler {

    private static final String NEWLINE = "\n";
    private static final String PACKAGE_NAME = "org.drools.ancompiler";
    private static final String BINARY_PACKAGE_NAME = PACKAGE_NAME.replace('.', '/');
    /**
     * This field hold the fully qualified class name that the {@link ObjectTypeNode} is representing.
     */
    private String className;

    /**
     * This field will hold the "simple" name of the generated class
     */
    private String generatedClassSimpleName;

    /**
     * OTN we are creating a compiled network for
     */
    private ObjectTypeNode objectTypeNode;

    private StringBuilder builder = new StringBuilder();

    private static final Logger logger = LoggerFactory.getLogger(ObjectTypeNodeCompiler.class);

    public ObjectTypeNodeCompiler(ObjectTypeNode objectTypeNode) {
        this.objectTypeNode = objectTypeNode;

        ClassObjectType classObjectType = (ClassObjectType) objectTypeNode.getObjectType();
        this.className = classObjectType.getClassName().replace("$", ".");
        final String classObjectTypeName = classObjectType.getClassName().replace('.', '_');
        final String otnHash = String.valueOf(objectTypeNode.hashCode()).replace("-", "");
        generatedClassSimpleName = String.format("Compiled%sNetwork%d%s"
                , classObjectTypeName
                , objectTypeNode.getId()
                , otnHash);
    }

    public CompiledNetworkSource generateSource() {
        createClassDeclaration();

        ObjectTypeNodeParser parser = new ObjectTypeNodeParser(objectTypeNode);

        // debug rete
        logger.debug("Compiling Alpha Network: ");
        DebugHandler debugHandler = new DebugHandler();
        parser.accept(debugHandler);

        // After the first parsing we decide whether to traverse hashedAlphaNodes or not

        if(parser.getIndexableConstraints().size() > 1) {
            logger.warn("Alpha Network Compiler with multiple Indexable Constraints is not supported, reverting to non hashed-ANC. This might be slower ");
            parser.setTraverseHashedAlphaNodes(false);
        }

        // create declarations
        DeclarationsHandler declarations = new DeclarationsHandler(builder);
        parser.accept(declarations);

        // we need the hashed declarations when creating the constructor
        Collection<HashedAlphasDeclaration> hashedAlphaDeclarations = declarations.getHashedAlphaDeclarations();

        Map<String, AlphaRangeIndex> rangeIndexDeclarationMap = declarations.getRangeIndexDeclarationMap();

        createConstructor(hashedAlphaDeclarations, rangeIndexDeclarationMap);

        // create set node method
        NodeCollectorHandler nodeCollectors = new NodeCollectorHandler();
        parser.accept(nodeCollectors);

        SetNodeReferenceHandler partitionedSwitch = new SetNodeReferenceHandler(nodeCollectors.getNodes());
        partitionedSwitch.emitCode(builder);

        // create assert method
        AssertHandler assertHandler = new AssertHandler(className, !hashedAlphaDeclarations.isEmpty());
        parser.accept(assertHandler);
        builder.append(assertHandler.emitCode());

        ModifyHandler modifyHandler = new ModifyHandler(className, !hashedAlphaDeclarations.isEmpty());
        parser.accept(modifyHandler);
        builder.append(modifyHandler.emitCode());

        DelegateMethodsHandler delegateMethodsHandler = new DelegateMethodsHandler(builder);
        parser.accept(delegateMethodsHandler);

        // end of class
        builder.append("}").append(NEWLINE);

        String sourceCode = builder.toString();
        if (logger.isDebugEnabled()) {
            logger.debug(String.format("Generated Compiled Alpha Network %s", sourceCode));
        }

        return new CompiledNetworkSource(
                sourceCode,
                parser.getIndexableConstraint(),
                getName(),
                getSourceName(),
                objectTypeNode,
                rangeIndexDeclarationMap);
    }

    /**
     * This method will output the package statement, followed by the opening of the class declaration
     */
    private void createClassDeclaration() {
        builder.append("package ").append(PACKAGE_NAME).append(";").append(NEWLINE);
        builder.append("public class ").append(generatedClassSimpleName).append(" extends ").
                append(CompiledNetwork.class.getName()).append("{ ").append(NEWLINE);

        builder.append(String.format("private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(%s.class);%n", generatedClassSimpleName));
        builder.append("org.drools.core.spi.InternalReadAccessor readAccessor;\n");
    }

    /**
     * Creates the constructor for the generated class. If the hashedAlphaDeclarations is empty, it will just
     * output a empty default constructor; if it is not, the constructor will contain code to fill the hash
     * alpha maps with the values and node ids.
     *
     * @param hashedAlphaDeclarations declarations used for creating statements to populate the hashed alpha
     *                                maps for the generate class
     */
    private void createConstructor(Collection<HashedAlphasDeclaration> hashedAlphaDeclarations, Map<String, AlphaRangeIndex> rangeIndexDeclarationMap) {
        builder.append("public ").append(generatedClassSimpleName).append("(org.drools.core.spi.InternalReadAccessor readAccessor, java.util.Map<String, " + AlphaRangeIndex.class.getCanonicalName() + "> rangeIndexDeclarationMap) {").append(NEWLINE);

        builder.append("this.readAccessor = readAccessor;\n");
        // for each hashed alpha, we need to fill in the map member variable with the hashed values to node Ids
        for (HashedAlphasDeclaration declaration : hashedAlphaDeclarations) {
            String mapVariableName = declaration.getVariableName();

            for (Object hashedValue : declaration.getHashedValues()) {
                Object value = hashedValue;

                if (value == null) {
                    // generate the map.put(hashedValue, nodeId) call
                    String nodeId = declaration.getNodeId(hashedValue);

                    builder.append(mapVariableName).append(".put(null,").append(nodeId).append(");");
                    builder.append(NEWLINE);
                } else {

                    // need to quote value if it is a string
                    if (value.getClass().equals(String.class)) {
                        value = "\"" + value + "\"";
                    } else if (value instanceof BigDecimal) {
                        value = "new java.math.BigDecimal(\"" + value + "\")";
                    } else if (value instanceof BigInteger) {
                        value = "new java.math.BigInteger(\"" + value + "\")";
                    }

                    String nodeId = declaration.getNodeId(hashedValue);

                    // generate the map.put(hashedValue, nodeId) call
                    builder.append(mapVariableName).append(".put(").append(value).append(", ").append(nodeId).append(");");
                    builder.append(NEWLINE);
                }
            }
        }

        // Range Index
        for (String variableName : rangeIndexDeclarationMap.keySet()) {
            builder.append("this." + variableName + " = rangeIndexDeclarationMap.get(\"" + variableName + "\");");
            builder.append(NEWLINE);
        }

        builder.append("}").append(NEWLINE);
    }

    /**
     * Returns the fully qualified name of the generated subclass of {@link CompiledNetwork}
     *
     * @return name of generated class
     */
    private String getName() {
        return getPackageName() + "." + generatedClassSimpleName;
    }

    /**
     * Returns the fully qualified binary name of the generated subclass of {@link CompiledNetwork}
     *
     * @return binary name of generated class
     */
    private String getBinaryName() {
        return BINARY_PACKAGE_NAME + "/" + generatedClassSimpleName + ".class";
    }

    /**
     * Returns the fully qualified source name of the generated subclass of {@link CompiledNetwork}
     *
     * @return binary name of generated class
     */
    private String getSourceName() {
        return BINARY_PACKAGE_NAME + "/" + generatedClassSimpleName + ".java";
    }

    private String getPackageName() {
        return PACKAGE_NAME;
    }

    public static List<CompiledNetworkSource> compiledNetworkSources(Rete rete) {
        return objectTypeNodeCompiler(rete)
                .stream()
                .map(ObjectTypeNodeCompiler::generateSource)
                .collect(Collectors.toList());
    }

    public static List<ObjectTypeNodeCompiler> objectTypeNodeCompiler(Rete rete) {
        return objectTypeNodes(rete)
                .stream()
                .map(ObjectTypeNodeCompiler::new)
                .collect(Collectors.toList());
    }

    public static List<ObjectTypeNode> objectTypeNodes(Rete rete) {
        return rete.getEntryPointNodes().values().stream()
                .flatMap(ep -> ep.getObjectTypeNodes().values().stream())
                .filter(ObjectTypeNodeCompiler::shouldCreateCompiledAlphaNetwork)
                .collect(Collectors.toList());
    }

    private static boolean shouldCreateCompiledAlphaNetwork(ObjectTypeNode f) {
        return !InitialFact.class.isAssignableFrom(f.getObjectType().getClassType())
                && !(f.getObjectSinkPropagator() instanceof CompiledNetwork); // DROOLS-6336 Avoid generating an ANC from an ANC, it won't work anyway
    }

    public static Map<String, CompiledNetworkSource> compiledNetworkSourceMap(Rete rete) {
        List<CompiledNetworkSource> compiledNetworkSources = ObjectTypeNodeCompiler.compiledNetworkSources(rete);
        return compiledNetworkSources
                .stream()
                .collect(Collectors.toMap(CompiledNetworkSource::getName, Function.identity()));
    }

    public static Map<ObjectTypeNode, String> otnWithClassName(Rete rete) {
        List<ObjectTypeNodeCompiler> compiledNetworkSources = ObjectTypeNodeCompiler.objectTypeNodeCompiler(rete);
        return compiledNetworkSources
                .stream()
                .collect(Collectors.toMap(k -> k.objectTypeNode, ObjectTypeNodeCompiler::getName));
    }
}
