/*
 * Copyright 2010 JBoss Inc
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

package org.drools.core.spi;

import org.drools.core.base.ClassObjectType;
import org.drools.core.definitions.InternalKnowledgePackage;
import org.drools.core.definitions.rule.impl.RuleImpl;
import org.drools.core.rule.Declaration;
import org.drools.core.rule.GroupElement;
import org.drools.core.rule.Pattern;
import org.drools.core.rule.RuleConditionElement;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

/**
 * A class capable of resolving a declaration in the current build context
 */
public class DeclarationScopeResolver {
    private final Stack<RuleConditionElement>        buildStack;
    private final Map<String, Class<?>>              map;
    private final InternalKnowledgePackage           pkg;

    private final Map<String, Declaration> declarations = new HashMap<String, Declaration>();

    public DeclarationScopeResolver(final Map<String, Class<?>> map) {
        this( map, new Stack<RuleConditionElement>() );
    }

    public DeclarationScopeResolver(final Map<String, Class<?>> map,
                                    final Stack<RuleConditionElement> buildStack) {
        this( map, buildStack, null );
    }

    public DeclarationScopeResolver(final Map<String, Class<?>> map,
                                    final InternalKnowledgePackage pkg) {
        this( map, new Stack<RuleConditionElement>(), pkg );
    }

    public DeclarationScopeResolver(final Map<String, Class<?>> map,
                                    final Stack<RuleConditionElement> buildStack,
                                    final InternalKnowledgePackage pkg) {
        this.map = map;
        this.buildStack = buildStack;
        this.pkg = pkg;
    }

    public RuleConditionElement peekBuildStack() {
        return buildStack.peek();
    }

    public RuleConditionElement popBuildStack() {
        return buildStack.pop();
    }

    public void pushOnBuildStack(RuleConditionElement element) {
        buildStack.push(element);
    }

    private Declaration getExtendedDeclaration(RuleImpl rule,
                                               String identifier) {
        if ( rule.getLhs().getInnerDeclarations().containsKey( identifier ) ) {
            return rule.getLhs().getInnerDeclarations().get( identifier );
        } else if ( null != rule.getParent() ) {
            return getExtendedDeclaration( rule.getParent(),
                                           identifier );
        }
        return null;

    }

    private Map<String, Declaration> getAllExtendedDeclaration(RuleImpl rule,
                                                               Map<String, Declaration> dec) {
        dec.putAll( rule.getLhs().getInnerDeclarations() );
        if ( null != rule.getParent() ) {
            return getAllExtendedDeclaration( rule.getParent(),
                                              dec );
        }
        return dec;

    }

    public Declaration getDeclaration(final RuleImpl rule,
                                      final String identifier) {
        // it may be a local bound variable
        for ( int i = this.buildStack.size() - 1; i >= 0; i-- ) {
            final Declaration declaration = buildStack.get( i ).getInnerDeclarations().get( identifier );
            if ( declaration != null ) {
                return declaration;
            }
        }
        // look at parent rules
        if ( rule != null && rule.getParent() != null ) {
            // recursive algorithm for each parent
            //     -> lhs.getInnerDeclarations()
            Declaration parentDeclaration = getExtendedDeclaration( rule.getParent(),
                                                                    identifier );
            if ( null != parentDeclaration ) {
                return parentDeclaration;
            }
        }

        // it may be a global or something
        if ( this.map.containsKey( (identifier) ) ) {
            Class<?> cls = this.map.get( identifier );
            ClassObjectType classObjectType = new ClassObjectType( cls );

            Declaration declaration;
            final Pattern dummy = new Pattern( 0,
                                               classObjectType );

            InternalReadAccessor globalExtractor = getReadAcessor( identifier,
                                                                   classObjectType );
            declaration = new Declaration( identifier,
                                           globalExtractor,
                                           dummy );
            if ( pkg != null ) {

                // make sure dummy and globalExtractor are wired up to correct ClassObjectType
                // and set as targets for rewiring
                pkg.getClassFieldAccessorStore().getClassObjectType( classObjectType,
                                                                     dummy );
                pkg.getClassFieldAccessorStore().getClassObjectType( classObjectType,
                                                                     ( AcceptsClassObjectType ) globalExtractor );
            }
            return declaration;
        }
        return null;
    }

    public static InternalReadAccessor getReadAcessor(String identifier,
                                                      ObjectType objectType) {
        Class returnType = ((ClassObjectType) objectType).getClassType();

        if (Number.class.isAssignableFrom( returnType ) ||
                ( returnType == byte.class ||
                  returnType == short.class ||
                  returnType == int.class ||
                  returnType == long.class ||
                  returnType == float.class ||
                  returnType == double.class ) ) {
            return new GlobalNumberExtractor(identifier,
                                             objectType);
         } else if (  Date.class.isAssignableFrom( returnType) ) {
          return new GlobalDateExtractor(identifier,
                                         objectType);
        } else {
          return new GlobalExtractor(identifier,
                                     objectType);
        }
    }

    public boolean available(RuleImpl rule,
                             final String name) {
        for ( int i = this.buildStack.size() - 1; i >= 0; i-- ) {
            final Declaration declaration = buildStack.get( i ).getInnerDeclarations().get( name );
            if ( declaration != null ) {
                return true;
            }
        }
        if ( this.map.containsKey( (name) ) ) {
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
        if ( this.map.containsKey( (name) ) ) {
            return true;
        }

        for ( int i = this.buildStack.size() - 1; i >= 0; i-- ) {
            final RuleConditionElement rce = buildStack.get( i );
            final Declaration declaration = rce.getInnerDeclarations().get( name );
            if ( declaration != null ) {
                // if it is an OR and it is duplicated, we can stop looking for duplication now
                // as it is a separate logical branch
                boolean inOr = ((rce instanceof GroupElement) && ((GroupElement) rce).isOr());
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
            Declaration parentDeclaration = getExtendedDeclaration( rule.getParent(),
                                                                    name );
            if ( null != parentDeclaration ) {
                return true;
            }
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
        Map<String, Declaration> declarations = new HashMap<String, Declaration>();
        for (RuleConditionElement aBuildStack : this.buildStack) {
            // if we are inside of an OR we don't want each previous stack entry added because we can't see those variables
            if (aBuildStack instanceof GroupElement && ((GroupElement)aBuildStack).getType() == GroupElement.Type.OR) {
                continue;
            }

            // this may be optimized in the future to only re-add elements at
            // scope breaks, like "NOT" and "EXISTS"
            Map<String,Declaration> innerDeclarations = aBuildStack instanceof GroupElement ?
                    ((GroupElement)aBuildStack).getInnerDeclarations(consequenceName) :
                    aBuildStack.getInnerDeclarations();
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
        final Map<String, Class<?>> classes = new HashMap<String, Class<?>>();
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

    public Pattern findPatternByIndex(int index) {
        if ( !this.buildStack.isEmpty() ) {
            return findPatternInNestedElements( index, buildStack.get( 0 ) );
        }
        return null;
    }

    private Pattern findPatternInNestedElements(final int index,
                                                final RuleConditionElement rce) {
        for ( RuleConditionElement element : rce.getNestedElements() ) {
            if ( element instanceof Pattern ) {
                Pattern p = (Pattern) element;
                if ( p.getIndex() == index ) {
                    return p;
                }
            } else if ( !element.isPatternScopeDelimiter() ) {
                Pattern p = findPatternInNestedElements( index,
                                                         element );
                if ( p != null ) {
                    return p;
                }
            }
        }
        return null;
    }
}
