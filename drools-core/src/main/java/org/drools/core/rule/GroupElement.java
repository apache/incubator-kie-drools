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

package org.drools.core.rule;

import org.drools.core.definitions.rule.impl.RuleImpl;
import org.drools.core.spi.ObjectType;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class GroupElement extends ConditionalElement
    implements
    Externalizable {

    private static final long serialVersionUID     = 510l;

    public static final Type  AND                  = Type.AND;
    public static final Type  OR                   = Type.OR;
    public static final Type  NOT                  = Type.NOT;
    public static final Type  EXISTS               = Type.EXISTS;

    private Type              type                 = null;

    private List<RuleConditionElement> children    = new ArrayList<RuleConditionElement>();

    private ObjectType        forallBaseObjectType = null;
    
    private boolean           root;
    
    private Map<String, Declaration> outerDeclrarations;

    public GroupElement() {
        this( Type.AND );
    }

    public GroupElement(final Type type) {
        this.type = type;
    }

    public void readExternal(ObjectInput in) throws IOException,
                                            ClassNotFoundException {
        this.type = (Type) in.readObject();
        children = (List) in.readObject();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject( type );
        out.writeObject( children );
    }

    /**
     * Adds a child to the current GroupElement.
     *
     * Restrictions are:
     * NOT/EXISTS: can have only one child, either a single Pattern or another CE
     *
     * @param child
     */
    public void addChild(final RuleConditionElement child) {
        if ( (this.isNot() || this.isExists()) && (this.children.size() > 0) ) {
            throw new RuntimeException( this.type.toString() + " can have only a single child element. Either a single Pattern or another CE." );
        }
        this.children.add( child );
    }

    /**
     * Adds the given child as the (index)th child of the this GroupElement
     * @param index
     * @param rce
     */
    public void addChild(final int index,
                         final RuleConditionElement rce) {
        this.children.add( index,
                           rce );
    }

    public List<RuleConditionElement> getChildren() {
        return this.children;
    }

    /**
     * @inheritDoc
     */
    public Map<String,Declaration> getInnerDeclarations() {
        return this.type.getInnerDeclarations( this.children );
    }

    public Map<String,Declaration> getInnerDeclarations(String consequenceName) {
        return this.type.getInnerDeclarations( this.children, consequenceName );
    }

    /**
     * @inheritDoc
     */
    public Map<String, Declaration> getOuterDeclarations() {
        return getOuterDeclarations( RuleImpl.DEFAULT_CONSEQUENCE_NAME );
    }

    public Map<String, Declaration> getOuterDeclarations(String consequenceName) {
        if ( outerDeclrarations != null ) {
            return outerDeclrarations;
        } else if ( root ) {
            outerDeclrarations = this.type.getOuterDeclarations( this.children, consequenceName );
            return outerDeclrarations;
        }
        return this.type.getOuterDeclarations( this.children, consequenceName );
    }

    /**
     * @inheritDoc
     */
    public Declaration resolveDeclaration(final String identifier) {
        return this.type.getInnerDeclarations( this.children ).get( identifier );
    }

    public void setForallBaseObjectType(ObjectType objectType) {
        this.forallBaseObjectType = objectType;
    }

    public ObjectType getForallBaseObjectType() {
        return this.forallBaseObjectType;
    }

    /**
     * Optimize the group element subtree by removing redundancies
     * like an AND inside another AND, OR inside OR, single branches
     * AND/OR, etc.
     *
     * LogicTransformer does further, more complicated, transformations
     */
    public void pack() {
        // we must clone, since we want to iterate only over the original list
        final Object[] clone = this.children.toArray();
        for (Object aClone : clone) {
            // if child is also a group element, there may be
            // some possible clean up / optimizations to be done
            if (aClone instanceof GroupElement) {
                final GroupElement childGroup = (GroupElement) aClone;
                childGroup.pack(this);
            }
        }

        // if after packing, this is an AND or OR GE with a single
        // child GE, then clone child into current node eliminating child
        if ( (this.isAnd() || this.isOr()) && (this.children.size() == 1) ) {
            final Object child = this.getChildren().get( 0 );
            if ( child instanceof GroupElement ) {
                mergeGroupElements( this, (GroupElement) child );
            }
        }
        
        // if after packing, this is a NOT GE with an EXISTS child
        // or this is an EXISTS GE with a NOT child, eliminate the redundant 
        // child and make this a NOT GE
        if ( this.isNot() && this.children.size() == 1 && this.getChildren().get( 0 ) instanceof GroupElement ) {
            final GroupElement child = (GroupElement) this.getChildren().get( 0 );
            if ( child.isExists() ) {
                this.children.clear();
                this.children.addAll( child.getChildren() );
            }
        }
        if ( this.isExists() && this.children.size() == 1 && this.getChildren().get( 0 ) instanceof GroupElement ) {
            final GroupElement child = (GroupElement) this.getChildren().get( 0 );
            if ( child.isNot() ) {
                this.setType( NOT );
                this.children.clear();
                this.children.addAll( child.getChildren() );
            }
        }

    }

    protected void mergeGroupElements(GroupElement parent, GroupElement child) {
        parent.type = child.getType();
        parent.children.clear();
        parent.children.addAll( child.getChildren() );
    }

    /**
     * @param parent
     */
    public void pack(final GroupElement parent) {
        if ( this.children.size() == 0 ) {
            // if there is no child, just remove this node
            parent.children.remove( this );
            return;
        }

        // If this is  an AND or OR or EXISTS, there are some possible merges
        if ( this.isAnd() || this.isOr() || this.isExists() ) {

            // if parent is of the same type as current node,
            // then merge this children with parent children
            if ( parent.getType() == this.getType() ) {

                // we must keep the order so, save index
                int index = parent.getChildren().indexOf( this );
                parent.getChildren().remove( this );
                // for each child, pack it and add it to parent
                for ( RuleConditionElement child : children ) {
                    // we must keep the order, so add in the same place were parent was before
                    parent.getChildren().add( index++,
                                              child );
                    if ( child instanceof GroupElement ) {
                        final int previousSize = parent.getChildren().size();
                        ((GroupElement) child).pack( parent );
                        // in case the child also added elements to the parent,
                        // we need to compensate
                        index += (parent.getChildren().size() - previousSize);
                    }
                }

                // if current node has a single child, then move it to parent and pack it
            } else if ( (!this.isExists()) && (this.children.size() == 1) ) {
                // we must keep the order so, save index
                final int index = parent.getChildren().indexOf( this );
                parent.getChildren().remove( this );

                final RuleConditionElement child = this.children.get( 0 );
                parent.getChildren().add( index,
                                          child );

                if ( child instanceof GroupElement ) {
                    ((GroupElement) child).pack( parent );
                }

                // otherwise pack itself
            } else {
                this.pack();
            }

            // also pack itself if it is a NOT
        } else {
            this.pack();
        }
    }


    public boolean equals(final Object object) {
        // Return false if its null or not an instance of ConditionalElement
        if ( object == null || !(object instanceof GroupElement) ) {
            return false;
        }

        // Return true if they are the same reference
        if ( this == object ) {
            return true;
        }

        // Now try a recurse manual check
        final GroupElement e2 = (GroupElement) object;
        if ( !this.type.equals( e2.type ) ) {
            return false;
        }

        final List e1Children = this.getChildren();
        final List e2Children = e2.getChildren();
        if ( e1Children.size() != e2Children.size() ) {
            return false;
        }

        for ( int i = 0; i < e1Children.size(); i++ ) {
            final Object e1Object1 = e1Children.get( i );
            final Object e2Object1 = e2Children.get( i );
            if ( !e1Object1.equals( e2Object1 ) ) {
                return false;
            }
        }

        return true;
    }

    public int hashCode() {
        return this.type.hashCode() + this.children.hashCode();
    }

    /**
     * Clones all Conditional Elements but references the non ConditionalElement
     * children
     *
     * @return
     */
    public GroupElement clone() {
        return clone(true);
    }

    public GroupElement cloneOnlyGroup() {
        return clone(false);
    }

    protected GroupElement clone(boolean deepClone) {
        GroupElement cloned = new GroupElement();
        cloned.setType( this.getType() );
        for ( RuleConditionElement re : children ) {
            cloned.addChild( deepClone && ( re instanceof GroupElement || re instanceof Pattern ) ? re.clone() : re );
        }
        return cloned;
    }

    public Type getType() {
        return this.type;
    }

    public void setType(final Type type) {
        this.type = type;
    }

    public boolean isAnd() {
        return AND.equals( this.type );
    }

    public boolean isOr() {
        return OR.equals( this.type );
    }

    public boolean isNot() {
        return NOT.equals( this.type );
    }

    public boolean isExists() {
        return EXISTS.equals( this.type );
    }

    public String toString() {
        return this.type.toString() + this.children.toString();
    }

    public List<RuleConditionElement> getNestedElements() {
        return this.children;
    }

    public boolean isPatternScopeDelimiter() {
        return this.type.isPatternScopeDelimiter();
    }

    public boolean isRoot() {
        return root;
    }

    public void setRoot(boolean root) {
        this.root = root;
    }
    
    public boolean containesNode(Type node) {
        return containesNode( node, this );
    }
    
    private static boolean containesNode(Type node, GroupElement groupElement) {
        for( RuleConditionElement rce : groupElement.getChildren() ) {
            if ( rce instanceof GroupElement ) {
                return ( (GroupElement) rce ).getType() == node || containesNode( node, (GroupElement) rce );
            }
        }
        return false;
    }    
    

    /**
     * A public enum for CE types
     */
    public enum Type {

        AND(ScopeDelimiter.NEVER),
        OR(ScopeDelimiter.CONSENSUS),
        NOT(ScopeDelimiter.ALWAYS),
        EXISTS(ScopeDelimiter.ALWAYS);

        enum ScopeDelimiter {
            NEVER,
            CONSENSUS, // it isn't a scope delimiter only if a given Declaration is present on ALL branches
            ALWAYS
        }

        private final ScopeDelimiter scopeDelimiter;

        Type(final ScopeDelimiter scopeDelimiter) {
            this.scopeDelimiter = scopeDelimiter;
        }

        /**
         * Returns a map of declarations that are
         * visible inside of an element of this type
         */
        private Map<String, Declaration> getInnerDeclarations(List<RuleConditionElement> children) {
            return getInnerDeclarations(children, RuleImpl.DEFAULT_CONSEQUENCE_NAME);
        }

        /**
         * Returns a map of declarations that are
         * visible inside of an element of this type
         * for the consequence with the given name
         */
        private Map<String, Declaration> getInnerDeclarations(List<RuleConditionElement> children, String consequenceName) {
            return getDeclarations(children, ScopeDelimiter.NEVER, consequenceName);
        }

        /**
         * Returns a map of declarations that are
         * visible outside of an element of this type
         */
        private Map<String, Declaration> getOuterDeclarations(List<RuleConditionElement> children, String consequenceName) {
            return getDeclarations(children, this.scopeDelimiter, consequenceName);
        }

        private Map<String, Declaration> getDeclarations(List<RuleConditionElement> children, ScopeDelimiter scopeDelimiter, String consequenceName) {
            if ( scopeDelimiter == ScopeDelimiter.ALWAYS || children.isEmpty() ) {
                return Collections.EMPTY_MAP;
            } else if ( children.size() == 1 ) {
                return getOuterDeclarations(children.get(0), consequenceName);
            } else {
                Map<String, Declaration> declarations = new HashMap<String, Declaration>();
                if ( scopeDelimiter == ScopeDelimiter.NEVER ) {
                    for ( RuleConditionElement rce : children ) {
                        declarations.putAll( getOuterDeclarations( rce, consequenceName ) );
                        if ( isConsequenceInvoker(rce, consequenceName) ) {
                            break;
                        }
                    }
                } else if ( scopeDelimiter == ScopeDelimiter.CONSENSUS ) {
                    Iterator<RuleConditionElement> i = children.iterator();
                    RuleConditionElement rce = i.next();
                    Map<String, Declaration> elementDeclarations = getOuterDeclarations( rce, consequenceName );
                    if ( isConsequenceInvoker(rce, consequenceName) ) {
                        return elementDeclarations;
                    }
                    declarations.putAll( elementDeclarations );
                    while ( i.hasNext() ) {
                        rce = i.next();
                        elementDeclarations = getOuterDeclarations( rce, consequenceName );
                        if ( isConsequenceInvoker(rce, consequenceName) ) {
                            return elementDeclarations;
                        }
                        declarations.keySet().retainAll( elementDeclarations.keySet() );
                    }
                }
                return declarations;
            }
        }

        private Map<String, Declaration> getOuterDeclarations(RuleConditionElement rce, String consequenceName) {
            return rce instanceof GroupElement ? ((GroupElement) rce).getOuterDeclarations(consequenceName) : rce.getOuterDeclarations();
        }

        private boolean isConsequenceInvoker(RuleConditionElement rce, String consequenceName) {
            if ( RuleImpl.DEFAULT_CONSEQUENCE_NAME.equals( consequenceName ) ) {
                return false;
            }
            if ( rce instanceof NamedConsequenceInvoker && ((NamedConsequenceInvoker)rce).invokesConsequence(consequenceName) ) {
                return true;
            }
            if ( rce instanceof GroupElement ) {
                for ( RuleConditionElement child : ((GroupElement) rce).getChildren() ) {
                    if ( isConsequenceInvoker(child, consequenceName) ) {
                        return true;
                    }
                }
            }
            return false;
        }

        /**
         * Returns true in case this RuleConditionElement delimits
         * a pattern visibility scope.
         *
         * For instance, AND CE is not a scope delimiter, while
         * NOT CE is a scope delimiter
         */
        public boolean isPatternScopeDelimiter() {
            return this.scopeDelimiter == ScopeDelimiter.ALWAYS;
        }

    }
}
