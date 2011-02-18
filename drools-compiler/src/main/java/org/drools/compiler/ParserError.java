/*
 * Copyright 2005 JBoss Inc
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

package org.drools.compiler;

public class ParserError extends DroolsError {
    private int    row;
    private int    col;
    private String message;

    public ParserError(final String message,
                       final int row,
                       final int col) {
        super();
        this.message = message;
        this.row = row;
        this.col = col;
    }

    public String getMessage() {
        return this.message;
    }
    
    public int[] getErrorLines() {
        return new int[] { this.row };
    }

    public int getCol() {
        return this.col;
    }

    public int getRow() {
        return this.row;
    }

    public String toString() {
        return "[" + this.row + "," + this.col + "]: " + this.message;
    }

}
