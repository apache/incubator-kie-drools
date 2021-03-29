package org.drools.modelcompiler.builder.generator;

import java.util.Collections;

import org.drools.core.addon.ClassTypeResolver;
import org.drools.core.addon.TypeResolver;
import org.drools.modelcompiler.builder.PackageModel;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalToIgnoringWhiteSpace;

public class PrimitiveTypeConsequenceRewriteTest {

    @Test
    public void shouldConvertCastOfShortToIntegerToShortValue() {
        RuleContext context = createContext();
        context.addDeclaration("$interimVar", int.class);

        String rewritten = new PrimitiveTypeConsequenceRewrite(context)
                .rewrite("{ $address.setShortNumber((short)$interimVar); }");

        assertThat(rewritten,
                   equalToIgnoringWhiteSpace("{ $address.setShortNumber(java.lang.Integer.valueOf($interimVar).shortValue()); }"));
    }

    @Test
    public void shouldConvertCastOfShortNegativeValueToIntegerToShortValue() {
        RuleContext context = createContext();

        String rewritten = new PrimitiveTypeConsequenceRewrite(context)
                .rewrite("{ $address.setShortNumber((short)-2); }");

        assertThat(rewritten,
                   equalToIgnoringWhiteSpace("{ $address.setShortNumber(java.lang.Integer.valueOf(-2).shortValue()); }"));
    }

    @Test
    public void doNotConverLiterals() {
        RuleContext context = createContext();

        String rewritten = new PrimitiveTypeConsequenceRewrite(context)
                .rewrite("{ $address.setShortNumber((short) (12)); }");

        assertThat(rewritten,
                   equalToIgnoringWhiteSpace("{ $address.setShortNumber((short) (12)); }"));
    }

    @Test
    public void doNotConvertBinaryExpr() {
        RuleContext context = createContext();
        context.addDeclaration("$testDouble", Double.class);

        String rewritten = new PrimitiveTypeConsequenceRewrite(context)
                .rewrite("{    $integerToShort.setTestShort((short)((16 + $testDouble))); \n }");

        assertThat(rewritten,
                   equalToIgnoringWhiteSpace("{    $integerToShort.setTestShort((short) ((16 + $testDouble))); \n }"));
    }

    @Test
    public void shouldConvertCastOfShortToShortValueEnclosed() {
        RuleContext context = createContext();
        context.addDeclaration("$interimVar", int.class);

        String rewritten = new PrimitiveTypeConsequenceRewrite(context)
                .rewrite("{ $address.setShortNumber((short)($interimVar)); }");

        assertThat(rewritten,
                   equalToIgnoringWhiteSpace("{ $address.setShortNumber(java.lang.Integer.valueOf($interimVar).shortValue()); }"));
    }

    public static class WithIntegerField {
        private Integer integerField;

        public WithIntegerField(Integer integerField) {
            this.integerField = integerField;
        }

        public Integer boxed() {
            return integerField;
        }
        public int unboxed() {
            return integerField.intValue();
        }
    }

    @Test
    public void shouldConvertCastOfShortToShortValueEnclosedWithField() {
        RuleContext context = createContext();
        context.addDeclaration("$interimVar", WithIntegerField.class);

        String rewritten = new PrimitiveTypeConsequenceRewrite(context)
                .rewrite("{ $address.setShortNumber((short)($interimVar.unboxed())); }");


        assertThat(rewritten,
                   equalToIgnoringWhiteSpace("{ $address.setShortNumber(java.lang.Integer.valueOf($interimVar.unboxed()).shortValue()); }"));
    }


    @Test
    public void doNotPreProcessDowncastToNonPrimitiveTypes() {
        RuleContext context = createContext();
        context.addDeclaration("$interimVar", WithIntegerField.class);

        String rewritten = new PrimitiveTypeConsequenceRewrite(context)
                .rewrite("{\n" +
                                 "  org.kie.dmn.model.api.List row = (org.kie.dmn.model.api.List) $e.getParent();\n" +
                                 "}");

        assertThat(rewritten,
                   equalToIgnoringWhiteSpace("{ org.kie.dmn.model.api.List row = (org.kie.dmn.model.api.List) $e.getParent(); }"));
    }

    private RuleContext createContext() {
        TypeResolver typeResolver = new ClassTypeResolver(Collections.emptySet(), this.getClass().getClassLoader());
        PackageModel packageModel = new PackageModel("", "", null, true, null, null);
        return new RuleContext(null, packageModel, typeResolver, true);
    }
}