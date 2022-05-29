package org.drools.parser;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.drools.drl.ast.descr.*;

import static org.drools.parser.DRLParserHelper.getTextWithoutErrorNode;
import static org.drools.parser.StringUtils.safeStripStringDelimiters;

public class DRLVisitorImpl extends DRLParserBaseVisitor<Object> {

    private final PackageDescr packageDescr = new PackageDescr();

    private RuleDescr currentRule;
    private PatternDescr currentPattern;

    private final Deque<ConditionalElementDescr> currentConstructStack = new ArrayDeque<>(); // e.g. whole LHS (= AndDescr), NotDescr, ExistsDescr

    @Override
    public Object visitCompilationUnit(DRLParser.CompilationUnitContext ctx) {
        return super.visitCompilationUnit(ctx);
    }

    @Override
    public Object visitPackagedef(DRLParser.PackagedefContext ctx) {
        packageDescr.setName(getTextWithoutErrorNode(ctx.name));
        return super.visitPackagedef(ctx);
    }

    @Override
    public Object visitUnitdef(DRLParser.UnitdefContext ctx) {
        packageDescr.setUnit(new UnitDescr(ctx.name.getText()));
        return super.visitUnitdef(ctx);
    }

    @Override
    public Object visitGlobaldef(DRLParser.GlobaldefContext ctx) {
        GlobalDescr globalDescr = new GlobalDescr(ctx.IDENTIFIER().getText(), ctx.type().getText());
        populateStartEnd(globalDescr, ctx);
        packageDescr.addGlobal(globalDescr);
        return super.visitGlobaldef(ctx);
    }

    @Override
    public Object visitImportdef(DRLParser.ImportdefContext ctx) {
        String target = ctx.qualifiedName().getText() + (ctx.MUL() != null ? ".*" : "");
        if (ctx.FUNCTION() != null || ctx.STATIC() != null) {
            FunctionImportDescr functionImportDescr = new FunctionImportDescr();
            functionImportDescr.setTarget(target);
            populateStartEnd(functionImportDescr, ctx);
            packageDescr.addFunctionImport(functionImportDescr);
        } else {
            ImportDescr importDescr = new ImportDescr();
            importDescr.setTarget(target);
            populateStartEnd(importDescr, ctx);
            packageDescr.addImport(importDescr);
        }
        return super.visitImportdef(ctx);
    }

    @Override
    public Object visitRuledef(DRLParser.RuledefContext ctx) {
        currentRule = new RuleDescr(safeStripStringDelimiters(ctx.name.getText()));
        currentRule.setConsequence(ctx.rhs().getText());
        packageDescr.addRule(currentRule);

        Object result = super.visitRuledef(ctx);
        currentRule = null;
        return result;
    }

    @Override
    public Object visitLhs(DRLParser.LhsContext ctx) {
        currentConstructStack.push(currentRule.getLhs());
        try {
            return super.visitLhs(ctx);
        } finally {
            currentConstructStack.pop();
        }
    }

    @Override
    public Object visitLhsPatternBind(DRLParser.LhsPatternBindContext ctx) {
        if (ctx.lhsPattern().size() == 1) {
            Object result = super.visitLhsPatternBind(ctx);
            PatternDescr patternDescr = (PatternDescr) currentConstructStack.peek().getDescrs().get(0);
            if (ctx.label() != null) {
                patternDescr.setIdentifier(ctx.label().IDENTIFIER().getText());
            }
            return result;
        } else if (ctx.lhsPattern().size() > 1) {
            OrDescr orDescr = new OrDescr();
            currentConstructStack.peek().addDescr(orDescr);
            currentConstructStack.push(orDescr);
            try {
                Object result = super.visitLhsPatternBind(ctx);
                List<? extends BaseDescr> descrs = orDescr.getDescrs();
                for (BaseDescr descr : descrs) {
                    PatternDescr patternDescr = (PatternDescr) descr;
                    if (ctx.label() != null) {
                        patternDescr.setIdentifier(ctx.label().IDENTIFIER().getText());
                    }
                }
                return result;
            } finally {
                currentConstructStack.pop();
            }
        } else {
            throw new IllegalStateException("ctx.lhsPattern().size() == 0 : " + ctx.getText());
        }
    }

    @Override
    public Object visitLhsPattern(DRLParser.LhsPatternContext ctx) {
        currentPattern = new PatternDescr(ctx.objectType.getText());
        if (ctx.patternSource() != null) {
            String expression = ctx.patternSource().getText();
            FromDescr from = new FromDescr();
            from.setDataSource(new MVELExprDescr(expression));
            from.setResource(currentPattern.getResource());
            currentPattern.setSource(from);
        }
        Object result = super.visitLhsPattern(ctx);
            currentConstructStack.peek().addDescr(currentPattern);
        currentPattern = null;
        return result;
    }

    @Override
    public Object visitConstraint(DRLParser.ConstraintContext ctx) {
        Object constraint = super.visitConstraint(ctx);
        if (constraint != null) {
            ExprConstraintDescr constr = new ExprConstraintDescr(constraint.toString());
            constr.setType(ExprConstraintDescr.Type.NAMED);
            currentPattern.addConstraint(constr);
        }
        return null;
    }

    @Override
    public Object visitExpression(DRLParser.ExpressionContext ctx) {
        return ctx.children.stream()
                .map(c -> c instanceof TerminalNode ? c : c.accept(this))
                .filter(Objects::nonNull)
                .map(Object::toString)
                .collect(Collectors.joining(" "));
    }

    @Override
    public Object visitIdentifier(DRLParser.IdentifierContext ctx) {
        if (ctx.IDENTIFIER() == null) {
            return "";
        } else {
            return ctx.IDENTIFIER().getText();
        }
    }

    @Override
    public Object visitLiteral(DRLParser.LiteralContext ctx) {
        ParseTree node = ctx;
        while (true) {
            if (node instanceof TerminalNode) {
                return node.toString();
            }
            if (node.getChildCount() != 1) {
                return super.visitLiteral(ctx);
            }
            node = node.getChild(0);
        }
    }

    @Override
    public Object visitDrlAnnotation(DRLParser.DrlAnnotationContext ctx) {
        AnnotationDescr annotationDescr = new AnnotationDescr(ctx.name.getText());
        annotationDescr.setValue(ctx.drlArguments().drlArgument(0).getText());
        currentRule.addAnnotation(annotationDescr);
        return super.visitDrlAnnotation(ctx);
    }

    @Override
    public Object visitAttribute(DRLParser.AttributeContext ctx) {
        AttributeDescr attributeDescr = new AttributeDescr(ctx.getChild(0).getText());
        if (ctx.getChildCount() > 1) {
            attributeDescr.setValue(ctx.getChild(1).getText());
        }
        currentRule.addAttribute(attributeDescr);
        return super.visitAttribute(ctx);
    }

    @Override
    public Object visitLhsExists(DRLParser.LhsExistsContext ctx) {
        ExistsDescr existsDescr = new ExistsDescr();
        currentConstructStack.peek().addDescr(existsDescr);
        currentConstructStack.push(existsDescr);
        try {
            return super.visitLhsExists(ctx);
        } finally {
            currentConstructStack.pop();
        }
    }

    @Override
    public Object visitLhsNot(DRLParser.LhsNotContext ctx) {
        NotDescr notDescr = new NotDescr();
        currentConstructStack.peek().addDescr(notDescr);
        currentConstructStack.push(notDescr);
        try {
            return super.visitLhsNot(ctx);
        } finally {
            currentConstructStack.pop();
        }
    }

    public PackageDescr getPackageDescr() {
        return packageDescr;
    }

    private void populateStartEnd(BaseDescr descr, ParserRuleContext ctx) {
        descr.setStartCharacter(ctx.getStart().getStartIndex());
        // TODO: Current DRL6Parser adds +1 for EndCharacter but it doesn't look reasonable. At the moment, I don't add. Instead, I fix unit tests.
        //       I will revisit if this is the right approach.
        descr.setEndCharacter(ctx.getStop().getStopIndex());
    }
}
