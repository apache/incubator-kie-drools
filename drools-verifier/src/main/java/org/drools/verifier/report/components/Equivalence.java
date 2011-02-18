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

package org.drools.verifier.report.components;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.drools.verifier.data.VerifierComponent;

/**
 * Object type that indicates a equivalence between two objects.
 * 
 * Equivalence happens when the LHS of the rules are redundant, but the LHS is different.
 * 
 */
public class Equivalence
    implements
    Cause {

    private final List<VerifierComponent> items = new ArrayList<VerifierComponent>( 2 );
    private final Collection<Cause>       causes;

    public Equivalence(VerifierComponent first,
                       VerifierComponent second) {
        items.add( first );
        items.add( second );
        this.causes = new ArrayList<Cause>();
        causes.add( first );
        causes.add( second );
    }

    public Equivalence(VerifierComponent first,
                       VerifierComponent second,
                       Collection<Cause> causes) {
        items.add( first );
        items.add( second );
        this.causes = causes;
    }

    public List<VerifierComponent> getItems() {
        return items;
    }

    @Override
    public String toString() {
        return "Equivalency between: (" + items.get( 0 ) + ") and (" + items.get( 1 ) + ").";
    }

    public Collection<Cause> getCauses() {
        return causes;
    }
}
