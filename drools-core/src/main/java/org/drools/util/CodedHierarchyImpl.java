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

package org.drools.util;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

public class CodedHierarchyImpl<T> implements CodedHierarchy<T>, Externalizable {

    protected SortedMap<BitSet, HierNode<T>> line = new TreeMap<BitSet, HierNode<T>>( new HierCodeComparator() );
    protected boolean fixedRoot = false;

    public int size() {
        return fixedRoot ? line.size() - 1 : line.size();
    }

    public boolean hasKey( BitSet key ) {
        return line.containsKey( key );
    }


    protected HierNode<T> getNodeByKey( BitSet key ) {
        return line.get( key );
    }

    protected HierNode<T> getNode( T name ) {
        for ( HierNode<T> node : getNodes() ) {
            if ( node.getValue() != null && node.getValue().equals( name ) ) {
                return node;
            }
        }
        return null;
    }

    protected void add( HierNode<T> node ) {
        line.put( node.getBitMask(), node );
    }

    protected void remove( HierNode<T> node ) {
        line.remove( node.getBitMask() );
    }

    protected boolean contains( HierNode<T> node ) {
        return line.containsKey( node.getBitMask() );
    }

    protected Collection<HierNode<T>> getNodes() {
        return line.values();
    }





    public BitSet getCode( T val ) {
        if ( val == null ) {
            return null;
        }
        HierNode<T> node = getNode( val );
        return node != null ? node.getBitMask() : null;
    }

    public BitSet metMembersCode( Collection<T> vals ) {
        BitSet x = new BitSet();
        for ( T val : vals ) {
            x.or( getNode( val ).getBitMask() );
        }
        return x;
    }

    public BitSet jointMembersCode( Collection<T> vals ) {
        BitSet x = new BitSet();
        boolean first = true;
        for ( T val : vals ) {
            if ( first ) {
                first = false;
                x.or( getNode( val ).getBitMask() );
            } else {
                x.and( getNode( val ).getBitMask() );
            }

        }
        return x;
    }

    public BitSet meetCode( Collection<BitSet> codes ) {
        BitSet x = new BitSet();
        for ( BitSet code : codes ) {
            x.or( code );
        }
        return x;
    }

    public BitSet joinCode( Collection<BitSet> codes ) {
        BitSet x = new BitSet();
        boolean first = true;
        for ( BitSet code : codes ) {
            if ( first ) {
                first = false;
                x.or( code );
            } else {
                x.and( code );
            }

        }
        return x;
    }




    public List<T> getSortedMembers() {
        List<T> anx = new ArrayList<T>( size() );
        for ( HierNode<T> node : getNodes() ) {
            if ( node.getValue() != null ) {
                anx.add( node.getValue() );
            }
        }
        return anx;
    }

    public Map<T,BitSet> getSortedMap() {
        Map<T,BitSet> anx = new LinkedHashMap<T, BitSet>( size() );
        for ( HierNode<T> node : getNodes() ) {
            if ( node.getValue() != null ) {
                anx.put( node.getValue(), node.getBitMask() );
            }
        }
        return anx;
    }

    public T getMember( BitSet key ) {
        return line.containsKey( key ) ? line.get( key ).getValue() : null;
    }




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

    public Collection<T> parents( T x ) {
        HierNode<T> node = getNode( x );
        return parentValues(node);
    }

    public Collection<T> parents( BitSet x ) {
        HierNode<T> node = getNodeByKey(x);
        return parentValues( node );
    }




    public Collection<T> ancestors( T x ) {
        HierNode<T> node = getNode( x );
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
            ancestors.addAll( ancestors( p.getValue() ) );
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



    public Collection<T> upperAncestors( BitSet key ) {
        List<T> vals = new LinkedList<T>();

        //System.out.println( key );

        BitSet start = new BitSet();
        BitSet end = new BitSet();

        int index = 0;

        T rootVal = getMember( new BitSet() );
        if ( rootVal != null ) {
            vals.add( rootVal );
        }

        while ( index >= 0 ) {
            int s = index;
            int t = key.nextClearBit( s );

            start.clear();
            start.set( s, true );
            end.set( s, t, true );

//            System.out.println( "X  >> " + s + " << " + t );
//            System.out.println( "S  >> " + start );
//            System.out.println( "E  >> " + end );
//            System.out.println( "E+1>> " + nextKey( end ) );
            if ( t > 0 ) {
                for ( HierNode<T> val : line.subMap( start, nextKey( end ) ).values() ) {
//                    System.out.println( "\t " + val.getValue() );
                    vals.add( val.getValue() );
                }
            }
            index = key.nextSetBit( t );
        }
        return vals;
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
        HierNode<T> node = getNode( x );
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
        HierNode<T> node = getNode( y );
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


    public Collection<T> lowerDescendants( BitSet key ) {
        List<T> vals = new LinkedList<T>();
        if ( key.length() == 0 ) {
            return new ArrayList( getSortedMembers() );
        }

//        System.out.println( "DESC MAX LEN " + line.lastKey().length() );
//        System.out.println( "KEY LEN " + key.length() );
        int n = line.lastKey().length();

        for ( int j = key.length(); j <= n; j++ ) {

            BitSet start = new BitSet();
            start.or( key );
            start.set( j - 1 );

            BitSet end = new BitSet();
            end.set( j );

//            System.out.println( "S  >> " + toBinaryString( start ) );
//            System.out.println( "E  >> " + toBinaryString( end ) );

            for ( HierNode<T> val : line.subMap( start, end ).values() ) {
                BitSet x = val.getBitMask();
                if ( superset( x, key ) >= 0 ) {
//                    System.out.println( "Extracting " + val.getValue() );
                    vals.add( val.getValue() );
                }
            }

        }
        return vals;
    }





    /**
     * @param key a key, possibly the meet of a number of member keys
     * @return
     */
    public Collection<T> lowerBorder( BitSet key ) {
        return gcs( key, true );
    }
    /**
     * @param key a key, possibly the meet of a number of member keys
     * @return
     */
    public Collection<T> immediateChildren( BitSet key ) {
        return gcs( key, false );
    }

    /**
     * @param key a key, possibly the meet of a number of member keys
     * @return
     */
    Collection<T> gcs( BitSet key, boolean includeEquals ) {

        List<T> vals = new LinkedList<T>();
        List<HierNode<T>> border = gcsBorderNodes( key, includeEquals );

        for ( int j = 0; j < border.size(); j++ ) {
            HierNode<T> node = border.get( j );
            if ( node != null ) {
                vals.add( node.getValue() );
            }
        }

        return vals;

    }

    List<HierNode<T>> gcsBorderNodes( BitSet key, boolean includeEquals ) {
        List<HierNode<T>> border = new LinkedList<HierNode<T>>();


        int n = line.size() != 0 ? line.lastKey().length() : 0;
        for ( int j = key.length(); j <= n; j++ ) {

            BitSet start = new BitSet();
            start.or( key );
            if ( j > 0 ) {
                start.set( j - 1 );
            }

            BitSet end = new BitSet();
            end.set( j );
            for ( HierNode<T> val : line.subMap( start, end ).values() ) {
                BitSet candidate = val.getBitMask();
                boolean minimal =  true;
                int check = superset( candidate, key );
                if ( ( includeEquals && check >= 0 ) || ( ! includeEquals && check > 0 ) ) {
                    // it is a descendant of the probe key
                    for ( int k = 0; k < border.size(); k++ ) {
                        // current border
                        HierNode<T> ex = border.get( k );
                        if ( ex != null ) {
                            if ( superset( candidate, ex.getBitMask() ) >= 0 ) {
//                                System.out.println( "Skipping " + val + " due to " + ex );
                                minimal = false;
                                break;
                            } else if ( superset( ex.getBitMask(), candidate ) > 0 ) {
//                                System.out.println( "Clearing " + ex + " due to " + val );
                                border.set( k, null );
                            }

                        } // else cleared border member, continue

                    }
                    if ( minimal ) {
                        border.add( val );
                    }
                } // else no desc, ignore

            }

        }
        return border;
    }





    /**
     * @param key a key, possibly the meet of a number of member keys
     * @return
     */
    public Collection<T> upperBorder( BitSet key ) {
        return lcs( key, true );
    }
    /**
     * @param key a key, possibly the meet of a number of member keys
     * @return
     */
    public Collection<T> immediateParents( BitSet key ) {
        return lcs( key, false );
    }

    /**
     * @param key a key, possibly the meet of a number of member keys
     * @return
     */
    Collection<T> lcs( BitSet key, boolean includeEquals ) {
        List<T> vals = new LinkedList<T>();
        List<HierNode<T>> border = lcsBorderNodes( key, includeEquals );
        for ( int j = 0; j < border.size(); j++ ) {
            HierNode<T> node = border.get( j );
            if ( node != null ) {
                vals.add( node.getValue() );
            }
        }
        return vals;
    }

    List<HierNode<T>> lcsBorderNodes( BitSet key, boolean includeEquals ) {
        List<HierNode<T>> border = new ArrayList<HierNode<T>>();

//        System.out.println( key );

        BitSet start = new BitSet();
        BitSet end = new BitSet();

        int index = 0;

        HierNode<T> root = line.get( new BitSet() );
        if ( root != null ) {
            border.add( root );
        }


        while ( index >= 0 ) {
            int s = index;
            int t = key.nextClearBit( s );

            start.clear();
            start.set( s, true );
            end.set( s, t, true );
            for ( HierNode<T> val : line.subMap( start, nextKey( end ) ).values() ) {
                BitSet candidate = val.getBitMask();
                int comp = superset( key, candidate );
                if ( ( includeEquals && comp >= 0 ) || ( ! includeEquals && comp > 0 ) ) {
                    border.add( val );

                    for ( int j = 0; j < border.size(); j++ ) {
                        HierNode<T> ex = border.get( j );

                        if ( ex != null ) {
                            if ( superset( candidate, ex.getBitMask() ) > 0 ) {
//                            System.out.println( "Clearing " + ex + " due to " + val );
                                border.set( j, null );
                            }
                        }
                    }
                }
//                System.out.println( "\t\t " + border );
            }

            index = key.nextSetBit( t );
        }
        return border;
    }









    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("*****************************************\n");

        int len = 0;
        for ( HierNode<T> node : getNodes() ) {
            len = Math.max( len, numBit( node.getBitMask() ) );
        }

        for ( HierNode<T> node : getNodes() ) {
            builder.append( node.toString( len ) ).append("\n");
        }
        builder.append( "*****************************************\n" );
        builder.append( getSortedMap() ).append("\n");
        builder.append("*****************************************\n");
        return builder.toString();
    }

    protected String toBinaryString( BitSet mask ) {
        return toBinaryString(mask, mask.length());
    }

    protected String toBinaryString( BitSet mask, int len ) {
        StringBuilder sb = new StringBuilder();
        for ( int j = len - 1; j >= 0; j-- ) {
            sb.append( mask.get( j ) ? "1 " : "0 " );
        }
        return sb.toString();
    }





    BitSet prevKey( BitSet key ) {
        BitSet b = new BitSet();
        b.or( key );
        int x = key.nextSetBit( 0 );
        if ( x == 0 ) {
            b.clear( 0 );
        } else {
            b.set( 0, x, true );
            b.clear( x );
        }
        return b;
    }


    BitSet nextKey( BitSet key ) {
        BitSet b = new BitSet();
        b.or( key );
        int x = key.nextSetBit( 0 );
        if ( x == 0 ) {
            int y = b.nextClearBit( 0 );
            b.set( x, y, false );
            b.set(y);
        } else {
            b.set(0);
        }
        return b;
    }


    public static boolean supersetOrEqualset( BitSet n1, BitSet n2 ) {
        BitSet x = new BitSet();
        x.or( n1 );
        x.and( n2 );
        return x.equals( n2 );
    }

    int superset( HierNode<T> n1, HierNode<T> n2 ) {
        return superset( n1.getBitMask(), n2.getBitMask() );
    }

    int superset( BitSet n1, BitSet n2 ) {
        if ( n1.equals( n2 ) ) {
            return 0;
        }
        return supersetOrEqualset(n1, n2) ? 1 : -1;
    }

    int numBit( BitSet x ) {
        return x.length();
    }

    public void writeExternal( ObjectOutput objectOutput ) throws IOException {
        objectOutput.writeObject( line );
        objectOutput.writeBoolean( fixedRoot );
    }

    public void readExternal( ObjectInput objectInput ) throws IOException, ClassNotFoundException {
        line = (SortedMap<BitSet, HierNode<T>>) objectInput.readObject();
        fixedRoot = objectInput.readBoolean();
    }

    public void clear() {
        line.clear();
        fixedRoot = false;
    }


    protected static class HierNode<T> implements Comparable<HierNode<T>>, Externalizable {

        public T value;
        public BitSet bitMask = null;
        public List<HierNode<T>> parents = new ArrayList<HierNode<T>>();
        public List<HierNode<T>> children = new ArrayList<HierNode<T>>();


        public HierNode() {

        }

        public HierNode( T value ) {
            this.value = value;
        }

        protected HierNode( HierNode<T> xt ) {
            value = xt.getValue();
            bitMask = xt.getBitMask();
            parents.addAll( xt.getParents() );
            children.addAll( xt.getChildren() );
        }

        public HierNode( BitSet key ) {
            bitMask = key;
        }

        public HierNode( T val, BitSet key ) {
            this.value = val;
            this.bitMask = key;
        }

        public T getValue() {
            return value;
        }

        public BitSet getBitMask() {
            return bitMask;
        }

        public void setBitMask( BitSet bitMask ) {
            if ( this.bitMask == null ) {
                this.bitMask = bitMask;
            } else {
                this.bitMask.clear();
                this.bitMask.or( bitMask );
            }
        }

        public Collection<HierNode<T>> getParents() {
            return parents;
        }

        public Collection<HierNode<T>> getChildren() {
            return children;
        }

        public void addChild( HierNode<T> node ) {
            children.add( node );
        }

        public void addParent( HierNode<T> node ) {
            parents.add( node );
        }


        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            HierNode<T> hierNode = (HierNode<T>) o;

            if (!bitMask.equals(hierNode.bitMask)) return false;

            return true;
        }

        @Override
        public int hashCode() {
            return bitMask.hashCode();
        }

        @Override
        public String toString() {
            return toString( bitMask != null ? bitMask.length() : 0 );
        }

        public String toString( int len ) {
            return value + "[ " +
                    ( ( bitMask != null ) ? toBinaryString( bitMask, len ) : "n/a" )
                    + "]";
        }

        protected String toBinaryString( BitSet mask, int len ) {
            StringBuilder sb = new StringBuilder();
            for ( int j = len - 1; j >= 0; j-- ) {
                sb.append( mask.get( j ) ? "1 " : "0 " );
            }
            return sb.toString();
        }

        public int compareTo( HierNode<T> hierNode ) {
            BitSet yset = hierNode.bitMask;
            int lx = bitMask.length();
            int ly = yset.length();
            int l = lx > ly ? lx : ly;

            for ( int j = l; j >= 0; j-- ) {
                boolean x = bitMask.get( j );
                boolean y = yset.get( j );
                if ( x && ! y ) {
                    return 1;
                }
                if ( y && ! x ) {
                    return -1;
                }
            }

            return 0;
        }

        public void setValue( T value ) {
            this.value = value;
        }

        public void writeExternal( ObjectOutput out ) throws IOException {
            out.writeObject( value );
            out.writeObject( bitMask );
            out.writeObject( parents );
            out.writeObject( children );
        }

        public void readExternal( ObjectInput in ) throws IOException, ClassNotFoundException {
            value = (T) in.readObject();
            bitMask = (BitSet) in.readObject();
            parents = (List<HierNode<T>>) in.readObject();
            children = (List<HierNode<T>>) in.readObject();
        }
    }


    protected static class HierCodeComparator implements Comparator<BitSet>, Externalizable {

        public HierCodeComparator() {

        }

        public int compare( BitSet bitMask, BitSet yset ) {
            int lx = bitMask.length();
            int ly = yset.length();
            int l = lx > ly ? lx : ly;

            for ( int j = l; j >= 0; j-- ) {
                boolean x = bitMask.get( j );
                boolean y = yset.get( j );
                if ( x && ! y ) {
                    return 1;
                }
                if ( y && ! x ) {
                    return -1;
                }
            }
            return 0;
        }

        public void writeExternal(ObjectOutput objectOutput) throws IOException {

        }

        public void readExternal(ObjectInput objectInput) throws IOException, ClassNotFoundException {

        }
    }

    public static BitSet stringToBitSet( String s ) {
        BitSet b = new BitSet();
        int n = s.length();
        for( int j = 0; j < s.length(); j++ ) {
            if ( s.charAt( j ) == '1' ) {
                b.set( n - j - 1 );
            } else if ( s.charAt( j ) != '0' ) {
                throw new IllegalStateException( "The string " + s + " is not a valid bitset encoding" );
            }
        }
        return b;
    }

}
