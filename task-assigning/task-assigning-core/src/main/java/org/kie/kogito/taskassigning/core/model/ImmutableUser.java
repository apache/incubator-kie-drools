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
package org.kie.kogito.taskassigning.core.model;

import java.util.Map;
import java.util.Set;

public class ImmutableUser extends User {

    public ImmutableUser() {
        // required for marshaling and FieldAccessingSolutionCloner purposes.
    }

    public ImmutableUser(String id, boolean enabled, Set<Group> groups, Map<String, Object> attributes) {
        super(id, enabled, Set.copyOf(groups), Map.copyOf(attributes));
    }

    @Override
    public void setId(String id) {
        throwImmutableException();
    }

    @Override
    public void setEnabled(boolean enabled) {
        throwImmutableException();
    }

    @Override
    public void setGroups(Set<Group> groups) {
        throwImmutableException();
    }

    @Override
    public void setAttributes(Map<String, Object> attributes) {
        throwImmutableException();
    }

    private void throwImmutableException() {
        throw new UnsupportedOperationException("ImmutableUser: " + this.getId() + " object can not be modified.");
    }
}
