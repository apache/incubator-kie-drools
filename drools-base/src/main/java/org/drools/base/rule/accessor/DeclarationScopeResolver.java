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
package org.drools.base.rule.accessor;

import java.lang.reflect.Type;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;

import org.drools.base.base.AcceptsClassObjectType;
import org.drools.base.base.ClassObjectType;
import org.drools.base.definitions.InternalKnowledgePackage;
import org.drools.base.definitions.rule.impl.RuleImpl;
import org.drools.base.rule.Declaration;
import org.drools.base.rule.GroupElement;
import org.drools.base.rule.Pattern;
import org.drools.base.rule.RuleConditionElement;
import org.kie.internal.ruleunit.RuleUnitDescription;

import static org.drools.util.ClassUtils.rawType;
import static org.kie.internal.ruleunit.RuleUnitUtil.RULE_UNIT_DECLARATION;

/**
 * A class capable of resolving a declaration in the current build context
 */
public class DeclarationScopeResolver {
    private final Deque<RuleConditionElement>   buildList;
    private final Map<String, Type>              globalMap;
    private final InternalKnowledgePackage           pkg;

    private RuleImpl rule;
    private Optional<RuleUnitDescription> ruleUnitDescr = Optional.empty();

    protected DeclarationScopeResolver() {
        this( new HashMap<>(), new ArrayDeque<>() );
    }

    public DeclarationScopeResolver(final Map<String, Type> globalMap,
            final Deque<RuleConditionElement> buildList) {
        this(globalMap, buildList, null);
    }

    public DeclarationScopeResolver(final Map<String, Type> globalMap,
                                    final InternalKnowledgePackage pkg) {
        this( globalMap, new ArrayDeque<>(), pkg );
    }

    private DeclarationScopeResolver(Map<String, Type> globalMap,
                                     Deque<RuleConditionElement> buildList,
                                     InternalKnowledgePackage pkg) {
        this.globalMap = globalMap;
        this.buildList = buildList;
        this.pkg = pkg;
    }

    public void setRule(RuleImpl rule) {
        this.rule = rule;
        this.ruleUnitDescr = pkg.getRuleUnitDescriptionLoader().getDescription(rule );
    }

    public RuleConditionElement peekBuildStack() {
        return buildList.peek();
    }

    public RuleConditionElement popBuildStack() {
        return buildList.pop();
    }

    public void pushOnBuildStack(RuleConditionElement element) {
        buildList.push(element);
    }

    private Declaration getExtendedDeclaration(RuleImpl rule, String identifier) {
        Declaration declaration = rule.getLhs().resolveDeclaration( identifier );
        if ( declaration != null ) {
            return declaration;
        }
        return rule.getParent() == null ? null : getExtendedDeclaration( rule.getParent(), identifier );

    }

    private Map<String, Declaration> getAllExtendedDeclaration(RuleImpl rule, Map<String, Declaration> dec) {
        dec.putAll( rule.getLhs().getInnerDeclarations() );
        if ( null != rule.getParent() ) {
            return getAllExtendedDeclaration( rule.getParent(),
                                              dec );
        }
        return dec;
    }

    public Declaration getDeclaration(String identifier) {
        // it may be a local bound variable
        for (final Iterator<RuleConditionElement> iterator = buildList.descendingIterator(); iterator.hasNext();) {
            final Declaration declaration = iterator.next().resolveDeclaration( identifier );
            if ( declaration != null ) {
                return declaration;
            }
        }
        // look at parent rules
        if ( rule != null && rule.getParent() != null ) {
            // recursive algorithm for each parent
            //     -> lhs.getInnerDeclarations()
            Declaration parentDeclaration = getExtendedDeclaration( rule.getParent(), identifier );
            if ( null != parentDeclaration ) {
                return parentDeclaration;
            }
        }

        // it may be a global or a rule unit variable
        Type type = resolveVarType( identifier );
        if ( type != null ) {
            ClassObjectType classObjectType = new ClassObjectType( rawType(type) );

            Declaration declaration;
            final Pattern dummy = new Pattern( 0,
                                               classObjectType );

            ReadAccessor globalExtractor = new GlobalExtractor( identifier, classObjectType );//FieldAccessorFactory.get().getGlobalReadAccessor( identifier, classObjectType );
            declaration = new Declaration( identifier, globalExtractor, dummy );
            if ( pkg != null ) {

                // make sure dummy and globalExtractor are wired up to correct ClassObjectType
                // and set as targets for rewiring
                pkg.wireObjectType( classObjectType, dummy );
                pkg.wireObjectType( classObjectType, (AcceptsClassObjectType) globalExtractor );
            }
            return declaration;
        }
        return null;
    }

    public Type resolveVarType( String identifier ) {
        return ruleUnitDescr.flatMap( unit -> unit.getVarType( identifier ) ) // resolve identifier on rule unit ...
                            .orElseGet( () -> globalMap.get( identifier ) );  // ... or alternatively among globals
    }

    public String normalizeValueForUnit( String value ) {
        return ruleUnitDescr.map( unit -> {
            int dotPos = value.indexOf( '.' );
            String firstPart = dotPos > 0 ? value.substring( 0, dotPos ) : value;
            return unit.hasVar( firstPart ) ? RULE_UNIT_DECLARATION + "." + value : value;
        }).orElse( value );
    }

    public boolean hasDataSource( String name ) {
        return ruleUnitDescr.map( descr -> descr.hasDataSource( name )).orElse( false );
    }

    public boolean available(RuleImpl rule,
                             final String name) {
        for (final Iterator<RuleConditionElement> iterator = buildList.descendingIterator(); iterator.hasNext();) {
            RuleConditionElement rce = iterator.next();
            final Declaration declaration = rce.resolveDeclaration( name );
            if ( declaration != null ) {
                return true;
            }
        }
        if ( this.globalMap.containsKey( (name) ) ) {
            return true;
        }

        // look at parent rules
        if ( rule != null && rule.getParent() != null ) {
            // recursive algorithm for each parent
            //     -> lhs.getInnerDeclarations()
            Declaration parentDeclaration = getExtendedDeclaration( rule.getParent(),
                                                                    name );
            if ( null != parentDeclaration ) {
                return true;
            }
        }
        return false;
    }

    public boolean isDuplicated(RuleImpl rule,
                                final String name,
                                final String type) {
        if ( this.globalMap.containsKey( (name) ) ) {
            return true;
        }
        
        
        for (final Iterator<RuleConditionElement> iterator = buildList.descendingIterator(); iterator.hasNext();) {
            RuleConditionElement rce = iterator.next();
            final Declaration declaration = rce.resolveDeclaration( name );
            if ( declaration != null ) {
                // if it is an OR and it is duplicated, we can stop looking for duplication now
                // as it is a separate logical branch
                boolean inOr = (rce instanceof GroupElement ge && ge.isOr());
                if ( ! inOr || type == null ) {
                    return ! inOr;
                }
                return ! declaration.getDeclarationClass().getName().equals( type );
            }
        }
        // look at parent rules
        if ( rule != null && rule.getParent() != null ) {
            // recursive algorithm for each parent
            //     -> lhs.getInnerDeclarations()
            Declaration parentDeclaration = getExtendedDeclaration( rule.getParent(), name );
            return null != parentDeclaration;
        }
        return false;
    }

    public Map<String, Declaration> getDeclarations(RuleImpl rule) {
        return getDeclarations(rule, RuleImpl.DEFAULT_CONSEQUENCE_NAME);
    }

    /**
     * Return all declarations scoped to the current
     * RuleConditionElement in the build stack
     */
    public Map<String, Declaration> getDeclarations(RuleImpl rule, String consequenceName) {
        Map<String, Declaration> declarations = new HashMap<>();
        for (RuleConditionElement aBuildList : this.buildList) {
            // if we are inside of an OR we don't want each previous stack entry added because we can't see those variables
            if (aBuildList instanceof GroupElement ge && ge.getType() == GroupElement.Type.OR) {
                continue;
            }

            // this may be optimized in the future to only re-add elements at
            // scope breaks, like "NOT" and "EXISTS"
            Map<String,Declaration> innerDeclarations = aBuildList instanceof GroupElement ge ?
                    ge.getInnerDeclarations(consequenceName) :
                    aBuildList.getInnerDeclarations();
            declarations.putAll(innerDeclarations);
        }
        if ( null != rule.getParent() ) {
            return getAllExtendedDeclaration( rule.getParent(), declarations );
        }
        return declarations;
    }

    public Map<String,Class<?>> getDeclarationClasses(RuleImpl rule) {
        return getDeclarationClasses( getDeclarations( rule ) );
    }

    public static Map<String,Class<?>> getDeclarationClasses( final Map<String, Declaration> declarations) {
        final Map<String, Class<?>> classes = new HashMap<>();
        for ( Map.Entry<String, Declaration> decl : declarations.entrySet() ) {
            Class<?> declarationClass = decl.getValue().getDeclarationClass();
            // the declaration class could be null when there's a compilation error
            // that has been already reported somewhere else
            if (declarationClass != null) {
                classes.put( decl.getKey(), declarationClass );
            }
        }
        return classes;
    }

    public Pattern findPatternById(int id) {
        if ( !this.buildList.isEmpty() ) {
            return findPatternInNestedElements( id, buildList.peekFirst() );
        }
        return null;
    }

    private Pattern findPatternInNestedElements(final int id,
                                                final RuleConditionElement rce) {
        for ( RuleConditionElement element : rce.getNestedElements() ) {
            if ( element instanceof Pattern p ) {
                if (p.getPatternId() == id ) {
                    return p;
                }
            } else if ( !element.isPatternScopeDelimiter() ) {
                Pattern p = findPatternInNestedElements( id,
                                                         element );
                if ( p != null ) {
                    return p;
                }
            }
        }
        return null;
    }
}
