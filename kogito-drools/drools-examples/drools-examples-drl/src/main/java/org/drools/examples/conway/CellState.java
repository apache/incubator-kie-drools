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

package org.drools.examples.conway;

/**
 * <code>CellState</code> enumerates all of the valid states that a Cell may
 * be in.
 * 
 * @author <a href="mailto:brown_j@ociweb.com">Jeff Brown</a>
 * @see Cell
 * @see CellGridImpl
 */
public class CellState {

    public static final CellState LIVE = new CellState( "LIVE" );
    public static final CellState DEAD = new CellState( "DEAD" );

    private final String          name;

    private CellState(final String name) {
        this.name = name;
    }

    public String toString() {
        return "CellState: " + this.name;
    }
}
