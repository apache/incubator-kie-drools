package org.drools.compiler.rule.builder;

import org.drools.compiler.lang.descr.AnnotationDescr;
import org.drools.core.base.ClassObjectType;
import org.drools.core.base.extractors.ArrayElementReader;
import org.drools.compiler.compiler.DescrBuildError;
import org.drools.compiler.lang.descr.QueryDescr;
import org.drools.core.beliefsystem.abductive.Abductive;
import org.drools.core.rule.AbductiveQuery;
import org.drools.core.rule.Declaration;
import org.drools.core.rule.Pattern;
import org.drools.core.rule.QueryImpl;
import org.drools.core.rule.constraint.QueryNameConstraint;
import org.drools.core.spi.InternalReadAccessor;
import org.drools.core.spi.ObjectType;

import java.util.Arrays;


public class QueryBuilder implements EngineElementBuilder {
    public Pattern build(final RuleBuildContext context,
                         final QueryDescr queryDescr) {
        ObjectType queryObjectType = ClassObjectType.DroolsQuery_ObjectType;
        final Pattern pattern = new Pattern( context.getNextPatternId(),
                                             0, // offset is 0 by default
                                             queryObjectType,
                                             null );
        
        final InternalReadAccessor extractor = PatternBuilder.getFieldReadAccessor(context, queryDescr, queryObjectType, "name", null, true);
        final QueryNameConstraint constraint = new QueryNameConstraint(extractor, queryDescr.getName());

        PatternBuilder.registerReadAccessor( context, queryObjectType, "name", constraint );

        // adds appropriate constraint to the pattern
        pattern.addConstraint( constraint );

        ObjectType argsObjectType = ClassObjectType.DroolsQuery_ObjectType;
        
        InternalReadAccessor arrayExtractor = PatternBuilder.getFieldReadAccessor( context, queryDescr, argsObjectType, "elements", null, true );

        QueryImpl query = ((QueryImpl) context.getRule());

        String[] params;
        String[] types;
        int numParams = queryDescr.getParameters().length;
        if ( query.isAbductive() ) {
            params = Arrays.copyOf( queryDescr.getParameters(), queryDescr.getParameters().length + 1 );
            types = Arrays.copyOf( queryDescr.getParameterTypes(), queryDescr.getParameterTypes().length + 1 );
        } else {
            params = queryDescr.getParameters();
            types = queryDescr.getParameterTypes();
        }

        Declaration[] declarations = new Declaration[ params.length ];

        if ( query.isAbductive() ) {
            AnnotationDescr ann = queryDescr.getAnnotation( Abductive.class );
            String returnName = ann.getValueAsString( "target" );
            try {
                Class<?> returnKlass = context.getPkg().getTypeResolver().resolveType( returnName.replace( ".class", "" ) );
                ClassObjectType objectType = new ClassObjectType( returnKlass, false );
                objectType = context.getPkg().getClassFieldAccessorStore().getClassObjectType( objectType,
                                                                                               (AbductiveQuery) query );
                params[ numParams ] = "";
                types[ numParams ] = returnKlass.getName();

                ((AbductiveQuery) query).setReturnType( objectType, params );

            } catch ( ClassNotFoundException e ) {
                context.addError( new DescrBuildError( context.getParentDescr(),
                                                       queryDescr,
                                                       e,
                                                       "Unable to resolve abducible type : " + returnName ) );
            } catch ( NoSuchMethodException e ) {
                context.addError( new DescrBuildError( context.getParentDescr(),
                                                       queryDescr,
                                                       e,
                                                       "Unable to resolve abducible constructor for type : " + returnName +
                                                       " with types " + Arrays.toString( types ) ) );

            }
        }

        int i = 0;
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

            query.setParameters( declarations );

        } catch ( ClassNotFoundException e ) {
            context.addError( new DescrBuildError( context.getParentDescr(),
                                                          queryDescr,
                                                          e,
                                                          "Unable to resolve type '" + types[i] + " for parameter" + params[i] ) );
        }
        context.setPrefixPattern( pattern );



        return pattern;
    }
}
