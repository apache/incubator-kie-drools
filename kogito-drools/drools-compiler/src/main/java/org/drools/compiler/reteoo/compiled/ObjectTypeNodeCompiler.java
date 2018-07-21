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

package org.drools.compiler.reteoo.compiled;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collection;
import java.util.UUID;

import org.drools.compiler.builder.impl.KnowledgeBuilderImpl;
import org.drools.compiler.commons.jci.compilers.CompilationResult;
import org.drools.compiler.commons.jci.compilers.JavaCompiler;
import org.drools.compiler.commons.jci.compilers.JavaCompilerFactory;
import org.drools.compiler.compiler.PackageRegistry;
import org.drools.compiler.compiler.io.memory.MemoryFileSystem;
import org.drools.compiler.lang.descr.PackageDescr;
import org.drools.compiler.rule.builder.dialect.java.JavaDialectConfiguration;
import org.drools.core.base.ClassObjectType;
import org.drools.core.base.ValueType;
import org.drools.core.common.ProjectClassLoader;
import org.drools.core.reteoo.ObjectTypeNode;
import org.drools.core.reteoo.compiled.AssertHandler;
import org.drools.core.reteoo.compiled.CompiledNetwork;
import org.drools.core.reteoo.compiled.DeclarationsHandler;
import org.drools.core.reteoo.compiled.DelegateMethodsHandler;
import org.drools.core.reteoo.compiled.HashedAlphasDeclaration;
import org.drools.core.reteoo.compiled.ModifyHandler;
import org.drools.core.reteoo.compiled.ObjectTypeNodeParser;
import org.drools.core.reteoo.compiled.SetNodeReferenceHandler;
import org.drools.core.rule.IndexableConstraint;
import org.drools.core.spi.InternalReadAccessor;
import org.drools.core.util.IoUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * todo: document
 */
public class ObjectTypeNodeCompiler {
    private static final String NEWLINE = "\n";
    private static final String PACKAGE_NAME = "org.drools.core.reteoo.compiled";
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


    private ObjectTypeNodeCompiler(ObjectTypeNode objectTypeNode) {
        this.objectTypeNode = objectTypeNode;

        ClassObjectType classObjectType = (ClassObjectType) objectTypeNode.getObjectType();
        this.className = classObjectType.getClassName().replace("$", ".");
        final String classObjectTypeName = classObjectType.getClassName().replace('.', '_');
        final String randomId = UUID.randomUUID().toString().replace("-", "");
        generatedClassSimpleName = String.format("Compiled%sNetwork%d%s"
                , classObjectTypeName
                , objectTypeNode.getId()
                , randomId);
    }

    public static class SourceGenerated {
        public final String source;
        public final IndexableConstraint indexableConstraint;

        public SourceGenerated(String source, IndexableConstraint indexableConstraint) {
            this.source = source;
            this.indexableConstraint = indexableConstraint;
        }
    }

    private SourceGenerated generateSource() {
        createClassDeclaration();

        ObjectTypeNodeParser parser = new ObjectTypeNodeParser(objectTypeNode);

        // create declarations
        DeclarationsHandler declarations = new DeclarationsHandler(builder);
        parser.accept(declarations);

        // we need the hashed declarations when creating the constructor
        Collection<HashedAlphasDeclaration> hashedAlphaDeclarations = declarations.getHashedAlphaDeclarations();

        createConstructor(hashedAlphaDeclarations);

        // create set node method
        SetNodeReferenceHandler setNode = new SetNodeReferenceHandler(builder);
        parser.accept(setNode);

        // create assert method
        AssertHandler assertHandler = new AssertHandler(builder, className, hashedAlphaDeclarations.size() > 0);
        parser.accept(assertHandler);

        ModifyHandler modifyHandler = new ModifyHandler(builder, className, hashedAlphaDeclarations.size() > 0);
        parser.accept(modifyHandler);

        DelegateMethodsHandler delegateMethodsHandler = new DelegateMethodsHandler(builder);
        parser.accept(delegateMethodsHandler);

        // end of class
        builder.append("}").append(NEWLINE);

        return new SourceGenerated(builder.toString(), parser.getIndexableConstraint());
    }

    /**
     * This method will output the package statement, followed by the opening of the class declaration
     */
    private void createClassDeclaration() {
        builder.append("package ").append(PACKAGE_NAME).append(";").append(NEWLINE);
        builder.append("public class ").append(generatedClassSimpleName).append(" extends ").
                append(CompiledNetwork.class.getName()).append("{ ").append(NEWLINE);

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
    private void createConstructor(Collection<HashedAlphasDeclaration> hashedAlphaDeclarations) {
        builder.append("public ").append(generatedClassSimpleName).append("(org.drools.core.spi.InternalReadAccessor readAccessor) {").append(NEWLINE);


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

    private static final JavaCompiler JAVA_COMPILER = JavaCompilerFactory.getInstance().loadCompiler(JavaDialectConfiguration.CompilerType.NATIVE, "1.8");

    /**
     * Creates a {@link CompiledNetwork} for the specified {@link ObjectTypeNode}. The {@link PackageBuilder} is used
     * to compile the generated source and load the class.
     *
     * @param kBuilder     builder used to compile and load class
     * @param objectTypeNode OTN we are generating a compiled network for
     * @return CompiledNetwork
     */
    public static CompiledNetwork compile(KnowledgeBuilderImpl kBuilder, ObjectTypeNode objectTypeNode) {
        if (objectTypeNode == null) {
            throw new IllegalArgumentException("ObjectTypeNode cannot be null!");
        }
        if (kBuilder == null) {
            throw new IllegalArgumentException("PackageBuilder cannot be null!");
        }
        ObjectTypeNodeCompiler compiler = new ObjectTypeNodeCompiler(objectTypeNode);

        String packageName = compiler.getPackageName();

        PackageRegistry pkgReg = kBuilder.getPackageRegistry(packageName);
        if (pkgReg == null) {
            kBuilder.addPackage(new PackageDescr(packageName));
            pkgReg = kBuilder.getPackageRegistry(packageName);
        }

        SourceGenerated source = compiler.generateSource();

        logger.debug("Generated alpha node compiled network source:\n" + source.source);

        MemoryFileSystem mfs = new MemoryFileSystem();
        mfs.write(compiler.getSourceName(), source.source.getBytes(IoUtils.UTF8_CHARSET));

        MemoryFileSystem trg = new MemoryFileSystem();
        ProjectClassLoader rootClassLoader = (ProjectClassLoader) kBuilder.getRootClassLoader();
        CompilationResult compiled = JAVA_COMPILER.compile(new String[]{compiler.getSourceName()}, mfs, trg, rootClassLoader);

        if (compiled.getErrors().length > 0) {
            throw new RuntimeException("This is a bug. Please contact the development team:\n" + Arrays.toString(compiled.getErrors()));
        }

        rootClassLoader.defineClass(compiler.getName(), trg.getBytes(compiler.getBinaryName()));

        CompiledNetwork network;
        try {
            final Class<?> aClass = Class.forName(compiler.getName(), true, rootClassLoader);
            final IndexableConstraint indexableConstraint = source.indexableConstraint;
            network = (CompiledNetwork) aClass.getConstructor(InternalReadAccessor.class).newInstance(indexableConstraint != null ? indexableConstraint.getFieldExtractor(): null);
        } catch (Exception e) {
            throw new RuntimeException("This is a bug. Please contact the development team", e);
        }

        return network;
    }
}
