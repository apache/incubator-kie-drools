package org.drools.parser;

import org.drools.drl.ast.descr.*;

public class DRLVisitorImpl extends DRLParserBaseVisitor<Object> {

    private final PackageDescr packageDescr = new PackageDescr();

    private RuleDescr ruleDescr;

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
        ruleDescr = new RuleDescr(ctx.name.getText());
        ruleDescr.setConsequence(ctx.rhs().getText());
        packageDescr.addRule(ruleDescr);

        Object result = super.visitRuledef(ctx);
        ruleDescr = null;
        return result;
    }

    @Override
    public Object visitLhsPatternBind(DRLParser.LhsPatternBindContext ctx) {
        if (ctx.lhsPattern().size() == 1) {
            PatternDescr patternDescr = new PatternDescr(ctx.lhsPattern(0).objectType.getText());
            if (ctx.label() != null) {
                patternDescr.setIdentifier(ctx.label().IDENTIFIER().getText());
            }
            ruleDescr.getLhs().addDescr(patternDescr);
        }
        return super.visitLhsPatternBind(ctx);
    }

    @Override
    public Object visitDrlAnnotation(DRLParser.DrlAnnotationContext ctx) {
        AnnotationDescr annotationDescr = new AnnotationDescr(ctx.name.getText());
        annotationDescr.setValue(ctx.drlArguments().drlArgument(0).getText());
        ruleDescr.addAnnotation(annotationDescr);
        return super.visitDrlAnnotation(ctx);
    }

    @Override
    public Object visitAttribute(DRLParser.AttributeContext ctx) {
        AttributeDescr attributeDescr = new AttributeDescr(ctx.getChild(0).getText());
        if (ctx.getChildCount() > 1) {
            attributeDescr.setValue(ctx.getChild(1).getText());
        }
        ruleDescr.addAttribute(attributeDescr);
        return super.visitAttribute(ctx);
    }

    public PackageDescr getPackageDescr() {
        return packageDescr;
    }
}
