/*
 * Copyright 2005 Red Hat, Inc. and/or its affiliates.
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

package org.drools.compiler.compiler;

import org.kie.api.io.Resource;

public class ParserError extends DroolsError {
    private final int    row;
    private final int    col;
    private final String message;
    private final String namespace;

    public ParserError(final String message,
                       final int row,
                       final int col) {
        this(null, message, row, col);
    }

    public ParserError(final Resource resource,
                       final String message,
                       final int row,
                       final int col) {
        this(resource, message, row, col, "");
    }

    public ParserError(final Resource resource,
                       final String message,
                       final int row,
                       final int col,
                       final String namespace) {
        super(resource);
        this.message = message;
        this.row = row;
        this.col = col;
        this.namespace = namespace;
    }

    public String getMessage() {
        return this.message;
    }

    @Override
    public String getNamespace() {
        return namespace;
    }

    public int[] getLines() {
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
