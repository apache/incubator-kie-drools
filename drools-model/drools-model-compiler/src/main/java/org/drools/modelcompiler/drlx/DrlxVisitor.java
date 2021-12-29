package org.drools.modelcompiler.drlx;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.PackageDeclaration;
import com.github.javaparser.ast.body.TypeDeclaration;
import com.github.javaparser.ast.modules.ModuleDeclaration;
import org.drools.compiler.lang.ParseException;
import org.drools.drl.ast.descr.AndDescr;
import org.drools.drl.ast.descr.PackageDescr;
import org.drools.drl.ast.dsl.*;
import org.drools.mvel.parser.ast.expr.RuleConsequence;
import org.drools.mvel.parser.ast.expr.RuleDeclaration;
import org.drools.mvel.parser.ast.expr.RuleItem;
import org.drools.mvel.parser.ast.expr.RulePattern;
import org.drools.mvel.parser.ast.visitor.DrlVoidVisitor;
import org.drools.mvel.parser.printer.PrintUtil;

public class DrlxVisitor implements DrlVoidVisitor<Void> {

    private final PackageDescrBuilder builder = DescrFactory.newPackage();

    public PackageDescr getPackageDescr() {
        return builder.getDescr();
    }

    public void visit(CompilationUnit u, Void arg) {
        PackageDeclaration packageDeclaration = u.getPackageDeclaration()
                .orElseThrow(() -> new ParseException("Expected package declaration.", -1));
        String pkgName = packageDeclaration.getNameAsString();
        builder.name(pkgName);

        for (ImportDeclaration i : u.getImports()) {
            this.visit(i, null);
        }
        ModuleDeclaration moduleDeclaration = u.getModule()
                .orElseThrow(() -> new ParseException("Expected unit declaration.", -1));
        builder.newUnit().target(String.format("%s.%s", pkgName, moduleDeclaration.getNameAsString()));

        for (TypeDeclaration<?> typeDeclaration : u.getTypes()) {
            RuleDeclaration rd = (RuleDeclaration) typeDeclaration;
            this.visit(rd, null);
        }
    }

    @Override
    public void visit(ImportDeclaration decl, Void v) {
        ImportDescrBuilder importDescrBuilder = builder.newImport();
        importDescrBuilder.target(decl.getNameAsString());
    }

    public void visit(RuleDeclaration decl, Void v) {
        RuleDescrBuilder ruleDescrBuilder = builder.newRule();
        ruleDescrBuilder.name(decl.getNameAsString());
        CEDescrBuilder<?, AndDescr> lhs = ruleDescrBuilder.lhs().and();
        for (RuleItem item : decl.getRuleBody().getItems()) {
            if (item instanceof RulePattern) {
                PatternDescrBuilder<? extends CEDescrBuilder<?, AndDescr>> pat = lhs.pattern();
                RulePattern p = (RulePattern) item;
                if (p.getBind() == null) {
                    pat.constraint(PrintUtil.printNode(p.getExpr()));
                } else {
                    pat.id(PrintUtil.printNode(p.getBind()), false).constraint(PrintUtil.printNode(p.getExpr()));
                }
            } else if (item instanceof RuleConsequence) {
                RuleConsequence c = (RuleConsequence) item;
                ruleDescrBuilder.rhs(PrintUtil.printNode(c.getStatement()));
            } else {
                throw new IllegalArgumentException(item.getClass().getCanonicalName());
            }
        }
    }
}
