package org.drools.reteoo.compiled;

import org.drools.base.ClassObjectType;
import org.drools.base.ValueType;
import org.drools.compiler.PackageBuilder;
import org.drools.compiler.PackageRegistry;
import org.drools.lang.descr.PackageDescr;
import org.drools.reteoo.ObjectTypeNode;
import org.drools.rule.builder.dialect.java.JavaDialect;

import java.util.Collection;

/**
 * todo: document
 */
public class ObjectTypeNodeCompiler {
    private static final String NEWLINE = "\n";
    private static final String PACKAGE_NAME = "org.drools.reteoo.compiled";
    private static final String BINARY_PACKAGE_NAME = PACKAGE_NAME.replaceAll("\\.", "/");
    /**
     * This field hold the fully qualified class name that the {@link org.drools.reteoo.ObjectTypeNode} is representing.
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

    private ObjectTypeNodeCompiler(ObjectTypeNode objectTypeNode) {
        this.objectTypeNode = objectTypeNode;

        ClassObjectType classObjectType = (ClassObjectType) objectTypeNode.getObjectType();
        this.className = classObjectType.getClassName();
        generatedClassSimpleName = "Compiled" + classObjectType.getClassName().replaceAll("\\.", "_") + "Network";
    }

    private String generateSource() {
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

        // end of class
        builder.append("}").append(NEWLINE);

        return builder.toString();
    }

    /**
     * This method will output the package statement, followed by the opening of the class declaration
     */
    private void createClassDeclaration() {
        builder.append("package ").append(PACKAGE_NAME).append(";").append(NEWLINE);
        builder.append("public class ").append(generatedClassSimpleName).append(" extends ").
                append(CompiledNetwork.class.getName()).append("{ ").append(NEWLINE);
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
        builder.append("public ").append(generatedClassSimpleName).append("() {").append(NEWLINE);

        // for each hashed alpha, we need to fill in the map member variable with the hashed values to node Ids
        for (HashedAlphasDeclaration declaration : hashedAlphaDeclarations) {
            String mapVariableName = declaration.getVariableName();

            for (Object hashedValue : declaration.getHashedValues()) {
                Object value = hashedValue;
                // need to quote value if it is a string
                if (declaration.getValueType() == ValueType.STRING_TYPE) {
                    value = "\"" + value + "\"";
                }

                String nodeId = declaration.getNodeId(hashedValue);

                // generate the map.put(hashedValue, nodeId) call
                builder.append(mapVariableName).append(".put(").append(value).append(", ").append(nodeId).append(");");
                builder.append(NEWLINE);
            }
        }

        builder.append("}").append(NEWLINE);
    }

    /**
     * Returns the fully qualified name of the generated subclass of {@link org.drools.reteoo.compiled.CompiledNetwork}
     *
     * @return name of generated class
     */
    private String getName() {
        return getPackageName() + "." + generatedClassSimpleName;
    }

    /**
     * Returns the fully qualified binary name of the generated subclass of {@link org.drools.reteoo.compiled.CompiledNetwork}
     *
     * @return binary name of generated class
     */
    private String getBinaryName() {
        return BINARY_PACKAGE_NAME + "." + generatedClassSimpleName + ".class";
    }

    private String getPackageName() {
        return PACKAGE_NAME;
    }

    /**
     * Creates a {@link CompiledNetwork} for the specified {@link ObjectTypeNode}. The {@link PackageBuilder} is used
     * to compile the generated source and load the class.
     *
     * @param pkgBuilder     builder used to compile and load class
     * @param objectTypeNode OTN we are generating a compiled network for
     * @return CompiledNetwork
     */
    public static CompiledNetwork compile(PackageBuilder pkgBuilder, ObjectTypeNode objectTypeNode) {
        if (objectTypeNode == null) {
            throw new IllegalArgumentException("ObjectTypeNode cannot be null!");
        }
        if (pkgBuilder == null) {
            throw new IllegalArgumentException("PackageBuilder cannot be null!");
        }
        ObjectTypeNodeCompiler compiler = new ObjectTypeNodeCompiler(objectTypeNode);

        String packageName = compiler.getPackageName();

        PackageRegistry pkgReg = pkgBuilder.getPackageRegistry(packageName);
        if (pkgReg == null) {
            pkgBuilder.addPackage(new PackageDescr(packageName));
            pkgReg = pkgBuilder.getPackageRegistry(packageName);
        }

        String source = compiler.generateSource();
        String generatedSourceName = compiler.getName();

        JavaDialect dialect = (JavaDialect) pkgReg.getDialectCompiletimeRegistry().getDialect("java");
        dialect.addSrc(compiler.getBinaryName(), source.getBytes());
        pkgBuilder.compileAll();
        pkgBuilder.updateResults();

        CompiledNetwork network;
        try {
            network = (CompiledNetwork) Class.forName(generatedSourceName, true, pkgBuilder.getRootClassLoader()).newInstance();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("This is a bug. Please contact the development team", e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("This is a bug. Please contact the development team", e);
        } catch (InstantiationException e) {
            throw new RuntimeException("This is a bug. Please contact the development team", e);
        }

        return network;
    }
}
