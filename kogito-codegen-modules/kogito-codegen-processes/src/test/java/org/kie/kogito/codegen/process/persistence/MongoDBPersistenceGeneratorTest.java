/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.kogito.codegen.process.persistence;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.kie.kogito.codegen.api.AddonsConfig;
import org.kie.kogito.codegen.api.GeneratedFile;
import org.kie.kogito.codegen.api.GeneratedFileType;
import org.kie.kogito.codegen.api.context.KogitoBuildContext;
import org.kie.kogito.codegen.api.context.impl.QuarkusKogitoBuildContext;
import org.kie.kogito.codegen.data.Person;
import org.kie.kogito.codegen.process.persistence.proto.ReflectionProtoGenerator;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.Name;
import com.github.javaparser.ast.expr.NormalAnnotationExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ReturnStmt;

import static com.github.javaparser.StaticJavaParser.parse;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.kie.kogito.codegen.process.persistence.PersistenceGenerator.MONGODB_PERSISTENCE_TYPE;

class MongoDBPersistenceGeneratorTest {

    private static final String TEST_RESOURCES = "src/test/resources";

    private static final String PERSISTENCE_FILE_PATH = "org/kie/kogito/persistence/KogitoProcessInstancesFactoryImpl.java";
    private static final String TRANSACTION_FILE_PATH = "org/kie/kogito/mongodb/transaction/MongoDBTransactionManagerImpl.java";

    KogitoBuildContext context = QuarkusKogitoBuildContext.builder()
            .withApplicationProperties(new File(TEST_RESOURCES))
            .withPackageName(this.getClass().getPackage().getName())
            .withAddonsConfig(AddonsConfig.builder().withPersistence(true).build())
            .build();

    @Test
    void test() {
        context.setApplicationProperty("kogito.persistence.type", MONGODB_PERSISTENCE_TYPE);

        ReflectionProtoGenerator protoGenerator = ReflectionProtoGenerator.builder().build(Collections.singleton(Person.class));
        PersistenceGenerator persistenceGenerator = new PersistenceGenerator(
                context,
                protoGenerator);
        Collection<GeneratedFile> generatedFiles = persistenceGenerator.generate();

        Optional<GeneratedFile> generatedCLASSFile = generatedFiles.stream().filter(gf -> gf.category() == GeneratedFileType.SOURCE.category())
                .filter(f -> PERSISTENCE_FILE_PATH.equals(f.relativePath())).findAny();
        assertTrue(generatedCLASSFile.isPresent());
        GeneratedFile classFile = generatedCLASSFile.get();
        assertEquals(PERSISTENCE_FILE_PATH, classFile.relativePath());

        final CompilationUnit compilationUnit = parse(new ByteArrayInputStream(classFile.contents()));

        final ClassOrInterfaceDeclaration classDeclaration =
                compilationUnit.findFirst(ClassOrInterfaceDeclaration.class).orElseThrow(() -> new NoSuchElementException("Compilation unit doesn't contain a class or interface declaration!"));

        assertNotNull(classDeclaration);

        final MethodDeclaration methodDeclaration = classDeclaration.findFirst(MethodDeclaration.class, d -> d.getName().getIdentifier().equals("dbName"))
                .orElseThrow(() -> new NoSuchElementException("Class declaration doesn't contain a method named \"dbName\"!"));
        assertNotNull(methodDeclaration);
        assertTrue(methodDeclaration.getBody().isPresent());

        final BlockStmt body = methodDeclaration.getBody().get();
        assertThat(body.getStatements().size()).isOne();
        assertTrue(body.getStatements().get(0).isReturnStmt());

        final ReturnStmt returnStmt = (ReturnStmt) body.getStatements().get(0);
        assertThat(returnStmt.toString()).contains("kogito");

        final MethodDeclaration transactionMethodDeclaration = classDeclaration.findFirst(MethodDeclaration.class, d -> "transactionManager".equals(d.getName().getIdentifier()))
                .orElseThrow(() -> new NoSuchElementException("Class declaration doesn't contain a method named \"transactionManager\"!"));
        assertNotNull(transactionMethodDeclaration);
        assertTrue(transactionMethodDeclaration.getBody().isPresent());

        final BlockStmt transactionMethodBody = transactionMethodDeclaration.getBody().get();
        assertThat(transactionMethodBody.getStatements().size()).isOne();
        assertTrue(transactionMethodBody.getStatements().get(0).isReturnStmt());

        final ReturnStmt transactionReturnStmt = (ReturnStmt) transactionMethodBody.getStatements().get(0);
        assertThat(transactionReturnStmt.toString()).contains("transactionManager");

        Optional<GeneratedFile> generatedTransactionCLASSFile = generatedFiles.stream().filter(gf -> gf.category() == GeneratedFileType.SOURCE.category())
                .filter(f -> TRANSACTION_FILE_PATH.equals(f.relativePath())).findAny();
        assertTrue(generatedTransactionCLASSFile.isPresent());

        final CompilationUnit transactionCompilationUnit = parse(new ByteArrayInputStream(generatedTransactionCLASSFile.get().contents()));
        final ClassOrInterfaceDeclaration transactionClassDeclaration = transactionCompilationUnit.findFirst(ClassOrInterfaceDeclaration.class)
                .orElseThrow(() -> new NoSuchElementException("Compilation unit doesn't contain a class or interface declaration!"));
        assertNotNull(transactionClassDeclaration);

        final List<ConstructorDeclaration> constructorDeclarations = transactionCompilationUnit.findAll(ConstructorDeclaration.class);
        assertEquals(2, constructorDeclarations.size());

        Optional<ConstructorDeclaration> annotatedConstructorDeclaration = constructorDeclarations.stream()
                .filter(c -> c.isAnnotationPresent("Inject")).findAny();
        assertTrue(annotatedConstructorDeclaration.isPresent());

        final MethodDeclaration transactionEnabledMethodDeclaration = transactionClassDeclaration.findFirst(MethodDeclaration.class, d -> d.getName().getIdentifier().equals("enabled"))
                .orElseThrow(() -> new NoSuchElementException("Class declaration doesn't contain a method named \"enabled\"!"));
        assertNotNull(transactionEnabledMethodDeclaration);
        assertTrue(transactionEnabledMethodDeclaration.getBody().isPresent());

        final BlockStmt enabledMethodBody = transactionEnabledMethodDeclaration.getBody().get();
        assertThat(enabledMethodBody.getStatements().size()).isOne();
        assertTrue(enabledMethodBody.getStatements().get(0).isReturnStmt());

        final ReturnStmt enabledReturnStmt = (ReturnStmt) enabledMethodBody.getStatements().get(0);
        assertThat(enabledReturnStmt.toString()).contains("enabled");

        final FieldDeclaration transactionEnabledFieldDeclaration = transactionClassDeclaration.findFirst(FieldDeclaration.class, f -> f.getVariable(0).getName().getIdentifier().equals("enabled"))
                .orElseThrow(() -> new NoSuchElementException("Class declaration doesn't contain a field named \"enabled\"!"));
        assertNotNull(transactionEnabledFieldDeclaration);
        final NormalAnnotationExpr transactionEnabledAnnotationDeclaration =
                transactionEnabledFieldDeclaration.findFirst(NormalAnnotationExpr.class, a -> ((Name) a.getChildNodes().get(0)).getIdentifier().equals("ConfigProperty"))
                        .orElseThrow(() -> new NoSuchElementException("Field declaration doesn't contain an annotation  named \"ConfigProperty\"!"));
        assertNotNull(transactionEnabledAnnotationDeclaration);
        assertEquals("kogito.persistence.transaction.enabled", ((StringLiteralExpr) transactionEnabledAnnotationDeclaration.getChildNodes().get(1).getChildNodes().get(1)).getValue());
    }
}
