package org.drools.parser;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.antlr.v4.runtime.ParserRuleContext;
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

import static org.drools.parser.DRLParserHelper.getTextWithoutErrorNode;
import static org.drools.parser.ParserStringUtils.getTextPreservingWhitespace;
import static org.drools.parser.ParserStringUtils.getTokenTextPreservingWhitespace;
import static org.drools.parser.ParserStringUtils.safeStripStringDelimiters;
import static org.drools.parser.ParserStringUtils.trimThen;
import static org.drools.util.StringUtils.unescapeJava;

public class DRLVisitorImpl extends DRLParserBaseVisitor<Object> {

    private final TokenStream tokenStream;

    public DRLVisitorImpl(TokenStream tokenStream) {
        this.tokenStream = tokenStream;
    }

    @Override
    public PackageDescr visitCompilationUnit(DRLParser.CompilationUnitContext ctx) {
        PackageDescr packageDescr = new PackageDescr();
        if (ctx.packagedef() != null) {
            packageDescr.setName(getTextWithoutErrorNode(ctx.packagedef().name));
        }
        List<BaseDescr> descrList = visitDescrChildren(ctx);
        applyChildrenDescrs(packageDescr, descrList);
        return packageDescr;
    }

    private void applyChildrenDescrs(PackageDescr packageDescr, List<BaseDescr> descrList) {
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
                packageDescr.addRule((RuleDescr) descr);
                packageDescr.afterRuleAdded((RuleDescr) descr);
            }
        });
    }

    @Override
    public UnitDescr visitUnitdef(DRLParser.UnitdefContext ctx) {
        return new UnitDescr(ctx.name.getText());
    }

    @Override
    public GlobalDescr visitGlobaldef(DRLParser.GlobaldefContext ctx) {
        GlobalDescr globalDescr = new GlobalDescr(ctx.drlIdentifier().getText(), ctx.type().getText());
        populateStartEnd(globalDescr, ctx);
        return globalDescr;
    }

    @Override
    public ImportDescr visitImportStandardDef(DRLParser.ImportStandardDefContext ctx) {
        String target = ctx.drlQualifiedName().getText() + (ctx.MUL() != null ? ".*" : "");
        if (ctx.DRL_FUNCTION() != null || ctx.STATIC() != null) {
            FunctionImportDescr functionImportDescr = new FunctionImportDescr();
            functionImportDescr.setTarget(target);
            populateStartEnd(functionImportDescr, ctx);
            return functionImportDescr;
        } else {
            ImportDescr importDescr = new ImportDescr();
            importDescr.setTarget(target);
            populateStartEnd(importDescr, ctx);
            return importDescr;
        }
    }

    @Override
    public AccumulateImportDescr visitImportAccumulateDef(DRLParser.ImportAccumulateDefContext ctx) {
        AccumulateImportDescr accumulateImportDescr = new AccumulateImportDescr();
        accumulateImportDescr.setTarget(ctx.drlQualifiedName().getText());
        accumulateImportDescr.setFunctionName(ctx.IDENTIFIER().getText());
        return accumulateImportDescr;
    }

    @Override
    public FunctionDescr visitFunctiondef(DRLParser.FunctiondefContext ctx) {
        FunctionDescr functionDescr = new FunctionDescr();
        if (ctx.typeTypeOrVoid() != null) {
            functionDescr.setReturnType(ctx.typeTypeOrVoid().getText());
        } else {
            functionDescr.setReturnType("void");
        }
        functionDescr.setName(ctx.IDENTIFIER().getText());
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
        functionDescr.setBody(getTextPreservingWhitespace(ctx.block()));
        return functionDescr;
    }

    @Override
    public BaseDescr visitDeclaredef(DRLParser.DeclaredefContext ctx) {
        return visitDescrChildren(ctx).get(0);
    }

    @Override
    public TypeDeclarationDescr visitTypeDeclaration(DRLParser.TypeDeclarationContext ctx) {
        TypeDeclarationDescr typeDeclarationDescr = new TypeDeclarationDescr(ctx.name.getText());
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
        EntryPointDeclarationDescr entryPointDeclarationDescr = new EntryPointDeclarationDescr();
        entryPointDeclarationDescr.setEntryPointId(ctx.name.getText());
        ctx.drlAnnotation().stream()
                .map(this::visitDrlAnnotation)
                .forEach(entryPointDeclarationDescr::addAnnotation);
        return entryPointDeclarationDescr;
    }

    @Override
    public WindowDeclarationDescr visitWindowDeclaration(DRLParser.WindowDeclarationContext ctx) {
        WindowDeclarationDescr windowDeclarationDescr = new WindowDeclarationDescr();
        windowDeclarationDescr.setName(ctx.name.getText());
        ctx.drlAnnotation().stream()
                .map(this::visitDrlAnnotation)
                .forEach(windowDeclarationDescr::addAnnotation);
        windowDeclarationDescr.setPattern((PatternDescr) visitLhsPatternBind(ctx.lhsPatternBind()));
        return windowDeclarationDescr;
    }

    @Override
    public RuleDescr visitRuledef(DRLParser.RuledefContext ctx) {
        RuleDescr ruleDescr = new RuleDescr(safeStripStringDelimiters(ctx.name.getText()));

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
            List<BaseDescr> lhsDescrList = visitLhs(ctx.lhs());
            lhsDescrList.forEach(descr -> ruleDescr.getLhs().addDescr(descr));
            slimLhsRootDescr(ruleDescr.getLhs());
        }

        if (ctx.rhs() != null) {
            ruleDescr.setConsequenceLocation(ctx.rhs().getStart().getLine(), ctx.rhs().getStart().getCharPositionInLine()); // location of "then"
            ruleDescr.setConsequence(trimThen(getTextPreservingWhitespace(ctx.rhs())));
        }

        return ruleDescr;
    }

    private void slimLhsRootDescr(AndDescr root) {
        List<BaseDescr> descrList = new ArrayList<>(root.getDescrs());
        root.getDescrs().clear();
        descrList.forEach(root::addOrMerge); // This slims down nested AndDescr
    }

    @Override
    public QueryDescr visitQuerydef(DRLParser.QuerydefContext ctx) {
        QueryDescr queryDescr = new QueryDescr(safeStripStringDelimiters(ctx.name.getText()));

        DRLParser.FormalParametersContext formalParametersContext = ctx.formalParameters();
        if (formalParametersContext != null) {
            DRLParser.FormalParameterListContext formalParameterListContext = formalParametersContext.formalParameterList();
            List<DRLParser.FormalParameterContext> formalParameterContexts = formalParameterListContext.formalParameter();
            formalParameterContexts.forEach(formalParameterContext -> {
                DRLParser.TypeTypeContext typeTypeContext = formalParameterContext.typeType();
                DRLParser.VariableDeclaratorIdContext variableDeclaratorIdContext = formalParameterContext.variableDeclaratorId();
                queryDescr.addParameter(typeTypeContext.getText(), variableDeclaratorIdContext.getText());
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
        AnnotationDescr annotationDescr = new AnnotationDescr(ctx.name.getText());
        if (ctx.drlElementValue() != null) {
            annotationDescr.setValue(getTextPreservingWhitespace(ctx.drlElementValue())); // single value
        } else if (ctx.drlElementValuePairs() != null) {
            visitDrlElementValuePairs(ctx.drlElementValuePairs(), annotationDescr); // multiple values
        }
        return annotationDescr;
    }

    @Override
    public TypeFieldDescr visitField(DRLParser.FieldContext ctx) {
        TypeFieldDescr typeFieldDescr = new TypeFieldDescr();
        typeFieldDescr.setFieldName(ctx.label().IDENTIFIER().getText());
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
        AttributeDescr attributeDescr = new AttributeDescr(ctx.name.getText());
        attributeDescr.setValue(getTextPreservingWhitespace(ctx.conditionalOrExpression()));
        attributeDescr.setType(AttributeDescr.Type.EXPRESSION);
        return attributeDescr;
    }

    @Override
    public AttributeDescr visitBooleanAttribute(DRLParser.BooleanAttributeContext ctx) {
        AttributeDescr attributeDescr = new AttributeDescr(ctx.name.getText());
        attributeDescr.setValue(ctx.BOOL_LITERAL() != null ? ctx.BOOL_LITERAL().getText() : "true");
        attributeDescr.setType(AttributeDescr.Type.BOOLEAN);
        return attributeDescr;
    }

    @Override
    public AttributeDescr visitStringAttribute(DRLParser.StringAttributeContext ctx) {
        AttributeDescr attributeDescr = new AttributeDescr(ctx.name.getText());
        attributeDescr.setValue(unescapeJava(safeStripStringDelimiters(ctx.DRL_STRING_LITERAL().getText())));
        attributeDescr.setType(AttributeDescr.Type.STRING);
        return attributeDescr;
    }

    @Override
    public AttributeDescr visitStringListAttribute(DRLParser.StringListAttributeContext ctx) {
        AttributeDescr attributeDescr = new AttributeDescr(ctx.name.getText());
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
        AttributeDescr attributeDescr = new AttributeDescr(ctx.name.getText());
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
        AttributeDescr attributeDescr = new AttributeDescr(ctx.name.getText());
        if (ctx.DECIMAL_LITERAL() != null) {
            attributeDescr.setValue(ctx.DECIMAL_LITERAL().getText());
            attributeDescr.setType(AttributeDescr.Type.NUMBER);
        } else {
            attributeDescr.setValue(unescapeJava(safeStripStringDelimiters(ctx.TIME_INTERVAL().getText())));
            attributeDescr.setType(AttributeDescr.Type.EXPRESSION);
        }
        return attributeDescr;
    }

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
        Optional<BaseDescr> optPatternDescr = visitFirstDescrChild(ctx);
        PatternDescr patternDescr = optPatternDescr.filter(PatternDescr.class::isInstance)
                .map(PatternDescr.class::cast)
                .orElseThrow(() -> new IllegalStateException("lhsPatternBind must have at least one lhsPattern : " + ctx.getText()));
        if (ctx.label() != null) {
            patternDescr.setIdentifier(ctx.label().IDENTIFIER().getText());
        } else if (ctx.unif() != null) {
            patternDescr.setIdentifier(ctx.unif().IDENTIFIER().getText());
            patternDescr.setUnification(true);
        }
        return patternDescr;
    }

    private OrDescr getOrDescrWithMultiplePatternDescr(DRLParser.LhsPatternBindContext ctx) {
        OrDescr orDescr = new OrDescr();
        List<BaseDescr> descrList = visitDescrChildren(ctx);
        descrList.stream()
                .filter(PatternDescr.class::isInstance)
                .map(PatternDescr.class::cast)
                .forEach(patternDescr -> {
                    if (ctx.label() != null) {
                        patternDescr.setIdentifier(ctx.label().IDENTIFIER().getText());
                    }
                    orDescr.addDescr(patternDescr);
                });

        return orDescr;
    }

    @Override
    public PatternDescr visitLhsPattern(DRLParser.LhsPatternContext ctx) {
        PatternDescr patternDescr = new PatternDescr(ctx.objectType.getText());
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
        ForallDescr forallDescr = new ForallDescr();
        visitDescrChildren(ctx).forEach(forallDescr::addDescr);
        return forallDescr;
    }

    @Override
    public PatternDescr visitLhsAccumulate(DRLParser.LhsAccumulateContext ctx) {
        AccumulateDescr accumulateDescr = new AccumulateDescr();
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
        BehaviorDescr behaviorDescr = new BehaviorDescr();
        behaviorDescr.setType(ctx.DRL_WINDOW().getText());
        behaviorDescr.setSubType(ctx.IDENTIFIER().getText());
        List<DRLParser.DrlExpressionContext> drlExpressionContexts = ctx.expressionList().drlExpression();
        List<String> parameters = drlExpressionContexts.stream().map(ParserStringUtils::getTextPreservingWhitespace).collect(Collectors.toList());
        behaviorDescr.setParameters(parameters);
        return behaviorDescr;
    }

    @Override
    public FromDescr visitFromExpression(DRLParser.FromExpressionContext ctx) {
        FromDescr fromDescr = new FromDescr();
        fromDescr.setDataSource(new MVELExprDescr(ctx.getText()));
        return fromDescr;
    }

    @Override
    public CollectDescr visitFromCollect(DRLParser.FromCollectContext ctx) {
        CollectDescr collectDescr = new CollectDescr();
        collectDescr.setInputPattern((PatternDescr) visitLhsPatternBind(ctx.lhsPatternBind()));
        return collectDescr;
    }

    @Override
    public AccumulateDescr visitFromAccumulate(DRLParser.FromAccumulateContext ctx) {
        AccumulateDescr accumulateDescr = new AccumulateDescr();
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
        String bind = ctx.label() == null ? null : ctx.label().IDENTIFIER().getText();
        String[] params = new String[]{getTextPreservingWhitespace(ctx.drlExpression())};
        return new AccumulateDescr.AccumulateFunctionCallDescr(function, bind, false, params);
    }

    @Override
    public EntryPointDescr visitFromEntryPoint(DRLParser.FromEntryPointContext ctx) {
        return new EntryPointDescr(safeStripStringDelimiters(ctx.stringId().getText()));
    }

    @Override
    public List<ExprConstraintDescr> visitConstraints(DRLParser.ConstraintsContext ctx) {
        List<ExprConstraintDescr> exprConstraintDescrList = new ArrayList<>();
        populateExprConstraintDescrList(ctx, exprConstraintDescrList);
        return exprConstraintDescrList;
    }

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

    @Override
    public ExprConstraintDescr visitConstraint(DRLParser.ConstraintContext ctx) {
        String constraint = visitConstraintChildren(ctx);
        if (!constraint.isEmpty()) {
            ExprConstraintDescr constraintDescr = new ExprConstraintDescr(constraint);
            constraintDescr.setType(ExprConstraintDescr.Type.NAMED);
            return constraintDescr;
        }
        return null;
    }

    @Override
    public String visitDrlIdentifier(DRLParser.DrlIdentifierContext ctx) {
        return ctx.getText();
    }

    @Override
    public ExistsDescr visitLhsExists(DRLParser.LhsExistsContext ctx) {
        ExistsDescr existsDescr = new ExistsDescr();
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
        NotDescr notDescr = new NotDescr();
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
        return new EvalDescr(getTextPreservingWhitespace(ctx.conditionalOrExpression()));
    }

    @Override
    public BaseDescr visitLhsExpressionEnclosed(DRLParser.LhsExpressionEnclosedContext ctx) {
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
            OrDescr orDescr = new OrDescr();
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
            AndDescr andDescr = new AndDescr();
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
        return visitDescrChildren(ctx).get(0);
    }

    private void populateStartEnd(BaseDescr descr, ParserRuleContext ctx) {
        descr.setStartCharacter(ctx.getStart().getStartIndex());
        // TODO: Current DRL6Parser adds +1 for EndCharacter but it doesn't look reasonable. At the moment, I don't add. Instead, I fix unit tests.
        //       I will revisit if this is the right approach.
        descr.setEndCharacter(ctx.getStop().getStopIndex());
    }

    private List<BaseDescr> visitDescrChildren(RuleNode node) {
        List<BaseDescr> aggregator = new ArrayList<>();
        int n = node.getChildCount();

        for (int i = 0; i < n && this.shouldVisitNextChild(node, aggregator); ++i) {
            ParseTree c = node.getChild(i);
            Object childResult = c.accept(this);
            if (childResult instanceof BaseDescr) {
                aggregator.add((BaseDescr) childResult);
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

    // leaves of constraint concatenate return Strings
    private String visitConstraintChildren(ParserRuleContext ctx) {
        return getTokenTextPreservingWhitespace(ctx, tokenStream);
    }

    private Optional<BaseDescr> visitFirstDescrChild(RuleNode node) {
        int n = node.getChildCount();

        for (int i = 0; i < n && this.shouldVisitNextChild(node, null); ++i) {
            ParseTree c = node.getChild(i);
            Object childResult = c.accept(this);
            if (childResult instanceof BaseDescr) {
                return Optional.of((BaseDescr) childResult);
            }
        }
        return Optional.empty();
    }
}
