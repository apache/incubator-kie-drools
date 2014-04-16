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

package org.drools.compiler.lang;

public class ParseException extends RuntimeException {

    private static final long serialVersionUID = 510l;

    private int               lineNumber;

    private Throwable         cause;

    /**
     * Thrown if there is an exception related to parsing a line in a drl file.
     * For more generic exception, a different exception class will be used.
     */
    public ParseException(final String message,
                          final int lineNumber) {
        super( message );
        this.lineNumber = lineNumber;
    }

    /**
     * Allows nesting of misc exceptions, yet preserving the line number
     * that triggered the error.
     */
    public ParseException(final String message,
                          final int lineNumber,
                          final Throwable cause) {
        super( message );
        this.lineNumber = lineNumber;
        this.cause = cause;
    }

    /**
     * The line number on which the error occurred.
     */
    public int getLineNumber() {
        return this.lineNumber;
    }

    /**
     * This will print out a summary, including the line number. 
     * It will also print out the cause message if applicable.
     */
    public String getMessage() {
        if ( this.cause == null ) {
            return super.getMessage() + " Line number: " + this.lineNumber;
        } else {
            return super.getMessage() + " Line number: " + this.lineNumber + ". Caused by: " + this.cause.getMessage();
        }
    }

    public String toString() {
        return getMessage();
    }

    public Throwable getCause() {
        return this.cause;
    }

}
