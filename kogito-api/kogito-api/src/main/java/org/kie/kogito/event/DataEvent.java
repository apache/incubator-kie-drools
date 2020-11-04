/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.kogito.event;

/**
 * Represents top level data event structure that can be emitted
 * from within running process, decision or rule.
 * <p>
 * It's main aim is to be transferred over the wire but the event
 * itself is not meant to do transformation to be "wire-friendly"
 * <p>
 * Main point of the event is to be compatible with cloud events
 * specification and thus comes with main fields that the spec defines.
 * <p>
 * Classes implementing can provide more information to be considered extensions
 * of the event - see cloud event extension elements.
 *
 * @param <T> type of the body of the event
 */
public interface DataEvent<T> extends EventMeta {

    /**
     * Returns unique id of the event
     *
     * @return unique event id
     */
    String getId();

    /**
     * Returns returns time when the event was created
     *
     * @return time of the event
     */
    String getTime();

    /**
     * The Content type of data value. This attribute enables data to carry any type of content, whereby format and encoding might differ from that of the chosen event format.
     *
     * @return Content type of data value
     */
    String getDataContentType();

    /**
     * Identifies the schema that data adheres to
     *
     * @return the schema that data adheres to
     */
    String getDataSchema();

    /**
     * The subject of the event in the context of the event producer (identified by source)
     *
     * @return The subject of the event
     */
    String getSubject();

    /**
     * Returns actual body of the event
     *
     * @return
     */
    T getData();
}
