/*
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
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

package org.kie.internal.conf;

import org.kie.api.conf.SingleValueKieBaseOption;

/**
 * An Enum for indexRightBetaMemory option.
 *
 * drools.indexRightBetaMemory = &lt;true|false&gt;
 *
 * DEFAULT = true
 */
public enum IndexRightBetaMemoryOption implements SingleValueKieBaseOption {

    YES(true),
    NO(false);

    /**
     * The property name for the share beta nodes option
     */
    public static final String PROPERTY_NAME = "drools.indexRightBetaMemory";

    private boolean value;

    IndexRightBetaMemoryOption( final boolean value ) {
        this.value = value;
    }

    /**
     * {@inheritDoc}
     */
    public String getPropertyName() {
        return PROPERTY_NAME;
    }

    public boolean isIndexRightBetaMemory() {
        return this.value;
    }

}
