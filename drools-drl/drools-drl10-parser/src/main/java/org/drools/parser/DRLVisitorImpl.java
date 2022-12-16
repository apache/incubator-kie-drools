package org.drools.parser;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.RuleNode;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.drools.drl.ast.descr.AndDescr;
import org.drools.drl.ast.descr.AnnotationDescr;
import org.drools.drl.ast.descr.AttributeDescr;
import org.drools.drl.ast.descr.BaseDescr;
import org.drools.drl.ast.descr.ExistsDescr;
import org.drools.drl.ast.descr.ExprConstraintDescr;
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
import org.drools.drl.ast.descr.RuleDescr;
import org.drools.drl.ast.descr.UnitDescr;

import static org.drools.parser.DRLParserHelper.getTextWithoutErrorNode;
import static org.drools.parser.ParserStringUtils.safeStripStringDelimiters;
import static org.drools.util.StringUtils.unescapeJava;

public class DRLVisitorImpl extends DRLParserBaseVisitor<Object> {

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
            } else if (descr instanceof AttributeDescr) {
                packageDescr.addAttribute((AttributeDescr) descr);
            } else if (descr instanceof RuleDescr) {
                packageDescr.addRule((RuleDescr) descr);
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
    public ImportDescr visitImportdef(DRLParser.ImportdefContext ctx) {
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
        functionDescr.setBody(ParserStringUtils.getTextPreservingWhitespace(ctx.block()));
        return functionDescr;
    }

    @Override
    public RuleDescr visitRuledef(DRLParser.RuledefContext ctx) {
        RuleDescr ruleDescr = new RuleDescr(safeStripStringDelimiters(ctx.name.getText()));

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
            ruleDescr.setConsequence(ParserStringUtils.getTextPreservingWhitespace(ctx.rhs().consequence()));
        }

        return ruleDescr;
    }

    private void slimLhsRootDescr(AndDescr root) {
        List<BaseDescr> descrList = new ArrayList<>(root.getDescrs());
        root.getDescrs().clear();
        descrList.forEach(root::addOrMerge); // This slims down nested AndDescr
    }

    @Override
    public AnnotationDescr visitDrlAnnotation(DRLParser.DrlAnnotationContext ctx) {
        AnnotationDescr annotationDescr = new AnnotationDescr(ctx.name.getText());
        annotationDescr.setValue(ctx.drlArguments().drlArgument(0).getText());
        return annotationDescr;
    }

    @Override
    public AttributeDescr visitAttribute(DRLParser.AttributeContext ctx) {
        AttributeDescr attributeDescr = new AttributeDescr(ctx.getChild(0).getText());
        if (ctx.getChildCount() > 1) {
            // TODO : will likely split visitAttribute methods using labels (e.g. #stringAttribute)
            String value = unescapeJava(safeStripStringDelimiters(ctx.getChild(1).getText()));
            attributeDescr.setValue(value);
        }
        return attributeDescr;
    }

    @Override
    public List<BaseDescr> visitLhs(DRLParser.LhsContext ctx) {
        if (ctx.lhsExpression() != null) {
            return visitLhsExpression(ctx.lhsExpression());
        } else {
            return new ArrayList<>();
        }
    }

    @Override
    public List<BaseDescr> visitLhsExpression(DRLParser.LhsExpressionContext ctx) {
        return visitDescrChildren(ctx);
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
        if (ctx.patternSource() != null) {
            String expression = ctx.patternSource().getText();
            FromDescr from = new FromDescr();
            from.setDataSource(new MVELExprDescr(expression));
            from.setResource(patternDescr.getResource());
            patternDescr.setSource(from);
        }
        List<ExprConstraintDescr> constraintDescrList = visitConstraints(ctx.constraints());
        constraintDescrList.forEach(patternDescr::addConstraint);
        return patternDescr;
    }

    @Override
    public List<ExprConstraintDescr> visitConstraints(DRLParser.ConstraintsContext ctx) {
        List<BaseDescr> descrList = visitDescrChildren(ctx);
        return descrList.stream()
                .filter(ExprConstraintDescr.class::isInstance)
                .map(ExprConstraintDescr.class::cast)
                .collect(Collectors.toList());
    }

    @Override
    public ExprConstraintDescr visitConstraint(DRLParser.ConstraintContext ctx) {
        Object constraint = super.visitConstraint(ctx);
        if (constraint != null) {
            String constraintString = constraint.toString();
            DRLParser.LabelContext label = ctx.label();
            if (label != null) {
                constraintString = label.getText() + constraintString;
            }
            ExprConstraintDescr constraintDescr = new ExprConstraintDescr(constraintString);
            constraintDescr.setType(ExprConstraintDescr.Type.NAMED);
            return constraintDescr;
        }
        return null;
    }

    @Override
    public String visitDrlExpression(DRLParser.DrlExpressionContext ctx) {
        return ctx.children.stream()
                .map(c -> c instanceof TerminalNode ? c : c.accept(this))
                .filter(Objects::nonNull)
                .map(Object::toString)
                .collect(Collectors.joining(" "));
    }

    @Override
    public String visitDrlPrimary(DRLParser.DrlPrimaryContext ctx) {
        return ctx.children.stream()
                .map(c -> c instanceof TerminalNode ? c : c.accept(this))
                .filter(Objects::nonNull)
                .map(Object::toString)
                .collect(Collectors.joining(" "));
    }

    @Override
    public String visitDrlIdentifier(DRLParser.DrlIdentifierContext ctx) {
        return ctx.getText();
    }

    @Override
    public String visitDrlLiteral(DRLParser.DrlLiteralContext ctx) {
        ParseTree node = ctx;
        while (true) {
            if (node instanceof TerminalNode) {
                return node.toString();
            }
            if (node.getChildCount() != 1) {
                return super.visitDrlLiteral(ctx).toString();
            }
            node = node.getChild(0);
        }
    }

    @Override
    public ExistsDescr visitLhsExists(DRLParser.LhsExistsContext ctx) {
        ExistsDescr existsDescr = new ExistsDescr();
        BaseDescr descr = visitLhsPatternBind(ctx.lhsPatternBind());
        existsDescr.addDescr(descr);
        return existsDescr;
    }

    @Override
    public NotDescr visitLhsNot(DRLParser.LhsNotContext ctx) {
        NotDescr notDescr = new NotDescr();
        BaseDescr descr = visitLhsPatternBind(ctx.lhsPatternBind());
        notDescr.addDescr(descr);
        return notDescr;
    }

    @Override
    public BaseDescr visitLhsOr(DRLParser.LhsOrContext ctx) {
        if (!ctx.DRL_OR().isEmpty()) {
            OrDescr orDescr = new OrDescr();
            List<BaseDescr> descrList = visitDescrChildren(ctx);
            descrList.forEach(orDescr::addDescr);
            return orDescr;
        } else {
            // No DRL_OR means only one lhsAnd
            return visitLhsAnd(ctx.lhsAnd().get(0));
        }
    }

    @Override
    public BaseDescr visitLhsAnd(DRLParser.LhsAndContext ctx) {
        if (!ctx.DRL_AND().isEmpty()) {
            AndDescr andDescr = new AndDescr();
            List<BaseDescr> descrList = visitDescrChildren(ctx);
            descrList.forEach(andDescr::addDescr);
            return andDescr;
        } else {
            // No DRL_AND means only one lhsUnary
            return visitLhsUnary(ctx.lhsUnary().get(0));
        }
    }

    @Override
    public BaseDescr visitLhsUnary(DRLParser.LhsUnaryContext ctx) {
        return (BaseDescr) visitChildren(ctx);
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
