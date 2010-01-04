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
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.drools.compiler.DescrBuildError;
import org.drools.compiler.Dialect;
import org.drools.lang.descr.RuleDescr;
import org.drools.rule.Declaration;
import org.drools.rule.builder.ConsequenceBuilder;
import org.drools.rule.builder.RuleBuildContext;
import org.drools.rule.builder.dialect.java.parser.JavaBlockDescr;
import org.drools.rule.builder.dialect.java.parser.JavaInterfacePointsDescr;
import org.drools.rule.builder.dialect.java.parser.JavaModifyBlockDescr;
import org.drools.rule.builder.dialect.java.parser.JavaBlockDescr.BlockType;
import org.drools.rule.builder.dialect.mvel.MVELDialect;
import org.drools.spi.PatternExtractor;
import org.drools.util.ClassUtils;
import org.mvel2.compiler.ExecutableStatement;

/**
 * @author etirelli
 *
 */
public class JavaConsequenceBuilder extends AbstractJavaRuleBuilder
    implements
    ConsequenceBuilder {

    private final Pattern lineBreakFinder = Pattern.compile( "\\r\\n|\\r|\\n" );

    /* (non-Javadoc)
     * @see org.drools.semantics.java.builder.ConsequenceBuilder#buildConsequence(org.drools.semantics.java.builder.BuildContext, org.drools.semantics.java.builder.BuildUtils, org.drools.lang.descr.RuleDescr)
     */
    public void build(final RuleBuildContext context) {

        // pushing consequence LHS into the stack for variable resolution
        context.getBuildStack().push( context.getRule().getLhs() );

        final String className = "consequence";

        final RuleDescr ruleDescr = context.getRuleDescr();

        Map<String, Class< ? >> variables = context.getDeclarationResolver().getDeclarationClasses( context.getRule() );
        Dialect.AnalysisResult analysis = context.getDialect().analyzeBlock( context,
                                                                             ruleDescr,
                                                                             (String) ruleDescr.getConsequence(),
                                                                             new Map[]{variables, context.getPackageBuilder().getGlobals()} );

        if ( analysis == null ) {
            // not possible to get the analysis results
            return;
        }

        String fixedConsequence = this.fixBlockDescr( context,
                                                      (JavaAnalysisResult) analysis,
                                                      (String) ruleDescr.getConsequence() );

        if ( fixedConsequence == null ) {
            // not possible to rewrite the modify blocks
            return;
        }
        fixedConsequence = ((JavaDialect) context.getDialect()).getKnowledgeHelperFixer().fix( fixedConsequence );

        final List<String>[] usedIdentifiers = (List<String>[]) analysis.getBoundIdentifiers();

        final Declaration[] declarations = new Declaration[usedIdentifiers[0].size()];

        for ( int i = 0, size = usedIdentifiers[0].size(); i < size; i++ ) {
            declarations[i] = context.getDeclarationResolver().getDeclaration( context.getRule(),
                                                                               (String) usedIdentifiers[0].get( i ) );
        }

        final Map<String, Object> map = createVariableContext( className,
                                                               fixedConsequence,
                                                               context,
                                                               declarations,
                                                               null,
                                                               (String[]) usedIdentifiers[1].toArray( new String[usedIdentifiers[1].size()] ) );

        // Must use the rule declarations, so we use the same order as used in the generated invoker
        final List list = Arrays.asList( context.getRule().getDeclarations() );

        //final int[] indexes = new int[declarations.length];
        final Integer[] indexes = new Integer[declarations.length];

        final Boolean[] notPatterns = new Boolean[declarations.length];
        for ( int i = 0, length = declarations.length; i < length; i++ ) {
            indexes[i] = new Integer( list.indexOf( declarations[i] ) );
            notPatterns[i] = (declarations[i].getExtractor() instanceof PatternExtractor) ? Boolean.FALSE : Boolean.TRUE ;
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

    protected String fixBlockDescr(final RuleBuildContext context,
                                   final JavaAnalysisResult analysis,
                                   final String originalCode) {
        MVELDialect mvel = (MVELDialect) context.getDialect( "mvel" );

        // sorting exit points for correct order iteration
        List<JavaBlockDescr> blocks = analysis.getBlockDescrs();
        Collections.sort( blocks,
                          new Comparator<JavaBlockDescr>() {
                              public int compare(JavaBlockDescr o1,
                                                 JavaBlockDescr o2) {
                                  return o1.getStart() - o2.getStart();
                              }
                          } );

        StringBuilder consequence = new StringBuilder();
        int lastAdded = 0;
        for ( JavaBlockDescr block : blocks ) {
            // adding chunk
            consequence.append( originalCode.substring( lastAdded,
                                                        block.getStart() - 1 ) );
            lastAdded = block.getEnd();

            switch ( block.getType() ) {
                case MODIFY :
                    rewriteModify( context,
                                   originalCode,
                                   mvel,
                                   consequence,
                                   (JavaModifyBlockDescr) block );
                    break;
                case ENTRY :
                case EXIT :
                    rewriteInterfacePoint( originalCode,
                                           consequence,
                                           (JavaInterfacePointsDescr) block );
                    break;
            }
        }
        consequence.append( originalCode.substring( lastAdded ) );

        return consequence.toString();
    }

    private void rewriteInterfacePoint(final String originalCode,
                                       StringBuilder consequence,
                                       JavaInterfacePointsDescr ep) {
        // rewriting it for proper exitPoints access
        consequence.append( "drools.get" );
        if ( ep.getType() == BlockType.EXIT ) {
            consequence.append( "ExitPoint( " );
        } else {
            consequence.append( "EntryPoint( " );
        }
        consequence.append( ep.getId() );
        consequence.append( " )" );

        // the following is a hack to preserve line breaks.
        String originalBlock = originalCode.substring( ep.getStart() - 1,
                                                       ep.getEnd() );
        int end = originalBlock.indexOf( "]" );
        addLineBreaks( consequence,
                       originalBlock.substring( 0,
                                                end ) );
    }

    private void rewriteModify(final RuleBuildContext context,
                               final String originalCode,
                               MVELDialect mvel,
                               StringBuilder consequence,
                               JavaModifyBlockDescr d) {
        Map<String, Class< ? >> variables = context.getDeclarationResolver().getDeclarationClasses( context.getRule() );
        Dialect.AnalysisResult mvelAnalysis = mvel.analyzeBlock( context,
                                                                 context.getRuleDescr(),
                                                                 mvel.getInterceptors(),
                                                                 d.getModifyExpression(),
                                                                 new Map[]{variables, context.getPackageBuilder().getGlobals()},
                                                                 null );

        final ExecutableStatement expr = (ExecutableStatement) mvel.compile( d.getModifyExpression(),
                                                                             mvelAnalysis,
                                                                             mvel.getInterceptors(),
                                                                             null,
                                                                             null,
                                                                             context );

        Class ret = expr.getKnownEgressType();

        if ( ret == null ) {
            // not possible to evaluate expression return value
            context.getErrors().add( new DescrBuildError( context.getParentDescr(),
                                                          context.getRuleDescr(),
                                                          originalCode,
                                                          "Unable to determine the resulting type of the expression: " + d.getModifyExpression() + "\n" ) );

            return;
        }
        
        if ( d.getEnd() <= 0 ) {
            // not correctly parse
            context.getErrors().add( new DescrBuildError( context.getParentDescr(),
                                                          context.getRuleDescr(),
                                                          originalCode,
                                                          "Incorrect syntax for expression: " + d.getModifyExpression() + "\n" ) );

            return;
        }        
        String retString = ClassUtils.canonicalName( ret );

        // adding modify expression
        consequence.append( "{ " );
        consequence.append( retString );
        consequence.append( " __obj__ = (" );
        consequence.append( retString );
        consequence.append( ") " );
        consequence.append( d.getModifyExpression() );
        consequence.append( "; " );
        // adding the modifyRetract call:
        consequence.append( "modifyRetract( __obj__ ); " );
        
        // the following is a hack to preserve line breaks.
        String originalBlock = originalCode.substring( d.getStart() - 1,
                                                       d.getEnd() );
        int end = originalBlock.indexOf( "{" );
        if( end == -1 ){
            // no block
            context.getErrors().add( new DescrBuildError( context.getParentDescr(),
                                                          context.getRuleDescr(),
                                                          null,
                                                          "Block missing after modify" + d.getModifyExpression() + " ?\n" ) );
            return;
        }        

        addLineBreaks( consequence,
                       originalBlock.substring( 0,
                                                end ) );

        int start = end + 1;
        // adding each of the expressions:
        for ( String exprStr : d.getExpressions() ) {
            end = originalBlock.indexOf( exprStr,
                                         start );
            addLineBreaks( consequence,
                           originalBlock.substring( start,
                                                    end ) );
            consequence.append( "__obj__." );
            consequence.append( exprStr );
            consequence.append( "; " );
            start = end + exprStr.length();
        }
        // adding the modifyInsert call:
        addLineBreaks( consequence,
                       originalBlock.substring( end ) );
        consequence.append( "modifyInsert( __obj__ ); }" );
    }

    /**
     * @param consequence
     * @param chunk
     */
    private void addLineBreaks(StringBuilder consequence,
                               String chunk) {
        Matcher m = lineBreakFinder.matcher( chunk );
        while ( m.find() ) {
            consequence.append( "\n" );
        }
    }

}
