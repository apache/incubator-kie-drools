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

package org.drools.template.model;

import java.io.StringWriter;

/**
 * This contains the DRL output that each piece of the parser spreadsheet will contribute to
 * 
 * @author Michael Neale
 *
 */
public class DRLOutput {

    private StringWriter writer;

    public void writeLine(final String line) {
        final StringBuffer buf = this.writer.getBuffer();
        buf.append( line );
        buf.append( '\n' );
    }

    public DRLOutput() {
        this.writer = new StringWriter();
    }

    /** Return the rendered DRL so far */
    public String getDRL() {
        return this.writer.toString();
    }

    public String toString() {
        return getDRL();
    }

}
