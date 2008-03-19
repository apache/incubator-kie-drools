/*
 * Copyright 2006 JBoss Inc
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

package org.drools.rule.builder.dialect.java;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.drools.compiler.DescrBuildError;
import org.drools.compiler.Dialect;
import org.drools.lang.descr.RuleDescr;
import org.drools.rule.Declaration;
import org.drools.rule.builder.ConsequenceBuilder;
import org.drools.rule.builder.RuleBuildContext;
import org.drools.rule.builder.dialect.java.parser.JavaModifyBlockDescr;
import org.drools.rule.builder.dialect.mvel.MVELDialect;
import org.drools.spi.PatternExtractor;
import org.mvel.compiler.ExecutableStatement;

/**
 * @author etirelli
 *
 */
public class JavaConsequenceBuilder extends AbstractJavaRuleBuilder
    implements
    ConsequenceBuilder {

    /* (non-Javadoc)
     * @see org.drools.semantics.java.builder.ConsequenceBuilder#buildConsequence(org.drools.semantics.java.builder.BuildContext, org.drools.semantics.java.builder.BuildUtils, org.drools.lang.descr.RuleDescr)
     */
    public void build(final RuleBuildContext context) {

        // pushing consequence LHS into the stack for variable resolution
        context.getBuildStack().push( context.getRule().getLhs() );

        final String className = "consequence";

        final RuleDescr ruleDescr = context.getRuleDescr();

        Dialect.AnalysisResult analysis = context.getDialect().analyzeBlock( context,
                                                                             ruleDescr,
                                                                             (String) ruleDescr.getConsequence(),
                                                                             new Set[]{context.getDeclarationResolver().getDeclarations().keySet(), context.getPkg().getGlobals().keySet()} );

        if ( analysis == null ) {
            // not possible to get the analysis results
            return;
        }
        
        String fixedConsequence = this.fixModifyBlocks( context, (JavaAnalysisResult) analysis, (String) ruleDescr.getConsequence() );
        
        if ( fixedConsequence == null ) {
            // not possible to rewrite the modify blocks
            return;
        }
        fixedConsequence = ((JavaDialect) context.getDialect()).getKnowledgeHelperFixer().fix( fixedConsequence );        

        final List[] usedIdentifiers = analysis.getBoundIdentifiers();

        final Declaration[] declarations = new Declaration[usedIdentifiers[0].size()];

        for ( int i = 0, size = usedIdentifiers[0].size(); i < size; i++ ) {
            declarations[i] = context.getDeclarationResolver().getDeclaration( (String) usedIdentifiers[0].get( i ) );
        }

        final Map map = createVariableContext( className,
                                               null,
                                               context,
                                               declarations,
                                               null,
                                               (String[]) usedIdentifiers[1].toArray( new String[usedIdentifiers[1].size()] ) );
        map.put( "text",
                  fixedConsequence);

        // Must use the rule declarations, so we use the same order as used in the generated invoker
        final List list = Arrays.asList( context.getRule().getDeclarations() );

        //final int[] indexes = new int[declarations.length];
        final Integer[] indexes = new Integer[declarations.length];

        final Boolean[] notPatterns = new Boolean[declarations.length];
        for ( int i = 0, length = declarations.length; i < length; i++ ) {
            indexes[i] = new Integer( list.indexOf( declarations[i] ) );
            notPatterns[i] = (declarations[i].getExtractor() instanceof PatternExtractor) ? new Boolean( false ) : new Boolean( true );
            if ( (indexes[i]).intValue() == -1 ) {
                context.getErrors().add( new DescrBuildError( context.getParentDescr(),
                                                              ruleDescr,
                                                              null,
                                                              "Internal Error : Unable to find declaration in list while generating the consequence invoker" ) );
            }
        }

        map.put( "indexes",
                 indexes );

        map.put( "notPatterns",
                 notPatterns );

        generatTemplates( "consequenceMethod",
                          "consequenceInvoker",
                          context,
                          className,
                          map,
                          context.getRule(),
                          ruleDescr );

        // popping Rule.getLHS() from the build stack
        context.getBuildStack().pop();
    }

    protected String fixModifyBlocks(final RuleBuildContext context,
                                     final JavaAnalysisResult analysis,
                                     final String originalCode) {
        MVELDialect mvel = (MVELDialect) context.getDialect( "mvel" );

        TreeSet blocks = new TreeSet( new Comparator() {
            public int compare(Object o1,
                               Object o2) {
                JavaModifyBlockDescr d1 = (JavaModifyBlockDescr) o1;
                JavaModifyBlockDescr d2 = (JavaModifyBlockDescr) o2;
                return d1.getStart() - d2.getStart();
            }
        } );

        for ( Iterator it = analysis.getModifyBlocks().iterator(); it.hasNext(); ) {
            blocks.add( it.next() );
        }

        StringBuffer consequence = new StringBuffer();
        int lastAdded = 0;
        for ( Iterator it = blocks.iterator(); it.hasNext(); ) {
            JavaModifyBlockDescr d = (JavaModifyBlockDescr) it.next();
            // adding chunk
            consequence.append( originalCode.substring( lastAdded,
                                                        d.getStart() - 1 ) );
            lastAdded = d.getEnd();

            Dialect.AnalysisResult mvelAnalysis = mvel.analyzeBlock( context,
                                                                     context.getRuleDescr(),
                                                                     mvel.getInterceptors(),
                                                                     d.getModifyExpression(),
                                                                     new Set[]{context.getDeclarationResolver().getDeclarations().keySet(), context.getPkg().getGlobals().keySet()},
                                                                     null );

            final ExecutableStatement expr = (ExecutableStatement) mvel.compile( d.getModifyExpression(),
                                                                                 mvelAnalysis,
                                                                                 mvel.getInterceptors(),
                                                                                 null,
                                                                                 null,
                                                                                 context );

            Class ret = expr.getKnownEgressType();
            
            if( ret == null ) {
                // not possible to evaluate expression return value
                context.getErrors().add( new DescrBuildError( context.getParentDescr(),
                                                              context.getRuleDescr(),
                                                              originalCode,
                                                              "Unable to determine the resulting type of the expression: " + d.getModifyExpression()+"\n" ) );
                
                return null;
            }

            // adding modify expression
            consequence.append( "{\n" );
            consequence.append( ret.getName() );
            consequence.append( " __obj__ = (" );
            consequence.append( ret.getName() );
            consequence.append( ") " );
            consequence.append( d.getModifyExpression() );
            consequence.append( ";\n" );
            // adding the modifyRetract call:
            consequence.append( "modifyRetract( __obj__ );\n" );
            

            // adding each of the expressions:
            for ( Iterator exprIt = d.getExpressions().iterator(); exprIt.hasNext(); ) {
                consequence.append( "__obj__." );
                consequence.append( exprIt.next() );
                consequence.append( ";\n" );
            }
            // adding the modifyInsert call:
            consequence.append( "modifyInsert( __obj__ );" );
            consequence.append( "}\n" );
        }
        consequence.append( originalCode.substring( lastAdded ) );

        return consequence.toString();
    }

}
