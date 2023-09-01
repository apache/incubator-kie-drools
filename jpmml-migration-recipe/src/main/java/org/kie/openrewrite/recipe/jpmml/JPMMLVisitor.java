/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.kie.openrewrite.recipe.jpmml;

import org.openrewrite.ExecutionContext;
import org.openrewrite.Tree;
import org.openrewrite.java.ChangeType;
import org.openrewrite.java.JavaParser;
import org.openrewrite.java.JavaTemplate;
import org.openrewrite.java.JavaVisitor;
import org.openrewrite.java.tree.*;
import org.openrewrite.marker.Markers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.nio.file.Path;
import java.util.*;

public class JPMMLVisitor extends JavaVisitor<ExecutionContext> {

    public static final String NEW_JPMML_MODEL = "pmml-model-1.6.4.jar";

    public static final String NEW_JPMML_MAVEN_PATH = String.format("%1$s%2$s.m2%2$srepository%2$sorg%2$sjpmml%2$spmml-model%2$s1.6.4%2$s%3$s", System.getProperty("user.home"), File.separator, NEW_JPMML_MODEL);

    private static final Logger logger = LoggerFactory.getLogger(JPMMLVisitor.class);
    static final String JPMML_MODEL_PACKAGE_BASE = "org.jpmml.model";
    static final String DMG_PMML_MODEL_PACKAGE_BASE = "org.dmg.pmml";
    final JavaType.Class originalInstantiatedType;
    final JavaType targetInstantiatedType;

    private static final String FIELD_NAME_FQDN = "org.dmg.pmml.FieldName";
    private static final String MODEL_NAME_FQDN = "org.dmg.pmml.Model";
    private static final String MINING_FUNCTION_NAME_FQDN = "org.dmg.pmml.MiningFunction";
    private static final String MINING_SCHEMA_NAME_FQDN = "org.dmg.pmml.MiningSchema";

    private static final String NUMERIC_PREDICTOR_FQDN = "org.dmg.pmml.regression.NumericPredictor";

    private static final String CATEGORICAL_PREDICTOR_FQDN = "org.dmg.pmml.regression.CategoricalPredictor";

    private static final List<String> GET_NAME_TO_GET_FIELD_CLASSES = Arrays.asList(NUMERIC_PREDICTOR_FQDN,
            CATEGORICAL_PREDICTOR_FQDN);

    private static final String DATADICTIONARY_FQDN = "org.dmg.pmml.DataDictionary";

    private static final Map<String, RemovedListTupla> REMOVED_LIST_FROM_INSTANTIATION = Map.of(DATADICTIONARY_FQDN,
            new RemovedListTupla("addDataFields", JavaType.buildType("org.dmg.pmml.DataField")));


    private static final J.Identifier STRING_IDENTIFIER = new J.Identifier(Tree.randomId(), Space.build(" ", Collections.emptyList()), Markers.EMPTY, "String", JavaType.buildType(String.class.getCanonicalName()), null);

    private static final J.Identifier PREDICTOR_GET_FIELD_IDENTIFIER = new J.Identifier(Tree.randomId(), Space.EMPTY, Markers.EMPTY, "getField", JavaType.Primitive.String, null);


    private static final JavaType LIST_JAVA_TYPE = JavaType.buildType(List.class.getCanonicalName());

    private static final JavaType.Parameterized LIST_GENERIC_JAVA_TYPE = new JavaType.Parameterized(null, (JavaType.FullyQualified) LIST_JAVA_TYPE, List.of(JavaType.GenericTypeVariable.Primitive.String));

    private static final JavaParser NEW_JPMMLMODEL_JAVAPARSER = getNewJPMMLJavaParser();

    private final JavaTemplate requireMiningFunctionTemplate = JavaTemplate.builder(this::getCursor,
                    "@Override\n" +
                            "    public MiningFunction requireMiningFunction() {\n" +
                            "        return null;\n" +
                            "    }\n")
            .javaParser(() -> NEW_JPMMLMODEL_JAVAPARSER)
            .build();

    private final JavaTemplate requireMiningSchemaTemplate = JavaTemplate.builder(this::getCursor,
                    "@Override\n" +
                            "    public MiningSchema requireMiningSchema() {\n" +
                            "        return null;\n" +
                            "    }\n")
            .javaParser(() -> NEW_JPMMLMODEL_JAVAPARSER)
            .build();


    public JPMMLVisitor(String oldInstantiatedFullyQualifiedTypeName, String newInstantiatedFullyQualifiedTypeName) {
        this.originalInstantiatedType = JavaType.ShallowClass.build(oldInstantiatedFullyQualifiedTypeName);
        this.targetInstantiatedType = JavaType.buildType(newInstantiatedFullyQualifiedTypeName);
    }


    @Override
    public J visitBinary(J.Binary binary, ExecutionContext executionContext) {
        logger.trace("visitBinary {}", binary);
        Expression left = (Expression) super.visitExpression(binary.getLeft(), executionContext);
        Expression right = (Expression) super.visitExpression(binary.getRight(), executionContext);
        binary = binary
                .withLeft(left)
                .withRight(right);
        return super.visitBinary(binary, executionContext);
    }

    @Override
    public J.ClassDeclaration visitClassDeclaration(J.ClassDeclaration classDecl, ExecutionContext executionContext) {
        if (extendsModel(classDecl)) {
            classDecl = addMissingMethod(classDecl, "requireMiningFunction", requireMiningFunctionTemplate);
            classDecl = addMissingMethod(classDecl, "requireMiningSchema", requireMiningSchemaTemplate);
        }
        return (J.ClassDeclaration) super.visitClassDeclaration(classDecl, executionContext);
    }

    @Override
    public J.CompilationUnit visitCompilationUnit(J.CompilationUnit cu, ExecutionContext executionContext) {
        logger.trace("visitCompilationUnit {}", cu);
        String cuName = cu.getSourcePath().toString();
        boolean toMigrate = toMigrate(cu.getImports());
        if (!toMigrate) {
            logger.info("Skipping {}", cuName);
            return cu;
        } else {
            logger.info("Going to migrate {}", cuName);
        }
        try {
            cu = (J.CompilationUnit) super.visitCompilationUnit(cu, executionContext);
            maybeAddImport(targetInstantiatedType.toString());
            maybeAddImport(MINING_FUNCTION_NAME_FQDN);
            maybeAddImport(MINING_SCHEMA_NAME_FQDN);
            maybeRemoveImport(FIELD_NAME_FQDN);
            cu = (J.CompilationUnit) new ChangeType(FIELD_NAME_FQDN, String.class.getCanonicalName(), false)
                    .getVisitor()
                    .visitCompilationUnit(cu, executionContext);
            return cu;
        } catch (Throwable t) {
            logger.error("Failed to visit {}", cu, t);
            return cu;
        }
    }

    @Override
    public J visitMethodInvocation(J.MethodInvocation method, ExecutionContext executionContext) {
        logger.trace("visitMethodInvocation {}", method);
        if (isFieldNameCreate(method)) {
            Expression createArgument = method.getArguments().get(0);
            createArgument = (Expression) super.visit(createArgument, executionContext);
            return createArgument;
        }
        if (useFieldNameGetValue(method)) {
            return method.getSelect();
        }
        if (isFieldNameGetNameToGetFieldMapped(method)) {
            JavaType.Method methodType = method
                    .getMethodType()
                    .withReturnType(JavaType.Primitive.String);
            return method
                    .withName(PREDICTOR_GET_FIELD_IDENTIFIER)
                    .withMethodType(methodType);
        }
        if (hasFieldNameParameter(method)) {
            JavaType.Method methodType = method.getMethodType()
                    .withParameterTypes(Collections.singletonList(JavaType.Primitive.String));
            return method.withMethodType(methodType);
        }
        return super.visitMethodInvocation(method, executionContext);
    }


    @Override
    public J visitNewClass(J.NewClass newClass, ExecutionContext executionContext) {
        logger.trace("visitNewClass {}", newClass);
        J toReturn = replaceInstantiation(newClass);
        if (toReturn != newClass) {
            return toReturn;
        } else {
            return super.visitNewClass(newClass, executionContext);
        }
    }

    @Override
    public J.VariableDeclarations.NamedVariable visitVariable(J.VariableDeclarations.NamedVariable variable, ExecutionContext executionContext) {
        logger.trace("visitVariable {}", variable);
        if (variable.getType() != null && variable.getType().toString().equals(FIELD_NAME_FQDN)) {
            variable = variable
                    .withType(JavaType.Primitive.String)
                    .withVariableType(variable.getVariableType().withType(JavaType.Primitive.String));
        }
        return (J.VariableDeclarations.NamedVariable) super.visitVariable(variable, executionContext);
    }

    @Override
    public J.VariableDeclarations visitVariableDeclarations(J.VariableDeclarations multiVariable,
                                                            ExecutionContext executionContext) {
        logger.trace("visitVariableDeclarations {}", multiVariable);
        multiVariable = (J.VariableDeclarations) super.visitVariableDeclarations(multiVariable, executionContext);
        if (multiVariable.getTypeAsFullyQualified() != null &&
                multiVariable.getTypeAsFullyQualified().getFullyQualifiedName() != null &&
                multiVariable.getTypeAsFullyQualified().getFullyQualifiedName().equals(FIELD_NAME_FQDN)) {
            multiVariable = multiVariable.withType(JavaType.Primitive.String).withTypeExpression(STRING_IDENTIFIER);
        }
        return multiVariable;
    }

    /**
     * Return <code>true</code> if the given <code>J.ClassDeclaration</code> extends it extends <code>org.dmg.pmml.Model</code>
     *
     * @param classDecl
     * @return
     */
    protected boolean extendsModel(J.ClassDeclaration classDecl) {
        return classDecl.getType() != null &&
                classDecl.getType().getSupertype() != null &&
                MODEL_NAME_FQDN.equals(classDecl.getType().getSupertype().getFullyQualifiedName());
    }

    /**
     * Return <code>true</code> if the given <code>J.CompilationUnit</code> contains an {@see #FIELD_NAME_FQDN} import,
     * <code>false</code> otherwise
     *
     * @param toCheck
     * @return
     */
    protected boolean hasFieldNameImport(J.CompilationUnit toCheck) {
        return toCheck.getImports().stream().anyMatch(this::isFieldNameImport);
    }

    /**
     * Return <code>true</code> if the given <code>J.Import</code> is {@see #FIELD_NAME_FQDN},
     * <code>false</code> otherwise
     *
     * @param toCheck
     * @return
     */
    protected boolean isFieldNameImport(J.Import toCheck) {
        return isSpecificImport(toCheck, FIELD_NAME_FQDN);
    }

    /**
     * Return <code>true</code> if the given <code>J.Import</code> is fqdn,
     * <code>false</code> otherwise
     *
     * @param toCheck
     * @return
     */
    protected boolean isSpecificImport(J.Import toCheck, String fqdn) {
        return (toCheck.getQualid().getType() instanceof JavaType.Class) && ((JavaType.Class) toCheck.getQualid().getType()).getFullyQualifiedName().equals(fqdn);
    }

    /**
     * Add a <code>J.MethodDeclaration</code> to the given <code>J.ClassDeclaration</code>  if the latter does not contain the <b>searchedMethod</b>,
     * otherwise it does nothing
     *
     * @param classDecl
     * @param searchedMethod
     * @param javaTemplate
     * @return
     */
    protected J.ClassDeclaration addMissingMethod(J.ClassDeclaration classDecl, String searchedMethod, JavaTemplate javaTemplate) {
        if (methodExists(classDecl, searchedMethod)) {
            return classDecl;
        }
        classDecl = classDecl.withBody(
                classDecl.getBody().withTemplate(
                        javaTemplate,
                        classDecl.getBody().getCoordinates().lastStatement()
                ));
        return classDecl;
    }

    /**
     * Return <code>true</code> if the given <code>J.ClassDeclaration</code> contains the <b>searchedMethod</b>,
     * <code>false</code> otherwise
     *
     * @param toCheck
     * @param searchedMethod
     * @return
     */
    protected boolean methodExists(J.ClassDeclaration toCheck, String searchedMethod) {
        return toCheck.getBody().getStatements().stream()
                .filter(statement -> statement instanceof J.MethodDeclaration)
                .map(J.MethodDeclaration.class::cast)
                .anyMatch(methodDeclaration -> methodDeclaration.getName().getSimpleName().equals(searchedMethod));
    }

    /**
     * @param newClass
     * @return
     */
    protected Expression replaceInstantiation(J.NewClass newClass) {
        logger.trace("replaceInstantiation {}", newClass);
        newClass = replaceOriginalToTargetInstantiation(newClass);
        return replaceInstantiationListRemoved(newClass);
    }

    /**
     * Returns a new <code>J.NewClass</code> with the <code>originalInstantiatedType</code>
     * replaced by <code>targetInstantiatedType</code>, if present.
     * Otherwise, returns the original newClass.
     *
     * @param newClass
     * @return
     */
    protected J.NewClass replaceOriginalToTargetInstantiation(J.NewClass newClass) {
        logger.trace("replaceOriginalToTargetInstantiation {}", newClass);
        if (newClass.getType() != null && newClass.getType().toString().equals(originalInstantiatedType.toString())) {
            JavaType.Method updatedMethod = updateMethodToTargetInstantiatedType(newClass.getConstructorType());
            TypeTree typeTree = updateTypeTreeToTargetInstantiatedType(newClass);
            newClass = newClass.withConstructorType(updatedMethod)
                    .withClazz(typeTree);
        }
        return newClass;
    }

    /**
     * Returns a new <code>J.NewClass</code> with the <code>originalInstantiatedType</code>
     * replaced by <code>targetInstantiatedType</code>, if present.
     * Otherwise, returns the original newClass.
     *
     * @param newClass
     * @return
     */
    protected Expression replaceInstantiationListRemoved(J.NewClass newClass) {
        logger.trace("replaceInstantiationListRemoved {}", newClass);
        Optional<RemovedListTupla> optionalRetrieved = getRemovedListTupla(newClass);
        if (optionalRetrieved.isPresent()) {
            RemovedListTupla removedListTupla = optionalRetrieved.get();
            return removedListTupla.getJMethod(newClass);
        } else {
            return newClass;
        }
    }

    /**
     * Return <code>Optional&lt;RemovedListTupla&gt;</code> if the given <code>J.NewClass</code> constructor has not the <b>List</b> anymore
     * <code>Optional.empty()</code> otherwise
     *
     * @param toCheck
     * @return
     */
    protected Optional<RemovedListTupla> getRemovedListTupla(J.NewClass toCheck) {
        return toCheck.getType() != null &&
                REMOVED_LIST_FROM_INSTANTIATION.containsKey(toCheck.getType().toString()) &&
                toCheck.getArguments() != null &&
                !toCheck.getArguments().isEmpty()
                && (toCheck.getArguments().get(0) instanceof J.Identifier) ? Optional.of(REMOVED_LIST_FROM_INSTANTIATION.get(toCheck.getType().toString())) : Optional.empty();
    }

    /**
     * Return <code>true</code> if the given <code>J.MethodInvocation</code> is <b>FieldName.create(...)</b>,
     * <code>false</code> otherwise
     *
     * @param toCheck
     * @return
     */
    protected boolean isFieldNameCreate(J.MethodInvocation toCheck) {
        return toCheck.getType() != null && toCheck.getType().toString().equals(FIELD_NAME_FQDN) && toCheck.getName().toString().equals("create");
    }

    /**
     * Return <code>true</code> if the given <code>J.MethodInvocation</code> is <b>FieldName.create(...)</b>,
     * <code>false</code> otherwise
     *
     * @param toCheck
     * @return
     */
    protected boolean hasFieldNameParameter(J.MethodInvocation toCheck) {
        return toCheck.getMethodType() != null &&
                toCheck.getMethodType().getParameterTypes() != null &&
                toCheck.getMethodType().getParameterTypes().stream().anyMatch(javaType -> javaType != null && javaType.toString().equals(FIELD_NAME_FQDN));
    }

    /**
     * Return <code>true</code> if the given <code>J.MethodInvocation</code> is <b>#FieldName(_any_).getName(...)</b>,
     * and the modified method is <b>String(_any_).getField(...)</b>
     * <code>false</code> otherwise.
     * Mapped elements are defined in {@link #GET_NAME_TO_GET_FIELD_CLASSES}
     *
     * @param toCheck
     * @return
     */
    protected boolean isFieldNameGetNameToGetFieldMapped(J.MethodInvocation toCheck) {
        return toCheck.getMethodType() != null &&
                toCheck.getMethodType().getDeclaringType() != null &&
                GET_NAME_TO_GET_FIELD_CLASSES.contains(toCheck.getMethodType().getDeclaringType().toString()) &&
                toCheck.getName().toString().equals("getName");
    }


    /**
     * Return <code>true</code> if the given <code>J.MethodInvocation</code> invokes <b>(_field_).getValue()</b>,
     * <code>false</code> otherwise
     *
     * @param toCheck
     * @return
     */
    protected boolean useFieldNameGetValue(J.MethodInvocation toCheck) {
        return toCheck.getMethodType() != null &&
                toCheck.getMethodType().getDeclaringType() != null &&
                toCheck.getMethodType().getDeclaringType().getFullyQualifiedName() != null &&
                toCheck.getMethodType().getDeclaringType().getFullyQualifiedName().equals(FIELD_NAME_FQDN) && toCheck.getMethodType().getName().equals("getValue");
    }

    protected boolean toMigrate(List<J.Import> imports) {
        return imports.stream()
                .anyMatch(anImport -> anImport.getPackageName().startsWith(JPMML_MODEL_PACKAGE_BASE) ||
                        anImport.getPackageName().startsWith(DMG_PMML_MODEL_PACKAGE_BASE));
    }

    protected JavaType.Method updateMethodToTargetInstantiatedType(JavaType.Method oldMethodType) {
        if (oldMethodType != null) {
            JavaType.Method method = oldMethodType;
            method = method.withDeclaringType((JavaType.FullyQualified) targetInstantiatedType)
                    .withReturnType(targetInstantiatedType);
            return method;
        }
        return null;
    }

    protected TypeTree updateTypeTreeToTargetInstantiatedType(J.NewClass newClass) {
        return ((J.Identifier) newClass.getClazz())
                .withSimpleName(((JavaType.ShallowClass) targetInstantiatedType).getClassName())
                .withType(targetInstantiatedType);
    }

    static class RemovedListTupla {

        private final String addMethodName;

        private final J.Identifier elementIdentifier;

        private final JavaType.Array elementArray;

        private final J.Identifier elementToArrayIdentifier;
        private final J.Identifier addMethodIdentifier;

        public RemovedListTupla(String addMethodName, JavaType elementJavaType) {
            this.addMethodName = addMethodName;
            elementIdentifier = new J.Identifier(Tree.randomId(), Space.build(" ", Collections.emptyList()), Markers.EMPTY, elementJavaType.toString(), elementJavaType, null);
            elementArray = new JavaType.Array(null, elementJavaType);
            JavaType.Parameterized elementListJavaType = new JavaType.Parameterized(null, (JavaType.FullyQualified) LIST_JAVA_TYPE, List.of(elementJavaType));
            elementToArrayIdentifier = new J.Identifier(Tree.randomId(), Space.EMPTY, Markers.EMPTY, "toArray", elementListJavaType, null);
            addMethodIdentifier = new J.Identifier(Tree.randomId(), Space.EMPTY, Markers.EMPTY, addMethodName, elementListJavaType, null);
        }

        public J.MethodInvocation getJMethod(J.NewClass newClass) {
            J.Identifier originalListIdentifier = (J.Identifier) newClass.getArguments().get(0);
            J.Literal literal = new J.Literal(Tree.randomId(), Space.EMPTY, Markers.EMPTY, 0, "0", null, JavaType.Primitive.Int);
            J.ArrayDimension arrayDimension = new J.ArrayDimension(Tree.randomId(), Space.EMPTY, Markers.EMPTY, JRightPadded.build(literal));
            J.NewArray newArray = new J.NewArray(Tree.randomId(), Space.EMPTY, Markers.EMPTY, elementIdentifier, Collections.singletonList(arrayDimension), null, elementArray);
            JavaType.Method methodType = new JavaType.Method(null, 1025, LIST_GENERIC_JAVA_TYPE, "toArray",
                    elementArray,
                    Collections.singletonList("arg0"),
                    Collections.singletonList(elementArray), null, null);
            J.MethodInvocation toArrayInvocation = new J.MethodInvocation(Tree.randomId(), Space.EMPTY, Markers.EMPTY, null, null,
                    elementToArrayIdentifier,
                    JContainer.build(Collections.emptyList()),
                    methodType)
                    .withSelect(originalListIdentifier)
                    .withArguments(Collections.singletonList(newArray));
            JavaType.Method constructorType = newClass.getConstructorType()
                    .withParameterTypes(Collections.emptyList())
                    .withParameterNames(Collections.emptyList());
            J.NewClass noArgClass = newClass.withArguments(Collections.emptyList())
                    .withConstructorType(constructorType);

            JavaType.Method addMethodInvocation = new JavaType.Method(null, 1025,
                    (JavaType.FullyQualified) JavaType.buildType(noArgClass.getType().toString()),
                    addMethodName,
                    JavaType.Primitive.Void,
                    Collections.singletonList("toAdd"),
                    Collections.singletonList(elementArray), null, null);

            return new J.MethodInvocation(Tree.randomId(), Space.EMPTY, Markers.EMPTY, null, null,
                    addMethodIdentifier,
                    JContainer.build(Collections.emptyList()),
                    addMethodInvocation)
                    .withSelect(noArgClass)
                    .withArguments(Collections.singletonList(toArrayInvocation));
        }
    }

    private static JavaParser getNewJPMMLJavaParser() {
        List<Path> paths = JavaParser.runtimeClasspath();
        Path newJpmmlModel = getNewJPMMLModelPath();
        paths.add(newJpmmlModel);
        return JavaParser.fromJavaVersion()
                .classpath(paths)
                .logCompilationWarningsAndErrors(true).build();
    }

    public static Path getNewJPMMLModelPath() {
        // The new version is expected to have been downloaded by maven plugin at validate phase
        File defaultTarget = new File(NEW_JPMML_MAVEN_PATH);
        if (!defaultTarget.exists()) {
            throw new RuntimeException("Failed to find " + NEW_JPMML_MAVEN_PATH);
        }
        return defaultTarget.toPath();
    }

}
