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

/**
 * Object type that indicates an opposity between two objects.
 * 
 * 
 * Opposity happens when only the values that would satisfy object A 
 * can not satisfy object B.
 * 
 * Example: 
 * A: a == 10
 * B: a != 10
 * 
 * @author Toni Rikkola
 */
public class Opposites extends Incompatibility
    implements
    Cause {

    public Opposites(Cause left,
                     Cause right) {
        super( left,
               right );
    }

    @Override
    public String toString() {
        return "Opposites: (" + getLeft() + ") and (" + getRight() + ").";
    }
}
