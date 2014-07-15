/*
 * Copyright 2013 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.core.util;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Encodes a hierachy using bit masks, according to the algorithm described in
 * M.F. van Bommel, P. Wang, Encoding Multiple Inheritance Hierarchies for Lattice Operations
 * Data & Knowledge Engineering 50 (2004) 175-194
 *
 * @param <T>
 */
public class HierarchyEncoderImpl<T> extends CodedHierarchyImpl<T> implements HierarchyEncoder<T>, Externalizable {


    private ImmutableBitSet bottom = new ImmutableBitSet();

    public BitSet getBottom() {
        return bottom;
    }

    @Override
    public void writeExternal( ObjectOutput objectOutput ) throws IOException {
        super.writeExternal( objectOutput );
        objectOutput.writeObject( bottom );
    }

    @Override
    public void readExternal( ObjectInput objectInput ) throws IOException, ClassNotFoundException {
        super.readExternal( objectInput );
        bottom = (ImmutableBitSet) objectInput.readObject();
    }

    public BitSet encode( T member, Collection<T> parents ) {
        BitSet existing = getCode( member );
        if ( existing != null ) {
            return existing;
        }

        HierNode<T> node = new HierNode<T>( member );

        Set<HierNode<T>> parentNodes = floor( parents );

        for ( HierNode<T> parentNode : parentNodes ) {
            node.addParent( parentNode );
            parentNode.addChild( node );
        }
        encode( node );

        add( node );

        return node.getBitMask();
    }


    @Override
    protected void add( HierNode<T> node ) {
        super.add( node );
        bottom.merge( node.getBitMask() );
    }

    public void clear() {
        super.clear();
        bottom = new ImmutableBitSet();
    }



    // Debug only
    List<T> ancestorValues( T name ) {
        List<T> anx = new ArrayList<T>();
        Set<HierNode<T>> nodes = ancestorNodes( getNode(name)  );
        for ( HierNode<T> node : nodes ) {
            anx.add( node.getValue() );
        }
        return anx;
    }



    protected void encode( HierNode<T> node ) {
        Collection<HierNode<T>> parents = node.getParents();
        //System.out.println( "Trying to encode " + node );
        switch ( parents.size() ) {
            case 0 :
                BitSet zero = new BitSet();

                if ( hasKey(zero) ) {
                    HierNode root = getNodeByKey(zero);

                    if ( root.getValue() != null ) {
                        fixedRoot = true;
                        HierNode previousRoot = root;
                        root = new HierNode( (Object) null );
                        root.addChild( previousRoot );
                        previousRoot.addParent( root );

                        root.setBitMask( zero );

                        propagate( previousRoot, freeBit( root ) );
                        add( root );
                    }

                    node.addParent( root );
                    updateMask( node, increment( root.getBitMask(), freeBit( root ) ) );

                } else {
                    updateMask( node, new BitSet() );
                }
                break;
            case 1 :
                HierNode<T> parent = parents.iterator().next();
                updateMask( node, increment( parent.getBitMask(), freeBit( parent ) ) );
                break;
            default:
                inheritMerged( node );
                //System.out.println( " ----------------------------------------------------------------------------------------- " );
                resolveConflicts( node );
                break;
        }
    }


    protected void resolveConflicts( HierNode<T> x ) {
        boolean conflicted = false;
        Collection<HierNode<T>> nodes = new ArrayList<HierNode<T>>( getNodes() );
        for ( HierNode<T> y : nodes ) {
            if ( incomparable( x, y ) ) {
//                System.out.println( " \t\tIncomparability between " + x + " and " + y );
                int sup = superset( y, x );
                if ( sup == 0 ) {
                    //System.out.println( " \t\tIncomparable, with same mask " + y );
                    // can't use update mask here, or the already existing node would be removed
                    x.setBitMask( increment( x.getBitMask(), freeBit( x ) ) );
                    propagate( y, freeBit( x, y ) );
                }
                if ( sup > 0 ) {
//                    System.out.println( " \t\tIncomparable, but as parent " + y );
                    updateMask( x, increment( x.getBitMask(), freeBit( x ) ) );
                }
                int sub = superset( x, y );
                if ( sub > 0 ) {
//                    System.out.println( " \t\tIncomparable, but as child " + y );
                    modify( x, y );
                    conflicted = true;
                }

            }
        }
        if ( conflicted ) {
            inheritMerged( x );
            resolveConflicts( x );
        }

    }

    protected void modify( HierNode<T> x, HierNode<T> y ) {
        //System.out.println( "Modifying on a inc between " + x + " and " + y );

        int i = freeBit( x, y );
        //System.out.println( "I " + i );

        //System.out.println( "Getting parents of " + y + " >> " + y.getParents() );
        Collection<HierNode<T>> py = y.getParents();
        BitSet t = new BitSet( y.getBitMask().length() );
        for ( HierNode<T> parent : py ) {
            t.or( parent.getBitMask() );
        }

        BitSet d = singleBitDiff( t, y.getBitMask() );
        int inDex = d.nextSetBit( 0 );

        if ( inDex < 0 ) {
            propagate( y, i );
        } else {

            //System.out.println( "D " + toBinaryString( d ) );


            Set<HierNode<T>> ancestors = ancestorNodes( x );
            Set<HierNode<T>> affectedAncestors = new HashSet<HierNode<T>>();

            for ( HierNode<T> anc : ancestors ) {
                if ( anc.getBitMask().get( inDex ) ) {
                    affectedAncestors.add( anc );
                }
            }
            //System.out.println( "Ancestors of " + x + " >> " + ancestors );
            //System.out.println( "Affected " + x + " >> " + affectedAncestors );

            if ( affectedAncestors.size() == 0 ) {
                return;
            }

            Set<HierNode<T>> gcs = gcs( affectedAncestors );
            //System.out.println( "GCS of Affected " + gcs );

            Set<HierNode<T>> affectedDescendants = new HashSet<HierNode<T>>();
            for ( HierNode<T> g : gcs ) {
                affectedDescendants.addAll( descendantNodes( g ) );
            }
            affectedDescendants.remove( x );    // take x out it's not yet in the set

            int dx = firstOne( d );

            if ( bottom.get( i ) ) {
                i = freeBit( new HierNode<T>( bottom ) );
            }

            for ( HierNode<T> sub : affectedDescendants ) {

                boolean keepsBit = false;
                for ( HierNode<T> sup : sub.getParents() ) {
                    if ( ! keepsBit && ! affectedDescendants.contains( sup ) && sup.getBitMask().get( inDex ) ) {
                        keepsBit = true;
                    }
                }
                BitSet subMask = sub.getBitMask();
                if ( ! keepsBit ) {
                    subMask = decrement( subMask, dx );
                }
                subMask = increment( subMask, i );

                updateMask( sub, subMask );

                //System.out.println( "\tModified Desc" + sub );
            }

            inDex = d.nextSetBit( inDex + 1 );
        }


    }

    protected void updateMask( HierNode<T> node, BitSet mask ) {
        boolean in = node.getBitMask() != null && contains ( node );
        if ( in ) { remove( node ); }
        node.setBitMask( mask );
        if ( in ) { add( node ); }
    }

    protected void inheritMerged( HierNode<T> x ) {
        BitSet mask = new BitSet( x.getBitMask() != null ? x.getBitMask().length() : 1 );
        for ( HierNode<T> p : x.getParents() ) {
            mask.or(p.getBitMask());
        }
        updateMask(x, mask);
    }


    protected Set<HierNode<T>> gcs( Set<HierNode<T>> set ) {
        Set<HierNode<T>> s = new HashSet<HierNode<T>>();

        Iterator<HierNode<T>> iter = set.iterator();
        BitSet a = new BitSet( this.size() );
        a.or( iter.next().getBitMask() );
        while ( iter.hasNext() ) {
            a.and( iter.next().getBitMask() );
        }
        //System.out.println( "Root mask for ceil " + toBinaryString( a ) );
        for ( HierNode<T> node : getNodes() ) {
            if ( superset( node.getBitMask(), a ) >= 0 ) {
                s.add( node );
            }
        }

        Set<HierNode<T>> cl = ceil( s );
        return cl;
    }

    protected Set<HierNode<T>> ceil( Set<HierNode<T>> s ) {
        //System.out.println( "Looking for the ceiling of " + s);
        if ( s.size() <= 1 ) { return s; }
        Set<HierNode<T>> ceil = new HashSet<HierNode<T>>( s );
        for ( HierNode<T> x : s ) {
            for( HierNode<T> y : s ) {
                if ( superset( x, y ) > 0 ) {
                    ceil.remove( x );
                    break;
                }
            }
        }
        //System.out.println("Found ceiling " + ceil);
        return ceil;
    }

    protected Set<HierNode<T>> floor( Set<HierNode<T>> s ) {
        //System.out.println( "Looking for the floor of " + s);
        if ( s.size() <= 1 ) { return s; }
        Set<HierNode<T>> ceil = new HashSet<HierNode<T>>( s );
        for ( HierNode<T> x : s ) {
            for( HierNode<T> y : s ) {
                if ( superset( y, x ) > 0 ) {
                    ceil.remove( x );
                    break;
                }
            }
        }
        //System.out.println("Found ceiling " + ceil);
        return ceil;
    }

    private Set<HierNode<T>> floor( Collection<T> parents ) {
        Set<HierNode<T>> floor = new HashSet();
        Set<HierNode<T>> subs = new HashSet();

        for ( T s : parents ) {
            HierNode<T> x = getNode( s );
            subs.addAll( floor );

            boolean minimal = true;
            for ( HierNode<T> y : subs ) {
                if ( superset( x, y ) > 0 ) {
                    floor.remove( y );
                }
                if ( superset( y, x ) > 0 ) {
                    minimal = false;
                    break;
                }
            }
            if ( minimal ) {
                floor.add( x );
            }

            subs.clear();
        }
        return floor;
    }


    protected void propagate( HierNode<T> y, int bit ) {
        Set<HierNode<T>> descendants = descendantNodes(y);
        for ( HierNode<T> s : descendants ) {
            updateMask( s, increment( s.getBitMask(), bit ) );
        }
    }


    protected int freeBit( HierNode<T> x ) {
        return freeBit( x, null );
    }

    protected int freeBit( HierNode<T> x, HierNode<T> z ) {
        //System.out.println( "Looking for a free bit in node " + x );
        BitSet forbid = new BitSet( this.size() );
        forbid.or( x.getBitMask() );
        for ( HierNode<T> y : getNodes() ) {

            if ( superset( y, x ) > 0 ) {
                //System.out.println( "\t\t Subtype " + y + " contributes " + toBinaryString( y.getBitMask() ) );
                forbid.or( y.getBitMask() );
            }

            if ( z != null ) {
                if ( superset( y, z ) > 0 ) {
//                  System.out.println( "\t\t Subtype " + y + " contributes " + toBinaryString( y.getBitMask() ) );
                    forbid.or( y.getBitMask() );
                }
            }

            if ( superset( x, y ) < 0 ) {
                BitSet diff = singleBitDiff( x.getBitMask(), y.getBitMask() );
                //System.out.println( "\t\t Incomparable " + y + " contributes " + toBinaryString( diff ) );
                forbid.or(diff);
            }
        }
        //System.out.println( "\t Forbidden mask " + toBinaryString( forbid ) );
        return firstZero( forbid );
    }






    protected boolean incomparable( HierNode<T> c1, HierNode<T> c2 ) {
        if ( c1 == c2 ) { return false; }
        Set<HierNode<T>> sup1 = ancestorNodes( c1 );
        Set<HierNode<T>> sup2 = ancestorNodes( c2 );
        return ! ( sup1.contains( c2 ) || sup2.contains( c1 ) );
    }















    BitSet increment( BitSet id, int i ) {
        BitSet x = new BitSet( Math.max( i + 1, id.length() ) );
        x.or( id );
        x.set(i);
        return x;
    }

    BitSet decrement( BitSet id, int d ) {
        BitSet x = new BitSet( id.length() );
        x.or( id );
        x.clear(d);
        return x;
    }

    BitSet singleBitDiff( BitSet x, BitSet y ) {
        BitSet t = new BitSet( x.length() );
        t.or( x );
        t.flip(0, t.size());
        t.and( y );

        switch ( t.cardinality() ) {
            case 0 : return t;
            case 1 : return t;
            default: return new BitSet();
        }
    }

    int firstOne( BitSet t ) {
        return t.nextSetBit(0);
    }

    int firstZero( BitSet t ) {
        return t.nextClearBit(0);
    }


    private class ImmutableBitSet extends BitSet {

        @Override
        public void flip(int bitIndex) { throw new UnsupportedOperationException( "Read Only" ); }

        @Override
        public void flip(int fromIndex, int toIndex) { throw new UnsupportedOperationException( "Read Only" ); }

        @Override
        public void set(int bitIndex) { throw new UnsupportedOperationException( "Read Only" ); }

        @Override
        public void set(int bitIndex, boolean value) { throw new UnsupportedOperationException( "Read Only" ); }

        @Override
        public void set(int fromIndex, int toIndex) { throw new UnsupportedOperationException( "Read Only" ); }

        @Override
        public void set(int fromIndex, int toIndex, boolean value) { throw new UnsupportedOperationException( "Read Only" ); }

        @Override
        public void clear(int bitIndex) { throw new UnsupportedOperationException( "Read Only" ); }

        @Override
        public void clear(int fromIndex, int toIndex) { throw new UnsupportedOperationException( "Read Only" ); }

        @Override
        public void clear() { throw new UnsupportedOperationException( "Read Only" ); }

        @Override
        public void and(BitSet set) { throw new UnsupportedOperationException( "Read Only" ); }

        @Override
        public void or(BitSet set) { throw new UnsupportedOperationException( "Read Only" ); }

        @Override
        public void xor(BitSet set) { throw new UnsupportedOperationException( "Read Only" ); }

        @Override
        public void andNot(BitSet set) { throw new UnsupportedOperationException( "Read Only" ); }

        protected void merge( BitSet set ) {
            super.or( set );
        }
    }


}
