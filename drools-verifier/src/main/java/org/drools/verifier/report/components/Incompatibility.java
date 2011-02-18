/**
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
import java.util.List;

/**
 * Object type that indicates an incompatibility between two objects.
 * 
 * Incompatibility happens when there is no value that would satisfy both objects.
 * 
 * 
 * Example: 
 * A: x > 10
 * B: x == 100
 * 
 * @author Toni Rikkola
 */
public class Incompatibility
    implements
    Cause {

    private final Cause  left;
    private final Cause  right;

    public Incompatibility(Cause left,
                           Cause right) {
        this.left = left;
        this.right = right;
    }

    public Cause getLeft() {
        return left;
    }

    public Cause getRight() {
        return right;
    }

    @Override
    public String toString() {
        return "(" + getLeft() + ") and (" + getRight() + ") are incompatible.";
    }

    public Collection<Cause> getCauses() {
        List<Cause> list = new ArrayList<Cause>();
        list.add( left );
        list.add( right );
        return list;
    }
}
