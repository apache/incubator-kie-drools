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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

    private final Map<Variable, Declaration> declarations = new HashMap<>();

    private Set<Variable> groupKeyVariables;
    private Map<Variable, Declaration> dependantDeclarations;

    private Map<Variable, Declaration> queryDeclarations;
    private Map<Variable, Accumulate> accumulateSource;
    private boolean afterAccumulate;

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
        Declaration existing = declarations.get( variable );
        if (existing == null) {
            declarations.put( variable, pattern.getDeclaration() );
        } else {
            Pattern oldPattern = existing.getPattern();
            for (Declaration declaration : declarations.values()) {
                if (declaration.getPattern() == oldPattern && declaration.getTypeName().equals( existing.getTypeName() )) {
                    declaration.setPattern( pattern );
                }
            }
        }

        Declaration dependant = dependantDeclarations == null ? null : dependantDeclarations.get( variable );
        if (dependant != null) {
            dependant.setPattern( pattern );
        }
    }

    Pattern getPattern( Variable variable ) {
        Declaration declaration = declarations.get( variable );
        return declaration == null ? null : declaration.getPattern();
    }

    Declaration getDeclaration( Variable variable ) {
        if (variable == null) {
            return null;
        }
        if ( variable.isFact() ) {
            Declaration declaration = declarations.get( variable );
            if (declaration == null) {
                declaration = getQueryDeclaration( variable );
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
        return queryDeclarations == null ? null : queryDeclarations.get( variable );
    }

    void addQueryDeclaration(Variable variable, Declaration declaration) {
        if ( queryDeclarations == null) {
            queryDeclarations = new HashMap<>();
        }
        queryDeclarations.put( variable, declaration );
    }

    void addDeclaration( Variable variable, Declaration declaration ) {
        declarations.put( variable, declaration );
    }

    void addGroupByDeclaration( Variable groupKeyVar, Variable dependingOn, Declaration declaration ) {
        addDeclaration( groupKeyVar, declaration );
        if ( groupKeyVariables == null) {
            groupKeyVariables = new HashSet<>();
        }
        groupKeyVariables.add( groupKeyVar );
        if (dependantDeclarations == null) {
            dependantDeclarations = new HashMap<>();
        }
        dependantDeclarations.put( dependingOn, declaration );
    }

    boolean isGroupKeyVariable( Variable groupKeyVar ) {
        return groupKeyVariables == null ? false : groupKeyVariables.contains( groupKeyVar );
    }

    Accumulate getAccumulateSource( Variable variable) {
        return accumulateSource == null ? null : accumulateSource.get( variable );
    }

    void addAccumulateSource(Variable variable, Accumulate accumulate) {
        if (accumulateSource == null) {
            accumulateSource = new HashMap<>();
        }
        accumulateSource.put( variable, accumulate );
        afterAccumulate = true;
    }

    public boolean isAfterAccumulate() {
        return afterAccumulate;
    }

    public void setAfterAccumulate( boolean afterAccumulate ) {
        this.afterAccumulate = afterAccumulate;
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
        declarations.forEach( ( var, decl) -> decls.put( var.getName(), decl ) );
        return decls;
    }

    public Class<?> getDeclarationClass(String name) {
        for (Map.Entry<Variable, Declaration> entry : declarations.entrySet()) {
            if (entry.getKey().getName().equals( name )) {
                return entry.getValue().getDeclarationClass();
            }
        }
        return null;
    }
}
