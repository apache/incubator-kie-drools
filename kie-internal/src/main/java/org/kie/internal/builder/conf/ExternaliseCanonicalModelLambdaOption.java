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
package org.kie.internal.builder.conf;

import org.kie.api.conf.OptionKey;

/**
 * An Enum for ExternaliseCanonicalModelLambda option.
 *
 * drools.externaliseCanonicalModelLambda = &lt;true|false&gt;
 *
 * DEFAULT = false
 */
public enum ExternaliseCanonicalModelLambdaOption implements SingleValueRuleBuilderOption {

    ENABLED(true),
    DISABLED(false);

    /**
     * The property name for the sequential mode option
     */
    public static final String PROPERTY_NAME = "drools.externaliseCanonicalModelLambda";

    public static OptionKey<ExternaliseCanonicalModelLambdaOption> KEY = new OptionKey<>(TYPE, PROPERTY_NAME);

    private boolean value;

    ExternaliseCanonicalModelLambdaOption(final boolean value ) {
        this.value = value;
    }

    /**
     * {@inheritDoc}
     */
    public String getPropertyName() {
        return PROPERTY_NAME;
    }

    public boolean isCanonicalModelLambdaExternalized() {
        return this.value;
    }

}
