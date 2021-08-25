/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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

package org.kie.dmn.feel.lang.types.impl;

import java.util.Map;

import org.kie.dmn.api.core.FEELPropertyAccessible;

public class ImmutableFPAWrappingPOJO implements FEELPropertyAccessible {

    public ImmutableFPAWrappingPOJO(Object wrapping) {
        throw new UnsupportedOperationException("reflection FPA not required and not supported on GWT platform");
    }

    @Override
    public AbstractPropertyValueResult getFEELProperty(String property) {
        throw new UnsupportedOperationException("reflection FPA not required and not supported on GWT platform");
    }

    @Override
    public void setFEELProperty(String key, Object value) {
        throw new UnsupportedOperationException("reflection FPA not required and not supported on GWT platform");
    }

    @Override
    public Map<String, Object> allFEELProperties() {
        throw new UnsupportedOperationException("reflection FPA not required and not supported on GWT platform");
    }

    @Override
    public void fromMap(Map<String, Object> values) {
        throw new UnsupportedOperationException("reflection FPA not required and not supported on GWT platform");
    }
}
