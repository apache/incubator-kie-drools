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
package org.drools.compiler.rule.builder;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Type;
import java.util.Optional;

import org.drools.base.definitions.InternalKnowledgePackage;
import org.drools.base.definitions.rule.impl.RuleImpl;
import org.drools.base.rule.EntryPointId;
import org.drools.base.rule.Pattern;
import org.drools.base.rule.accessor.DeclarationScopeResolver;
import org.drools.compiler.builder.impl.TypeDeclarationContext;
import org.drools.compiler.compiler.Dialect;
import org.drools.compiler.compiler.DialectCompiletimeRegistry;
import org.drools.compiler.compiler.RuleBuildError;
import org.drools.core.common.TruthMaintenanceSystemFactory;
import org.drools.drl.ast.descr.QueryDescr;
import org.drools.drl.ast.descr.RuleDescr;
import org.drools.util.TypeResolver;
import org.kie.internal.ruleunit.RuleUnitComponentFactory;
import org.kie.internal.ruleunit.RuleUnitDescription;

/**
 * A context for the current build
 */
public class RuleBuildContext extends PackageBuildContext {

    // current rule
    private final RuleImpl rule;

    // current Rule descriptor
    private final RuleDescr ruleDescr;

    // available declarationResolver 
    private DeclarationScopeResolver declarationResolver;

    // a simple counter for patterns
    private int patternId = -1;

    private boolean needStreamMode = false;

    private Pattern prefixPattern;

    private boolean inXpath;

    private int xpathChuckNr = 0;

    private int xpathOffsetadjustment = 0;

    /**
     * Default constructor
     */
    public RuleBuildContext(final TypeDeclarationContext kBuilder,
                            final RuleDescr ruleDescr,
                            final DialectCompiletimeRegistry dialectCompiletimeRegistry,
                            final InternalKnowledgePackage pkg,
                            final Dialect defaultDialect) {
        this.ruleDescr = ruleDescr;

        this.rule = ruleDescr instanceof QueryDescr ? TruthMaintenanceSystemFactory.createQuery(ruleDescr.getName(), ruleDescr::hasAnnotation) : descrToRule(ruleDescr);
        this.rule.setPackage(pkg.getName());
        this.rule.setDialect(ruleDescr.getDialect());
        this.rule.setLoadOrder(ruleDescr.getLoadOrder());

        initContext(kBuilder, pkg, ruleDescr, dialectCompiletimeRegistry, defaultDialect, this.rule);

        if (this.rule.getDialect() == null) {
            this.rule.setDialect(getDialect().getId());
        }

        if (ruleDescr.getUnit() != null) {
            rule.setRuleUnitClassName(pkg.getName() + "." + ruleDescr.getUnit().getTarget().replace('.', '$'));
        }

        Dialect dialect = getDialect();
        if (dialect != null) {
            dialect.init(ruleDescr);
        }

        this.declarationResolver = new DeclarationScopeResolver(kBuilder.getGlobals(), getPkg());
    }

    /**
     * Returns the current Rule being built
     */
    public RuleImpl getRule() {
        return this.rule;
    }

    /**
     * Returns the current RuleDescriptor
     */
    public RuleDescr getRuleDescr() {
        return this.ruleDescr;
    }

    /**
     * Returns the available declarationResolver instance
     */
    public DeclarationScopeResolver getDeclarationResolver() {
        return this.declarationResolver;
    }

    /**
     * Sets the available declarationResolver instance
     */
    public void setDeclarationResolver(final DeclarationScopeResolver declarationResolver) {
        this.declarationResolver = declarationResolver;
    }

    public int getNextPatternId() {
        return ++this.patternId;
    }

    public boolean needsStreamMode() {
        return needStreamMode;
    }

    public void setNeedStreamMode() {
        this.needStreamMode = true;
    }

    public void setPrefixPattern(Pattern prefixPattern) {
        this.prefixPattern = prefixPattern;
    }

    public Pattern getPrefixPattern() {
        return prefixPattern;
    }

    public boolean isInXpath() {
        return inXpath;
    }

    public void setInXpath(boolean inXpath) {
        this.inXpath = inXpath;
    }

    public void initRule() {
        initRuleUnitClassName();
        declarationResolver.setRule(rule);
    }

    @Override
    public Type resolveVarType(String identifier) {
        return getDeclarationResolver().resolveVarType(identifier);
    }

    private void initRuleUnitClassName() {
        String ruleUnitClassName = rule.getRuleUnitClassName();
        boolean nameInferredFromResource = false;

        if (ruleUnitClassName == null && rule.getResource() != null && rule.getResource().getSourcePath() != null) {
            // We cannot depend on splitting based on File.separator, because e.g. MemoryFileSystem is "/" based
            // also on Windows => We need to parse the classname based on Java classname allowed characters.
            ruleUnitClassName = extractClassNameFromSourcePath();
            nameInferredFromResource = true;
        }

        if (RuleUnitComponentFactory.get() != null && ruleUnitClassName != null) {
            TypeResolver typeResolver = getPkg().getTypeResolver();
            boolean unitFound = false;
            Class<?> ruleUnitClass = null;
            try {
                ruleUnitClass = typeResolver.resolveType(ruleUnitClassName);
            } catch (ClassNotFoundException e) {
                if (!nameInferredFromResource) {
                    addError(new RuleBuildError(rule, getParentDescr(), null,
                            "Cannot find rule unit class " + ruleUnitClassName));
                    return;
                }
            }
            if (ruleUnitClass != null) {
                unitFound = RuleUnitComponentFactory.get().isRuleUnitClass( ruleUnitClass );
                if (unitFound && nameInferredFromResource) {
                    rule.setRuleUnitClassName(ruleUnitClassName);
                }

                try {
                    for (PropertyDescriptor prop : Introspector.getBeanInfo(ruleUnitClass).getPropertyDescriptors()) {
                        if (!"class".equals(prop.getName())) {
                            getPkg().addGlobal(prop.getName(), prop.getPropertyType());
                        }
                    }
                } catch (IntrospectionException e) {
                    throw new RuntimeException();
                }
            }

            if (!unitFound && !nameInferredFromResource) {
                addError(new RuleBuildError(rule, getParentDescr(), null,
                                            ruleUnitClassName + " must implement RuleUnitData"));
            }
        }
    }

    public Optional<EntryPointId> getEntryPointId(String name) {
        return getPkg().getRuleUnitDescriptionLoader().getDescription(getRule()).flatMap(ruDescr -> getEntryPointId(ruDescr, name));
    }

    public Optional<EntryPointId> getEntryPointId( RuleUnitDescription ruDescr, String name ) {
        return ruDescr.hasVar( name ) ? Optional.of( new EntryPointId( name ) ) : Optional.empty();
    }

    private String extractClassNameFromSourcePath() {
        String drlPath = rule.getResource().getSourcePath();
        final int fileTypeDotIndex = drlPath.lastIndexOf('.');
        // If '.' is the first character, it may be a path like ./somepath/something/etc,
        // otherwise, if found somewhere, remove it with file type suffix
        if (fileTypeDotIndex > 0) {
            drlPath = drlPath.substring(0, fileTypeDotIndex);
        }

        StringBuilder classNameBuilder = new StringBuilder();
        int actualIndex = drlPath.length() - 1;
        char actualChar = drlPath.charAt(actualIndex);
        while (Character.isJavaIdentifierPart(actualChar) || Character.isJavaIdentifierStart(actualChar)) {
            classNameBuilder.append(actualChar);
            actualIndex--;
            if (actualIndex >= 0) {
                actualChar = drlPath.charAt(actualIndex);
            } else {
                break;
            }
        }

        return rule.getPackage() + "." + classNameBuilder.reverse();
    }

    public void increaseXpathChuckNr() {
        xpathChuckNr++;
    }

    public void resetXpathChuckNr() {
        xpathChuckNr = 0;
    }

    public int getXpathChuckNr() {
        return xpathChuckNr + xpathOffsetadjustment;
    }

    public void setXpathChuckNr(int chunkNbr) {
        this.xpathChuckNr = chunkNbr + xpathOffsetadjustment;
    }

    public void setXpathOffsetadjustment(int xpathOffsetadjustment) {
        this.xpathOffsetadjustment = xpathOffsetadjustment;
    }

    public int getXpathOffsetadjustment() {
        return xpathOffsetadjustment;
    }

    public static RuleImpl descrToRule(RuleDescr ruleDescr) {
        RuleImpl rule = new RuleImpl( ruleDescr.getName() );
        rule.setResource( rule.getResource() );
        return rule;
    }
}
