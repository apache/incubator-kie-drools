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

package org.drools.examples.conway.patterns;

/**
 * Represents a simple glider
 * 
 * @author <a href="mailto:brown_j@ociweb.com">Jeff Brown</a>
 * @see ConwayPattern
 * @see org.drools.examples.conway.CellGridImpl
 */
public class SimpleGlider
    implements
    ConwayPattern {

    private static final long serialVersionUID = 510l;

    private final boolean[][] grid = {{false, true, false}, {true, false, false}, {true, true, true}};
    //private final boolean[][] grid = {{false, false, false}, {true, true, false}, {false, false, false}};

    /**
     * This method should return a 2 dimensional array of boolean that represent
     * a conway grid, with <code>true</code> values in the positions where
     * cells are alive
     * 
     * @return array representing a conway grid
     */
    public boolean[][] getPattern() {
        return this.grid;
    }

    /**
     * @return the name of this pattern
     */
    public String getPatternName() {
        return "Simple Glider";
    }

    public String toString() {
        return getPatternName();
    }
}
