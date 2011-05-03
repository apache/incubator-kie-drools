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
package org.drools.examples.sudoku;

/**
 * Abstract class for "numbered" cell groups: rows and columns.
 */
public abstract class CellFile extends CellGroup {
    
    private int number;
    
    /**
     * Constructor.
     * 
     * @param number thw row or column number.
     */
    protected CellFile(int number) {
        super();
        this.number = number;
    }

    /**
     * Retrieves the row or column number.
     * @return an int value
     */
    public int getNumber() {
        return number;
    }
    
    /*
     * (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        String del = "";
        for (int i = 0; i < getCells().size(); i++) {
            String cStr = getCells().get( i ).toString();
            sb.append(del).append(cStr);
            del = ", ";
        }
        return sb.toString();
    }
}
