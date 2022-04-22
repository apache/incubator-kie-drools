package org.drools.parser;

import java.util.Objects;
import java.util.stream.Collectors;

import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.drools.drl.ast.descr.*;

public class DRLVisitorImpl extends DRLParserBaseVisitor<Object> {

    private final PackageDescr packageDescr = new PackageDescr();

    private RuleDescr currentRule;
    private PatternDescr currentPattern;

    @Override
    public Object visitCompilationUnit(DRLParser.CompilationUnitContext ctx) {
        return super.visitCompilationUnit(ctx);
    }

    @Override
    public Object visitPackagedef(DRLParser.PackagedefContext ctx) {
        packageDescr.setName(ctx.name.getText());
        return super.visitPackagedef(ctx);
    }

    @Override
    public Object visitUnitdef(DRLParser.UnitdefContext ctx) {
        packageDescr.setUnit(new UnitDescr(ctx.name.getText()));
        return super.visitUnitdef(ctx);
    }

    @Override
    public Object visitGlobaldef(DRLParser.GlobaldefContext ctx) {
        packageDescr.addGlobal(new GlobalDescr(ctx.IDENTIFIER().getText(), ctx.type().getText()));
        return super.visitGlobaldef(ctx);
    }

    @Override
    public Object visitImportdef(DRLParser.ImportdefContext ctx) {
        String imp = ctx.qualifiedName().getText() + (ctx.MUL() != null ? ".*" : "");
        packageDescr.addImport(new ImportDescr(imp));
        return super.visitImportdef(ctx);
    }

    @Override
    public Object visitRuledef(DRLParser.RuledefContext ctx) {
        currentRule = new RuleDescr(ctx.name.getText());
        currentRule.setConsequence(ctx.rhs().getText());
        packageDescr.addRule(currentRule);

        Object result = super.visitRuledef(ctx);
        currentRule = null;
        return result;
    }

    @Override
    public Object visitLhsPatternBind(DRLParser.LhsPatternBindContext ctx) {
        if (ctx.lhsPattern().size() == 1) {
            currentPattern = new PatternDescr(ctx.lhsPattern(0).objectType.getText());
            if (ctx.label() != null) {
                currentPattern.setIdentifier(ctx.label().IDENTIFIER().getText());
            }
            currentRule.getLhs().addDescr(currentPattern);
        }
        Object result = super.visitLhsPatternBind(ctx);
        currentPattern = null;
        return result;
    }

    @Override
    public Object visitConstraint(DRLParser.ConstraintContext ctx) {
        Object constraint = super.visitConstraint(ctx);
        ExprConstraintDescr constr = new ExprConstraintDescr( constraint.toString() );
        constr.setType( ExprConstraintDescr.Type.NAMED );
        currentPattern.addConstraint( constr );
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
        return ctx.IDENTIFIER().getText();
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

    public PackageDescr getPackageDescr() {
        return packageDescr;
    }
}
