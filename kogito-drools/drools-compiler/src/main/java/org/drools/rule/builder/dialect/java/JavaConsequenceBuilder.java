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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
import org.drools.rule.builder.dialect.java.parser.JavaContainerBlockDescr;
import org.drools.rule.builder.dialect.java.parser.JavaElseBlockDescr;
import org.drools.rule.builder.dialect.java.parser.JavaFinalBlockDescr;
import org.drools.rule.builder.dialect.java.parser.JavaForBlockDescr;
import org.drools.rule.builder.dialect.java.parser.JavaIfBlockDescr;
import org.drools.rule.builder.dialect.java.parser.JavaInterfacePointsDescr;
import org.drools.rule.builder.dialect.java.parser.JavaLocalDeclarationDescr;
import org.drools.rule.builder.dialect.java.parser.JavaLocalDeclarationDescr.IdentifierDescr;
import org.drools.rule.builder.dialect.java.parser.JavaModifyBlockDescr;
import org.drools.rule.builder.dialect.java.parser.JavaThrowBlockDescr;
import org.drools.rule.builder.dialect.java.parser.JavaTryBlockDescr;
import org.drools.rule.builder.dialect.java.parser.JavaWhileBlockDescr;
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
import static org.drools.rule.builder.dialect.java.JavaRuleBuilderHelper.*;

public class JavaConsequenceBuilder
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

        Map<String, Declaration> decls = context.getDeclarationResolver().getDeclarations( context.getRule() );

        JavaAnalysisResult analysis = createJavaAnalysisResult(context, consequenceName, decls);

        if ( analysis == null ) {
            // not possible to get the analysis results
            return;
        }
        
        // This is a list of all the non container blocks, which initially are in tree form.
        List<JavaBlockDescr> blocks = new ArrayList<JavaBlockDescr>();
        buildBlockDescrs( blocks, analysis.getBlockDescrs() );     
        
        // @TODO <!--(mdp) commented this out until MVEL supports generics.
//        // Set the inputs for each container, this is needed for modifes when the target context is the result of an expression
//        setContainerBlockInputs(context, 
//                                descrs,
//                                analysis.getBlockDescrs(), 
//                                consequenceStr,
//                                bindings,
//                                new HashMap(),
//                                0 );
        // @TODO (mdp) commented this out until MVEL supports generics.-->

        // this will fix modify, retract, insert, update, entrypoints and channels
        String fixedConsequence = this.fixBlockDescr( context,
                                                      analysis.getAnalyzedExpr(),
                                                      blocks,
                                                      analysis.getBoundIdentifiers(),
                                                      decls );

        if ( fixedConsequence == null ) {
            // not possible to rewrite the modify blocks
            return;
        }
        fixedConsequence = ((JavaDialect) context.getDialect()).getKnowledgeHelperFixer().fix( fixedConsequence );

        Map<String, Object> map = createConsequenceContext(context, consequenceName, className, fixedConsequence, decls, analysis.getBoundIdentifiers());

        generatTemplates( "consequenceMethod",
                          "consequenceInvoker",
                          context,
                          className,
                          map,
                          context.getRule(),
                          context.getRuleDescr() );
        // popping Rule.getLHS() from the build stack
        context.getBuildStack().pop();
    }
    
    private static void buildBlockDescrs(List<JavaBlockDescr> descrs,
                                 JavaContainerBlockDescr parentBlock) {
        for ( JavaBlockDescr block : parentBlock.getJavaBlockDescrs() ) {
            if( block instanceof JavaContainerBlockDescr ) {
                buildBlockDescrs( descrs, (JavaContainerBlockDescr) block );
            } else {
                descrs.add( block );
            }
        }
    }

    String fixBlockDescr(final RuleBuildContext context,
                                   String originalCode, 
                                   List<JavaBlockDescr> blocks,
                                   BoundIdentifiers bindings,
                                   Map<String, Declaration> decls) {
        MVELDialect mvel = (MVELDialect) context.getDialect( "mvel" );

        // sorting exit points for correct order iteration
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
            if ( block.getEnd() == 0 ) {
                // do nothing, it was incorrectly parsed, but this error should be picked up else where
                continue;            
            }
            
            // adding chunk
            consequence.append( originalCode.substring( lastAdded,
                                                        block.getStart() - 1 ) );
            
            lastAdded = block.getEnd();
  
            switch ( block.getType() ) {
                case MODIFY :
                case UPDATE :
                case RETRACT :
                    rewriteDescr( context,
                                  originalCode,
                                  mvel,
                                  consequence,
                                 (JavaBlockDescr) block,
                                 bindings,
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
                default:
                    consequence.append( originalCode.substring( block.getStart() - 1,
                                                                lastAdded ) );                    
            }
        }
        consequence.append( originalCode.substring( lastAdded ) );

        return consequence.toString();
    }    
    
    /**
     * This code is not currently used, it's commented out in method caller. This is because we couldn't
     * get this to work and will have to wait until MVEL supports genercs (mdp).
     * @param context
     * @param descrs
     * @param parentBlock
     * @param originalCode
     * @param bindings
     * @param parentVars
     * @param offset
     */
    protected void setContainerBlockInputs(RuleBuildContext context,
                                           List<JavaBlockDescr> descrs,
                                           JavaContainerBlockDescr parentBlock,
                                           String originalCode, 
                                           BoundIdentifiers bindings,
                                           Map<String, Class<?>> parentVars,
                                           int offset) {
        StringBuilder consequence = new StringBuilder();
        int lastAdded = 0;
        
        // strip blocks, so we can analyse this block with MVEL
        for ( JavaBlockDescr block : parentBlock.getJavaBlockDescrs() ) {      
            if ( block.getEnd() == 0 ) {
                // do nothing, it was incorrectly parsed, but this error should be picked up else where
                continue;            
            }
            
            if ( block.getType() == BlockType.TRY ) {
                // adding previous chunk up to the start of this block
                consequence.append( originalCode.substring( lastAdded,
                                                            block.getStart() - 1 - offset ) );                
                JavaTryBlockDescr tryDescr = (JavaTryBlockDescr) block;
                if ( tryDescr.getFinal() != null ) {
                    lastAdded = tryDescr.getFinal().getEnd() - offset;
                } else {
                    lastAdded = tryDescr.getCatches().get( tryDescr.getCatches().size()-1 ).getEnd() - offset;
                }
                
                stripTryDescr( context,
                               originalCode,
                               consequence,
                               (JavaTryBlockDescr) block,
                               offset );                
            } else if (block.getType() == BlockType.THROW) {
                // adding previous chunk up to the start of this block
                consequence.append( originalCode.substring( lastAdded,
                                                            block.getStart() - 1 - offset ) ); 
                
                JavaThrowBlockDescr throwBlock = (JavaThrowBlockDescr) block;
                addWhiteSpaces(originalCode, consequence,  throwBlock.getStart()-offset, throwBlock.getTextStart()-offset);                
                consequence.append( originalCode.substring( throwBlock.getTextStart()-offset-1, throwBlock.getEnd()-1-offset ) +";");
                lastAdded = throwBlock.getEnd()-offset;
            } else if ( block.getType() == BlockType.IF ) {
                // adding previous chunk up to the start of this block
                consequence.append( originalCode.substring( lastAdded,
                                                            block.getStart() - 1 - offset ) );                
                JavaIfBlockDescr ifDescr = (JavaIfBlockDescr) block;
                lastAdded = ifDescr.getEnd() - offset;               
                stripBlockDescr( context,
                              originalCode,
                              consequence,
                              ifDescr,
                              offset );   
            } else if ( block.getType() == BlockType.ELSE ) {
                // adding previous chunk up to the start of this block
                consequence.append( originalCode.substring( lastAdded,
                                                            block.getStart() - 1 - offset ) );                
                JavaElseBlockDescr elseDescr = (JavaElseBlockDescr) block;
                lastAdded = elseDescr.getEnd() - offset;               
                stripBlockDescr( context,
                              originalCode,
                              consequence,
                              elseDescr,
                              offset );   
            } else if ( block.getType() == BlockType.WHILE) {
                // adding previous chunk up to the start of this block
                consequence.append( originalCode.substring( lastAdded,
                                                            block.getStart() - 1 - offset ) );                
                JavaWhileBlockDescr whileDescr = (JavaWhileBlockDescr) block;
                lastAdded = whileDescr.getEnd() - offset;               
                stripBlockDescr( context,
                              originalCode,
                              consequence,
                              whileDescr,
                              offset );   
            } else if ( block.getType() == BlockType.FOR) {
                // adding previous chunk up to the start of this block
                consequence.append( originalCode.substring( lastAdded,
                                                            block.getStart() - 1 - offset ) );                
                JavaForBlockDescr forDescr = (JavaForBlockDescr) block;
                lastAdded = forDescr.getEnd() - offset;               
                stripBlockDescr( context,
                                 originalCode,
                                 consequence,
                                 forDescr,
                                 offset );   
            }
        }           
        consequence.append( originalCode.substring( lastAdded ) );
        
        // We need to do this as MVEL doesn't recognise "modify"
        MacroProcessor macroProcessor = new MacroProcessor();
        Map macros = new HashMap( MVELConsequenceBuilder.macros );
        macros.put( "modify",
                    new Macro() {
                        public String doMacro() {
                            return "with  ";
                        }
                    } );
        macroProcessor.setMacros( macros );
        String mvelCode = macroProcessor.parse(  consequence.toString() );

        
        Map<String, Class<?>> inputs = (Map<String, Class<?>> )(Map)getInputs(context, mvelCode, bindings, parentVars);
        inputs.putAll( parentVars );
        parentBlock.setInputs( inputs );
        
        // now go depth, set inputs for each nested container
        // set inputs for current container blocks to be rewritten
        for ( JavaBlockDescr block : parentBlock.getJavaBlockDescrs() ) {
            if ( block.getType() == BlockType.TRY ) {
                JavaTryBlockDescr tryBlock = (JavaTryBlockDescr)block;
                setContainerBlockInputs(context,
                                        descrs,
                                        tryBlock,
                                        originalCode.substring( tryBlock.getTextStart()-offset, tryBlock.getEnd()-1-offset ), 
                                        bindings,
                                        inputs,
                                        tryBlock.getTextStart() );
                for ( JavaCatchBlockDescr catchBlock : tryBlock.getCatches() ) {
                    setContainerBlockInputs(context,
                                            descrs,
                                            catchBlock,
                                            catchBlock.getClause() + "=null;" + originalCode.substring( catchBlock.getTextStart()-offset, catchBlock.getEnd()-1-offset ), 
                                            bindings,
                                            inputs,
                                            tryBlock.getTextStart());                     
                }
                
                if ( tryBlock.getFinal() != null ) {
                    JavaFinalBlockDescr finalBlock = ( JavaFinalBlockDescr ) tryBlock.getFinal();
                    setContainerBlockInputs(context,
                                            descrs,
                                            finalBlock,
                                            originalCode.substring( finalBlock.getTextStart()-offset, finalBlock.getEnd()-1-offset ), 
                                            bindings,
                                            inputs,
                                            tryBlock.getTextStart());                    
                }                  
            } else if ( block.getType() == BlockType.IF) { 
                JavaIfBlockDescr ifBlock = (JavaIfBlockDescr)block;
                int adjustBlock = (originalCode.charAt(  ifBlock.getTextStart()-offset-1 ) == '{') ? 0: 1 ;                
                setContainerBlockInputs(context,
                                        descrs,
                                        ifBlock,
                                        originalCode.substring( ifBlock.getTextStart()-offset+adjustBlock, ifBlock.getEnd()-1-offset-adjustBlock ), 
                                        bindings,
                                        inputs,
                                        ifBlock.getTextStart() );            
            } else if ( block.getType() == BlockType.ELSE ) { 
                JavaElseBlockDescr elseBlock = (JavaElseBlockDescr)block;
                int adjustBlock = (originalCode.charAt(  elseBlock.getTextStart()-offset-1 ) == '{') ? 0: 1 ;
                setContainerBlockInputs(context,
                                        descrs,
                                        elseBlock,
                                        originalCode.substring( elseBlock.getTextStart()-offset+adjustBlock, elseBlock.getEnd()-1-offset-adjustBlock ), 
                                        bindings,
                                        inputs,
                                        elseBlock.getTextStart() );            
            } else if ( block.getType() == BlockType.WHILE) { 
                JavaWhileBlockDescr whileBlock = (JavaWhileBlockDescr)block;
                int adjustBlock = (originalCode.charAt(  whileBlock.getTextStart()-offset-1 ) == '{') ? 0: 1 ;                
                setContainerBlockInputs(context,
                                        descrs,
                                        whileBlock,
                                        originalCode.substring( whileBlock.getTextStart()-offset+adjustBlock, whileBlock.getEnd()-1-offset-adjustBlock ), 
                                        bindings,
                                        inputs,
                                        whileBlock.getTextStart() );            
            } else if ( block.getType() == BlockType.FOR) { 
                JavaForBlockDescr forBlock = (JavaForBlockDescr)block;
                int adjustBlock = (originalCode.charAt(  forBlock.getTextStart()-offset-1 ) == '{') ? 0: 1 ;    
                setContainerBlockInputs(context,
                                        descrs,
                                        forBlock,
                                        originalCode.substring( forBlock.getStartParen()-offset,forBlock.getInitEnd()-offset) +
                                        originalCode.substring( forBlock.getTextStart()-offset+adjustBlock, forBlock.getEnd()-1-offset-adjustBlock ), 
                                        bindings,
                                        inputs,
                                        forBlock.getTextStart() - (forBlock.getInitEnd()-forBlock.getStartParen() ) );            
            } else {
                block.setInputs(inputs); // each block to be rewritten now knows it's own variables
                descrs.add( block );
            }
        }
    }
    
    public Map<String, Class<?>> getInputs(final RuleBuildContext context,
                                           String code,
                                           BoundIdentifiers bindings,
                                           Map<String, Class<?>> parentVars) {        
        MVELDialect mvel = (MVELDialect) context.getDialect( "mvel" );
        
        MVELAnalysisResult mvelAnalysis = null;
        try {
            mvelAnalysis = ( MVELAnalysisResult ) mvel.analyzeBlock( context,
                                                                     context.getRuleDescr(),
                                                                     null,
                                                                     code,
                                                                     bindings,
                                                                     parentVars,
                                                                     "drools",
                                                                     KnowledgeHelper.class );
        } catch(Exception e) {
            // swallow this as the error will be reported else where
        }        
                
        return (mvelAnalysis != null) ? mvelAnalysis.getMvelVariables() : new HashMap<String, Class<?>>();
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
    
    private void stripTryDescr(RuleBuildContext context,
                               String originalCode,
                               StringBuilder consequence,
                               JavaTryBlockDescr block,
                               int offset) {      
        
        addWhiteSpaces(originalCode, consequence, consequence.length(), block.getTextStart()-offset);
        addWhiteSpaces(originalCode, consequence, consequence.length(),  block.getEnd()-offset);       
        
        for ( JavaCatchBlockDescr catchBlock : block.getCatches() ) {
            
            addWhiteSpaces( originalCode, consequence,  consequence.length(),
                            catchBlock.getTextStart()-offset  );                                                                     
            addWhiteSpaces( originalCode, consequence,  consequence.length(),
                            catchBlock.getEnd()-offset  );                        
        }
        
        if ( block.getFinal() != null ) {
            addWhiteSpaces(originalCode, consequence, consequence.length(), block.getFinal().getTextStart()-offset );            
            addWhiteSpaces(originalCode, consequence, consequence.length(), block.getFinal().getEnd()-offset );
        }           
    } 
    
    private void stripBlockDescr(RuleBuildContext context,
                              String originalCode,
                              StringBuilder consequence,
                              JavaBlockDescr block,
                              int offset) {      
        
        addWhiteSpaces(originalCode, consequence, consequence.length(), block.getEnd()-offset);        
    }  
    
    private void stripElseDescr(RuleBuildContext context,
                              String originalCode,
                              StringBuilder consequence,
                              JavaElseBlockDescr block,
                              int offset) {              
        addWhiteSpaces(originalCode, consequence, consequence.length(), block.getEnd()-offset);        
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
    
    @SuppressWarnings("unchecked")
    private boolean rewriteDescr(final RuleBuildContext context,
                                 final String originalCode,
                                 final MVELDialect mvel,
                                 final StringBuilder consequence,
                                 final JavaBlockDescr d, 
                                 final BoundIdentifiers bindings,
                                 final Map<String, Declaration> decls) {         
        if ( d.getEnd() == 0 ) {
            // do nothing, it was incorrectly parsed, but this error should be picked up else where
            return false;            
        }
        
        boolean typeSafety = context.isTypesafe();
        context.setTypesafe( false ); // we have to analyse in dynamic mode for now, as we cannot safely determine all input vars
        
        Map<String, Class<?>> localTypes = d.getInputs();
        if( d.getInScopeLocalVars() != null && ! d.getInScopeLocalVars().isEmpty() ) {
            localTypes = new HashMap<String, Class<?>>( d.getInputs() != null ? d.getInputs() : Collections.EMPTY_MAP );
            for( JavaLocalDeclarationDescr local : d.getInScopeLocalVars() ) {
                // these are variables declared in the code itself that are in the scope for this expression
                try {
                    Class<?> type = context.getDialect( "java" ).getPackageRegistry().getTypeResolver().resolveType( local.getType() );
                    for( IdentifierDescr id : local.getIdentifiers() ) {
                        localTypes.put( id.getIdentifier(), type );
                    }
                } catch ( ClassNotFoundException e ) {
                    context.getErrors().add( new DescrBuildError( context.getRuleDescr(),
                                                                  context.getParentDescr(),
                                                                  null,
                                                                  "Unable to resolve type " + local.getType() + ":\n" + e.getMessage() ) );
                }
            }
        }
        
           MVELAnalysisResult mvelAnalysis = ( MVELAnalysisResult ) mvel.analyzeBlock( context,
                                                                                       context.getRuleDescr(),
                                                                                       mvel.getInterceptors(),
                                                                                       d.getTargetExpression(),
                                                                                       bindings,
                                                                                       localTypes,
                                                                                       "drools",
                                                                                       KnowledgeHelper.class);
           context.setTypesafe( typeSafety );
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
                 
           // adding modify expression           
           String retString = ClassUtils.canonicalName( ret );                      
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
               consequence.append( " = " );
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
