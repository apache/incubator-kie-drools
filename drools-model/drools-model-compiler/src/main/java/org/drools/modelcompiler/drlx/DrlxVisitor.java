package org.drools.modelcompiler.drlx;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.PackageDeclaration;
import com.github.javaparser.ast.body.TypeDeclaration;
import com.github.javaparser.ast.modules.ModuleDeclaration;
import org.drools.compiler.lang.ParseException;
import org.drools.drl.ast.descr.*;
import org.drools.drl.ast.dsl.*;
import org.drools.mvel.parser.ast.expr.*;
import org.drools.mvel.parser.ast.visitor.DrlVoidVisitor;
import org.drools.mvel.parser.printer.PrintUtil;

import java.util.Stack;

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

    RuleDescrBuilder ruleDescrBuilder;
    Stack<CEDescrBuilder<?, ?>> lhsStack = new Stack<>();

    public void visit(RuleDeclaration decl, Void v) {
        this.ruleDescrBuilder = builder.newRule();
        ruleDescrBuilder.name(decl.getNameAsString());

        CEDescrBuilder<?, ?> lhs = ruleDescrBuilder.lhs();
        lhsStack.push(lhs);
        for (RuleItem item : decl.getRuleBody().getItems()) {
            item.accept(this, v);
        }
        lhsStack.pop();
        ruleDescrBuilder = null;
    }

    public void visit(RulePattern p, Void v) {
        CEDescrBuilder<?,?> lhs = lhsStack.peek();
        PatternDescrBuilder<? extends CEDescrBuilder<?, ?>> pat = lhs.pattern();
        if (p.getBind() == null) {
            pat.constraint(PrintUtil.printNode(p.getExpr()));
        } else {
            pat.id(PrintUtil.printNode(p.getBind()), false).constraint(PrintUtil.printNode(p.getExpr()));
        }

    }

    public void visit(RuleConsequence c, Void v) {
        ruleDescrBuilder.rhs(PrintUtil.printNode(c.getStatement()));
    }

    public void visit(RuleJoinedPatterns jp, Void v) {
        if (jp.getType() == RuleJoinedPatterns.Type.AND) {
            CEDescrBuilder<?, ?> lhs = lhsStack.peek().and();
            lhsStack.push(lhs);
            for (RuleItem item : jp.getItems()) {
                item.accept(this, v);
            }
            lhsStack.pop();
            return;
        }
        if (jp.getType() == RuleJoinedPatterns.Type.OR) {
            CEDescrBuilder<?, ?> lhs = lhsStack.peek().or();
            lhsStack.push(lhs);
            for (RuleItem ruleItem : jp.getItems()) {
                ruleItem.accept(this, v);
            }
            lhsStack.pop();
            return;
        }
    }

}

