/*
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
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

import org.drools.verifier.data.VerifierComponent;

/**
 * Object type that indicates a subsumption between two objects.
 * 
 * Subsumption happens when all possible the values for component A,
 * are able to satisfy component B and only some values that are able to 
 * satisfy component B are able to satisfy component A.
 * 
 * Example "if x is more than 1 both restrictions are satisfied":
 * A: x > 10 
 * B: x > 1 
 */
public class Subsumption
    implements
    Cause {

    private final VerifierComponent left;
    private final VerifierComponent right;
    private final Collection<Cause> causes;

    public Subsumption(VerifierComponent left,
                       VerifierComponent right) {
        this.left = left;
        this.right = right;
        this.causes = new ArrayList<Cause>();
        causes.add( left );
        causes.add( right );
    }

    public Subsumption(VerifierComponent left,
                       VerifierComponent right,
                       Collection<Cause> causes) {
        this.left = left;
        this.right = right;
        this.causes = causes;
    }

    public VerifierComponent getLeft() {
        return left;
    }

    public VerifierComponent getRight() {
        return right;
    }

    @Override
    public String toString() {
        return "Subsumption between: (" + getLeft() + ") and (" + getRight() + ").";
    }

    public Collection<Cause> getCauses() {
        return causes;
    }
}
