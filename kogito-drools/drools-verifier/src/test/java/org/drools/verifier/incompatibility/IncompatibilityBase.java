/*
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

package org.drools.verifier.incompatibility;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.drools.verifier.TestBaseOld;

import static org.junit.Assert.*;
import org.drools.verifier.components.VerifierComponentType;
import org.drools.verifier.data.VerifierComponent;
import org.drools.verifier.report.components.Cause;
import org.drools.verifier.report.components.Incompatibility;

public class IncompatibilityBase extends TestBaseOld {

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
