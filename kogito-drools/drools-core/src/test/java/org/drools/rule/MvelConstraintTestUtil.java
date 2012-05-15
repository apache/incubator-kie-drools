package org.drools.rule;

import org.drools.Cheese;
import org.drools.base.mvel.MVELCompilationUnit;
import org.drools.common.InternalWorkingMemory;
import org.drools.rule.constraint.MvelConstraint;
import org.drools.spi.FieldValue;
import org.drools.spi.InternalReadAccessor;
import org.mvel2.ParserConfiguration;

public class MvelConstraintTestUtil extends MvelConstraint {

    public MvelConstraintTestUtil(String expression, FieldValue fieldValue, InternalReadAccessor extractor) {
        super(null, expression, null, expression.contains("=="), fieldValue, extractor);
    }

    @Override
    protected ParserConfiguration getParserConfiguration(InternalWorkingMemory workingMemory) {
        ParserConfiguration parserConfiguration = new ParserConfiguration();
        parserConfiguration.addImport(Cheese.class);
        return parserConfiguration;
    }
}
