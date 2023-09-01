package org.kie.pmml.compiler.commons.utils;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.PackageDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.expr.Name;
import org.junit.jupiter.api.Test;
import org.kie.pmml.api.exceptions.KiePMMLInternalException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;

public class JavaParserUtilsTest {

    private static final String TEMPLATE_FILE = "Template.tmpl";
    private static final String TEMPLATE_CLASS = "Template";
    private static final String NOT_PARSABLE_FILE = "Unparsable.tmpl";


    @Test
    void getFromFileName() {
        CompilationUnit retrieved = JavaParserUtils.getFromFileName(TEMPLATE_FILE);
        assertThat(retrieved).isNotNull();
    }

    @Test
    void getFromFileNameNotParsable() {
        assertThatExceptionOfType(KiePMMLInternalException.class).isThrownBy(() -> {
            JavaParserUtils.getFromFileName(NOT_PARSABLE_FILE);
        });
    }

    @Test
    void getFromFileNameNotExisting() {
        assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> {
            JavaParserUtils.getFromFileName("not_existing");
        });
    }

    @Test
    void getKiePMMLModelCompilationUnitWithPackage() {
        String className = "ClassName";
        String packageName = "apackage";
        CompilationUnit retrieved = JavaParserUtils.getKiePMMLModelCompilationUnit(className, packageName,  TEMPLATE_FILE, TEMPLATE_CLASS);
        assertThat(retrieved).isNotNull();
        assertThat(retrieved.getPackageDeclaration()).isPresent();
        assertThat(retrieved.getPackageDeclaration().get().getName().asString()).isEqualTo(packageName);
        assertThat(retrieved.getClassByName(TEMPLATE_CLASS)).isNotPresent();
        assertThat(retrieved.getClassByName(className)).isPresent();
    }

    @Test
    void getKiePMMLModelCompilationUnitWithoutPackage() {
        String className = "ClassName";
        CompilationUnit retrieved = JavaParserUtils.getKiePMMLModelCompilationUnit(className, null,  TEMPLATE_FILE, TEMPLATE_CLASS);
        assertThat(retrieved).isNotNull();
        assertThat(retrieved.getPackageDeclaration()).isNotPresent();
        assertThat(retrieved.getClassByName(TEMPLATE_CLASS)).isNotPresent();
        assertThat(retrieved.getClassByName(className)).isPresent();
    }

    @Test
    void getFullClassName() {
        String className = "ClassName";
        String packageName = "apackage";
        PackageDeclaration packageDeclaration = new PackageDeclaration();
        packageDeclaration.setName(new Name(packageName));
        ClassOrInterfaceDeclaration classOrInterfaceDeclaration = new ClassOrInterfaceDeclaration();
        classOrInterfaceDeclaration.setName(className);
        CompilationUnit compilationUnit = new CompilationUnit();
        compilationUnit.setPackageDeclaration(packageDeclaration);
        compilationUnit.setTypes(NodeList.nodeList(classOrInterfaceDeclaration));
        String retrieved = JavaParserUtils.getFullClassName(compilationUnit);
        String expected = packageName + "." + className;
        assertThat(retrieved).isEqualTo(expected);

    }
}