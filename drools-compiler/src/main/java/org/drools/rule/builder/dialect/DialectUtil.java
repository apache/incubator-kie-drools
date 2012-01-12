package org.drools.rule.builder.dialect;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.drools.base.ClassObjectType;
import org.drools.commons.jci.readers.*;
import org.drools.compiler.BoundIdentifiers;
import org.drools.compiler.DescrBuildError;
import org.drools.core.util.BitMaskUtil;
import org.drools.core.util.ClassUtils;
import org.drools.lang.descr.BaseDescr;
import org.drools.rule.Declaration;
import org.drools.rule.builder.RuleBuildContext;
import org.drools.rule.builder.dialect.java.*;
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
import org.drools.rule.builder.dialect.java.parser.JavaRetractBlockDescr;
import org.drools.rule.builder.dialect.java.parser.JavaUpdateBlockDescr;
import org.drools.rule.builder.dialect.mvel.MVELAnalysisResult;
import org.drools.rule.builder.dialect.mvel.MVELConsequenceBuilder;
import org.drools.rule.builder.dialect.mvel.MVELDialect;
import org.drools.spi.KnowledgeHelper;
import org.mvel2.CompileException;
import org.mvel2.Macro;
import org.mvel2.MacroProcessor;

import static org.drools.core.util.ClassUtils.getSettableProperties;
import static org.drools.core.util.ClassUtils.setter2property;
import static org.drools.core.util.StringUtils.generateUUID;

public final class DialectUtil {

    private static final Pattern NON_ALPHA_REGEX = Pattern.compile("[ -/:-@\\[-`\\{-\\xff]");
    private static final Pattern LINE_BREAK_FINDER = Pattern.compile( "\\r\\n|\\r|\\n" );

    /**
     * Takes a given name and makes sure that its legal and doesn't already exist. If the file exists it increases counter appender untill it is unique.
     * <p/>
     *
     * @param packageName
     * @param name
     * @param ext
     * @return
     */
    public static String getUniqueLegalName(final String packageName,
                                            final String name,
                                            final String ext,
                                            final String prefix,
                                            final ResourceReader src) {
        // replaces all non alphanumeric or $ chars with _
        final String newName = prefix + "_" + NON_ALPHA_REGEX.matcher(name).replaceAll("_");
        if (ext.equals("java")) return newName + "_" + generateUUID();

        final String fileName = packageName.replace('.', '/') + "/" + newName;

        if (src == null || !src.isAvailable(fileName + "." + ext)) return newName;

        // make sure the class name does not exist, if it does increase the counter
        int counter = -1;
        while (true) {

            counter++;
            final String actualName = fileName + "_" + counter + "." + ext;

            //MVEL:test null to Fix failing test on org.drools.rule.builder.dialect.mvel.MVELConsequenceBuilderTest.testImperativeCodeError()
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
            if (block.getEnd() == 0) {
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
                            (JavaBlockDescr) block,
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
                default:
                    consequence.append(originalCode.substring(block.getStart() - 1,
                            lastAdded));
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
     *
     * @param context
     * @param descrs
     * @param parentBlock
     * @param originalCode
     * @param bindings
     * @param parentVars
     * @param offset
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

                stripTryDescr(context,
                        originalCode,
                        consequence,
                        (JavaTryBlockDescr) block,
                        offset);
            } else if (block.getType() == JavaBlockDescr.BlockType.THROW) {
                // adding previous chunk up to the start of this block
                consequence.append(originalCode.substring(lastAdded,
                        block.getStart() - 1 - offset));

                JavaThrowBlockDescr throwBlock = (JavaThrowBlockDescr) block;
                addWhiteSpaces(originalCode, consequence, throwBlock.getStart() - offset, throwBlock.getTextStart() - offset);
                consequence.append(originalCode.substring(throwBlock.getTextStart() - offset - 1, throwBlock.getEnd() - 1 - offset) + ";");
                lastAdded = throwBlock.getEnd() - offset;
            } else if (block.getType() == JavaBlockDescr.BlockType.IF) {
                // adding previous chunk up to the start of this block
                consequence.append(originalCode.substring(lastAdded,
                        block.getStart() - 1 - offset));
                JavaIfBlockDescr ifDescr = (JavaIfBlockDescr) block;
                lastAdded = ifDescr.getEnd() - offset;
                stripBlockDescr(context,
                        originalCode,
                        consequence,
                        ifDescr,
                        offset);
            } else if (block.getType() == JavaBlockDescr.BlockType.ELSE) {
                // adding previous chunk up to the start of this block
                consequence.append(originalCode.substring(lastAdded,
                        block.getStart() - 1 - offset));
                JavaElseBlockDescr elseDescr = (JavaElseBlockDescr) block;
                lastAdded = elseDescr.getEnd() - offset;
                stripBlockDescr(context,
                        originalCode,
                        consequence,
                        elseDescr,
                        offset);
            } else if (block.getType() == JavaBlockDescr.BlockType.WHILE) {
                // adding previous chunk up to the start of this block
                consequence.append(originalCode.substring(lastAdded,
                        block.getStart() - 1 - offset));
                JavaWhileBlockDescr whileDescr = (JavaWhileBlockDescr) block;
                lastAdded = whileDescr.getEnd() - offset;
                stripBlockDescr(context,
                        originalCode,
                        consequence,
                        whileDescr,
                        offset);
            } else if (block.getType() == JavaBlockDescr.BlockType.FOR) {
                // adding previous chunk up to the start of this block
                consequence.append(originalCode.substring(lastAdded,
                        block.getStart() - 1 - offset));
                JavaForBlockDescr forDescr = (JavaForBlockDescr) block;
                lastAdded = forDescr.getEnd() - offset;
                stripBlockDescr(context,
                        originalCode,
                        consequence,
                        forDescr,
                        offset);
            }
        }
        consequence.append(originalCode.substring(lastAdded));

        // We need to do this as MVEL doesn't recognise "modify"
        MacroProcessor macroProcessor = new MacroProcessor();
        Map macros = new HashMap(MVELConsequenceBuilder.macros);
        macros.put("modify",
                new Macro() {
                    public String doMacro() {
                        return "with  ";
                    }
                });
        macroProcessor.setMacros(macros);
        String mvelCode = macroProcessor.parse(consequence.toString());


        Map<String, Class<?>> inputs = (Map<String, Class<?>>) (Map) getInputs(context, mvelCode, bindings, parentVars);
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
                    JavaFinalBlockDescr finalBlock = (JavaFinalBlockDescr) tryBlock.getFinal();
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
                    context.getRuleDescr(),
                    null,
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

    private static void stripTryDescr(RuleBuildContext context,
                                      String originalCode,
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

    private static void stripBlockDescr(RuleBuildContext context,
                                        String originalCode,
                                        StringBuilder consequence,
                                        JavaBlockDescr block,
                                        int offset) {

        addWhiteSpaces(originalCode, consequence, consequence.length(), block.getEnd() - offset);
    }

    private static void stripElseDescr(RuleBuildContext context,
                                       String originalCode,
                                       StringBuilder consequence,
                                       JavaElseBlockDescr block,
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
            context.getErrors().add(new DescrBuildError(context.getParentDescr(),
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
               consequence.append( "org.drools.FactHandle " );
               consequence.append( obj );
               consequence.append( "__Handle2__ = drools.getFactHandle(" );
               consequence.append( obj );
               consequence.append( ");" );
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

    private static void rewriteModifyDescr(final RuleBuildContext context,
                                              JavaBlockDescr d,
                                              String originalBlock,
                                              StringBuilder consequence,
                                              Declaration declr,
                                              String obj) {
        boolean isInternalFact = declr != null && !declr.isInternalFact();
        long modificationMask = isInternalFact ? 0 : Long.MAX_VALUE;

        int end = originalBlock.indexOf("{");
        if (end == -1) {
            // no block
            context.getErrors().add(new DescrBuildError(context.getParentDescr(),
                    context.getRuleDescr(),
                    null,
                    "Block missing after modify" + d.getTargetExpression() + " ?\n"));
            return;
        }

        addLineBreaks(consequence, originalBlock.substring(0, end));

        List<String> settableProperties = isInternalFact ?
                getSettableProperties(((ClassObjectType) declr.getPattern().getObjectType()).getClassType()) :
                null;

        int start = end + 1;
        // adding each of the expressions:
        for (String exprStr : ((JavaModifyBlockDescr) d).getExpressions()) {
            end = originalBlock.indexOf(exprStr, start);
            addLineBreaks(consequence, originalBlock.substring(start, end));
            consequence.append(obj + ".");
            consequence.append(exprStr);
            consequence.append("; ");
            start = end + exprStr.length();

            if (isInternalFact) {
                int endMethodName = exprStr.indexOf('(');
                String methodName = exprStr.substring(0, endMethodName).trim();
                String propertyName = setter2property(methodName);
                if (propertyName != null) {
                    int pos = settableProperties.indexOf(propertyName);
                    modificationMask = BitMaskUtil.set(modificationMask, pos);
                } else {
                    // Invocation of a non-setter => cannot calculate the mask
                    modificationMask = Long.MAX_VALUE;
                }
            }
        }

        // adding the modifyInsert call:
        addLineBreaks(consequence, originalBlock.substring(end));

        consequence
                .append("drools.update( ")
                .append(obj)
                .append(isInternalFact ? "__Handle__, " : "__Handle2__, ")
                .append(modificationMask)
                .append("L ); }");
    }

    private static boolean rewriteUpdateDescr(JavaBlockDescr d,
                                              String originalBlock,
                                              StringBuilder consequence,
                                              Declaration declr,
                                              String obj) {
        if (declr != null && !declr.isInternalFact()) {
            consequence.append("drools.update( " + obj + "__Handle__ ); }");
        } else {
            consequence.append("drools.update( " + obj + "__Handle2__ ); }");
        }

        return declr != null;
    }

    private static boolean rewriteRetractDescr(JavaBlockDescr d,
                                               String originalBlock,
                                               StringBuilder consequence,
                                               Declaration declr,
                                               String obj) {
        if (declr != null && !declr.isInternalFact()) {
            consequence.append("drools.retract( " + obj + "__Handle__ ); }");
        } else {
            consequence.append("drools.retract( " + obj + "__Handle2__ ); }");
        }

        return declr != null;
    }

    /**
     * @param consequence
     * @param chunk
     */
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
}
