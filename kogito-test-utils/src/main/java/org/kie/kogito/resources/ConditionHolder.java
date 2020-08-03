/**
 *  Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.kie.kogito.resources;

import java.util.Optional;

import org.apache.commons.lang3.StringUtils;

/**
 * Condition holder to keep the constraints so a condition resource needs to be started or not.
 */
public class ConditionHolder {

    protected static final String TEST_CATEGORY_PROPERTY = "enable.resource.%s";

    private final String resourceName;
    private boolean enabled = true;

    public ConditionHolder(String resourceName) {
        this.resourceName = resourceName;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void enableConditional() {
        enableIfSystemPropertyIs(String.format(TEST_CATEGORY_PROPERTY, resourceName), Boolean.TRUE.toString());
    }

    private void enableIfSystemPropertyIs(String name, String value) {
        this.enabled = Optional.ofNullable(System.getProperty(name)).map(property -> StringUtils.equalsIgnoreCase(property, value)).orElse(false);
    }
}
