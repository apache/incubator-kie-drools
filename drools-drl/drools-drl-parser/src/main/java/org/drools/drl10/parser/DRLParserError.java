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
package org.drools.drl10.parser;

/**
 * Error information while parsing DRL
 */
public class DRLParserError {

    private int lineNumber;
    private int column;
    private String message;

    private Exception exception;

    public DRLParserError(int lineNumber, int column, String message) {
        this.lineNumber = lineNumber;
        this.column = column;
        this.message = message;
    }

    public DRLParserError(Exception exception) {
        this.exception = exception;
    }

    public int getLineNumber() {
        return lineNumber;
    }

    public void setLineNumber(int lineNumber) {
        this.lineNumber = lineNumber;
    }

    public int getColumn() {
        return column;
    }

    public void setColumn(int column) {
        this.column = column;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "DRLParserError{" +
                "lineNumber=" + lineNumber +
                ", column=" + column +
                ", message='" + message + '\'' +
                ", exception=" + exception +
                '}';
    }
}
