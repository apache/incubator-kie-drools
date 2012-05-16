package org.drools.rule;

import org.drools.Cheese;
import org.drools.common.InternalWorkingMemory;
import org.drools.rule.constraint.MvelConstraint;
import org.drools.spi.FieldValue;
import org.drools.spi.InternalReadAccessor;
import org.mvel2.MVEL;
import org.mvel2.ParserConfiguration;

public class MvelConstraintTestUtil extends MvelConstraint {

    static {
        MVEL.COMPILER_OPT_ALLOW_NAKED_METH_CALL = true;
        MVEL.COMPILER_OPT_ALLOW_OVERRIDE_ALL_PROPHANDLING = true;
        MVEL.COMPILER_OPT_ALLOW_RESOLVE_INNERCLASSES_WITH_DOTNOTATION = true;
        MVEL.COMPILER_OPT_SUPPORT_JAVA_STYLE_CLASS_LITERALS = true;
    }

    public MvelConstraintTestUtil(String expression, FieldValue fieldValue, InternalReadAccessor extractor) {
        super(null, expression, null, expression.contains("=="), fieldValue, extractor);
    }

    public MvelConstraintTestUtil(String expression, Declaration declaration, InternalReadAccessor extractor) {
        super(null, expression, new Declaration[] { declaration }, null, expression.contains("=="), declaration, extractor, expression.contains(":="));
    }

    @Override
    protected ParserConfiguration getParserConfiguration(InternalWorkingMemory workingMemory) {
        ParserConfiguration parserConfiguration = new ParserConfiguration();
        parserConfiguration.addImport(Cheese.class);
        return parserConfiguration;
    }
}
