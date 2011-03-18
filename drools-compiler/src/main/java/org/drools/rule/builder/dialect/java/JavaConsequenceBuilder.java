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

import org.drools.base.mvel.MVELCompilationUnit;
import org.drools.compiler.AnalysisResult;
import org.drools.compiler.BoundIdentifiers;
import org.drools.compiler.DescrBuildError;
import org.drools.core.util.ClassUtils;
import org.drools.lang.descr.RuleDescr;
import org.drools.reteoo.RuleTerminalNode.SortDeclarations;
import org.drools.rule.Declaration;
import org.drools.rule.builder.ConsequenceBuilder;
import org.drools.rule.builder.RuleBuildContext;
import org.drools.rule.builder.dialect.java.parser.JavaBlockDescr;
import org.drools.rule.builder.dialect.java.parser.JavaCatchBlockDescr;
import org.drools.rule.builder.dialect.java.parser.JavaInterfacePointsDescr;
import org.drools.rule.builder.dialect.java.parser.JavaModifyBlockDescr;
import org.drools.rule.builder.dialect.java.parser.JavaTryBlockDescr;
import org.drools.rule.builder.dialect.java.parser.JavaBlockDescr.BlockType;
import org.drools.rule.builder.dialect.java.parser.JavaRetractBlockDescr;
import org.drools.rule.builder.dialect.java.parser.JavaUpdateBlockDescr;
import org.drools.rule.builder.dialect.mvel.MVELAnalysisResult;
import org.drools.rule.builder.dialect.mvel.MVELConsequenceBuilder;
import org.drools.rule.builder.dialect.mvel.MVELDialect;
import org.drools.spi.KnowledgeHelper;
import org.drools.spi.PatternExtractor;
import org.mvel2.Macro;
import org.mvel2.MacroProcessor;
import org.mvel2.compiler.ExecutableStatement;

public class JavaConsequenceBuilder extends AbstractJavaRuleBuilder
    implements
    ConsequenceBuilder {

    private final Pattern lineBreakFinder = Pattern.compile( "\\r\\n|\\r|\\n" );

    /* (non-Javadoc)
     * @see org.drools.semantics.java.builder.ConsequenceBuilder#buildConsequence(org.drools.semantics.java.builder.BuildContext, org.drools.semantics.java.builder.BuildUtils, org.drools.lang.descr.RuleDescr)
     */
    public void build(final RuleBuildContext context, String consequenceName) {

        // pushing consequence LHS into the stack for variable resolution
        context.getBuildStack().push( context.getRule().getLhs() );

        final String className = consequenceName + "Consequence";

        final RuleDescr ruleDescr = context.getRuleDescr();

        Map<String, Declaration> decls = context.getDeclarationResolver().getDeclarations( context.getRule() );
        
        AnalysisResult analysis = context.getDialect().analyzeBlock( context,
                                                                     ruleDescr,
                                                                     (String) ruleDescr.getConsequence(),
                                                                     new BoundIdentifiers(context.getDeclarationResolver().getDeclarationClasses( decls ), 
                                                                                          context.getPackageBuilder().getGlobals() ) );

        if ( analysis == null ) {
            // not possible to get the analysis results
            return;
        }

        String fixedConsequence = this.fixBlockDescr( context,
                                                      (JavaAnalysisResult) analysis,
                                                      ( "default".equals( consequenceName ) ) ? (String) ruleDescr.getConsequence() : (String) ruleDescr.getNamedConsequences().get( consequenceName ),
                                                      decls );

        if ( fixedConsequence == null ) {
            // not possible to rewrite the modify blocks
            return;
        }
        fixedConsequence = ((JavaDialect) context.getDialect()).getKnowledgeHelperFixer().fix( fixedConsequence );

        final BoundIdentifiers usedIdentifiers = analysis.getBoundIdentifiers();
                
        final Declaration[] declarations =  new Declaration[usedIdentifiers.getDeclarations().size()];
        String[] declrStr = new String[declarations.length];
        int j = 0;
        for (String str : usedIdentifiers.getDeclarations().keySet() ) {
            declrStr[j] = str;
            declarations[j++] = decls.get( str );
        }
        Arrays.sort( declarations, SortDeclarations.instance  );
        for ( int i = 0; i < declrStr.length; i++) {
            declrStr[i] = declarations[i].getIdentifier();
        }
        context.getRule().setRequiredDeclarations( declrStr );
                
        final Map<String, Object> map = createVariableContext( className,
                                                               fixedConsequence,
                                                               context,
                                                               declarations,
                                                               null,
                                                               usedIdentifiers.getGlobals(),
                                                               (JavaAnalysisResult) analysis );
        
        map.put( "consequenceName", consequenceName );

        //final int[] indexes = new int[declarations.length];
        final Integer[] indexes = new Integer[declarations.length];

        final Boolean[] notPatterns = new Boolean[declarations.length];
        for ( int i = 0, length = declarations.length; i < length; i++ ) {
            indexes[i] = i;
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
                                   String originalCode, 
                                   Map<String, Declaration> decls) {
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
        boolean modifyExpr = true;
        
        // first do simple rewrites for try/catch
        for ( JavaBlockDescr block : blocks ) {
            // adding chunk
            consequence.append( originalCode.substring( lastAdded,
                                                        block.getStart() - 1 ) );
            switch ( block.getType() ) {
                case TRY :
                    rewriteTryDescr( lastAdded,
                                     context,
                                     originalCode,
                                     consequence,
                                     (JavaTryBlockDescr) block,
                                      decls );
            }
        }           
        
        originalCode = consequence.toString();
        consequence = new StringBuilder();
        
        System.out.println( originalCode );
        
        // We need to do this as MVEL doesn't recognise "with"
        MacroProcessor macroProcessor = new MacroProcessor();
        Map macros = new HashMap( MVELConsequenceBuilder.macros );
        macros.put( "modify",
                    new Macro() {
                        public String doMacro() {
                            return "with";
                        }
                    } );
        macroProcessor.setMacros( macros );
        originalCode = macroProcessor.parse( originalCode );
        
        Map<String, Class<?>> variables = context.getDeclarationResolver().getDeclarationClasses(decls);
        
        MVELAnalysisResult mvelAnalysis = null;
        try {
            mvelAnalysis = ( MVELAnalysisResult ) mvel.analyzeBlock( context,
                                                                     context.getRuleDescr(),
                                                                     mvel.getInterceptors(),
                                                                     originalCode,
                                                                     new BoundIdentifiers( variables, context.getPackageBuilder().getGlobals(), KnowledgeHelper.class ),
                                                                     null );
        } catch(Exception e) {
            // swallow this as the error will be reported else where
        }
        
        for ( JavaBlockDescr block : blocks ) {
            // adding chunk
            consequence.append( originalCode.substring( lastAdded,
                                                        block.getStart() - 1 ) );
            lastAdded = block.getEnd();

            switch ( block.getType() ) {
                case MODIFY :
                case UPDATE :
                case RETRACT :
                    modifyExpr = rewriteDescr( context,
                                               mvelAnalysis,
                                               originalCode,
                                               mvel,
                                               consequence,
                                               (JavaBlockDescr) block,
                                               decls );
                    break;
                case ENTRY :
                case EXIT :
                case CHANNEL :
                    rewriteInterfacePoint( context, 
                                           originalCode,
                                           consequence,
                                           (JavaInterfacePointsDescr) block );
                    break;
            }
        }
        analysis.setModifyExpr( modifyExpr );
        consequence.append( originalCode.substring( lastAdded ) );

        return consequence.toString();
    }

    private void addWhiteSpaces(String original, StringBuilder consequence, int start, int end) {
        for ( int i = start; i < end; i++ ) {
            switch(original.charAt( i )) {
                case '\n':
                case '\r':
                case '\t':
                case ' ':
                    consequence.append(original.charAt( i ));
                    break;
                default:
                    consequence.append( " " );
            }
        }
    }
    
    private void rewriteTryDescr(int lastAdded, 
                                 RuleBuildContext context,
                                 String originalCode,
                                 StringBuilder consequence,
                                 JavaTryBlockDescr block,
                                 Map<String, Declaration> decls) {       
        addWhiteSpaces(originalCode, consequence, consequence.length(), block.getTextStart()-1);
        consequence.append( "{" );
        consequence.append( originalCode.substring( consequence.length(),
                                                    block.getEnd()-1 ) );           
        consequence.append( "}" );

        for ( JavaCatchBlockDescr catchBlock : block.getCatches() ) {
            consequence.append( "{" );            
            consequence.append( catchBlock.getClause() );
            consequence.append( " = null;" );            
            addWhiteSpaces( originalCode, consequence,  consequence.length(),
                                                        catchBlock.getTextStart() );            
            consequence.append( originalCode.substring( consequence.length(),
                                                        catchBlock.getEnd()-1 ) );
            consequence.append( "}" );                     
        }
        
        if ( block.getFinal() != null ) {
            addWhiteSpaces(originalCode, consequence, consequence.length(), block.getFinal().getTextStart()-1 );               
            consequence.append( "{" );                
            consequence.append( originalCode.substring( consequence.length(),
                                                        block.getFinal().getEnd()-1 ) );          
            consequence.append( "}" );
        }           
    }

    @SuppressWarnings("unchecked")
    private void rewriteInterfacePoint(final RuleBuildContext context,
                                       final String originalCode,
                                       final StringBuilder consequence,
                                       final JavaInterfacePointsDescr ep) {
        // rewriting it for proper exitPoints access
        consequence.append( "drools.get" );
        if ( ep.getType() == BlockType.EXIT ) {
            consequence.append( "ExitPoint( " );
        } else if( ep.getType() == BlockType.ENTRY ) {
            consequence.append( "EntryPoint( " );
        } else if( ep.getType() == BlockType.CHANNEL ) {
            consequence.append( "Channel( " );
        } else {
            context.getErrors().add( new DescrBuildError( context.getParentDescr(),
                                                          context.getRuleDescr(),
                                                          ep,
                                                          "Unable to rewrite code block: " + ep + "\n" ) );

            return;
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
    
    private boolean rewriteDescr(final RuleBuildContext context,
                                 MVELAnalysisResult analysis, 
                                 final String originalCode,
                                 MVELDialect mvel,
                                 StringBuilder consequence,
                                 JavaBlockDescr d, 
                                 Map<String, Declaration> decls) {
           if ( d.getEnd() <= 0 ) {
               // not correctly parse
               context.getErrors().add( new DescrBuildError( context.getParentDescr(),
                                                             context.getRuleDescr(),
                                                             originalCode,
                                                             "Incorrect syntax for expression: " + d.getTargetExpression() + "\n" ) );

               return false;
           }
           
           Map<String, Class<?>> variables = context.getDeclarationResolver().getDeclarationClasses(decls);
           
           MVELAnalysisResult mvelAnalysis = ( MVELAnalysisResult ) mvel.analyzeBlock( context,
                                                                                       context.getRuleDescr(),
                                                                                       mvel.getInterceptors(),
                                                                                       d.getTargetExpression(),
                                                                                       new BoundIdentifiers( variables, context.getPackageBuilder().getGlobals() ),
                                                                                       analysis != null ?  analysis.getMvelVariables() : null );

           if ( mvelAnalysis == null ) {
               // something bad happened, issue already logged in errors
               return false;
           }
           
           Class ret = mvelAnalysis.getReturnType();

           if ( ret == null ) {
               // not possible to evaluate expression return value
               context.getErrors().add( new DescrBuildError( context.getParentDescr(),
                                                             context.getRuleDescr(),
                                                             originalCode,
                                                             "Unable to determine the resulting type of the expression: " + d.getTargetExpression() + "\n" ) );

               return false;
           }
                 
           String retString = ClassUtils.canonicalName( ret );
           // adding modify expression
                      
           String declrString;
           if (d.getTargetExpression().charAt( 0 ) == '(' ) {
               declrString = d.getTargetExpression().substring( 1,d.getTargetExpression().length() -1 ).trim();
           } else {
               declrString = d.getTargetExpression();
           }
           String obj = declrString;
           Declaration declr = decls.get( declrString );
           
           consequence.append( "{ " );
           
           if ( declr == null  ) {
               obj = "__obj__";
               consequence.append( retString );
               consequence.append( " " );
               consequence.append( obj);
               consequence.append( " = (" );
               consequence.append( retString );
               consequence.append( ") " );
               consequence.append( d.getTargetExpression() );
               consequence.append( "; " );
           }
           
           if ( declr == null || declr.isInternalFact() ) {
               consequence.append( "org.drools.FactHandle "  );
               consequence.append( obj  );
               consequence.append( "__Handle2__ = drools.getFactHandle("  );
               consequence.append( obj  );
               consequence.append( ");"  );
           }
           
           // the following is a hack to preserve line breaks.
           String originalBlock = originalCode.substring( d.getStart() - 1,
                                                          d.getEnd() );
           
           if ( d instanceof JavaModifyBlockDescr ) {
               rewriteModifyDescr( context, d, originalBlock, consequence, declr, obj );
           } else if ( d instanceof JavaUpdateBlockDescr ) {
               rewriteUpdateDescr( d, originalBlock, consequence, declr, obj );
           } else if ( d instanceof JavaRetractBlockDescr ) {
               rewriteRetractDescr( d, originalBlock, consequence, declr, obj );
           }

           return declr != null;
       }
    
    private boolean rewriteModifyDescr(final RuleBuildContext context,
                                       JavaBlockDescr d,
                                       String originalBlock,
                                       StringBuilder consequence,
                                       Declaration declr,
                                       String obj) {
        int end = originalBlock.indexOf( "{" );
        if( end == -1 ){
            // no block
            context.getErrors().add( new DescrBuildError( context.getParentDescr(),
                                                          context.getRuleDescr(),
                                                          null,
                                                          "Block missing after modify" + d.getTargetExpression() + " ?\n" ) );
            return false;
        }

        addLineBreaks( consequence,
                       originalBlock.substring( 0,
                                                end ) );
        
           int start = end + 1;
           // adding each of the expressions:
           for ( String exprStr : ((JavaModifyBlockDescr)d).getExpressions() ) {
               end = originalBlock.indexOf( exprStr,
                                            start );
               addLineBreaks( consequence,
                              originalBlock.substring( start,
                                                       end ) );
               consequence.append( obj + "." );
               consequence.append( exprStr );
               consequence.append( "; " );
               start = end + exprStr.length();
           }
           
           // adding the modifyInsert call:
           addLineBreaks( consequence,
                          originalBlock.substring( end ) );

           if ( declr != null && !declr.isInternalFact() ) {
               consequence.append( "drools.update( " + obj + "__Handle__ ); }" );
           } else {
               consequence.append( "drools.update( " + obj + "__Handle2__ ); }" );
           }
           
           return declr != null;
       }
    
    private boolean rewriteUpdateDescr(JavaBlockDescr d,
                                       String originalBlock,
                                       StringBuilder consequence,
                                       Declaration declr,
                                       String obj) {
           if ( declr != null && !declr.isInternalFact() ) {
               consequence.append( "drools.update( " + obj + "__Handle__ ); }" );
           } else {
               consequence.append( "drools.update( " + obj + "__Handle2__ ); }" );
           }
           
           return declr != null;
       }
    
    private boolean rewriteRetractDescr(JavaBlockDescr d,
                                        String originalBlock,
                                        StringBuilder consequence,
                                        Declaration declr,
                                        String obj) {
        if ( declr != null && !declr.isInternalFact() ) {
               consequence.append( "drools.retract( " + obj + "__Handle__ ); }" );
           } else {
               consequence.append( "drools.retract( " + obj + "__Handle2__ ); }" );
           }
           
           return declr != null;
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
