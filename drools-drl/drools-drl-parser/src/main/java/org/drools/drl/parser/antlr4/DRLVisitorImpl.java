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
package org.drools.drl.parser.antlr4;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.RuleNode;
import org.drools.drl.ast.descr.AccumulateDescr;
import org.drools.drl.ast.descr.AccumulateImportDescr;
import org.drools.drl.ast.descr.AndDescr;
import org.drools.drl.ast.descr.AnnotationDescr;
import org.drools.drl.ast.descr.AttributeDescr;
import org.drools.drl.ast.descr.BaseDescr;
import org.drools.drl.ast.descr.BehaviorDescr;
import org.drools.drl.ast.descr.CollectDescr;
import org.drools.drl.ast.descr.EntryPointDeclarationDescr;
import org.drools.drl.ast.descr.EntryPointDescr;
import org.drools.drl.ast.descr.EvalDescr;
import org.drools.drl.ast.descr.ExistsDescr;
import org.drools.drl.ast.descr.ExprConstraintDescr;
import org.drools.drl.ast.descr.ForallDescr;
import org.drools.drl.ast.descr.FromDescr;
import org.drools.drl.ast.descr.FunctionDescr;
import org.drools.drl.ast.descr.FunctionImportDescr;
import org.drools.drl.ast.descr.GlobalDescr;
import org.drools.drl.ast.descr.ImportDescr;
import org.drools.drl.ast.descr.MVELExprDescr;
import org.drools.drl.ast.descr.NotDescr;
import org.drools.drl.ast.descr.OrDescr;
import org.drools.drl.ast.descr.PackageDescr;
import org.drools.drl.ast.descr.PatternDescr;
import org.drools.drl.ast.descr.PatternSourceDescr;
import org.drools.drl.ast.descr.QueryDescr;
import org.drools.drl.ast.descr.RuleDescr;
import org.drools.drl.ast.descr.TypeDeclarationDescr;
import org.drools.drl.ast.descr.TypeFieldDescr;
import org.drools.drl.ast.descr.UnitDescr;
import org.drools.drl.ast.descr.WindowDeclarationDescr;
import org.drools.drl.ast.descr.WindowReferenceDescr;

import static org.drools.drl.parser.antlr4.Antlr4ParserStringUtils.getTextPreservingWhitespace;
import static org.drools.drl.parser.antlr4.Antlr4ParserStringUtils.getTokenTextPreservingWhitespace;
import static org.drools.drl.parser.antlr4.Antlr4ParserStringUtils.trimThen;
import static org.drools.drl.parser.antlr4.DRLParserHelper.getTextWithoutErrorNode;
import static org.drools.drl.parser.util.ParserStringUtils.appendPrefix;
import static org.drools.drl.parser.util.ParserStringUtils.safeStripStringDelimiters;
import static org.drools.util.StringUtils.unescapeJava;

/**
 * Visitor implementation for DRLParser.
 * Basically, each visit method creates and returns a Descr object traversing the parse tree.
 * Finally, visitCompilationUnit() returns a PackageDescr object.
 * Try not to depend on DRLVisitorImpl's internal state for clean maintainability
 */
public class DRLVisitorImpl extends DRLParserBaseVisitor<Object> {

    private final TokenStream tokenStream;

    public DRLVisitorImpl(TokenStream tokenStream) {
        this.tokenStream = tokenStream;
    }

    /**
     * Main entry point for creating PackageDescr from a parser tree.
     */
    @Override
    public PackageDescr visitCompilationUnit(DRLParser.CompilationUnitContext ctx) {
        PackageDescr packageDescr = DescrHelper.init(new PackageDescr(), ctx);
        if (ctx.packagedef() != null) {
            packageDescr.setName(getTextWithoutErrorNode(ctx.packagedef().name));
        }
        List<BaseDescr> descrList = visitDescrChildren(ctx);
        applyChildrenDescrs(packageDescr, descrList);
        return packageDescr;
    }

    /**
     * Add all children Descr to PackageDescr
     */
    private void applyChildrenDescrs(PackageDescr packageDescr, List<BaseDescr> descrList) {
        // This bunch of if-blocks will be refactored by DROOLS-7564
        descrList.forEach(descr -> {
            if (descr instanceof UnitDescr) {
                packageDescr.setUnit((UnitDescr) descr);
            } else if (descr instanceof GlobalDescr) {
                packageDescr.addGlobal((GlobalDescr) descr);
            } else if (descr instanceof FunctionImportDescr) {
                packageDescr.addFunctionImport((FunctionImportDescr) descr);
            } else if (descr instanceof AccumulateImportDescr) {
                packageDescr.addAccumulateImport((AccumulateImportDescr) descr);
            } else if (descr instanceof ImportDescr) {
                packageDescr.addImport((ImportDescr) descr);
            } else if (descr instanceof FunctionDescr) {
                FunctionDescr functionDescr = (FunctionDescr) descr;
                functionDescr.setNamespace(packageDescr.getNamespace());
                AttributeDescr dialect = packageDescr.getAttribute("dialect");
                if (dialect != null) {
                    functionDescr.setDialect(dialect.getValue());
                }
                packageDescr.addFunction(functionDescr);
            } else if (descr instanceof TypeDeclarationDescr) {
                packageDescr.addTypeDeclaration((TypeDeclarationDescr) descr);
            } else if (descr instanceof EntryPointDeclarationDescr) {
                packageDescr.addEntryPointDeclaration((EntryPointDeclarationDescr) descr);
            } else if (descr instanceof WindowDeclarationDescr) {
                packageDescr.addWindowDeclaration((WindowDeclarationDescr) descr);
            } else if (descr instanceof AttributeDescr) {
                packageDescr.addAttribute((AttributeDescr) descr);
            } else if (descr instanceof RuleDescr) { // QueryDescr extends RuleDescr
                RuleDescr ruleDescr = (RuleDescr) descr;
                packageDescr.addRule(ruleDescr);
                packageDescr.afterRuleAdded(ruleDescr);
                ruleDescr.setNamespace(packageDescr.getNamespace());
            }
        });
    }

    @Override
    public UnitDescr visitUnitdef(DRLParser.UnitdefContext ctx) {
        return DescrHelper.init(new UnitDescr(ctx.name.getText()), ctx);
    }

    @Override
    public GlobalDescr visitGlobaldef(DRLParser.GlobaldefContext ctx) {
        return DescrHelper.init(new GlobalDescr(ctx.drlIdentifier().getText(), ctx.type().getText()), ctx);
    }

    @Override
    public ImportDescr visitImportStandardDef(DRLParser.ImportStandardDefContext ctx) {
        String target = ctx.drlQualifiedName().getText() + (ctx.MUL() != null ? ".*" : "");
        if (ctx.DRL_FUNCTION() != null || ctx.STATIC() != null) {
            FunctionImportDescr functionImportDescr = DescrHelper.init(new FunctionImportDescr(), ctx);
            functionImportDescr.setTarget(target);
            return functionImportDescr;
        } else {
            ImportDescr importDescr = DescrHelper.init(new ImportDescr(), ctx);
            importDescr.setTarget(target);
            return importDescr;
        }
    }

    @Override
    public AccumulateImportDescr visitImportAccumulateDef(DRLParser.ImportAccumulateDefContext ctx) {
        AccumulateImportDescr accumulateImportDescr = DescrHelper.init(new AccumulateImportDescr(), ctx);
        accumulateImportDescr.setTarget(ctx.drlQualifiedName().getText());
        accumulateImportDescr.setFunctionName(ctx.IDENTIFIER().getText());
        return accumulateImportDescr;
    }

    @Override
    public FunctionDescr visitFunctiondef(DRLParser.FunctiondefContext ctx) {
        FunctionDescr functionDescr = DescrHelper.init(new FunctionDescr(), ctx);
        if (ctx.typeTypeOrVoid() != null) {
            functionDescr.setReturnType(ctx.typeTypeOrVoid().getText());
        } else {
            functionDescr.setReturnType("void");
        }
        functionDescr.setName(ctx.IDENTIFIER().getText());

        // add function parameters
        DRLParser.FormalParametersContext formalParametersContext = ctx.formalParameters();
        DRLParser.FormalParameterListContext formalParameterListContext = formalParametersContext.formalParameterList();
        if (formalParameterListContext != null) {
            List<DRLParser.FormalParameterContext> formalParameterContexts = formalParameterListContext.formalParameter();
            formalParameterContexts.forEach(formalParameterContext -> {
                DRLParser.TypeTypeContext typeTypeContext = formalParameterContext.typeType();
                DRLParser.VariableDeclaratorIdContext variableDeclaratorIdContext = formalParameterContext.variableDeclaratorId();
                functionDescr.addParameter(typeTypeContext.getText(), variableDeclaratorIdContext.getText());
            });
        }
        functionDescr.setBody(getTextPreservingWhitespace(ctx.drlBlock().drlBlockStatement()));
        return functionDescr;
    }

    @Override
    public BaseDescr visitDeclaredef(DRLParser.DeclaredefContext ctx) {
        return visitDescrChildren(ctx).get(0); // only one child
    }

    @Override
    public TypeDeclarationDescr visitTypeDeclaration(DRLParser.TypeDeclarationContext ctx) {
        TypeDeclarationDescr typeDeclarationDescr = DescrHelper.init(new TypeDeclarationDescr(ctx.name.getText()), ctx);
        if (ctx.DRL_TRAIT() != null) {
            typeDeclarationDescr.setTrait(true);
        }
        if (ctx.EXTENDS() != null) {
            typeDeclarationDescr.addSuperType(ctx.superType.getText());
        }
        ctx.drlAnnotation().stream()
                .map(this::visitDrlAnnotation)
                .forEach(typeDeclarationDescr::addAnnotation);
        ctx.field().stream()
                .map(this::visitField)
                .forEach(typeDeclarationDescr::addField);
        return typeDeclarationDescr;
    }

    @Override
    public EntryPointDeclarationDescr visitEntryPointDeclaration(DRLParser.EntryPointDeclarationContext ctx) {
        EntryPointDeclarationDescr entryPointDeclarationDescr = DescrHelper.init(new EntryPointDeclarationDescr(), ctx);
        entryPointDeclarationDescr.setEntryPointId(ctx.name.getText());
        ctx.drlAnnotation().stream()
                .map(this::visitDrlAnnotation)
                .forEach(entryPointDeclarationDescr::addAnnotation);
        return entryPointDeclarationDescr;
    }

    @Override
    public WindowDeclarationDescr visitWindowDeclaration(DRLParser.WindowDeclarationContext ctx) {
        WindowDeclarationDescr windowDeclarationDescr = DescrHelper.init(new WindowDeclarationDescr(), ctx);
        windowDeclarationDescr.setName(ctx.name.getText());
        ctx.drlAnnotation().stream()
                .map(this::visitDrlAnnotation)
                .forEach(windowDeclarationDescr::addAnnotation);
        windowDeclarationDescr.setPattern((PatternDescr) visitLhsPatternBind(ctx.lhsPatternBind()));
        return windowDeclarationDescr;
    }

    /**
     * entry point for one rule
     */
    @Override
    public RuleDescr visitRuledef(DRLParser.RuledefContext ctx) {
        RuleDescr ruleDescr = DescrHelper.init(new RuleDescr(safeStripStringDelimiters(ctx.name.getText())), ctx);

        if (ctx.EXTENDS() != null) {
            ruleDescr.setParentName(safeStripStringDelimiters(ctx.parentName.getText()));
        }

        ctx.drlAnnotation().stream().map(this::visitDrlAnnotation).forEach(ruleDescr::addAnnotation);

        if (ctx.attributes() != null) {
            List<BaseDescr> descrList = visitDescrChildren(ctx.attributes());
            descrList.stream()
                    .filter(AttributeDescr.class::isInstance)
                    .map(AttributeDescr.class::cast)
                    .forEach(ruleDescr::addAttribute);
        }

        if (ctx.lhs() != null) {
            final AndDescr rootDescr = ruleDescr.getLhs();
            List<BaseDescr> lhsDescrList = visitLhs(ctx.lhs());
            lhsDescrList.forEach(descr -> rootDescr.addDescr(descr));
            slimLhsRootDescr(rootDescr);
            DescrHelper.refreshRootProperties(rootDescr);
        }

        if (ctx.rhs() != null) {
            ruleDescr.setConsequenceLocation(ctx.rhs().getStart().getLine(), ctx.rhs().getStart().getCharPositionInLine()); // location of "then"
            ruleDescr.setConsequence(trimThen(getTokenTextPreservingWhitespace(ctx.rhs(), tokenStream))); // RHS is just a text
        }

        return ruleDescr;
    }

    private void slimLhsRootDescr(AndDescr root) {
        // Root Descr is always AndDescr.
        // For example, if there are nested AndDescr like
        //  AndDescr
        //  /\
        // P  AndDescr
        //     /\
        //    P  P
        // is slimmed down to
        //  AndDescr
        //  / | \
        // P  P  P
        List<BaseDescr> descrList = new ArrayList<>(root.getDescrs());
        root.getDescrs().clear();
        descrList.forEach(root::addOrMerge);
    }

    @Override
    public QueryDescr visitQuerydef(DRLParser.QuerydefContext ctx) {
        QueryDescr queryDescr = DescrHelper.init(new QueryDescr(safeStripStringDelimiters(ctx.name.getText())), ctx);

        DRLParser.ParametersContext parametersContext = ctx.parameters();
        if (parametersContext != null) {
            List<DRLParser.ParameterContext> parameterContexts = parametersContext.parameter();
            parameterContexts.forEach(parameterContext -> {
                String type = parameterContext.type() != null ? parameterContext.type().getText() : "Object"; // default type is Object
                String name = parameterContext.drlIdentifier().getText();
                queryDescr.addParameter(type, name);
            });
        }

        ctx.drlAnnotation().stream().map(this::visitDrlAnnotation).forEach(queryDescr::addAnnotation);

        ctx.lhsExpression().stream()
                           .flatMap(lhsExpressionContext -> visitDescrChildren(lhsExpressionContext).stream())
                           .forEach(descr -> queryDescr.getLhs().addDescr(descr));

        slimLhsRootDescr(queryDescr.getLhs());

        return queryDescr;
    }

    @Override
    public AnnotationDescr visitDrlAnnotation(DRLParser.DrlAnnotationContext ctx) {
        AnnotationDescr annotationDescr = DescrHelper.init(new AnnotationDescr(ctx.name.getText()), ctx);
        if (ctx.drlElementValue() != null) {
            annotationDescr.setValue(getTextPreservingWhitespace(ctx.drlElementValue())); // single value
        } else if (ctx.drlElementValuePairs() != null) {
            visitDrlElementValuePairs(ctx.drlElementValuePairs(), annotationDescr); // multiple values
        } else if (ctx.chunk() != null) {
            // A chunk that is neither a single value nor a list of key-value pairs. For example `!*, age` in `@watch(!*, age)`.
            annotationDescr.setValue(getTextPreservingWhitespace(ctx.chunk()));
        }
        return annotationDescr;
    }

    @Override
    public TypeFieldDescr visitField(DRLParser.FieldContext ctx) {
        TypeFieldDescr typeFieldDescr = DescrHelper.init(new TypeFieldDescr(), ctx);
        typeFieldDescr.setFieldName(ctx.label().drlIdentifier().getText());
        typeFieldDescr.setPattern(new PatternDescr(ctx.type().getText()));
        if (ctx.ASSIGN() != null) {
            typeFieldDescr.setInitExpr(getTextPreservingWhitespace(ctx.initExpr));
        }
        ctx.drlAnnotation().stream()
                .map(this::visitDrlAnnotation)
                .forEach(typeFieldDescr::addAnnotation);
        return typeFieldDescr;
    }

    private void visitDrlElementValuePairs(DRLParser.DrlElementValuePairsContext ctx, AnnotationDescr annotationDescr) {
        ctx.drlElementValuePair().forEach(pairCtx -> {
            String key = pairCtx.key.getText();
            String value = getTextPreservingWhitespace(pairCtx.value);
            annotationDescr.setKeyValue(key, value);
        });
    }

    @Override
    public AttributeDescr visitExpressionAttribute(DRLParser.ExpressionAttributeContext ctx) {
        AttributeDescr attributeDescr = DescrHelper.init(new AttributeDescr(ctx.name.getText()), ctx);
        attributeDescr.setValue(getTextPreservingWhitespace(ctx.conditionalOrExpression()));
        attributeDescr.setType(AttributeDescr.Type.EXPRESSION);
        return attributeDescr;
    }

    @Override
    public AttributeDescr visitBooleanAttribute(DRLParser.BooleanAttributeContext ctx) {
        AttributeDescr attributeDescr = DescrHelper.init(new AttributeDescr(ctx.name.getText()), ctx);
        attributeDescr.setValue(ctx.BOOL_LITERAL() != null ? ctx.BOOL_LITERAL().getText() : "true");
        attributeDescr.setType(AttributeDescr.Type.BOOLEAN);
        return attributeDescr;
    }

    @Override
    public AttributeDescr visitStringAttribute(DRLParser.StringAttributeContext ctx) {
        AttributeDescr attributeDescr = DescrHelper.init(new AttributeDescr(ctx.name.getText()), ctx);
        attributeDescr.setValue(unescapeJava(safeStripStringDelimiters(ctx.DRL_STRING_LITERAL().getText())));
        attributeDescr.setType(AttributeDescr.Type.STRING);
        return attributeDescr;
    }

    @Override
    public AttributeDescr visitStringListAttribute(DRLParser.StringListAttributeContext ctx) {
        AttributeDescr attributeDescr = DescrHelper.init(new AttributeDescr(ctx.name.getText()), ctx);
        List<String> valueList = ctx.DRL_STRING_LITERAL().stream()
                .map(ParseTree::getText)
                .collect(Collectors.toList());
        attributeDescr.setValue(createStringList(valueList));
        attributeDescr.setType(AttributeDescr.Type.LIST);
        return attributeDescr;
    }

    private static String createStringList(List<String> valueList) {
        StringBuilder sb = new StringBuilder();
        sb.append("[ ");
        for (int i = 0; i < valueList.size(); i++) {
            sb.append(valueList.get(i));
            if (i < valueList.size() - 1) {
                sb.append(", ");
            }
        }
        sb.append(" ]");
        return sb.toString();
    }

    @Override
    public AttributeDescr visitIntOrChunkAttribute(DRLParser.IntOrChunkAttributeContext ctx) {
        AttributeDescr attributeDescr = DescrHelper.init(new AttributeDescr(ctx.name.getText()), ctx);
        if (ctx.DECIMAL_LITERAL() != null) {
            attributeDescr.setValue(ctx.DECIMAL_LITERAL().getText());
            attributeDescr.setType(AttributeDescr.Type.NUMBER);
        } else {
            attributeDescr.setValue(getTextPreservingWhitespace(ctx.chunk()));
            attributeDescr.setType(AttributeDescr.Type.EXPRESSION);
        }
        return attributeDescr;
    }

    @Override
    public AttributeDescr visitDurationAttribute(DRLParser.DurationAttributeContext ctx) {
        AttributeDescr attributeDescr = DescrHelper.init(new AttributeDescr(ctx.name.getText()), ctx);
        if (ctx.DECIMAL_LITERAL() != null) {
            attributeDescr.setValue(ctx.DECIMAL_LITERAL().getText());
            attributeDescr.setType(AttributeDescr.Type.NUMBER);
        } else {
            attributeDescr.setValue(unescapeJava(safeStripStringDelimiters(ctx.TIME_INTERVAL().getText())));
            attributeDescr.setType(AttributeDescr.Type.EXPRESSION);
        }
        return attributeDescr;
    }

    /**
     * entry point for LHS
     */
    @Override
    public List<BaseDescr> visitLhs(DRLParser.LhsContext ctx) {
        if (ctx.lhsExpression() != null) {
            return visitDescrChildren(ctx);
        } else {
            return new ArrayList<>();
        }
    }

    @Override
    public BaseDescr visitLhsPatternBind(DRLParser.LhsPatternBindContext ctx) {
        if (ctx.lhsPattern().size() == 1) {
            return getSinglePatternDescr(ctx);
        } else if (ctx.lhsPattern().size() > 1) {
            return getOrDescrWithMultiplePatternDescr(ctx);
        } else {
            throw new IllegalStateException("ctx.lhsPattern().size() == 0 : " + ctx.getText());
        }
    }

    private PatternDescr getSinglePatternDescr(DRLParser.LhsPatternBindContext ctx) {
        List<BaseDescr> patternDescrList = visitDescrChildren(ctx);
        if (patternDescrList.isEmpty() || !(patternDescrList.get(0) instanceof PatternDescr)) {
            throw new IllegalStateException("lhsPatternBind must have at least one lhsPattern : " + ctx.getText());
        }
        PatternDescr patternDescr = (PatternDescr)patternDescrList.get(0);

        if (ctx.label() != null) {
            patternDescr.setIdentifier(ctx.label().drlIdentifier().getText());
        } else if (ctx.unif() != null) {
            patternDescr.setIdentifier(ctx.unif().IDENTIFIER().getText());
            patternDescr.setUnification(true);
        }
        DescrHelper.refreshPatternDescrProperties(patternDescr, ctx);
        return patternDescr;
    }

    private OrDescr getOrDescrWithMultiplePatternDescr(DRLParser.LhsPatternBindContext ctx) {
        OrDescr orDescr = DescrHelper.init(new OrDescr(), ctx);
        List<BaseDescr> descrList = visitDescrChildren(ctx);
        descrList.stream()
                .filter(PatternDescr.class::isInstance)
                .map(PatternDescr.class::cast)
                .forEach(patternDescr -> {
                    if (ctx.label() != null) {
                        patternDescr.setIdentifier(ctx.label().drlIdentifier().getText());
                    }
                    orDescr.addDescr(patternDescr);
                });

        return orDescr;
    }

    /**
     * entry point for a Pattern
     */
    @Override
    public PatternDescr visitLhsPattern(DRLParser.LhsPatternContext ctx) {
        PatternDescr patternDescr = DescrHelper.init(new PatternDescr(ctx.objectType.getText()), ctx);
        if (ctx.QUESTION() != null) {
            patternDescr.setQuery(true);
        }
        if (ctx.patternFilter() != null) {
            patternDescr.addBehavior(visitPatternFilter(ctx.patternFilter()));
        }
        if (ctx.patternSource() != null) {
            PatternSourceDescr patternSourceDescr = (PatternSourceDescr) visitPatternSource(ctx.patternSource());
            patternSourceDescr.setResource(patternDescr.getResource());
            patternDescr.setSource(patternSourceDescr);
        }

        ctx.drlAnnotation().stream().map(this::visitDrlAnnotation).forEach(patternDescr::addAnnotation);
        List<ExprConstraintDescr> constraintDescrList = visitConstraints(ctx.positionalConstraints(), ctx.constraints());
        constraintDescrList.forEach(descr -> addToPatternDescr(patternDescr, descr));
        return patternDescr;
    }

    private void addToPatternDescr(PatternDescr patternDescr, ExprConstraintDescr exprConstraintDescr) {
        exprConstraintDescr.setResource(patternDescr.getResource());
        patternDescr.addConstraint(exprConstraintDescr);
    }

    @Override
    public ForallDescr visitLhsForall(DRLParser.LhsForallContext ctx) {
        ForallDescr forallDescr = DescrHelper.init(new ForallDescr(), ctx);
        visitDescrChildren(ctx).forEach(forallDescr::addDescr);
        return forallDescr;
    }

    @Override
    public PatternDescr visitLhsAccumulate(DRLParser.LhsAccumulateContext ctx) {
        AccumulateDescr accumulateDescr = DescrHelper.init(new AccumulateDescr(), ctx);
        accumulateDescr.setInput(visitLhsAndDef(ctx.lhsAndDef()));

        // accumulate function
        for (DRLParser.AccumulateFunctionContext accumulateFunctionContext : ctx.accumulateFunction()) {
            accumulateDescr.addFunction(visitAccumulateFunction(accumulateFunctionContext));
        }

        PatternDescr patternDescr = new PatternDescr("Object");
        patternDescr.setSource(accumulateDescr);
        List<ExprConstraintDescr> constraintDescrList = visitConstraints(ctx.constraints());
        constraintDescrList.forEach(patternDescr::addConstraint);

        return patternDescr;
    }

    @Override
    public BehaviorDescr visitPatternFilter(DRLParser.PatternFilterContext ctx) {
        BehaviorDescr behaviorDescr = DescrHelper.init(new BehaviorDescr(), ctx);
        behaviorDescr.setType(ctx.DRL_WINDOW().getText());
        behaviorDescr.setSubType(ctx.IDENTIFIER().getText());
        List<DRLParser.DrlExpressionContext> drlExpressionContexts = ctx.expressionList().drlExpression();
        List<String> parameters = drlExpressionContexts.stream().map(Antlr4ParserStringUtils::getTextPreservingWhitespace).collect(Collectors.toList());
        behaviorDescr.setParameters(parameters);
        return behaviorDescr;
    }

    @Override
    public FromDescr visitFromExpression(DRLParser.FromExpressionContext ctx) {
        FromDescr fromDescr = DescrHelper.init(new FromDescr(), ctx);
        fromDescr.setDataSource(new MVELExprDescr(getTextPreservingWhitespace(ctx)));
        return fromDescr;
    }

    @Override
    public CollectDescr visitFromCollect(DRLParser.FromCollectContext ctx) {
        CollectDescr collectDescr = DescrHelper.init(new CollectDescr(), ctx);
        collectDescr.setInputPattern((PatternDescr) visitLhsPatternBind(ctx.lhsPatternBind()));
        return collectDescr;
    }

    @Override
    public AccumulateDescr visitFromAccumulate(DRLParser.FromAccumulateContext ctx) {
        AccumulateDescr accumulateDescr = DescrHelper.init(new AccumulateDescr(), ctx);
        accumulateDescr.setInput(visitLhsAndDef(ctx.lhsAndDef()));
        if (ctx.DRL_INIT() != null) {
            // inline custom accumulate
            accumulateDescr.setInitCode(getTextPreservingWhitespace(ctx.initBlockStatements));
            accumulateDescr.setActionCode(getTextPreservingWhitespace(ctx.actionBlockStatements));
            if (ctx.DRL_REVERSE() != null) {
                accumulateDescr.setReverseCode(getTextPreservingWhitespace(ctx.reverseBlockStatements));
            }
            accumulateDescr.setResultCode(ctx.expression().getText());
        } else {
            // accumulate function
            accumulateDescr.addFunction(visitAccumulateFunction(ctx.accumulateFunction()));
        }
        return accumulateDescr;
    }

    @Override
    public AccumulateDescr.AccumulateFunctionCallDescr visitAccumulateFunction(DRLParser.AccumulateFunctionContext ctx) {
        String function = ctx.IDENTIFIER().getText();
        String bind = ctx.label() == null ? null : ctx.label().drlIdentifier().getText();
        String[] params = new String[]{getTextPreservingWhitespace(ctx.drlExpression())};
        return new AccumulateDescr.AccumulateFunctionCallDescr(function, bind, false, params);
    }

    @Override
    public EntryPointDescr visitFromEntryPoint(DRLParser.FromEntryPointContext ctx) {
        return DescrHelper.init(new EntryPointDescr(safeStripStringDelimiters(ctx.stringId().getText())), ctx);
    }

    @Override
    public WindowReferenceDescr visitFromWindow(DRLParser.FromWindowContext ctx) {
        return DescrHelper.init(new WindowReferenceDescr(ctx.IDENTIFIER().getText()), ctx);
    }

    /**
     * Collect constraints in a Pattern
     */
    @Override
    public List<ExprConstraintDescr> visitConstraints(DRLParser.ConstraintsContext ctx) {
        List<ExprConstraintDescr> exprConstraintDescrList = new ArrayList<>();
        populateExprConstraintDescrList(ctx, exprConstraintDescrList);
        return exprConstraintDescrList;
    }

    /**
     * Collect constraints in a Pattern. Positional constraints comes first with semicolon.
     */
    private List<ExprConstraintDescr> visitConstraints(DRLParser.PositionalConstraintsContext positionalCtx, DRLParser.ConstraintsContext ctx) {
        List<ExprConstraintDescr> exprConstraintDescrList = new ArrayList<>();
        populateExprConstraintDescrList(positionalCtx, exprConstraintDescrList);
        populateExprConstraintDescrList(ctx, exprConstraintDescrList);
        return exprConstraintDescrList;
    }

    private void populateExprConstraintDescrList(ParserRuleContext ctx, List<ExprConstraintDescr> exprConstraintDescrList) {
        if (ctx == null) {
            return;
        }
        List<BaseDescr> descrList = visitDescrChildren(ctx);
        for (BaseDescr descr : descrList) {
            if (descr instanceof ExprConstraintDescr) {
                ExprConstraintDescr exprConstraintDescr = (ExprConstraintDescr) descr;
                exprConstraintDescr.setType(ctx instanceof DRLParser.PositionalConstraintsContext ? ExprConstraintDescr.Type.POSITIONAL : ExprConstraintDescr.Type.NAMED);
                exprConstraintDescr.setPosition(exprConstraintDescrList.size());
                exprConstraintDescrList.add(exprConstraintDescr);
            }
        }
    }

    /**
     * Takes one constraint as String and create ExprConstraintDescr.
     * In case of nested constraint, it could be multiple ExprConstraintDescr objects.
     */
    @Override
    public List<ExprConstraintDescr> visitConstraint(DRLParser.ConstraintContext ctx) {
        List<ExprConstraintDescr> descrList = new ArrayList<>();
        if (ctx.nestedConstraint() != null) {
            // nested constraint requires special string manipulation
            return visitNestedConstraint(ctx.nestedConstraint());
        }
        // get a simple constraint as String
        String constraint = visitConstraintChildren(ctx);
        if (!constraint.isEmpty()) {
            ExprConstraintDescr constraintDescr = DescrHelper.init(new ExprConstraintDescr(constraint), ctx);
            constraintDescr.setType(ExprConstraintDescr.Type.NAMED);
            descrList.add(constraintDescr);
            return descrList;
        }
        return descrList;
    }

    /**
     * Append a prefix to nested constraints.
     * For example,
     *     address.(city.startsWith("I"), city.length() == 5)
     * becomes
     *     address.city.startsWith("I"), address.city.length() == 5
     */
    @Override
    public List<ExprConstraintDescr> visitNestedConstraint(DRLParser.NestedConstraintContext ctx) {
        Token prefixStartToken = ctx.start;
        Token prefixEndToken = tokenStream.get(ctx.LPAREN().getSymbol().getTokenIndex() - 1);
        String prefix = tokenStream.getText(prefixStartToken, prefixEndToken);
        List<ExprConstraintDescr> exprConstraintDescr = visitConstraints(ctx.constraints());
        exprConstraintDescr.forEach(d -> d.setText(appendPrefix(prefix, d.getText())));
        return exprConstraintDescr;
    }

    @Override
    public String visitDrlIdentifier(DRLParser.DrlIdentifierContext ctx) {
        return ctx.getText();
    }

    @Override
    public ExistsDescr visitLhsExists(DRLParser.LhsExistsContext ctx) {
        ExistsDescr existsDescr = DescrHelper.init(new ExistsDescr(), ctx);
        if (ctx.lhsExpression() != null) {
            // exists( A() or B() )
            List<BaseDescr> baseDescrs = visitDescrChildren(ctx);
            if (baseDescrs.size() == 1) {
                existsDescr.addDescr(baseDescrs.get(0));
            } else {
                throw new IllegalStateException("'exists()' children descr size must be 1 : " + ctx.getText());
            }
        } else {
            // exists A()
            BaseDescr descr = visitLhsPatternBind(ctx.lhsPatternBind());
            existsDescr.addDescr(descr);
        }
        return existsDescr;
    }

    @Override
    public NotDescr visitLhsNot(DRLParser.LhsNotContext ctx) {
        NotDescr notDescr = DescrHelper.init(new NotDescr(), ctx);
        if (ctx.lhsExpression() != null) {
            // not ( A() or B() )
            List<BaseDescr> baseDescrs = visitDescrChildren(ctx);
            if (baseDescrs.size() == 1) {
                notDescr.addDescr(baseDescrs.get(0));
            } else {
                throw new IllegalStateException("'not()' children descr size must be 1 : " + ctx.getText());
            }
        } else {
            // not A()
            BaseDescr descr = visitLhsPatternBind(ctx.lhsPatternBind());
            notDescr.addDescr(descr);
        }
        return notDescr;
    }

    @Override
    public EvalDescr visitLhsEval(DRLParser.LhsEvalContext ctx) {
        return DescrHelper.init(new EvalDescr(getTextPreservingWhitespace(ctx.conditionalOrExpression())), ctx);
    }

    @Override
    public BaseDescr visitLhsExpressionEnclosed(DRLParser.LhsExpressionEnclosedContext ctx) {
        // enclosed expression is simply stripped because Descr itself is encapsulated
        return (BaseDescr) visit(ctx.lhsExpression());
    }

    @Override
    public BaseDescr visitLhsOr(DRLParser.LhsOrContext ctx) {
        // For flatten nested OrDescr logic, we call visitDescrChildrenForDescrNodePair instead of usual visitDescrChildren
        List<DescrNodePair> descrList = visitDescrChildrenForDescrNodePair(ctx);
        if (descrList.size() == 1) {
            // Avoid nested OrDescr
            return descrList.get(0).getDescr();
        } else {
            OrDescr orDescr = DescrHelper.init(new OrDescr(), ctx);
            // For example, in case of A() or B() or C(),
            // Parser creates AST like this:
            //  lhsOr
            //   / \
            // A() lhsOr
            //     / \
            //  B()  C()
            // So, we need to flatten it so that OrDescr has A(), B() and C() as children.
            List<BaseDescr> flattenedDescrs = flattenOrDescr(descrList);
            flattenedDescrs.forEach(orDescr::addDescr);
            return orDescr;
        }
    }

    private List<BaseDescr> flattenOrDescr(List<DescrNodePair> descrList) {
        List<BaseDescr> flattenedDescrs = new ArrayList<>();
        for (DescrNodePair descrNodePair : descrList) {
            BaseDescr descr = descrNodePair.getDescr();
            ParseTree node = descrNodePair.getNode(); // parser node corresponding to the descr
            if (descr instanceof OrDescr && !(node instanceof DRLParser.LhsExpressionEnclosedContext)) {
                // sibling OrDescr should be flattened unless it's explicitly enclosed by parenthesis
                flattenedDescrs.addAll(((OrDescr) descr).getDescrs());
            } else {
                flattenedDescrs.add(descr);
            }
        }
        return flattenedDescrs;
    }

    @Override
    public BaseDescr visitLhsAnd(DRLParser.LhsAndContext ctx) {
        return createAndDescr(ctx);
    }

    private BaseDescr createAndDescr(ParserRuleContext ctx) {
        // For flatten nested AndDescr logic, we call visitDescrChildrenForDescrNodePair instead of usual visitDescrChildren
        List<DescrNodePair> descrList = visitDescrChildrenForDescrNodePair(ctx);
        if (descrList.size() == 1) {
            // Avoid nested AndDescr
            return descrList.get(0).getDescr();
        } else {
            AndDescr andDescr = DescrHelper.init(new AndDescr(), ctx);
            // For example, in case of A() and B() and C(),
            // Parser creates AST like this:
            //  lhsAnd
            //   / \
            // A() lhsAnd
            //     / \
            //  B()  C()
            // So, we need to flatten it so that AndDescr has A(), B() and C() as children.
            List<BaseDescr> flattenedDescrs = flattenAndDescr(descrList);
            flattenedDescrs.forEach(andDescr::addDescr);
            return andDescr;
        }
    }

    private List<BaseDescr> flattenAndDescr(List<DescrNodePair> descrList) {
        List<BaseDescr> flattenedDescrs = new ArrayList<>();
        for (DescrNodePair descrNodePair : descrList) {
            BaseDescr descr = descrNodePair.getDescr();
            ParseTree node = descrNodePair.getNode(); // parser node corresponding to the descr
            if (descr instanceof AndDescr && !(node instanceof DRLParser.LhsExpressionEnclosedContext)) {
                // sibling AndDescr should be flattened unless it's explicitly enclosed by parenthesis
                flattenedDescrs.addAll(((AndDescr) descr).getDescrs());
            } else {
                flattenedDescrs.add(descr);
            }
        }
        return flattenedDescrs;
    }

    @Override
    public BaseDescr visitLhsAndDef(DRLParser.LhsAndDefContext ctx) {
        return createAndDescr(ctx);
    }

    @Override
    public BaseDescr visitLhsUnary(DRLParser.LhsUnaryContext ctx) {
        return visitDescrChildren(ctx).get(0); // lhsUnary has only one child
    }

    /**
     * This is a special version of visitChildren().
     * This collects children BaseDescr objects and returns them as a list.
     */
    private List<BaseDescr> visitDescrChildren(RuleNode node) {
        List<BaseDescr> aggregator = new ArrayList<>();
        int n = node.getChildCount();

        for (int i = 0; i < n && this.shouldVisitNextChild(node, aggregator); ++i) {
            ParseTree c = node.getChild(i);
            Object childResult = c.accept(this);
            if (childResult instanceof BaseDescr) {
                aggregator.add((BaseDescr) childResult);
            } else if (childResult instanceof List) {
                aggregator.addAll((List<BaseDescr>) childResult);
            }
        }
        return aggregator;
    }

    // This method is used when the parent descr requires children parser node information for composing the descr.
    // Ideally, we should use visitDescrChildren as possible and use the returned Descr object to compose the parent descr.
    // However, for example, in flatten OrDescr/AndDescr logic,
    // enhancing the returned Descr object of visitLhsExpressionEnclosed() (e.g. adding 'enclosed' flag to OrDescr/AndDescr) could be intrusive just for composing the parent descr.
    private List<DescrNodePair> visitDescrChildrenForDescrNodePair(RuleNode node) {
        List<DescrNodePair> aggregator = new ArrayList<>();
        int n = node.getChildCount();

        for (int i = 0; i < n && this.shouldVisitNextChild(node, aggregator); ++i) {
            ParseTree c = node.getChild(i);
            Object childResult = c.accept(this);
            if (childResult instanceof BaseDescr) {
                aggregator.add(new DescrNodePair((BaseDescr) childResult, c)); // pairing the returned Descr and the parser node
            }
        }
        return aggregator;
    }

    private static class DescrNodePair {
        private final BaseDescr descr; // returned Descr object
        private final ParseTree node; // parser node corresponding to the descr. This is used for composing the parent descr.

        private DescrNodePair(BaseDescr descr, ParseTree node) {
            this.descr = descr;
            this.node = node;
        }

        public BaseDescr getDescr() {
            return descr;
        }

        public ParseTree getNode() {
            return node;
        }
    }

    /**
     * Return the text of constraint as-is
     */
    private String visitConstraintChildren(ParserRuleContext ctx) {
        return getTokenTextPreservingWhitespace(ctx, tokenStream);
    }
}
