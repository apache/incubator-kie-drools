package org.drools.rule.builder;

import org.drools.RuntimeDroolsException;
import org.drools.base.ClassObjectType;
import org.drools.base.DroolsQuery;
import org.drools.base.FieldFactory;
import org.drools.base.ValueType;
import org.drools.base.evaluators.Operator;
import org.drools.base.extractors.ArrayExtractor;
import org.drools.compiler.DescrBuildError;
import org.drools.lang.descr.QueryDescr;
import org.drools.rule.LiteralConstraint;
import org.drools.rule.Pattern;
import org.drools.spi.Extractor;
import org.drools.spi.FieldExtractor;
import org.drools.spi.FieldValue;
import org.drools.spi.ObjectType;

public class QueryBuilder {
    public Pattern build(final RuleBuildContext context,
                         final QueryDescr queryDescr) {
        ObjectType objectType = new ClassObjectType( DroolsQuery.class );
        final Pattern pattern = new Pattern( context.getNextPatternId(),
                                             0, // offset is 0 by default
                                             objectType,
                                             null );
        ClassLoader classloader = context.getPkg().getPackageCompilationData().getClassLoader();
        final FieldExtractor extractor = context.getDialect().getClassFieldExtractorCache().getExtractor( DroolsQuery.class,
                                                                                                          "name",
                                                                                                          classloader );

        final FieldValue field = FieldFactory.getFieldValue( queryDescr.getName(),
                                                             ValueType.STRING_TYPE );

        final LiteralConstraint constraint = new LiteralConstraint( extractor,
                                                                    context.getConfiguration().getEvaluatorRegistry().getEvaluator( ValueType.STRING_TYPE,
                                                                                                                                    Operator.EQUAL ),
                                                                    field );
        // adds appropriate constraint to the pattern
        pattern.addConstraint( constraint );

        Extractor arrayExtractor = null;
        try {
            arrayExtractor = context.getDialect().getClassFieldExtractorCache().getExtractor( ((ClassObjectType) objectType).getClassType(),
                                                                                              "arguments",
                                                                                              classloader );
        } catch ( final RuntimeDroolsException e ) {
            context.getErrors().add( new DescrBuildError( context.getParentDescr(),
                                                          queryDescr,
                                                          e,
                                                          "Unable to create Field Extractor for 'getArguments'" ) );
        }

        String[] params = queryDescr.getParameters();
        String[] types = queryDescr.getParameterTypes();
        int i = 0;
        try {
            for ( i = 0; i < params.length; i++ ) {
                pattern.addDeclaration( params[i],
                                        new ArrayExtractor( arrayExtractor,
                                                            i,
                                                            context.getDialect().getTypeResolver().resolveType( types[i] ) ) );
            }
        } catch ( ClassNotFoundException e ) {
            context.getErrors().add( new DescrBuildError( context.getParentDescr(),
                                                          queryDescr,
                                                          e,
                                                          "Unable to resolve type '" + types[i] + " for parameter" + params[i] ) );
        }
        return pattern;
    }
}
