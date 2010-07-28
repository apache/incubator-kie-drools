/**
 * Copyright 2010 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.rule.builder.dialect.clips;

import java.util.HashMap;
import java.util.Map;

import org.drools.clips.Appendable;
import org.drools.clips.FunctionHandlers;
import org.drools.clips.LispForm;
import org.drools.clips.StringBuilderAppendable;
import org.drools.compiler.Dialect;
import org.drools.compiler.PackageBuilder;
import org.drools.compiler.PackageRegistry;
import org.drools.lang.descr.AccumulateDescr;
import org.drools.lang.descr.AndDescr;
import org.drools.lang.descr.BaseDescr;
import org.drools.lang.descr.CollectDescr;
import org.drools.lang.descr.EntryPointDescr;
import org.drools.lang.descr.EvalDescr;
import org.drools.lang.descr.ExistsDescr;
import org.drools.lang.descr.ForallDescr;
import org.drools.lang.descr.FromDescr;
import org.drools.lang.descr.FunctionDescr;
import org.drools.lang.descr.NotDescr;
import org.drools.lang.descr.OrDescr;
import org.drools.lang.descr.PatternDescr;
import org.drools.lang.descr.PredicateDescr;
import org.drools.lang.descr.QueryDescr;
import org.drools.lang.descr.ReturnValueRestrictionDescr;
import org.drools.rule.Package;
import org.drools.rule.builder.ConsequenceBuilder;
import org.drools.rule.builder.PackageBuildContext;
import org.drools.rule.builder.PredicateBuilder;
import org.drools.rule.builder.ReturnValueBuilder;
import org.drools.rule.builder.RuleConditionBuilder;
import org.drools.rule.builder.dialect.mvel.MVELDialect;

public class ClipsDialect extends MVELDialect {

    private static final ClipsConsequenceBuilder CONSEQUENCE_BUILDER  = new ClipsConsequenceBuilder();
    private static final ClipsEvalBuilder        EVAL_BUILDER         = new ClipsEvalBuilder();
    private static final ClipsReturnValueBuilder RETURN_VALUE_BUILDER = new ClipsReturnValueBuilder();
    private static final ClipsPredicateBuilder   PREDICATE_BUILDER    = new ClipsPredicateBuilder();
    
    // a map of registered builders
    private static Map                           builders;
    static {
        initBuilder();
    }    

    public final static String                   ID                   = "clips";

    public ClipsDialect(PackageBuilder builder,
                        PackageRegistry pkgRegistry,
                        Package pkg) {
        super( builder,
               pkgRegistry,
               pkg,
               ID );
        setLanguageLevel( 5 );
    }
    
    public static void initBuilder() {
        if ( builders != null ) {
            return;
        }

        // statically adding all builders to the map
        // but in the future we can move that to a configuration
        // if we want to
        builders = new HashMap();

        builders.put( AndDescr.class,
                      GE_BUILDER );

        builders.put( OrDescr.class,
                      GE_BUILDER );

        builders.put( NotDescr.class,
                      GE_BUILDER );

        builders.put( ExistsDescr.class,
                      GE_BUILDER );

        builders.put( PatternDescr.class,
                      PATTERN_BUILDER );

        builders.put( FromDescr.class,
                      FROM_BUILDER );

        builders.put( QueryDescr.class,
                      QUERY_BUILDER );

        builders.put( AccumulateDescr.class,
                      ACCUMULATE_BUILDER );

        builders.put( EvalDescr.class,
                      EVAL_BUILDER );

        builders.put( CollectDescr.class,
                      COLLECT_BUILDER );

        builders.put( ForallDescr.class,
                      FORALL_BUILDER );

        builders.put( FunctionDescr.class,
                      FUNCTION_BUILDER );

        builders.put( EntryPointDescr.class,
                      ENTRY_POINT_BUILDER );
    }    

    public String getId() {
        return ID;
    }

    public Map getBuilders() {
        return this.builders;
    }
    
    public RuleConditionBuilder getBuilder(final Class clazz) {
        return (RuleConditionBuilder) this.builders.get( clazz );
    }    

    public ConsequenceBuilder getConsequenceBuilder() {
        return this.CONSEQUENCE_BUILDER;
    }

    public RuleConditionBuilder getEvalBuilder() {
        return this.EVAL_BUILDER;
    }

    public ReturnValueBuilder getReturnValueBuilder() {
        return this.RETURN_VALUE_BUILDER;
    }

    public PredicateBuilder getPredicateBuilder() {
        return this.PREDICATE_BUILDER;
    }

    public Dialect.AnalysisResult analyzeExpression(PackageBuildContext context,
                                                    BaseDescr descr,
                                                    Object content,
                                                    final Map<String,Class<?>>[] availableIdentifiers,
                                                    Map<String,Class<?>> localTypes) {
        if ( descr instanceof PredicateDescr ) {
            Appendable builder = new StringBuilderAppendable();
            PredicateDescr pdescr = (PredicateDescr) descr;
            if ( pdescr.getContent() instanceof LispForm ) {
                FunctionHandlers.dump( (LispForm) pdescr.getContent(),
                                       builder );

                content = builder.toString();
                pdescr.setContent( content );
            }
        } else if ( descr instanceof ReturnValueRestrictionDescr ) {
            Appendable builder = new StringBuilderAppendable();
            ReturnValueRestrictionDescr rdescr = (ReturnValueRestrictionDescr) descr;
            if ( rdescr.getContent() instanceof LispForm ) {
                FunctionHandlers.dump( (LispForm) rdescr.getContent(),
                                       builder );
                content = builder.toString();
                rdescr.setContent( content );
            }
        }
        return super.analyzeExpression( context,
                                        descr,
                                        content,
                                        availableIdentifiers,
                                        localTypes );
    }

}
