/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.dmn.api.feel.runtime.events;

/**
 * An interface for FEEL related events
 */
public interface FEELEvent {

    enum Severity {
        TRACE, INFO, WARN, ERROR;
    }

    /**
     * Returns the severity of the event
     *
     * @return
     */
    Severity getSeverity();

    /**
     * Returns a human readable message about the event
     *
     * @return
     */
    String getMessage();

    /**
     * In case the event relates to an exception, returns
     * the caught Throwable
     *
     * @return
     */
    Throwable getSourceException();

    /**
     * In case the event refers to the source code, returns
     * the line in the source code where the event was generated
     * or -1 if it does not refer to a source code line.
     *
     * The line is 1-based. I.e., the first line is 1,
     * second line is 2, etc.
     *
     * @return
     */
    int getLine();

    /**
     * In case the event refers to the source code, returns
     * the character in the line of the the source code where
     * the event was generated or -1 if it does not refer to a
     * source code character.
     *
     * The column is 0-based. I.e. the first character in the
     * line is 0, the second is 1, and so on.
     *
     * @return
     */
    int getColumn();

    /**
     * In case the event refers to a symbol in the source code,
     * this method returns the offending symbol, as an ANTLR
     * CommonToken instance. Otherwise, it returns null.
     *
     * @return
     */
    Object getOffendingSymbol();
}
