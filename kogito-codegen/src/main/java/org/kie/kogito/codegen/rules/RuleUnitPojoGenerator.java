/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 *
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.kogito.codegen.rules;

import java.util.Collections;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import org.drools.modelcompiler.builder.JavaParserCompiler;
import org.kie.internal.ruleunit.RuleUnitVariable;
import org.kie.kogito.codegen.FileGenerator;
import org.kie.kogito.rules.DataStore;
import org.kie.kogito.rules.RuleUnitData;
import org.kie.kogito.rules.units.GeneratedRuleUnitDescription;

public class RuleUnitPojoGenerator implements FileGenerator {

    private final GeneratedRuleUnitDescription ruleUnitDescription;
    private final RuleUnitHelper ruleUnitHelper;

    public RuleUnitPojoGenerator(GeneratedRuleUnitDescription ruleUnitDescription, RuleUnitHelper ruleUnitHelper) {
        this.ruleUnitDescription = ruleUnitDescription;
        this.ruleUnitHelper = ruleUnitHelper;
    }

    @Override
    public String generate() {
        return JavaParserCompiler.toPojoSource(
                ruleUnitDescription.getPackageName(),
                Collections.emptyList(),
                Collections.emptyList(),
                classOrInterfaceDeclaration());
    }

    private ClassOrInterfaceDeclaration classOrInterfaceDeclaration() {
        ClassOrInterfaceDeclaration c =
                new ClassOrInterfaceDeclaration()
                        .setPublic(true)
                        .addImplementedType(RuleUnitData.class.getCanonicalName())
                        .setName(ruleUnitDescription.getSimpleName());

        for (RuleUnitVariable v : ruleUnitDescription.getUnitVarDeclarations()) {
            ClassOrInterfaceType t = new ClassOrInterfaceType()
                    .setName(v.getType().getCanonicalName());
            FieldDeclaration f = new FieldDeclaration();
            VariableDeclarator vd = new VariableDeclarator(t, v.getName());
            f.getVariables().add(vd);
            if (v.isDataSource()) {
                t.setTypeArguments( StaticJavaParser.parseType( v.getDataSourceParameterType().getCanonicalName() ) );
                if (ruleUnitHelper.isAssignableFrom(DataStore.class, v.getType())) {
                    vd.setInitializer("org.kie.kogito.rules.DataSource.createStore()");
                } else {
                    vd.setInitializer("org.kie.kogito.rules.DataSource.createSingleton()");
                }
            }
            c.addMember(f);
            f.createGetter();
            if (v.setter() != null) {
                f.createSetter();
            }
        }

        return c;
    }

    public String generatedFilePath() {
        return ruleUnitDescription.getPackageName().replace('.', '/') + "/" + ruleUnitDescription.getSimpleName() + ".java";
    }

    public String getClassName() {
        return ruleUnitDescription.getRuleUnitName();
    }
}
