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
package org.drools.compiler.compiler;


import org.drools.drl.ast.descr.ImportDescr;
import org.drools.drl.parser.DroolsError;
import org.kie.api.io.Resource;

/**
 * This is used for reporting errors with loading a ruleflow.
 */
public class ProcessLoadError extends DroolsError {

    private Exception exception;
    private static final int[] lines = new int[0];

    public ProcessLoadError(Resource resource, String message, Exception nested) {
        super(resource, getSpecificMessage(message, nested));
        this.exception = nested;
    }
    
    public int[] getLines() {
        return this.lines;
    }


    private static String getSpecificMessage(String message, Exception exception) {
        if (exception != null) {
            return message + " : Exception " + exception.getClass() + " : "+ exception.getMessage();
        } else {
            return message;
        }
    }

}
