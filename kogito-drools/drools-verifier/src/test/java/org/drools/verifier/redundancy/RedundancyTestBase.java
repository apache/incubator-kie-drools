package org.drools.verifier.redundancy;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.drools.verifier.TestBase;
import org.drools.verifier.components.RuleComponent;
import org.drools.verifier.report.components.Cause;
import org.drools.verifier.report.components.CauseType;
import org.drools.verifier.report.components.Redundancy;

/**
 *
 * @author Toni Rikkola
 *
 */
public class RedundancyTestBase extends TestBase {

    /**
     * Creates redundancy map from Redundancy objects, one rule may have several
     * redundancy dependencies.
     *
     * @param iter
     * @return
     */
    protected Map<String, Set<String>> createRedundancyMap(Iterator<Object> iter) {

        Map<String, Set<String>> map = new HashMap<String, Set<String>>();
        while ( iter.hasNext() ) {
            Object o = (Object) iter.next();
            if ( o instanceof Redundancy ) {
                Redundancy r = (Redundancy) o;
                RuleComponent left = (RuleComponent) r.getLeft();
                RuleComponent right = (RuleComponent) r.getRight();

                if ( map.containsKey( left.getRuleName() ) ) {
                    Set<String> set = map.get( left.getRuleName() );
                    set.add( right.getRuleName() );
                } else {
                    Set<String> set = new HashSet<String>();
                    set.add( right.getRuleName() );
                    map.put( left.getRuleName(),
                             set );
                }
            }
        }

        return map;
    }

    /**
     * Creates redundancy map from Redundancy objects, one rule may have several
     * redundancy dependencies.
     *
     * @param iter
     * @return
     */
    protected Map<Cause, Set<Cause>> createRedundancyCauseMap(CauseType type,
                                                              Iterator<Object> iter) {

        Map<Cause, Set<Cause>> map = new HashMap<Cause, Set<Cause>>();
        while ( iter.hasNext() ) {
            Object o = (Object) iter.next();
            if ( o instanceof Redundancy ) {
                Redundancy r = (Redundancy) o;

                if ( r.getLeft().getCauseType() == type ) {
                    Cause left = r.getLeft();
                    Cause right = r.getRight();

                    if ( map.containsKey( left ) ) {
                        Set<Cause> set = map.get( left );
                        set.add( right );
                    } else {
                        Set<Cause> set = new HashSet<Cause>();
                        set.add( right );
                        map.put( left,
                                 set );
                    }
                }
            }
        }

        return map;
    }

    public void testDummy() {
        // this is needed as eclipse will try to run this and produce a failure
        // if its not here.
    }

    /**
     * Returns true if map contains redundancy where key is redundant to value.
     *
     * @param map
     * @param key
     * @param value
     * @return True if redundancy exists.
     */
    protected static boolean mapContains(Map<String, Set<Redundancy>> map,
                                         String key,
                                         Object value) {
        if ( map.containsKey( key ) ) {
            Set<Redundancy> set = map.get( key );
            boolean exists = set.remove( value );

            // If set is empty remove key from map.
            if ( set.isEmpty() ) {
                map.remove( key );
            }
            return exists;
        }
        return false;
    }
}
