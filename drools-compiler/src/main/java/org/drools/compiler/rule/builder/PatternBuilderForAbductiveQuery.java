/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.drools.compiler.rule.builder;

import java.util.Arrays;

import org.drools.base.base.AcceptsClassObjectType;
import org.drools.base.base.ClassObjectType;
import org.drools.base.base.ObjectType;
import org.drools.base.definitions.rule.impl.QueryImpl;
import org.drools.base.rule.Declaration;
import org.drools.compiler.compiler.DescrBuildError;
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
