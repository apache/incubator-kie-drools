/*
 * Copyright 2005 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.modelcompiler;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.drools.core.definitions.InternalKnowledgePackage;
import org.drools.core.definitions.impl.KnowledgePackageImpl;
import org.drools.core.definitions.rule.impl.RuleImpl;
import org.drools.core.rule.Accumulate;
import org.drools.core.rule.Declaration;
import org.drools.core.rule.Pattern;
import org.drools.core.spi.GlobalExtractor;
import org.drools.core.spi.InternalReadAccessor;
import org.drools.core.spi.ObjectType;
import org.drools.model.Global;
import org.drools.model.Rule;
import org.drools.model.Variable;

public class RuleContext {

    private final KiePackagesBuilder builder;
    private final KnowledgePackageImpl pkg;
    private final RuleImpl rule;

    private final Map<Variable, Declaration> innerDeclaration = new HashMap<>();

    private Map<Variable, Declaration> queryDeclaration;
    private Map<Variable, Accumulate> accumulateSource;

    private final Map<Variable, Pattern> patterns = new HashMap<>();

    private List<Rule> subRules;

    private int patternIndex = -1;
    private boolean needStreamMode = false;

    public RuleContext( KiePackagesBuilder builder, KnowledgePackageImpl pkg, RuleImpl rule ) {
        this.builder = builder;
        this.pkg = pkg;
        this.rule = rule;
    }

    /**
     * All KiePackage known to the KiePackagesBuilder
     */
    public Collection<InternalKnowledgePackage> getKnowledgePackages() {
        return builder.getKiePackages();
    }

    public KnowledgePackageImpl getPkg() {
        return pkg;
    }

    public RuleImpl getRule() {
        return rule;
    }

    int getNextPatternIndex() {
        return ++patternIndex;
    }

    void registerPattern( Variable variable, Pattern pattern ) {
        patterns.put( variable, pattern );
    }

    Pattern getPattern( Variable variable ) {
        return patterns.get( variable );
    }

    Declaration getDeclaration( Variable variable ) {
        if (variable == null) {
            return null;
        }
        if ( variable.isFact() ) {
            Declaration declaration = innerDeclaration.get( variable );
            if (declaration == null) {
                declaration = getQueryDeclaration( variable );
            }
            if (declaration == null) {
                Pattern pattern = patterns.get( variable );
                declaration = pattern != null ? pattern.getDeclaration() : null;
            }
            return declaration;
        } else {
            Global global = (( Global ) variable);
            ObjectType objectType = builder.getObjectType( global );
            InternalReadAccessor globalExtractor = new GlobalExtractor( global.getName(), objectType );
            return new Declaration( global.getName(), globalExtractor, new Pattern( 0, objectType ) );
        }
    }

    Declaration getQueryDeclaration( Variable variable ) {
        return queryDeclaration == null ? null : queryDeclaration.get( variable );
    }

    void addQueryDeclaration(Variable variable, Declaration declaration) {
        if (queryDeclaration == null) {
            queryDeclaration = new HashMap<>();
        }
        queryDeclaration.put( variable, declaration );
    }

    void addInnerDeclaration(Variable variable, Declaration declaration) {
        innerDeclaration.put( variable, declaration );
    }

    Accumulate getAccumulateSource( Variable variable) {
        return accumulateSource == null ? null : accumulateSource.get( variable );
    }

    void addAccumulateSource(Variable variable, Accumulate accumulate) {
        if (accumulateSource == null) {
            accumulateSource = new HashMap<>();
        }
        accumulateSource.put( variable, accumulate );
    }

    boolean hasSubRules() {
        return subRules != null;
    }

    List<Rule> getSubRules() {
        return subRules == null ? Collections.emptyList() : subRules;
    }

    public void addSubRule(Rule rule) {
        if (subRules == null) {
            subRules = new ArrayList<>();
        }
        subRules.add( rule );
    }

    public ClassLoader getClassLoader() {
        return builder.getClassLoader();
    }

    public boolean needsStreamMode() {
        return needStreamMode;
    }

    public void setNeedStreamMode() {
        this.needStreamMode = true;
    }

    public Map<String, Declaration> getDeclarations() {
        Map<String, Declaration> decls = new HashMap<>();
        innerDeclaration.forEach( (var, decl) -> decls.put( var.getName(), decl ) );
        patterns.forEach( (var, pattern) -> decls.put( var.getName(), pattern.getDeclaration() ) );
        return decls;
    }

    public Class<?> getDeclarationClass(String name) {
        for (Map.Entry<Variable, Declaration> entry : innerDeclaration.entrySet()) {
            if (entry.getKey().getName().equals( name )) {
                return entry.getValue().getDeclarationClass();
            }
        }
        for (Map.Entry<Variable, Pattern> entry : patterns.entrySet()) {
            if (entry.getKey().getName().equals( name )) {
                return entry.getValue().getDeclaration().getDeclarationClass();
            }
        }
        return null;
    }
}
