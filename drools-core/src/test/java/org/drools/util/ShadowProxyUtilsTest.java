package org.drools.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import junit.framework.TestCase;

public class ShadowProxyUtilsTest extends TestCase {

    protected void setUp() throws Exception {
        super.setUp();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testCloneList() {
        List list = new ArrayList();
        list.add( "a" );
        list.add( "b" );

        List clone = (List) ShadowProxyUtils.cloneObject( list );
        assertEquals( list,
                      clone );
        assertNotSame( list,
                       clone );
    }

    public void testCloneMap() {
        Map map = new TreeMap();
        map.put( "a",
                 "a" );
        map.put( "b",
                 "b" );

        Map clone = (Map) ShadowProxyUtils.cloneObject( map );
        assertEquals( map,
                      clone );
        assertNotSame( map,
                       clone );
    }

    public void testCloneArray() {
        int[][] array = new int[][]{{0, 0}, {0, 1}, {1, 0}, {1, 1}};

        int[][] clone = (int[][]) ShadowProxyUtils.cloneObject( array );
        assertTrue( ArrayUtils.deepEquals( array,
                                           clone ) );
        assertNotSame( array,
                       clone );
    }

    public void testCloneUnmodifiableSet() {
        Set set = new HashSet();
        set.add( "a" );
        set.add( "b" );

        Set unmod = Collections.unmodifiableSet( set );

        Set clone = (Set) ShadowProxyUtils.cloneObject( unmod );
        assertEquals( unmod,
                      clone );
        assertSame( unmod,
                    clone );
    }

    public void testCloneUnmodifiableMap() {
        Map map = new TreeMap();
        map.put( "a",
                 "a" );
        map.put( "b",
                 "b" );
        Map unmod = Collections.unmodifiableMap( map );

        Map clone = (Map) ShadowProxyUtils.cloneObject( unmod );
        assertEquals( unmod,
                      clone );
        assertSame( unmod,
                    clone );
    }

    public void testCloneEmptyList() {
        List list = Collections.EMPTY_LIST;

        List clone = (List) ShadowProxyUtils.cloneObject( list );
        assertEquals( list,
                      clone );
        assertSame( list,
                    clone );
    }

    public void testCloneEmptySet() {
        Set set = Collections.EMPTY_SET;

        Set clone = (Set) ShadowProxyUtils.cloneObject( set );
        assertEquals( set,
                      clone );
        assertSame( set,
                    clone );
    }

    public void testCloneEmptyMap() {
        Map map = Collections.EMPTY_MAP;

        Map clone = (Map) ShadowProxyUtils.cloneObject( map );
        assertEquals( map,
                      clone );
        assertSame( map,
                    clone );
    }

    public void testCloneRegularObject() {
        // this is never supposed to happen,
        // but we don't want the method to blow up if it happens
        Object obj = new Object();

        Object clone = (Object) ShadowProxyUtils.cloneObject( obj );
        assertEquals( obj,
                      clone );
        assertSame( obj,
                    clone );

    }

}
