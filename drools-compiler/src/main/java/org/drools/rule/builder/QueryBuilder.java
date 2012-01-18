package org.drools.rule.builder;

import org.drools.base.ArrayElements;
import org.drools.base.ClassObjectType;
import org.drools.base.DroolsQuery;
import org.drools.base.FieldFactory;
import org.drools.base.ValueType;
import org.drools.base.evaluators.Operator;
import org.drools.base.extractors.ArrayElementReader;
import org.drools.compiler.DescrBuildError;
import org.drools.lang.descr.QueryDescr;
import org.drools.rule.Declaration;
import org.drools.rule.LiteralConstraint;
import org.drools.rule.Pattern;
import org.drools.rule.Query;
import org.drools.spi.FieldValue;
import org.drools.spi.InternalReadAccessor;
import org.drools.spi.ObjectType;

public class QueryBuilder implements EngineElementBuilder {
    public Pattern build(final RuleBuildContext context,
                         final QueryDescr queryDescr) {
        ObjectType queryObjectType = ClassObjectType.DroolsQuery_ObjectType;
        final Pattern pattern = new Pattern( context.getNextPatternId(),
                                             0, // offset is 0 by default
                                             queryObjectType,
                                             null );
        
        final InternalReadAccessor extractor = PatternBuilder.getFieldReadAccessor( context, queryDescr, queryObjectType, "name", null, true );

        final FieldValue field = FieldFactory.getFieldValue( queryDescr.getName(),
                                                             ValueType.STRING_TYPE,
                                                             context.getPackageBuilder().getDateFormats() );

        final LiteralConstraint constraint = new LiteralConstraint( extractor,
                                                                    context.getConfiguration().getEvaluatorRegistry().getEvaluator( ValueType.STRING_TYPE,
                                                                                                                                    Operator.EQUAL ),
                                                                    field );
        
        PatternBuilder.registerReadAccessor( context, queryObjectType, "name", constraint );

        // adds appropriate constraint to the pattern
        pattern.addConstraint( constraint );

        ObjectType argsObjectType = ClassObjectType.DroolsQuery_ObjectType;
        
        InternalReadAccessor arrayExtractor = PatternBuilder.getFieldReadAccessor( context, queryDescr, argsObjectType, "elements", null, true );

        String[] params = queryDescr.getParameters();
        String[] types = queryDescr.getParameterTypes();
        int i = 0;
        
        Declaration[] declarations = new Declaration[ params.length ];
        
        try {
            for ( i = 0; i < params.length; i++ ) {
                Declaration declr = pattern.addDeclaration( params[i] );
                
                // this bit is different, notice its the ArrayElementReader that we wire up to, not the declaration.
                ArrayElementReader reader = new ArrayElementReader( arrayExtractor,
                                                                    i,
                                                                    context.getDialect().getTypeResolver().resolveType( types[i] ) );
                PatternBuilder.registerReadAccessor( context, argsObjectType, "elements", reader );
                
                declr.setReadAccessor( reader );
                
                declarations[i] = declr;
             }
            
            ((Query)context.getRule()).setParameters( declarations );
            
        } catch ( ClassNotFoundException e ) {
            context.addError( new DescrBuildError( context.getParentDescr(),
                                                          queryDescr,
                                                          e,
                                                          "Unable to resolve type '" + types[i] + " for parameter" + params[i] ) );
        }
        return pattern;
    }
}
