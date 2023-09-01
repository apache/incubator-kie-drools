package org.drools.compiler.rule.builder;

import java.util.Arrays;

import org.drools.compiler.compiler.DescrBuildError;
import org.drools.base.base.ClassObjectType;
import org.drools.base.rule.Declaration;
import org.drools.base.definitions.rule.impl.QueryImpl;
import org.drools.base.base.AcceptsClassObjectType;
import org.drools.base.base.ObjectType;
import org.drools.drl.ast.descr.AnnotationDescr;
import org.drools.drl.ast.descr.QueryDescr;

import static org.drools.compiler.rule.builder.util.AnnotationFactory.getTypedAnnotation;

public class PatternBuilderForAbductiveQuery extends PatternBuilderForQuery {

    @Override
    protected void postBuild(RuleBuildContext context, QueryDescr queryDescr, QueryImpl query, String[] params, String[] types, Declaration[] declarations) {
        int numParams = queryDescr.getParameters().length;
        String returnName = "";
        try {
            AnnotationDescr ann = queryDescr.getAnnotation( query.getAbductiveAnnotationClass() );
            Object[] argsVal = ((Object[]) ann.getValue( "args" ));
            String[] args = argsVal != null ? Arrays.copyOf( argsVal, argsVal.length, String[].class ) : null;

            returnName = types[ numParams ];
            Class<?> abductionReturnKlass = query.getAbductionClass(annotationClass -> getTypedAnnotation(queryDescr, annotationClass ));
            ObjectType objectType = context.getPkg().wireObjectType( new ClassObjectType( abductionReturnKlass, false ), (AcceptsClassObjectType) query);

            query.setReturnType( objectType, params, args, declarations);
        } catch ( NoSuchMethodException e ) {
            context.addError( new DescrBuildError( context.getParentDescr(),
                    queryDescr,
                    e,
                    "Unable to resolve abducible constructor for type : " + returnName +
                            " with types " + Arrays.toString(types) ) );

        } catch ( IllegalArgumentException e ) {
            context.addError( new DescrBuildError( context.getParentDescr(), queryDescr, e, e.getMessage() ) );
        }
    }

    @Override
    protected String[] getQueryParams(QueryDescr queryDescr) {
        String[] params = Arrays.copyOf( queryDescr.getParameters(), queryDescr.getParameters().length + 1 );
        params[ params.length-1 ] = "";
        return params;
    }

    @Override
    protected String[] getQueryTypes(QueryDescr queryDescr, QueryImpl query) {
        String[] types = Arrays.copyOf( queryDescr.getParameterTypes(), queryDescr.getParameterTypes().length + 1 );
        Class<?> abductionReturnKlass = query.getAbductionClass(annotationClass -> getTypedAnnotation(queryDescr, annotationClass ));
        types[types.length-1 ] = abductionReturnKlass.getName();
        return types;
    }
}
