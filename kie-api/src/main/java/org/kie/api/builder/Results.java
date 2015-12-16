/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
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

package org.kie.api.builder;

import org.kie.api.builder.Message.Level;

import java.util.List;

/**
 * The Results of the building process of a KieModule
 */
public interface Results {

    /**
     * Returns true if these Results contains at least one Message of one of the given levels
     */
    boolean hasMessages(Level... levels);

    /**
     * Returns all the Messages of the given levels in these Results
     */
    List<Message>  getMessages(Level... levels);

    /**
     * Returns all the Messages in these Results
     */
    List<Message>  getMessages();  
}
