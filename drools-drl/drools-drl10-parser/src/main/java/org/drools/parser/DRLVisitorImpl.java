package org.drools.parser;

import org.drools.drl.ast.descr.ImportDescr;
import org.drools.drl.ast.descr.PackageDescr;
import org.drools.drl.ast.descr.RuleDescr;

public class DRLVisitorImpl extends DRLBaseVisitor<Object> {

    private final PackageDescr packageDescr = new PackageDescr();

    @Override
    public Object visitCompilationunit(DRLParser.CompilationunitContext ctx) {
        return super.visitCompilationunit(ctx);
    }

    @Override
    public Object visitPackagedef(DRLParser.PackagedefContext ctx) {
        packageDescr.setName(ctx.FQNAME().getText());
        return super.visitPackagedef(ctx);
    }

    @Override
    public Object visitImportdef(DRLParser.ImportdefContext ctx) {
        String imp = ctx.FQNAME().getText() + (ctx.STAR() != null ? ".*" : "");
        packageDescr.addImport(new ImportDescr(imp));
        return super.visitImportdef(ctx);
    }

    @Override
    public Object visitRuledef(DRLParser.RuledefContext ctx) {
        RuleDescr rule = new RuleDescr(ctx.IDENTIFIER().getText());
        rule.setConsequence(ctx.rhs().getText());
        packageDescr.addRule(rule);
        return super.visitRuledef(ctx);
    }

    public PackageDescr getPackageDescr() {
        return packageDescr;
    }
}
