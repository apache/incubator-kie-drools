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
package org.kie.dmn.api.core;

import java.util.List;

/**
 * An interface for message container objects like
 * DMNResults and DMNModel
 */
public interface DMNMessageContainer {
    /**
     * Returns a list of all the messages produced
     * during the DMN service invocation.
     *
     * @return list of messages
     */
    List<DMNMessage> getMessages();

    /**
     * Returns a list of all the messages produced
     * during the DMN service invocation, filtered
     * by the list of severities given.
     *
     * @param sevs the list of severities to filter
     *             the messages by
     *
     * @return filtered list of messages
     */
    List<DMNMessage> getMessages(DMNMessage.Severity... sevs);

    /**
     * A helper method to quick check for the presence
     * of error messages. The actual error messages can
     * be retrieved by invoking <code>#getMessages()</code>
     *
     * @return true if there are any error messages,
     *         false otherwise.
     */
    boolean hasErrors();
}
