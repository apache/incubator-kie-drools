package org.drools.rule;
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



import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.drools.RuntimeDroolsException;

public abstract class GroupElement extends ConditionalElement {
    private List children = new ArrayList();

    /**
     * This removes single branch 'and' and 'or'
     * It also does basic nested removal, where an 'and' is
     * nested inside an 'and' and when an 'or' is nested inside an 'or'
     * 
     *  LogicTransformer does further, more complicated, transformations
     * @param child
     */
    public void addChild(Object child) {
        // @todo : I've commented this out for 3.0, but we want this working for 3.1
//        if ( child instanceof Not ) {
//            Not not = ( Not )  child;
//            Object notChild = not.getChild();
//            if ( notChild instanceof Or ) {
//                Or or = (Or) notChild;
//                And and = new And();
//                for ( Iterator it = or.getChildren().iterator(); it.hasNext(); ) {
//                    Not newNot = new Not();
//                    newNot.addChild( it.next() );
//                    and.addChild( newNot );
//                }                
//                child = and;
//            } else if ( notChild instanceof And ) {
//                
//            }            
//        }
        
//        if ( child instanceof GroupElement && ( child instanceof And || child instanceof Or ) ) {
//            GroupElement group = ( GroupElement )  child;
//            
//            // Removal single branch group elements 
//            // If the child is a GroupElement iterate down until we either
//            // find a GroupElement that has more than one children, or its not a GroupElement            
//            if (  group.getChildren().size() == 1 ) {
//                child = group.getChildren().get( 0 );
//            }            
//        }
//        
//        if ( child instanceof GroupElement && ( child instanceof And || child instanceof Or ) ) {
//            GroupElement group = ( GroupElement )  child;
//            
//            // Remove nested Ands/Ors            
//            if ( group.getClass() == this.getClass() ) {
//                    
//                    GroupElement newGroup = null;
//                    if ( group instanceof And) {
//                        newGroup = new And();
//                    } else {
//                        newGroup =  new Or();
//                    }
//                    
//                    for ( Iterator it = group.getChildren().iterator(); it.hasNext(); ) {
//                        this.children.add( it.next() );
//                    }
//            } else {
//                this.children.add( child );
//            }
//        }   else {        
//            this.children.add( child );
//        }
        
        this.children.add( child );
    }

    public List getChildren() {
        return this.children;
    }

    /**
     * Traverses two trees and checks that they are structurally equal at all
     * levels
     * 
     * @param e1
     * @param e2
     * @return
     */
    public boolean equals(Object object) {
        // Return false if its null or not an instance of ConditionalElement
        if ( object == null || !(object instanceof GroupElement) ) {
            return false;
        }

        // Return true if they are the same reference
        if ( this == object ) {
            return true;
        }

        // Now try a recurse manual check
        GroupElement e2 = (GroupElement) object;
        List e1Children = this.getChildren();
        List e2Children = e2.getChildren();
        if ( e1Children.size() != e2Children.size() ) {
            return false;
        }

        for ( int i = 0; i < e1Children.size(); i++ ) {
            Object e1Object1 = e1Children.get( i );
            Object e2Object1 = e2Children.get( i );
            if ( e1Object1 instanceof GroupElement ) {
                if ( e1Object1.getClass().isInstance( e2Object1 ) ) {
                    if ( !e1Object1.equals( e2Object1 ) ) {
                        System.out.println( e1Object1.getClass().getName() + " did not have identical children" );
                        return false;
                    }
                } else {
                    System.out.println( "Should be the equal Conditionalelements but instead was '" + e1Object1.getClass().getName() + "', '" + e2Object1.getClass().getName() + "'" );
                    return false;
                }
            } else if ( e1Object1 instanceof String ) {
                if ( !e1Object1.equals( e2Object1 ) ) {
                    System.out.println( "Should be the equal Strings but instead was '" + e1Object1 + "', '" + e2Object1 + "'" );
                    return false;
                }
            } else {
                System.out.println( "Objects are neither instances of ConditionalElement or String" );
                return false;
            }
        }

        return true;
    }

    public int hashCode() {
        return this.children.hashCode();
    }

    /**
     * Clones all Conditional Elements but references the non ConditionalElement
     * children
     * 
     * @param e1
     * @param e2
     * @return
     */
    public Object clone() {
        GroupElement cloned = null;

        try {
            cloned = (GroupElement) this.getClass().newInstance();
        } catch ( InstantiationException e ) {
            throw new RuntimeException( "Could not clone '" + this.getClass().getName() + "'" );
        } catch ( IllegalAccessException e ) {
            throw new RuntimeException( "Could not clone '" + this.getClass().getName() + "'" );
        }

        for ( Iterator it = this.children.iterator(); it.hasNext(); ) {
            Object object = it.next();
            if ( object instanceof GroupElement ) {
                object = ((GroupElement) object).clone();
            }
            cloned.addChild( object );

        }

        return cloned;
    }

}