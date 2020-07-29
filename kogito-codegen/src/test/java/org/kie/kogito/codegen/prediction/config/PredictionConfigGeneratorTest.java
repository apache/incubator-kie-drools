package org.kie.kogito.codegen.prediction.config;

import java.util.List;
import java.util.Optional;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.kie.kogito.codegen.di.CDIDependencyInjectionAnnotator;
import org.kie.kogito.codegen.di.SpringDependencyInjectionAnnotator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PredictionConfigGeneratorTest {

    private final static String PACKAGE_NAME = "PACKAGENAME";
    private static PredictionConfigGenerator predictionConfigGenerator;

    @BeforeAll
    public static void setup() {
        predictionConfigGenerator = new PredictionConfigGenerator(PACKAGE_NAME);
        assertNotNull(predictionConfigGenerator);
    }

    @Test
    void compilationUnitWithCDI() {
        predictionConfigGenerator.withDependencyInjection(new CDIDependencyInjectionAnnotator());
        final Optional<CompilationUnit> retrievedOpt = predictionConfigGenerator.compilationUnit();
        assertTrue(retrievedOpt.isPresent());
        String retrieved = retrievedOpt.get().toString();
        String expected = "@javax.inject.Singleton";
        assertTrue(retrieved.contains(expected));
        expected = "@javax.inject.Inject";
        assertTrue(retrieved.contains(expected));
        String unexpected = "@org.springframework.stereotype.Component";
        assertFalse(retrieved.contains(unexpected));
        unexpected = "@org.springframework.beans.factory.annotation.Autowired";
        assertFalse(retrieved.contains(unexpected));
    }

    @Test
    void compilationUnitWithSpring() {
        predictionConfigGenerator.withDependencyInjection(new SpringDependencyInjectionAnnotator());
        final Optional<CompilationUnit> retrievedOpt = predictionConfigGenerator.compilationUnit();
        assertTrue(retrievedOpt.isPresent());
        String retrieved = retrievedOpt.get().toString();
        String expected = "@org.springframework.stereotype.Component";
        assertTrue(retrieved.contains(expected));
        expected = "@org.springframework.beans.factory.annotation.Autowired";
        assertTrue(retrieved.contains(expected));
        String unexpected = "@javax.inject.Singleton";
        assertFalse(retrieved.contains(unexpected));
        unexpected = "@javax.inject.Inject";
        assertFalse(retrieved.contains(unexpected));
    }

    @Test
    void newInstance() {
        ObjectCreationExpr retrieved = predictionConfigGenerator.newInstance();
        String expected = "new org.kie.kogito.pmml.config.StaticPredictionConfig()";
        assertEquals(expected, retrieved.toString());
    }

    @Test
    void members() {
        List<BodyDeclaration<?>> retrieved = predictionConfigGenerator.members();
        assertNotNull(retrieved);
        assertTrue(retrieved.isEmpty());
    }
}