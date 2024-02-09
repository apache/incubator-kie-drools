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

import org.drools.base.base.ClassObjectType;
import org.drools.base.base.ObjectType;
import org.drools.base.base.extractors.ArrayElementReader;
import org.drools.base.definitions.rule.impl.QueryImpl;
import org.drools.base.rule.Declaration;
import org.drools.base.rule.Pattern;
import org.drools.base.rule.accessor.ReadAccessor;
import org.drools.base.rule.constraint.QueryNameConstraint;
import org.drools.compiler.compiler.DescrBuildError;
import org.drools.drl.ast.descr.QueryDescr;


public class PatternBuilderForQuery implements EngineElementBuilder {
    public void build(RuleBuildContext context, QueryDescr queryDescr) {
        ObjectType queryObjectType = ClassObjectType.DroolsQuery_ObjectType;
        final Pattern pattern = new Pattern( context.getNextPatternId(),
                                             0, // tupleIndex is 0 by default
                                             0, // patternIndex is 0 by default
                                             queryObjectType,
                                             null );
        
        final ReadAccessor extractor = PatternBuilder.getFieldReadAccessor(context, queryDescr, pattern, "name", null, true);
        final QueryNameConstraint constraint = new QueryNameConstraint(extractor, queryDescr.getName());

        PatternBuilder.registerReadAccessor( context, queryObjectType, "name", constraint );

        // adds appropriate constraint to the pattern
        pattern.addConstraint( constraint );

        ObjectType argsObjectType = ClassObjectType.DroolsQuery_ObjectType;

        ReadAccessor arrayExtractor = PatternBuilder.getFieldReadAccessor( context, queryDescr, null, argsObjectType, "elements", null, true );

        QueryImpl query = ((QueryImpl) context.getRule());

        String[] params = getQueryParams(queryDescr);
        String[] types = getQueryTypes(queryDescr, query);

        Declaration[] declarations = new Declaration[ params.length ];

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

        postBuild(context, queryDescr, query, params, types, declarations);
    }

    protected void postBuild(RuleBuildContext context, QueryDescr queryDescr, QueryImpl query, String[] params, String[] types, Declaration[] declarations) {
    }

    protected String[] getQueryParams(QueryDescr queryDescr) {
        return queryDescr.getParameters();
    }

    protected String[] getQueryTypes(QueryDescr queryDescr, QueryImpl query) {
        return queryDescr.getParameterTypes();
    }
}
