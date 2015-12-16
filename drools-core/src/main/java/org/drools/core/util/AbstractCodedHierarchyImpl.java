/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.drools.core.util;

import java.io.Externalizable;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public abstract class AbstractCodedHierarchyImpl<T> extends AbstractBitwiseHierarchyImpl<T,HierNode<T>> implements CodedHierarchy<T>, Externalizable {

    protected abstract HierNode<T> getNode( T name );


    public void addMember( T val, BitSet key ) {
        if ( hasKey( key ) ) {
            HierNode<T> node = line.get( key );
            node.setValue( val );
        } else {
            HierNode<T> node = new HierNode<T>( val, key );
            Collection<HierNode<T>> infs = gcsBorderNodes( key, false );
            Collection<HierNode<T>> sups = lcsBorderNodes( key, false );

            for ( HierNode<T> child : infs ) {
                if ( child != null ) {
                    child.getParents().add( node );
                    child.getParents().removeAll( sups );
                    node.getChildren().add( child );
                }
            }
            for ( HierNode<T> parent : sups ) {
                if ( parent != null ) {
                    parent.getChildren().add( node );
                    parent.getChildren().removeAll( infs );
                    node.getParents().add( parent );
                }
            }
            add( node );
//            System.out.println( " Added inst node " + node );
//            System.out.println( " \t parents " + parents( key ) );
//            System.out.println( " \t children " + children( key ) );
        }
    }



    public void removeMember( T val ) {
        if ( val == null ) {
            return;
        }
        BitSet key = getCode( val );
        removeMember( key );
    }

    public void removeMember( BitSet key ) {
        if ( ! hasKey( key ) ) {
            return;
        } else {
            HierNode<T> node = getNodeByKey( key );
            Collection<HierNode<T>> children = node.getChildren();
            Collection<HierNode<T>> parents = node.getParents();

            for ( HierNode<T> child : children ) {
                child.getParents().remove( node );
                child.getParents().addAll( parents );
            }
            for ( HierNode<T> parent : parents ) {
                parent.getChildren().remove( node );
                parent.getChildren().addAll( children );
            }
            remove( node );
        }
    }


    protected Collection<T> parentValues( HierNode<T> node ) {
        if ( node == null ) {
            return Collections.EMPTY_LIST;
        }
        List<T> p = new ArrayList<T>( node.getParents().size() );
        for ( HierNode<T> parent : node.getParents() ) {
            p.add( parent.getValue() );
        }
        return p;
    }


    public Collection<T> ancestors( T x ) {
        HierNode<T> node = getNode(x);
        return ancestorValues( node );
    }

    public Collection<T> ancestors( BitSet key ) {
        HierNode<T> node = getNodeByKey(key);
        return ancestorValues( node );
    }

    protected Collection<T> ancestorValues( HierNode<T> node ) {
        if ( node == null ) {
            return Collections.EMPTY_SET;
        }

        Set<T> ancestors = new HashSet<T>();

        Collection<HierNode<T>> parents = node.getParents();
        for ( HierNode<T> p : parents ) {
            ancestors.add( p.getValue() );
            ancestors.addAll( ancestors(p.getValue()) );
        }
        return ancestors;
    }

    protected Set<HierNode<T>> ancestorNodes( HierNode<T> x ) {
        Set<HierNode<T>> ancestors = new HashSet<HierNode<T>>();

        Collection<HierNode<T>> parents = x.getParents();
        ancestors.addAll( parents );
        for ( HierNode<T> p : parents ) {
            ancestors.addAll( ancestorNodes( p ) );
        }
        return ancestors;
    }








    protected Collection<T> childrenValues( HierNode<T> node ) {
        if ( node == null ) {
            return Collections.EMPTY_LIST;
        }
        List<T> c = new ArrayList<T>( node.getChildren().size() );
        for ( HierNode<T> child : node.getChildren() ) {
            c.add( child.getValue() );
        }
        return c;
    }

    public Collection<T> children( T x ) {
        HierNode<T> node = getNode(x);
        return childrenValues( node );
    }

    public Collection<T> children( BitSet key ) {
        HierNode<T> node = getNodeByKey(key);
        return childrenValues( node );
    }



    protected Collection<T> descendantValues( HierNode<T> node ) {
        if ( node == null ) {
            return Collections.EMPTY_SET;
        }
        Set<T> descendants = new HashSet<T>();
        descendants.add( node.getValue() );

        Collection<HierNode<T>> children = node.getChildren();

        for ( HierNode<T> c : children ) {
            descendants.add( c.getValue() );
            descendants.addAll(descendants(c.getValue()));
        }
        return descendants;
    }


    public Collection<T> descendants( T y ) {
        HierNode<T> node = getNode(y);
        return descendantValues( node );
    }

    public Collection<T> descendants( BitSet key ) {
        HierNode<T> node = getNodeByKey(key);
        return descendantValues( node );
    }

    protected Set<HierNode<T>> descendantNodes( HierNode<T> y ) {
        Set<HierNode<T>> descendants = new HashSet<HierNode<T>>();
        descendants.add( y );

        Collection<HierNode<T>> children = y.getChildren();
        descendants.addAll( children );
        for ( HierNode<T> c : children ) {
            descendants.addAll( descendantNodes( c ) );
        }
        return descendants;
    }




}
