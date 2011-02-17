package org.drools.rule.builder.dialect.java;

import java.util.List;
import java.util.Map;

import org.drools.compiler.AnalysisResult;
import org.drools.compiler.BoundIdentifiers;
import org.drools.lang.descr.ReturnValueRestrictionDescr;
import org.drools.rule.Declaration;
import org.drools.rule.ReturnValueRestriction;
import org.drools.rule.builder.ReturnValueBuilder;
import org.drools.rule.builder.RuleBuildContext;

public class JavaReturnValueBuilder extends AbstractJavaRuleBuilder
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
                                               usedIdentifiers.getGlobals(),
                                               null );

        map.put( "readLocalsFromTuple", Boolean.FALSE );

        generatTemplates( "returnValueMethod",
                          "returnValueInvoker",
                          context,
                          className,
                          map,
                          returnValueRestriction,
                          returnValueRestrictionDescr );
    }
}
