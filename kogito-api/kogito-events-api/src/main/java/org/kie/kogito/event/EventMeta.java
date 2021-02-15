/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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
package org.kie.kogito.event;

/**
 * Interface for Event metadata information
 */
public interface EventMeta {

    /**
     * Returns specification version of the cloud event
     *
     * @return specification version
     */
    String getSpecVersion();

    /**
     * Returns type of the event this instance represents e.g. ProcessInstanceEvent
     *
     * @return type of the event
     */
    String getType();

    /**
     * Returns source of the event that is in URI syntax
     *
     * @return uri source
     */
    String getSource();
}
