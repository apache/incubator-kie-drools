package org.drools.rule.builder.dialect.java;

import java.util.List;
import java.util.Map;

import org.drools.lang.descr.ReturnValueRestrictionDescr;
import org.drools.rule.Declaration;
import org.drools.rule.ReturnValueRestriction;
import org.drools.rule.builder.ReturnValueBuilder;
import org.drools.rule.builder.RuleBuildContext;

public class JavaReturnValueBuilder extends AbstractJavaBuilder
    implements
    ReturnValueBuilder {
    public void build(final RuleBuildContext context,
                      final List[] usedIdentifiers,
                      final Declaration[] previousDeclarations,
                      final Declaration[] localDeclarations,
                      final ReturnValueRestriction returnValueRestriction,
                      final ReturnValueRestrictionDescr returnValueRestrictionDescr) {
        final String className = "returnValue" + context.getNextId();
        returnValueRestrictionDescr.setClassMethodName( className );

        final Map map = createVariableContext( className,
                                         (String) returnValueRestrictionDescr.getContent(),
                                         context,
                                         previousDeclarations,
                                         localDeclarations,
                                         (String[]) usedIdentifiers[1].toArray( new String[usedIdentifiers[1].size()] ) );

        generatTemplates( "returnValueMethod",
                          "returnValueInvoker",
                          context,
                          className,
                          map,
                          returnValueRestriction,
                          returnValueRestrictionDescr );
    }
}
