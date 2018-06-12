/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jbpm.runtime.manager.impl;

import java.util.Iterator;

import org.kie.api.task.model.Group;
import org.kie.api.task.model.OrganizationalEntity;
import org.kie.internal.task.api.UserInfo;

public class CustomUserInfoImpl implements UserInfo {

    @Override
    public String getDisplayName(OrganizationalEntity entity) {
        return null;
    }

    @Override
    public Iterator<OrganizationalEntity> getMembersForGroup(Group group) {
        return null;
    }

    @Override
    public boolean hasEmail(Group group) {
        return false;
    }

    @Override
    public String getEmailForEntity(OrganizationalEntity entity) {
        return null;
    }

    @Override
    public String getLanguageForEntity(OrganizationalEntity entity) {
        return null;
    }

    @Override
    public String getEntityForEmail(String email) {
        return null;
    }

}