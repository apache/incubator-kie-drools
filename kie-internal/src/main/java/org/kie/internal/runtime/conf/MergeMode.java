/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
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

package org.kie.internal.runtime.conf;

import javax.xml.bind.annotation.XmlType;

/**
 * Defines merging strategy of two descriptors
 */
@XmlType
public enum MergeMode {
    /**
     * The 'master' descriptor values are all kept
     */
    KEEP_ALL,

    /**
     * The 'slave' descriptor values are all used
     */
    OVERRIDE_ALL,

    /**
     * The 'slave' non-empty values override corresponding values of the master, including collections
     */
    OVERRIDE_EMPTY,

    /**
     * The same as OVERRIDE_EMPTY except that collections are merged instead of being overridden
     */
    MERGE_COLLECTIONS;
}