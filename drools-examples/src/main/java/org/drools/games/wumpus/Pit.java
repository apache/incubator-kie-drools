/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.drools.games.wumpus;

import org.kie.api.definition.type.PropertyReactive;

@PropertyReactive
public class Pit extends Thing {

    public Pit(int row,
                int col) {
        super(row, col);
    }

    @Override
    public String toString() {
        return "Pitt [row=" + getRow() + ", col=" + getCol() + "]";
    }


    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + getCol();
        result = prime * result + getRow();
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if ( this == obj ) return true;
        if ( obj == null ) return false;
        if ( getClass() != obj.getClass() ) return false;
        Pit other = (Pit) obj;
        if ( getCol() != other.getCol() ) return false;
        if ( getRow() != other.getRow() ) return false;
        return true;
    }

}
