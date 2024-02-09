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
package org.drools.model.codegen.execmodel.drlx;

import java.util.ArrayDeque;
import java.util.Deque;

import org.drools.drl.ast.descr.PackageDescr;
import org.drools.drl.ast.dsl.CEDescrBuilder;
import org.drools.drl.ast.dsl.DescrFactory;
import org.drools.drl.ast.dsl.ImportDescrBuilder;
import org.drools.drl.ast.dsl.PackageDescrBuilder;
import org.drools.drl.ast.dsl.PatternDescrBuilder;
import org.drools.drl.ast.dsl.RuleDescrBuilder;
import org.drools.drl.parser.lang.ParseException;
import org.drools.mvel.parser.ast.expr.RuleConsequence;
import org.drools.mvel.parser.ast.expr.RuleDeclaration;
import org.drools.mvel.parser.ast.expr.RuleItem;
import org.drools.mvel.parser.ast.expr.RuleJoinedPatterns;
import org.drools.mvel.parser.ast.expr.RulePattern;
import org.drools.mvel.parser.ast.visitor.DrlVoidVisitor;
import org.drools.mvel.parser.printer.PrintUtil;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.PackageDeclaration;
import com.github.javaparser.ast.body.TypeDeclaration;
import com.github.javaparser.ast.modules.ModuleDeclaration;

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
    Deque<CEDescrBuilder<?, ?>> lhsList = new ArrayDeque<>();

    public void visit(RuleDeclaration decl, Void v) {
        this.ruleDescrBuilder = builder.newRule();
        ruleDescrBuilder.name(decl.getNameAsString());

        CEDescrBuilder<?, ?> lhs = ruleDescrBuilder.lhs();
        lhsList.push(lhs);
        for (RuleItem item : decl.getRuleBody().getItems()) {
            item.accept(this, v);
        }
        lhsList.pop();
        ruleDescrBuilder = null;
    }

    public void visit(RulePattern p, Void v) {
        CEDescrBuilder<?,?> lhs = lhsList.peek();
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
            CEDescrBuilder<?, ?> lhs = lhsList.peek().and();
            lhsList.push(lhs);
            for (RuleItem item : jp.getItems()) {
                item.accept(this, v);
            }
            lhsList.pop();
            return;
        }
        if (jp.getType() == RuleJoinedPatterns.Type.OR) {
            CEDescrBuilder<?, ?> lhs = lhsList.peek().or();
            lhsList.push(lhs);
            for (RuleItem ruleItem : jp.getItems()) {
                ruleItem.accept(this, v);
            }
            lhsList.pop();
            return;
        }
    }

}

