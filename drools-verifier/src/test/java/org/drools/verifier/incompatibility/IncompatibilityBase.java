package org.drools.verifier.incompatibility;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.drools.verifier.TestBase;
import org.drools.verifier.components.VerifierComponentType;
import org.drools.verifier.data.VerifierComponent;
import org.drools.verifier.report.components.Cause;
import org.drools.verifier.report.components.Incompatibility;

public class IncompatibilityBase extends TestBase {

    public void testDummy() {
        // this is needed as eclipse will try to run this and produce a failure
        // if its not here.
        assertTrue( true );
    }

    /**
     * Creates incompatibility map from Incompatibility objects, one rule may
     * have several incompatibility dependencies.
     * 
     * @param iter
     * @return
     */
    protected Map<Cause, Set<Cause>> createIncompatibilityMap(VerifierComponentType type,
                                                              Iterator<Object> iter) {

        Map<Cause, Set<Cause>> map = new HashMap<Cause, Set<Cause>>();
        while ( iter.hasNext() ) {
            Object o = (Object) iter.next();
            if ( o instanceof Incompatibility ) {
                Incompatibility r = (Incompatibility) o;

                if ( ((VerifierComponent) r.getLeft()).getVerifierComponentType().equals( type ) ) {
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
}
