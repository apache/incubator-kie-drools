/**
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

package org.drools.spi;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import org.drools.base.ClassObjectType;
import org.drools.rule.Declaration;
import org.drools.rule.GroupElement;
import org.drools.rule.Package;
import org.drools.rule.Pattern;
import org.drools.rule.Rule;
import org.drools.rule.RuleConditionElement;

import org.drools.spi.InternalReadAccessor;

/**
 * A class capable of resolving a declaration in the current build context
 * 
 * @author etirelli
 */
public class DeclarationScopeResolver {
    private static final Stack<RuleConditionElement> EMPTY_STACK = new Stack<RuleConditionElement>();
    private Map<String, Class>                  map;
    private Stack<RuleConditionElement>              buildStack;
    private Package                                  pkg;

    public DeclarationScopeResolver(final Map<String, Class> maps) {
        this( maps,
              EMPTY_STACK );
    }

    public DeclarationScopeResolver(final Map<String, Class> map,
                                    final Stack<RuleConditionElement> buildStack) {
        this.map = map;
        if ( buildStack == null ) {
            this.buildStack = EMPTY_STACK;
        } else {
            this.buildStack = buildStack;
        }
    }

    public void setPackage(Package pkg) {
        this.pkg = pkg;
    }

    private Declaration getExtendedDeclaration(Rule rule,
                                               String identifier) {
        if ( rule.getLhs().getInnerDeclarations().containsKey( identifier ) ) {
            return (Declaration) rule.getLhs().getInnerDeclarations().get( identifier );
        } else if ( null != rule.getParent() ) {
            return getExtendedDeclaration( rule.getParent(),
                                           identifier );
        }
        return null;

    }

    private HashMap<String, Declaration> getAllExtendedDeclaration(Rule rule,
                                                                   HashMap<String, Declaration> dec) {
        dec.putAll( ((RuleConditionElement) rule.getLhs()).getInnerDeclarations() );
        if ( null != rule.getParent() ) {
            return getAllExtendedDeclaration( rule.getParent(),
                                              dec );
        }
        return dec;

    }

    public Declaration getDeclaration(final Rule rule,
                                      final String identifier) {
        // it may be a local bound variable
        for ( int i = this.buildStack.size() - 1; i >= 0; i-- ) {
            final Declaration declaration = (Declaration) ((RuleConditionElement) this.buildStack.get( i )).getInnerDeclarations().get( identifier );
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
            if ( pkg != null ) {
                Class<?> cls = (Class<?>) this.map.get( identifier );
                ClassObjectType classObjectType = new ClassObjectType( cls );

                Declaration declaration = null;
                final Pattern dummy = new Pattern( 0,
                                                   classObjectType );

                GlobalExtractor globalExtractor = new GlobalExtractor( identifier,
                                                                       classObjectType );
                declaration = new Declaration( identifier,
                                               globalExtractor,
                                               dummy );

                // make sure dummy and globalExtractor are wired up to correct ClassObjectType
                // and set as targets for rewiring
                pkg.getClassFieldAccessorStore().getClassObjectType( classObjectType,
                                                                     dummy );
                pkg.getClassFieldAccessorStore().getClassObjectType( classObjectType,
                                                                     globalExtractor );

                return declaration;
            } else {
                throw new UnsupportedOperationException( "This shoudln't happen outside of PackageBuilder" );
            }
        }
        return null;
    }

    public boolean available(Rule rule,
                             final String name) {
        for ( int i = this.buildStack.size() - 1; i >= 0; i-- ) {
            final Declaration declaration = (Declaration) ((RuleConditionElement) this.buildStack.get( i )).getInnerDeclarations().get( name );
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

    public boolean isDuplicated(Rule rule,
                                final String name) {
        if ( this.map.containsKey( (name) ) ) {
            return true;
        }
        
        for ( int i = this.buildStack.size() - 1; i >= 0; i-- ) {
            final RuleConditionElement rce = (RuleConditionElement) this.buildStack.get( i );
            final Declaration declaration = (Declaration) rce.getInnerDeclarations().get( name );
            if ( declaration != null ) {
                if ( (rce instanceof GroupElement) && ((GroupElement) rce).isOr() ) {
                    // if it is an OR and it is duplicated, we can stop looking for duplication now
                    // as it is a separate logical branch
                    return false;
                }
                return true;
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

    /**
     * Return all declarations scoped to the current
     * RuleConditionElement in the build stack
     * 
     * @return
     */
    public Map<String, Declaration> getDeclarations(Rule rule) {
        final Map<String, Declaration> declarations = new HashMap<String, Declaration>();
        for ( int i = 0; i < this.buildStack.size(); i++ ) {
            // this may be optimized in the future to only re-add elements at 
            // scope breaks, like "NOT" and "EXISTS"
            declarations.putAll( ((RuleConditionElement) this.buildStack.get( i )).getInnerDeclarations() );
        }
        if ( null != rule.getParent() ) {
            return getAllExtendedDeclaration( rule.getParent(),
                                              (HashMap<String, Declaration>) declarations );
        }
        return declarations;
    }
    
    public Map<String,Class> getDeclarationClasses(Rule rule) {
        final Map<String, Declaration> declarations = getDeclarations( rule );
        return getDeclarationClasses( declarations );
    }
    
    public static Map<String,Class> getDeclarationClasses( final Map<String, Declaration> declarations) {
        final Map<String, Class> classes = new HashMap<String, Class>();
        for ( Map.Entry<String, Declaration> decl : declarations.entrySet() ) {
            InternalReadAccessor ira = decl.getValue().getExtractor();
            // FIXME when would the IRA be null?
            if( ira != null ) {
                classes.put( decl.getKey(), ira.getExtractToClass() );
            }
        }
        return classes;
    }

    public Pattern findPatternByIndex(int index) {
        if ( !this.buildStack.isEmpty() ) {
            return findPatternInNestedElements( index,
                                                (RuleConditionElement) this.buildStack.get( 0 ) );
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
