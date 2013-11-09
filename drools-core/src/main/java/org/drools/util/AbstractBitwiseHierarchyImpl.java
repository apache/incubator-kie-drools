package org.drools.util;

import org.drools.factmodel.traits.LatticeElement;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.*;


public abstract class AbstractBitwiseHierarchyImpl<H ,J extends LatticeElement<H>> implements Externalizable {


    protected SortedMap<BitSet,J> line = new TreeMap<BitSet,J>( new HierCodeComparator() );
    protected boolean fixedRoot = false;

    public int size() {
        return fixedRoot ? line.size() - 1 : line.size();
    }

    protected J getNodeByKey( BitSet key ) {
        return line.get( key );
    }

    protected abstract J getNode( LatticeElement<H> name );

    protected void remove( J node ) {
        line.remove( node.getBitMask() );
    }

    protected boolean contains( J node ) {
        return line.containsKey( node.getBitMask() );
    }

    public BitSet getCode( LatticeElement<H> val ) {
        if ( val == null ) {
            return null;
        }
        J node = getNode( val );
        return node != null ? node.getBitMask() : null;
    }

    public BitSet metMembersCode( Collection<LatticeElement<H>> vals ) {
        BitSet x = new BitSet( this.size() );
        for ( LatticeElement<H> val : vals ) {
            x.or( getNode( val ).getBitMask() );
        }
        return x;
    }

    public BitSet jointMembersCode( Collection<LatticeElement<H>> vals ) {
        BitSet x = new BitSet( this.size() );
        boolean first = true;
        for ( LatticeElement<H> val : vals ) {
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
        BitSet x = new BitSet( this.size() );
        for ( BitSet code : codes ) {
            x.or( code );
        }
        return x;
    }

    public BitSet joinCode( Collection<BitSet> codes ) {
        BitSet x = new BitSet( this.size() );
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

    public List<LatticeElement<H>> getSortedMembers() {
        List<LatticeElement<H>> anx = new ArrayList<LatticeElement<H>>( size() );
        for ( J node : getNodes() ) {
            if ( node.getValue() != null ) {
                anx.add( node );
            }
        }
        return anx;
    }

    public Collection<LatticeElement<H>> upperAncestors( BitSet key ) {
        List<LatticeElement<H>> vals = new LinkedList<LatticeElement<H>>();
        int l = key.length();
        //System.out.println( key );

        BitSet start = new BitSet( l );
        BitSet end = new BitSet( l );

        int index = 0;

        LatticeElement<H> rootVal = getMember( new BitSet() );
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
                for ( J val : line.subMap( start, nextKey( end ) ).values() ) {
//                    System.out.println( "\t " + val.getValue() );
                    vals.add( val );
                }
            }
            index = key.nextSetBit( t );
        }
        return vals;
    }

     /**
     * @param key a key, possibly the meet of a number of member keys
     * @return
     */
    public Collection<LatticeElement<H>> lowerBorder( BitSet key ) {
        return gcs( key, true );
    }

    /**
     * @param key a key, possibly the meet of a number of member keys
     * @return
     */
    public Collection<LatticeElement<H>> immediateChildren( BitSet key ) {
        return gcs( key, false );
    }

    /**
     * @param key a key, possibly the meet of a number of member keys
     * @return
     */
    Collection<LatticeElement<H>> gcs( BitSet key, boolean includeEquals ) {

        List<LatticeElement<H>> vals = new LinkedList<LatticeElement<H>>();
        List<J> border = gcsBorderNodes( key, includeEquals );

        for ( int j = 0; j < border.size(); j++ ) {
            J node = border.get( j );
            if ( node != null ) {
                vals.add( node );
            }
        }

        return vals;

    }

    List<J> gcsBorderNodes( BitSet key, boolean includeEquals ) {
        List<J> border = new LinkedList<J>();
        int l = key.length();

        int n = line.size() != 0 ? line.lastKey().length() : 0;
        BitSet start = new BitSet( n );
        BitSet end = new BitSet( n );
        start.or( key );

//        for ( int j = l; j <= n; j++ ) {
        if ( l > n ) { return border; }

        if ( l > 0 ) {
            start.set( l - 1 );
        }
        end.set( n );
//            end.set( j );

        for ( J val : line.subMap( start, end ).values() ) {
            BitSet candidate = val.getBitMask();
            boolean minimal =  true;
            int check = superset( candidate, key );
            if ( ( includeEquals && check >= 0 ) || ( ! includeEquals && check > 0 ) ) {
                // it is a descendant of the probe key
                for ( int k = 0; k < border.size(); k++ ) {
                    // current border
                    J ex = border.get( k );
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

//            if ( j > 0 ) {
//                start.clear( j - 1 );
//            }
//            end.clear( j );
//
//        }
        return border;
    }

    /**
     * @param key a key, possibly the meet of a number of member keys
     * @return
     */
    Collection<LatticeElement<H>> lcs( BitSet key, boolean includeEquals ) {
        List<LatticeElement<H>> vals = new LinkedList<LatticeElement<H>>();
        List<J> border = lcsBorderNodes( key, includeEquals );
        for ( int j = 0; j < border.size(); j++ ) {
            J node = border.get( j );
            if ( node != null ) {
                vals.add( node);//.getValue() );
            }
        }
        return vals;
    }

    List<J> lcsBorderNodes( BitSet key, boolean includeEquals ) {
        List<J> border = new ArrayList<J>();
        if ( key == null ) { return border; }
//        System.out.println( key );

        int l = key.length();
        BitSet start = new BitSet( l + 1 );
        BitSet end = new BitSet( l + 1 );

        int index = 0;

        J root = line.get( new BitSet() );
        if ( root != null ) {
            border.add( root );
        }


        while ( index >= 0 ) {
            int s = index;
            int t = key.nextClearBit( s );

            start.clear();
            start.set( s, true );
            end.set( s, t, true );
            for ( J val : line.subMap( start, nextKey( end ) ).values() ) {
                BitSet candidate = val.getBitMask();
                int comp = superset( key, candidate );
                if ( ( includeEquals && comp >= 0 ) || ( ! includeEquals && comp > 0 ) ) {
                    border.add( val );

                    for ( int j = 0; j < border.size(); j++ ) {
                        J ex = border.get( j );

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

//------------------------------

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
        BitSet b = new BitSet( key.length() );
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
        int l = key.length();
        if ( l == 0 ) {
            BitSet b = new BitSet( 1 );
            b.set( 0 );
            return b;
        }

        BitSet b = new BitSet( l + 1 );
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
        BitSet x;
        int l1 = n1.length();
        int l2 = n2.length();

        if ( l1 > l2 ) {
            x = new BitSet( l2 );
            x.or( n2 );
            x.and( n1 );
        } else {
            x = new BitSet( l1 );
            x.or( n1 );
            x.and( n2 );
        }
        return x.equals( n2 );
    }

    int superset( J n1, J n2 ) {
        return superset( n1.getBitMask(), n2.getBitMask() );
    }

    public int superset( BitSet n1, BitSet n2 ) {
        if ( n1.equals( n2 ) ) {
            return 0;
        }
        return supersetOrEqualset(n1, n2) ? 1 : -1;
    }

    protected int numBit( BitSet x ) {
        return x.length();
    }

    public void writeExternal( ObjectOutput objectOutput ) throws IOException {
    }

    public void readExternal( ObjectInput objectInput ) throws IOException, ClassNotFoundException {
    }

    protected static class HierCodeComparator implements Comparator<BitSet>, Externalizable {

        public HierCodeComparator() {

        }

        public int compare( BitSet bitMask, BitSet yset ) {
            int lx = bitMask.length();
            int ly = yset.length();

            if ( lx == 0 && ly == 0 ) { return 0; }
            if ( lx > ly ) { return 1; }
            if ( ly > lx ) { return -1; }

            BitSet x;
            x = new BitSet( ly );
            x.or( yset );
            x.xor( bitMask );

            if ( x.isEmpty() ) { return 0; }

            int ix = x.length() - 1;
            if ( bitMask.get( ix ) ) {
                return 1;
            } else if ( yset.get( ix ) ) {
                return -1;
            } else {
                return 0;
            }
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

    //---------------needed by sub classes of TypeHierarchy---------------

//    public void addMember(LatticeElement<H> val, BitSet key) {
//        System.out.println(">>>>>>************* it should not happen");
//    }

    public void removeMember(LatticeElement<H> val) {
        //To change body of implemented methods use File | Settings | File Templates.
        System.out.println(">>>>>>************* it should not happen");
    }

    public void removeMember(BitSet key) {
        J node = line.get(key);
        remove(node);
    }

    public Map<LatticeElement<H>, BitSet> getSortedMap() {
        Map<LatticeElement<H>,BitSet> anx = new LinkedHashMap<LatticeElement<H>, BitSet>( size() );
        for ( J node : getNodes() ) {
            if ( node.getValue() != null ) {
                anx.put( node, node.getBitMask() );
            }
        }
        return anx;
    }

    public boolean hasKey(BitSet key) {
        return line.containsKey( key );
    }

//    public Collection<J> children(BitSet key) {
//        return null;  //To change body of implemented methods use File | Settings | File Templates.
//    }

    public Collection<LatticeElement<H>> lowerDescendants( BitSet key ) {
        List<LatticeElement<H>> vals = new LinkedList<LatticeElement<H>>();
        int l = key.length();
        if ( l == 0 || line.size()==0) {//changed by mamad
            return new ArrayList( getSortedMembers() );
        }
        int n = line.lastKey().length();

        if ( l > n ) { return vals; }

        BitSet start = new BitSet( n );
        BitSet end = new BitSet( n );
        start.or( key );

        start.set( l - 1 );
        end.set( n );

        for ( J val : line.subMap( start, end ).values() ) {
            BitSet x = val.getBitMask();
            if ( superset( x, key ) >= 0 ) {
                vals.add( val);//.getValue() );
            }
        }

        start.clear( l - 1 );

        return vals;
    }

    protected Collection<LatticeElement<H>> parentValues( J node ) {
        if ( node == null ) {
            return Collections.EMPTY_LIST;
        }

//        List<H> p = new ArrayList<H>( node.getParents().size() );
//        for ( J parent : node.getParents() ) {
//            p.add( parent.getValue() );
//        }
//        return p;
        System.out.println(">>>>>>************* it should not happen");
        return null;
    }

    public Collection<LatticeElement<H>> parents(LatticeElement<H> x) {
        J node = getNode( x );
        return parentValues(node);
    }

    public Collection<LatticeElement<H>> parents(BitSet x) {
        J node = getNodeByKey(x);
        return parentValues( node );
    }

    public Collection<LatticeElement<H>> upperBorder(BitSet key) {
        return lcs( key, true );
    }

    public Collection<LatticeElement<H>> immediateParents(BitSet key) {
        return lcs( key, false );
    }

    public boolean isEmpty() {
        return line.isEmpty();
    }

    protected void add( J node ) {
        line.put(node.getBitMask(), node);
    }

    protected Collection<J> getNodes() {
        return line.values();
    }

    public LatticeElement<H> getMember( BitSet key ) {
        return line.containsKey( key ) ? line.get( key ) : null;
    }

}
