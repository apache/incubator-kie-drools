package org.drools.rule.builder.dialect.java;

import java.util.Map;

import org.drools.compiler.AnalysisResult;
import org.drools.compiler.BoundIdentifiers;
import org.drools.lang.descr.ReturnValueRestrictionDescr;
import org.drools.rule.Declaration;
import org.drools.rule.ReturnValueRestriction;
import org.drools.rule.builder.ReturnValueBuilder;
import org.drools.rule.builder.RuleBuildContext;
import static org.drools.rule.builder.dialect.java.JavaRuleBuilderHelper.*;

public class JavaReturnValueBuilder
    implements
    ReturnValueBuilder {
    public void build(final RuleBuildContext context,
                      final BoundIdentifiers usedIdentifiers,
                      final Declaration[] previousDeclarations,
                      final Declaration[] localDeclarations,
                      final ReturnValueRestriction returnValueRestriction,
                      final ReturnValueRestrictionDescr returnValueRestrictionDescr,
                      final AnalysisResult analysis) {
        final String className = "returnValue" + context.getNextId();
        returnValueRestrictionDescr.setClassMethodName( className );

        final Map map = createVariableContext( className,
                                               (String) returnValueRestrictionDescr.getContent(),
                                               context,
                                               previousDeclarations,
                                               localDeclarations,
                                               usedIdentifiers.getGlobals()
        );

        map.put( "readLocalsFromTuple", Boolean.FALSE );

        generateTemplates("returnValueMethod",
                "returnValueInvoker",
                context,
                className,
                map,
                returnValueRestriction,
                returnValueRestrictionDescr);
    }
}
