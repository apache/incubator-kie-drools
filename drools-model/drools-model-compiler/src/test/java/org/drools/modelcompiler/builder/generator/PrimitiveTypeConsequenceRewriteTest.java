package org.drools.modelcompiler.builder.generator;

import java.util.Collections;

import org.drools.core.addon.ClassTypeResolver;
import org.drools.core.addon.TypeResolver;
import org.drools.modelcompiler.builder.PackageModel;
import org.drools.modelcompiler.domain.Address;
import org.junit.Test;

import static org.hamcrest.Matchers.equalToIgnoringWhiteSpace;
import static org.junit.Assert.*;

public class PrimitiveTypeConsequenceRewriteTest {

    @Test
    public void shouldConvertCastOfShortToShortValue() {
        RuleContext context = createContext();
        context.addDeclaration("$interimVar", int.class);

        String rewritten = new PrimitiveTypeConsequenceRewrite(context)
                .rewrite("{ $address.setShortNumber((short)$interimVar); }");

        assertThat(rewritten,
                   equalToIgnoringWhiteSpace("{ $address.setShortNumber($interimVar.shortValue()); }"));
    }

    @Test
    public void shouldConvertCastOfShortToShortValueEnclosed() {
        RuleContext context = createContext();
        context.addDeclaration("$interimVar", int.class);

        String rewritten = new PrimitiveTypeConsequenceRewrite(context)
                .rewrite("{ $address.setShortNumber((short)($interimVar)); }");

        assertThat(rewritten,
                   equalToIgnoringWhiteSpace("{ $address.setShortNumber($interimVar.shortValue()); }"));
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
                   equalToIgnoringWhiteSpace("{ $address.setShortNumber($interimVar.unboxed().shortValue()); }"));
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