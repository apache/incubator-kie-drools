/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.commons.jci.compilers;

import org.codehaus.janino.Location;
import org.codehaus.janino.util.LocatedException;
import org.drools.commons.jci.problems.CompilationProblem;

/**
 * Janino version of a CompilationProblem
 * 
 */
public final class JaninoCompilationProblem implements CompilationProblem {

    private final Location location;
    private final String fileName;
    private final String message;
    private final boolean error;

    public JaninoCompilationProblem(final LocatedException pLocatedException) {
        this(pLocatedException.getLocation(), pLocatedException.getMessage(), true);
    }

    public JaninoCompilationProblem(final Location pLocation, final String pMessage, final boolean pError) {
      this(pLocation.getFileName(), pLocation, pMessage, pError);
    }

    public JaninoCompilationProblem(final String pFilename, final String pMessage, final boolean pError) {
        this(pFilename, null, pMessage, pError);
    }

    public JaninoCompilationProblem(final String pFilename, final Location pLocation, final String pMessage, final boolean pError) {
        location = pLocation;
        fileName = pFilename;
        message = pMessage;
        error = pError;
    }

    public boolean isError() {
        return error;
    }

    public String getFileName() {
        return fileName;
    }

    public int getStartLine() {
        if (location == null) {
            return 0;
        }
        return location.getLineNumber();
    }

    public int getStartColumn() {
        if (location == null) {
            return 0;
        }
        return location.getColumnNumber();
    }

    public int getEndLine() {
        return getStartLine();
    }

    public int getEndColumn() {
        return getStartColumn();
    }

    public String getMessage() {
        return message;
    }

    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append(getFileName()).append(" (");
        sb.append(getStartLine());
        sb.append(":");
        sb.append(getStartColumn());
        sb.append(") : ");
        sb.append(getMessage());
        return sb.toString();
    }

}
