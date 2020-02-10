package org.drools.modelcompiler.builder.generator;

import java.util.Collections;

import org.drools.core.addon.ClassTypeResolver;
import org.drools.core.addon.TypeResolver;
import org.drools.modelcompiler.builder.PackageModel;
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

    private RuleContext createContext() {
        TypeResolver typeResolver = new ClassTypeResolver(Collections.emptySet(), this.getClass().getClassLoader());
        PackageModel packageModel = new PackageModel("", "", null, true, null, null);
        return new RuleContext(null, packageModel, typeResolver, true);
    }
}