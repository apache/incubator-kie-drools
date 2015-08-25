/*
 * Copyright 2015 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.drools.compiler.rule.builder.dialect;

import org.drools.compiler.builder.impl.KnowledgeBuilderImpl;
import org.drools.compiler.commons.jci.readers.ResourceReader;
import org.drools.compiler.compiler.BoundIdentifiers;
import org.drools.compiler.compiler.DescrBuildError;
import org.drools.compiler.lang.descr.BaseDescr;
import org.drools.compiler.lang.descr.FunctionDescr;
import org.drools.compiler.lang.descr.ImportDescr;
import org.drools.compiler.lang.descr.PackageDescr;
import org.drools.compiler.rule.builder.RuleBuildContext;
import org.drools.compiler.rule.builder.dialect.java.JavaAnalysisResult;
import org.drools.compiler.rule.builder.dialect.java.parser.JavaBlockDescr;
import org.drools.compiler.rule.builder.dialect.java.parser.JavaCatchBlockDescr;
import org.drools.compiler.rule.builder.dialect.java.parser.JavaContainerBlockDescr;
import org.drools.compiler.rule.builder.dialect.java.parser.JavaElseBlockDescr;
import org.drools.compiler.rule.builder.dialect.java.parser.JavaFinalBlockDescr;
import org.drools.compiler.rule.builder.dialect.java.parser.JavaForBlockDescr;
import org.drools.compiler.rule.builder.dialect.java.parser.JavaIfBlockDescr;
import org.drools.compiler.rule.builder.dialect.java.parser.JavaInterfacePointsDescr;
import org.drools.compiler.rule.builder.dialect.java.parser.JavaLocalDeclarationDescr;
import org.drools.compiler.rule.builder.dialect.java.parser.JavaLocalDeclarationDescr.IdentifierDescr;
import org.drools.compiler.rule.builder.dialect.java.parser.JavaModifyBlockDescr;
import org.drools.compiler.rule.builder.dialect.java.parser.JavaThrowBlockDescr;
import org.drools.compiler.rule.builder.dialect.java.parser.JavaTryBlockDescr;
import org.drools.compiler.rule.builder.dialect.java.parser.JavaWhileBlockDescr;
import org.drools.compiler.rule.builder.dialect.mvel.MVELAnalysisResult;
import org.drools.compiler.rule.builder.dialect.mvel.MVELConsequenceBuilder;
import org.drools.compiler.rule.builder.dialect.mvel.MVELDialect;
import org.drools.core.factmodel.ClassDefinition;
import org.drools.core.rule.ConsequenceMetaData;
import org.drools.core.rule.Declaration;
import org.drools.core.rule.TypeDeclaration;
import org.drools.core.spi.ClassWireable;
import org.drools.core.spi.KnowledgeHelper;
import org.drools.core.util.ClassUtils;
import org.drools.core.util.bitmask.AllSetBitMask;
import org.drools.core.util.bitmask.BitMask;
import org.kie.api.definition.type.FactField;
import org.mvel2.CompileException;
import org.mvel2.Macro;
import org.mvel2.MacroProcessor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.drools.core.reteoo.PropertySpecificUtil.allSetButTraitBitMask;
import static org.drools.core.reteoo.PropertySpecificUtil.getEmptyPropertyReactiveMask;
import static org.drools.core.reteoo.PropertySpecificUtil.setPropertyOnMask;
import static org.drools.core.util.ClassUtils.*;
import static org.drools.core.util.StringUtils.*;

public final class DialectUtil {

    private static final Pattern NON_ALPHA_REGEX = Pattern.compile("[ -/:-@\\[-`\\{-\\xff]");
    private static final Pattern LINE_BREAK_FINDER = Pattern.compile( "\\r\\n|\\r|\\n" );

    /**
     * Takes a given name and makes sure that its legal and doesn't already exist. If the file exists it increases counter appender untill it is unique.
     */
    public static String getUniqueLegalName(final String packageName,
                                            final String name,
                                            final int seed,
                                            final String ext,
                                            final String prefix,
                                            final ResourceReader src) {
        // replaces all non alphanumeric or $ chars with _
        final String newName = prefix + "_" + normalizeRuleName( name );
        if (ext.equals("java")) {
            return newName + Math.abs(seed);
        }

        final String fileName = packageName.replace('.', '/') + "/" + newName;

        if (src == null || !src.isAvailable(fileName + "." + ext)) return newName;

        // make sure the class name does not exist, if it does increase the counter
        int counter = -1;
        while (true) {

            counter++;
            final String actualName = fileName + "_" + counter + "." + ext;

            //MVEL:test null to Fix failing test on MVELConsequenceBuilderTest.testImperativeCodeError()
            if (!src.isAvailable(actualName)) break;
        }
        // we have duplicate file names so append counter
        return newName + "_" + counter;
    }

    public static String fixBlockDescr(final RuleBuildContext context,
                                       final JavaAnalysisResult analysis,
                                       Map<String, Declaration> decls) {
        // This is a list of all the non container blocks, which initially are in tree form.
        List<JavaBlockDescr> blocks = buildBlockDescrs(new ArrayList<JavaBlockDescr>(), analysis.getBlockDescrs());

        return fixBlockDescr(context, analysis, decls, blocks);
    }

    public static String fixBlockDescr(final RuleBuildContext context,
                                       final JavaAnalysisResult analysis,
                                       Map<String, Declaration> decls,
                                       List<JavaBlockDescr> blocks) {

        MVELDialect mvel = (MVELDialect) context.getDialect("mvel");


        String originalCode = analysis.getAnalyzedExpr();
        BoundIdentifiers bindings = analysis.getBoundIdentifiers();

        // sorting exit points for correct order iteration
        Collections.sort(blocks,
                new Comparator<JavaBlockDescr>() {
                    public int compare(JavaBlockDescr o1,
                                       JavaBlockDescr o2) {
                        return o1.getStart() - o2.getStart();
                    }
                });

        StringBuilder consequence = new StringBuilder();
        int lastAdded = 0;

        for (JavaBlockDescr block : blocks) {
            if (block.getEnd() == 0 || block.getEnd() > originalCode.length() ) {
                // do nothing, it was incorrectly parsed, but this error should be picked up else where
                continue;
            }

            // adding chunk
            consequence.append(originalCode.substring(lastAdded,
                    block.getStart() - 1));

            lastAdded = block.getEnd();

            switch (block.getType()) {
                case MODIFY:
                case UPDATE:
                case RETRACT:
                    rewriteDescr(context,
                            originalCode,
                            mvel,
                            consequence,
                            block,
                            bindings,
                            decls);
                    break;
                case ENTRY:
                case EXIT:
                case CHANNEL:
                    rewriteInterfacePoint(context,
                            originalCode,
                            consequence,
                            (JavaInterfacePointsDescr) block);
                    break;
                case INSERT:
                    parseInsertDescr(context, block);
                default:
                    consequence.append(originalCode.substring(block.getStart() - 1, lastAdded));
            }
        }
        consequence.append(originalCode.substring(lastAdded));

        return consequence.toString();
    }

    private static List<JavaBlockDescr> buildBlockDescrs(List<JavaBlockDescr> descrs,
                                                         JavaContainerBlockDescr parentBlock) {
        for (JavaBlockDescr block : parentBlock.getJavaBlockDescrs()) {
            if (block instanceof JavaContainerBlockDescr) {
                buildBlockDescrs(descrs, (JavaContainerBlockDescr) block);
            } else {
                descrs.add(block);
            }
        }
        return descrs;
    }

    /**
     * This code is not currently used, it's commented out in method caller. This is because we couldn't
     * get this to work and will have to wait until MVEL supports genercs (mdp).
     */
    public static void setContainerBlockInputs(RuleBuildContext context,
                                                List<JavaBlockDescr> descrs,
                                                JavaContainerBlockDescr parentBlock,
                                                String originalCode,
                                                BoundIdentifiers bindings,
                                                Map<String, Class<?>> parentVars,
                                                int offset) {
        StringBuilder consequence = new StringBuilder();
        int lastAdded = 0;

        // strip blocks, so we can analyse this block with MVEL
        for (JavaBlockDescr block : parentBlock.getJavaBlockDescrs()) {
            if (block.getEnd() == 0) {
                // do nothing, it was incorrectly parsed, but this error should be picked up else where
                continue;
            }

            if (block.getType() == JavaBlockDescr.BlockType.TRY) {
                // adding previous chunk up to the start of this block
                consequence.append(originalCode.substring(lastAdded,
                        block.getStart() - 1 - offset));
                JavaTryBlockDescr tryDescr = (JavaTryBlockDescr) block;
                if (tryDescr.getFinal() != null) {
                    lastAdded = tryDescr.getFinal().getEnd() - offset;
                } else {
                    lastAdded = tryDescr.getCatches().get(tryDescr.getCatches().size() - 1).getEnd() - offset;
                }

                stripTryDescr(originalCode,
                        consequence,
                        (JavaTryBlockDescr) block,
                        offset);
            } else if (block.getType() == JavaBlockDescr.BlockType.THROW) {
                // adding previous chunk up to the start of this block
                consequence.append(originalCode.substring(lastAdded,
                        block.getStart() - 1 - offset));

                JavaThrowBlockDescr throwBlock = (JavaThrowBlockDescr) block;
                addWhiteSpaces(originalCode, consequence, throwBlock.getStart() - offset, throwBlock.getTextStart() - offset);
                consequence.append(originalCode.substring(throwBlock.getTextStart() - offset - 1, throwBlock.getEnd() - 1 - offset)).append(";");
                lastAdded = throwBlock.getEnd() - offset;
            } else if (block.getType() == JavaBlockDescr.BlockType.IF) {
                // adding previous chunk up to the start of this block
                consequence.append(originalCode.substring(lastAdded,
                        block.getStart() - 1 - offset));
                JavaIfBlockDescr ifDescr = (JavaIfBlockDescr) block;
                lastAdded = ifDescr.getEnd() - offset;
                stripBlockDescr(originalCode,
                                consequence,
                                ifDescr,
                                offset);
            } else if (block.getType() == JavaBlockDescr.BlockType.ELSE) {
                // adding previous chunk up to the start of this block
                consequence.append(originalCode.substring(lastAdded,
                        block.getStart() - 1 - offset));
                JavaElseBlockDescr elseDescr = (JavaElseBlockDescr) block;
                lastAdded = elseDescr.getEnd() - offset;
                stripBlockDescr(originalCode,
                                consequence,
                                elseDescr,
                                offset);
            } else if (block.getType() == JavaBlockDescr.BlockType.WHILE) {
                // adding previous chunk up to the start of this block
                consequence.append(originalCode.substring(lastAdded,
                        block.getStart() - 1 - offset));
                JavaWhileBlockDescr whileDescr = (JavaWhileBlockDescr) block;
                lastAdded = whileDescr.getEnd() - offset;
                stripBlockDescr(originalCode,
                                consequence,
                                whileDescr,
                                offset);
            } else if (block.getType() == JavaBlockDescr.BlockType.FOR) {
                // adding previous chunk up to the start of this block
                consequence.append(originalCode.substring(lastAdded,
                        block.getStart() - 1 - offset));
                JavaForBlockDescr forDescr = (JavaForBlockDescr) block;
                lastAdded = forDescr.getEnd() - offset;
                stripBlockDescr(originalCode,
                                consequence,
                                forDescr,
                                offset);
            }
        }
        consequence.append(originalCode.substring(lastAdded));

        // We need to do this as MVEL doesn't recognise "modify"
        MacroProcessor macroProcessor = new MacroProcessor();
        Map<String, Macro> macros = new HashMap<String, Macro>(MVELConsequenceBuilder.macros);
        macros.put("modify",
                new Macro() {
                    public String doMacro() {
                        return "with  ";
                    }
                });
        macroProcessor.setMacros(macros);
        String mvelCode = macroProcessor.parse(consequence.toString());


        Map<String, Class<?>> inputs = getInputs(context, mvelCode, bindings, parentVars);
        inputs.putAll(parentVars);
        parentBlock.setInputs(inputs);

        // now go depth, set inputs for each nested container
        // set inputs for current container blocks to be rewritten
        for (JavaBlockDescr block : parentBlock.getJavaBlockDescrs()) {
            if (block.getType() == JavaBlockDescr.BlockType.TRY) {
                JavaTryBlockDescr tryBlock = (JavaTryBlockDescr) block;
                setContainerBlockInputs(context,
                        descrs,
                        tryBlock,
                        originalCode.substring(tryBlock.getTextStart() - offset, tryBlock.getEnd() - 1 - offset),
                        bindings,
                        inputs,
                        tryBlock.getTextStart());
                for (JavaCatchBlockDescr catchBlock : tryBlock.getCatches()) {
                    setContainerBlockInputs(context,
                            descrs,
                            catchBlock,
                            catchBlock.getClause() + "=null;" + originalCode.substring(catchBlock.getTextStart() - offset, catchBlock.getEnd() - 1 - offset),
                            bindings,
                            inputs,
                            tryBlock.getTextStart());
                }

                if (tryBlock.getFinal() != null) {
                    JavaFinalBlockDescr finalBlock = tryBlock.getFinal();
                    setContainerBlockInputs(context,
                            descrs,
                            finalBlock,
                            originalCode.substring(finalBlock.getTextStart() - offset, finalBlock.getEnd() - 1 - offset),
                            bindings,
                            inputs,
                            tryBlock.getTextStart());
                }
            } else if (block.getType() == JavaBlockDescr.BlockType.IF) {
                JavaIfBlockDescr ifBlock = (JavaIfBlockDescr) block;
                int adjustBlock = (originalCode.charAt(ifBlock.getTextStart() - offset - 1) == '{') ? 0 : 1;
                setContainerBlockInputs(context,
                        descrs,
                        ifBlock,
                        originalCode.substring(ifBlock.getTextStart() - offset + adjustBlock, ifBlock.getEnd() - 1 - offset - adjustBlock),
                        bindings,
                        inputs,
                        ifBlock.getTextStart());
            } else if (block.getType() == JavaBlockDescr.BlockType.ELSE) {
                JavaElseBlockDescr elseBlock = (JavaElseBlockDescr) block;
                int adjustBlock = (originalCode.charAt(elseBlock.getTextStart() - offset - 1) == '{') ? 0 : 1;
                setContainerBlockInputs(context,
                        descrs,
                        elseBlock,
                        originalCode.substring(elseBlock.getTextStart() - offset + adjustBlock, elseBlock.getEnd() - 1 - offset - adjustBlock),
                        bindings,
                        inputs,
                        elseBlock.getTextStart());
            } else if (block.getType() == JavaBlockDescr.BlockType.WHILE) {
                JavaWhileBlockDescr whileBlock = (JavaWhileBlockDescr) block;
                int adjustBlock = (originalCode.charAt(whileBlock.getTextStart() - offset - 1) == '{') ? 0 : 1;
                setContainerBlockInputs(context,
                        descrs,
                        whileBlock,
                        originalCode.substring(whileBlock.getTextStart() - offset + adjustBlock, whileBlock.getEnd() - 1 - offset - adjustBlock),
                        bindings,
                        inputs,
                        whileBlock.getTextStart());
            } else if (block.getType() == JavaBlockDescr.BlockType.FOR) {
                JavaForBlockDescr forBlock = (JavaForBlockDescr) block;
                int adjustBlock = (originalCode.charAt(forBlock.getTextStart() - offset - 1) == '{') ? 0 : 1;
                setContainerBlockInputs(context,
                        descrs,
                        forBlock,
                        originalCode.substring(forBlock.getStartParen() - offset, forBlock.getInitEnd() - offset) +
                                originalCode.substring(forBlock.getTextStart() - offset + adjustBlock, forBlock.getEnd() - 1 - offset - adjustBlock),
                        bindings,
                        inputs,
                        forBlock.getTextStart() - (forBlock.getInitEnd() - forBlock.getStartParen()));
            } else {
                block.setInputs(inputs); // each block to be rewritten now knows it's own variables
                descrs.add(block);
            }
        }
    }

    private static Map<String, Class<?>> getInputs(final RuleBuildContext context,
                                                   String code,
                                                   BoundIdentifiers bindings,
                                                   Map<String, Class<?>> parentVars) {
        MVELDialect mvel = (MVELDialect) context.getDialect("mvel");

        MVELAnalysisResult mvelAnalysis = null;
        try {
            mvelAnalysis = (MVELAnalysisResult) mvel.analyzeBlock(context,
                    code,
                    bindings,
                    parentVars,
                    "drools",
                    KnowledgeHelper.class);
        } catch (Exception e) {
            // swallow this as the error will be reported else where
        }

        return (mvelAnalysis != null) ? mvelAnalysis.getMvelVariables() : new HashMap<String, Class<?>>();
    }

    private static void addWhiteSpaces(String original, StringBuilder consequence, int start, int end) {
        for (int i = start; i < end; i++) {
            switch (original.charAt(i)) {
                case '\n':
                case '\r':
                case '\t':
                case ' ':
                    consequence.append(original.charAt(i));
                    break;
                default:
                    consequence.append(" ");
            }
        }
    }

    private static void stripTryDescr(String originalCode,
                                      StringBuilder consequence,
                                      JavaTryBlockDescr block,
                                      int offset) {

        addWhiteSpaces(originalCode, consequence, consequence.length(), block.getTextStart() - offset);
        addWhiteSpaces(originalCode, consequence, consequence.length(), block.getEnd() - offset);

        for (JavaCatchBlockDescr catchBlock : block.getCatches()) {

            addWhiteSpaces(originalCode, consequence, consequence.length(),
                    catchBlock.getTextStart() - offset);
            addWhiteSpaces(originalCode, consequence, consequence.length(),
                    catchBlock.getEnd() - offset);
        }

        if (block.getFinal() != null) {
            addWhiteSpaces(originalCode, consequence, consequence.length(), block.getFinal().getTextStart() - offset);
            addWhiteSpaces(originalCode, consequence, consequence.length(), block.getFinal().getEnd() - offset);
        }
    }

    private static void stripBlockDescr(String originalCode,
                                        StringBuilder consequence,
                                        JavaBlockDescr block,
                                        int offset) {

        addWhiteSpaces(originalCode, consequence, consequence.length(), block.getEnd() - offset);
    }

    @SuppressWarnings("unchecked")
    private static void rewriteInterfacePoint(final RuleBuildContext context,
                                              final String originalCode,
                                              final StringBuilder consequence,
                                              final JavaInterfacePointsDescr ep) {
        // rewriting it for proper exitPoints access
        consequence.append("drools.get");
        if (ep.getType() == JavaBlockDescr.BlockType.EXIT) {
            consequence.append("ExitPoint( ");
        } else if (ep.getType() == JavaBlockDescr.BlockType.ENTRY) {
            consequence.append("EntryPoint( ");
        } else if (ep.getType() == JavaBlockDescr.BlockType.CHANNEL) {
            consequence.append("Channel( ");
        } else {
            context.addError(new DescrBuildError(context.getParentDescr(),
                    context.getRuleDescr(),
                    ep,
                    "Unable to rewrite code block: " + ep + "\n"));

            return;
        }

        consequence.append(ep.getId());
        consequence.append(" )");

        // the following is a hack to preserve line breaks.
        String originalBlock = originalCode.substring(ep.getStart() - 1,
                ep.getEnd());
        int end = originalBlock.indexOf("]");
        addLineBreaks(consequence,
                originalBlock.substring(0,
                        end));
    }

    private static boolean rewriteDescr(final RuleBuildContext context,
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
                    Class<?> type = context.getDialect( "java" ).getPackageRegistry().getTypeResolver().resolveType( local.getRawType() );
                    for( IdentifierDescr id : local.getIdentifiers() ) {
                        localTypes.put( id.getIdentifier(), type );
                    }
                } catch ( ClassNotFoundException e ) {
                    context.addError(new DescrBuildError(context.getRuleDescr(),
                            context.getParentDescr(),
                            null,
                            "Unable to resolve type " + local.getRawType() + ":\n" + e.getMessage()));
                }
            }
        }

        MVELAnalysisResult mvelAnalysis = ( MVELAnalysisResult ) mvel.analyzeBlock( context,
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
           context.addError(new DescrBuildError(context.getParentDescr(),
                   context.getRuleDescr(),
                   originalCode,
                   "Unable to determine the resulting type of the expression: " + d.getTargetExpression() + "\n"));

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

        if ( declr == null ) {
           obj = "__obj__";
           consequence.append( retString );
           consequence.append( " " );
           consequence.append( obj);
           consequence.append( " = " );
           consequence.append( d.getTargetExpression() );
           consequence.append( "; " );
        }

        if ( declr == null || declr.isInternalFact() ) {
           consequence.append( "org.kie.api.runtime.rule.FactHandle " );
           consequence.append( obj );
           consequence.append( "__Handle2__ = drools.getFactHandle(" );
           consequence.append( obj );
           consequence.append( ");" );
        }

        // the following is a hack to preserve line breaks.
        String originalBlock = originalCode.substring( d.getStart() - 1, d.getEnd() );

        switch (d.getType()) {
            case MODIFY:
                rewriteModifyDescr(context, d, originalBlock, consequence, declr, obj);
                break;
            case UPDATE:
                rewriteUpdateDescr(context, d, consequence, declr, obj);
                break;
            case RETRACT:
                rewriteRetractDescr( context, d, consequence, declr, obj );
                break;
        }

        return declr != null;
    }

    private static void rewriteModifyDescr( RuleBuildContext context,
                                            JavaBlockDescr d,
                                            String originalBlock,
                                            StringBuilder consequence,
                                            Declaration declr,
                                            String obj ) {
        List<String> settableProperties = null;

        Class<?> typeClass = findModifiedClass(context, d, declr);
        TypeDeclaration typeDeclaration = typeClass == null ? null : context.getKnowledgeBuilder().getTypeDeclaration(typeClass);
        boolean isPropertyReactive = typeDeclaration != null && typeDeclaration.isPropertyReactive();
        if (isPropertyReactive) {
            typeDeclaration.setTypeClass(typeClass);
            settableProperties = typeDeclaration.getSettableProperties();
        }

        ConsequenceMetaData.Statement statement = null;
        if (typeDeclaration != null) {
            statement = new ConsequenceMetaData.Statement(ConsequenceMetaData.Statement.Type.MODIFY, typeClass);
            context.getRule().getConsequenceMetaData().addStatement(statement);
        }
        BitMask modificationMask = isPropertyReactive ? getEmptyPropertyReactiveMask(settableProperties.size()) : allSetButTraitBitMask();

        int end = originalBlock.indexOf("{");
        if (end == -1) {
            // no block
            context.addError(new DescrBuildError(context.getParentDescr(),
                    context.getRuleDescr(),
                    null,
                    "Block missing after modify" + d.getTargetExpression() + " ?\n"));
            return;
        }

        addLineBreaks(consequence, originalBlock.substring(0, end));

        int start = end + 1;
        // adding each of the expressions:
        for (String exprStr : ((JavaModifyBlockDescr) d).getExpressions()) {
            end = originalBlock.indexOf(exprStr, start);
            addLineBreaks(consequence, originalBlock.substring(start, end));
            consequence.append(obj).append(".");
            consequence.append(exprStr);
            consequence.append("; ");
            start = end + exprStr.length();

            if (typeDeclaration != null) {
                modificationMask = parseModifiedProperties(statement, settableProperties, typeDeclaration, isPropertyReactive, modificationMask, exprStr);
            }
        }

        addLineBreaks(consequence, originalBlock.substring(end));

        appendUpdateStatement(consequence, declr, obj, modificationMask, typeClass);
    }

    private static void rewriteUpdateDescr( RuleBuildContext context,
                                            JavaBlockDescr d,
                                            StringBuilder consequence,
                                            Declaration declr,
                                            String obj) {
        BitMask modificationMask = AllSetBitMask.get();

        Class<?> typeClass = findModifiedClass(context, d, declr);
        TypeDeclaration typeDeclaration = typeClass == null ? null : context.getKnowledgeBuilder().getTypeDeclaration(typeClass);

        if (typeDeclaration != null) {
            boolean isPropertyReactive = typeDeclaration.isPropertyReactive();
            List<String> settableProperties = null;
            if (isPropertyReactive) {
                typeDeclaration.setTypeClass(typeClass);
                settableProperties = typeDeclaration.getSettableProperties();
                modificationMask = getEmptyPropertyReactiveMask(settableProperties.size());
            }

            ConsequenceMetaData.Statement statement = new ConsequenceMetaData.Statement(ConsequenceMetaData.Statement.Type.MODIFY, typeClass);
            context.getRule().getConsequenceMetaData().addStatement(statement);

            for (String expr : splitStatements(consequence)) {
                String updateExpr = expr.replaceFirst("^\\Q" + obj + "\\E\\s*\\.", "");
                if (!updateExpr.equals(expr)) {
                    modificationMask = parseModifiedProperties(statement, settableProperties, typeDeclaration, isPropertyReactive, modificationMask, updateExpr);
                }
            }
        }

        appendUpdateStatement(consequence, declr, obj, modificationMask, typeClass);
    }

    private static void appendUpdateStatement(StringBuilder consequence, Declaration declr, String obj, BitMask modificationMask, Class<?> typeClass) {
        boolean isInternalFact = declr == null || declr.isInternalFact();
        consequence
                .append("drools.update( ")
                .append(obj)
                .append(isInternalFact ? "__Handle2__, " : "__Handle__, ")
                .append(modificationMask.getInstancingStatement())
                .append(", ")
                .append(typeClass != null ? typeClass.getCanonicalName() : "java.lang.Object")
                .append(".class")
                .append(" ); }");
    }

    private static BitMask parseModifiedProperties( ConsequenceMetaData.Statement statement,
                                                    List<String> settableProperties,
                                                    TypeDeclaration typeDeclaration,
                                                    boolean propertyReactive,
                                                    BitMask modificationMask,
                                                    String exprStr) {
        int endMethodName = exprStr.indexOf('(');
        if (endMethodName >= 0) {
            String methodName = exprStr.substring(0, endMethodName).trim();
            String propertyName = setter2property(methodName);

            int endMethodArgs = findEndOfMethodArgsIndex(exprStr, endMethodName);
            String methodParams = exprStr.substring(endMethodName+1, endMethodArgs).trim();
            List<String> args = splitArgumentsList(methodParams);
            int argsNr = args.size();

            if (propertyName == null && exprStr.length() > endMethodArgs+1 && exprStr.substring(endMethodArgs+1).trim().startsWith(".")) {
                propertyName = getter2property(methodName);
            }

            if (propertyName != null) {
                modificationMask = updateModificationMask(settableProperties, propertyReactive, modificationMask, propertyName);
                statement.addField(propertyName, argsNr > 0 ? args.get(0) : null);
            }

            String methodWithArgsNr = methodName + "_" + argsNr;
            List<String> modifiedProps = typeDeclaration.getTypeClassDef().getModifiedPropsByMethod(methodWithArgsNr);
            if (modifiedProps != null) {
                for (String modifiedProp : modifiedProps) {
                    modificationMask = updateModificationMask(settableProperties, propertyReactive, modificationMask, modifiedProp);
                    statement.addField(modifiedProp, argsNr > 0 ? args.get(0) : null);
                }
            }
        } else {
            String propertyName = extractFirstIdentifier(exprStr, 0);
            modificationMask = updateModificationMask(settableProperties, propertyReactive, modificationMask, propertyName);
            int equalPos = exprStr.indexOf('=');
            if (equalPos >= 0) {
                String value = exprStr.substring(equalPos+1).trim();
                statement.addField(propertyName, value);
            }
        }
        return modificationMask;
    }

    private static BitMask updateModificationMask( List<String> settableProperties,
                                                   boolean propertyReactive,
                                                   BitMask modificationMask,
                                                   String propertyName) {
        if (propertyReactive) {
            int index = settableProperties.indexOf(propertyName);
            if (index >= 0) {
                modificationMask = setPropertyOnMask(modificationMask, index);
            }
        }
        return modificationMask;
    }

    private static Class<?> findModifiedClass(RuleBuildContext context, JavaBlockDescr d, Declaration declr) {
        if (declr != null) {
            return ((ClassWireable) declr.getPattern().getObjectType()).getClassType();
        }

        String targetId = d.getTargetExpression().trim();
        while (targetId.charAt(0) == '(' && targetId.charAt(targetId.length()-1) == ')') {
            targetId = targetId.substring(1, targetId.length()-1).trim();
        }

        if (targetId.charAt(0) == '(') {
            int endCast = targetId.indexOf(')');
            if (endCast > 0) {
                String castName = targetId.substring(1, endCast).trim();
                Class<?> cast = findClassByName(context, castName);
                if (cast != null) {
                    return cast;
                }
                targetId = targetId.substring(endCast+1).trim();
            }
        }

        return targetId.contains("(") ? findFunctionReturnedClass(context, targetId) : findDeclarationClass(context, d, targetId);
    }

    private static Class<?> findDeclarationClass(RuleBuildContext context, JavaBlockDescr d, String statement) {
        Class<?> inputClass = d.getInputs() == null ? null : d.getInputs().get(statement);
        if (inputClass != null) {
            return inputClass;
        }

        List<JavaLocalDeclarationDescr> localDeclarationDescrs = d.getInScopeLocalVars();
        if (localDeclarationDescrs == null) {
            return null;
        }

        String className = null;
        for (JavaLocalDeclarationDescr localDeclr : localDeclarationDescrs) {
            for (IdentifierDescr idDescr : localDeclr.getIdentifiers()) {
                if (statement.equals(idDescr.getIdentifier())) {
                    className = localDeclr.getType();
                    break;
                }
            }
            if (className != null) {
                break;
            }
        }

        return findClassByName(context, className);
    }

    public static Class<?> findClassByName(RuleBuildContext context, String className) {
        if (className == null) {
            return null;
        }

        String namespace = context.getRuleDescr().getNamespace();
        KnowledgeBuilderImpl packageBuilder = context.getKnowledgeBuilder();

        Class<?> clazz = null;
        try {
            clazz = Class.forName(className.indexOf('.') < 0 ? namespace + "." + className : className, false, packageBuilder.getRootClassLoader());
        } catch (ClassNotFoundException e) { }

        if (clazz != null) {
            return clazz;
        }

        Set<String> imports = new HashSet<String>();
        List<PackageDescr> pkgDescrs = packageBuilder.getPackageDescrs(namespace);
        if (pkgDescrs == null) {
            return null;
        }
        for (PackageDescr pkgDescr : pkgDescrs) {
            for (ImportDescr importDescr : pkgDescr.getImports()) {
                imports.add(importDescr.getTarget());
            }
        }
        return findClass(className, imports, packageBuilder.getRootClassLoader());
    }

    private static Class<?> findFunctionReturnedClass(RuleBuildContext context, String statement) {
        String functionName = statement.substring(0, statement.indexOf('('));
        FunctionDescr function = lookupFunction(context, functionName);
        return function == null ? null : findClassByName(context, function.getReturnType());
    }

    private static boolean rewriteRetractDescr(RuleBuildContext context,
                                               JavaBlockDescr d,
                                               StringBuilder consequence,
                                               Declaration declr,
                                               String obj) {
        Class<?> typeClass = findModifiedClass(context, d, declr);
        if (typeClass != null) {
            ConsequenceMetaData.Statement statement = new ConsequenceMetaData.Statement(ConsequenceMetaData.Statement.Type.RETRACT, typeClass);
            context.getRule().getConsequenceMetaData().addStatement(statement);
        }

        if (declr != null && !declr.isInternalFact()) {
            consequence.append("drools.retract( ").append(obj).append("__Handle__ ); }");
        } else {
            consequence.append("drools.retract( ").append(obj).append("__Handle2__ ); }");
        }

        return declr != null;
    }

    private static void parseInsertDescr(RuleBuildContext context, JavaBlockDescr block) {
        String expr = block.getTargetExpression();
        if (expr.startsWith("new ")) {
            int argsStart = expr.indexOf('(');
            if (argsStart > 0) {
                String className = expr.substring(4, argsStart).trim();
                Class<?> typeClass = findClassByName(context, className);
                TypeDeclaration typeDeclaration = typeClass == null ? null : context.getKnowledgeBuilder().getTypeDeclaration(typeClass);
                if (typeDeclaration != null) {
                    ConsequenceMetaData.Statement statement = new ConsequenceMetaData.Statement(ConsequenceMetaData.Statement.Type.INSERT, typeClass);
                    context.getRule().getConsequenceMetaData().addStatement(statement);

                    String constructorParams = expr.substring(argsStart+1, expr.indexOf(')')).trim();
                    List<String> args = splitArgumentsList(constructorParams);
                    ClassDefinition classDefinition = typeDeclaration.getTypeClassDef();
                    List<FactField> fields = classDefinition.getFields();
                    if (args.size() == fields.size()) {
                        for (int i = 0; i < args.size(); i++) {
                            statement.addField(fields.get(i).getName(), args.get(i));
                        }
                    }
                }
            }
        }
    }

    private static void addLineBreaks(StringBuilder consequence,
                                      String chunk) {
        Matcher m = LINE_BREAK_FINDER.matcher(chunk);
        while (m.find()) {
            consequence.append("\n");
        }
    }

    public static void copyErrorLocation(Exception e, BaseDescr descr) {
        if (e instanceof CompileException) {
            CompileException compileException = (CompileException)e;
            compileException.setLineNumber(descr.getLine());
            compileException.setColumn(descr.getColumn());
        }
    }

    private static FunctionDescr lookupFunction(RuleBuildContext context, String functionName) {
        String packageName = context.getRule().getPackageName();
        List<PackageDescr> pkgDescrs = context.getKnowledgeBuilder().getPackageDescrs(packageName);
        for (PackageDescr pkgDescr : pkgDescrs) {
            for (FunctionDescr function : pkgDescr.getFunctions()) {
                if (function.getName().equals(functionName)) {
                    return function;
                }
            }
        }
        return null;
    }

    static String normalizeRuleName(String name) {
        String normalized = name.replace(' ', '_');
        if (!NON_ALPHA_REGEX.matcher(normalized).find()) {
            return normalized;
        }
        StringBuilder sb = new StringBuilder(normalized.length());
        for (char ch : normalized.toCharArray()) {
            if (ch == '$') {
                sb.append("_dollar_");
            } else if (Character.isJavaIdentifierPart(ch)) {
                sb.append(ch);
            } else {
                sb.append("$u").append((int)ch).append("$");
            }
        }
        return sb.toString();
    }
}
