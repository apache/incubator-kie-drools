package org.drools.model.codegen.execmodel.util.lambdareplace;

import com.github.javaparser.ast.body.MethodDeclaration;

import static org.assertj.core.api.Assertions.assertThat;

/* The diff produced while using equalToIgnoringWhiteSpace is abysmal but is correct, while the one
 * produced by JavaParser's equals is better but it fails also on identical ASTs.
 * By using this method to verify produced classes we've got the best of two worlds
 */
public class MaterializedLambdaTestUtils {


    public static void verifyCreatedClass(CreatedClass aClass, String expectedResult) {
        assertThat(aClass.getContents()).isEqualToIgnoringWhitespace(expectedResult);
    }

    public static void verifyCreatedClass(MethodDeclaration expected, MethodDeclaration actual) {
        try {
        	assertThat(actual).asString().isEqualToIgnoringWhitespace(expected.toString());
        } catch (AssertionError e) {
        	assertThat(actual).isEqualTo(expected);
        }
    }
}
