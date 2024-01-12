/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.drools.drl.parser.lang;

import org.drools.drl.parser.DroolsError;


public class ExpanderException extends DroolsError {

    private static final long serialVersionUID = 510l;

    private int[]             line;

    public ExpanderException(final String message,
                             final int line) {
        super("[" + line + "] " + message);
        this.line = new int[] { line };
    }
    
    public int[] getLines() {
        return this.line;
    }
    
    public int getLine() {
        return this.line[0];
    }
    
    public String toString() {
        return getMessage();
    }

}
