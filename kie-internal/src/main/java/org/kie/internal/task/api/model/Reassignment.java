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

package org.kie.internal.task.api.model;

import java.io.Externalizable;
import java.util.List;

import org.kie.api.task.model.I18NText;
import org.kie.api.task.model.OrganizationalEntity;

public interface Reassignment extends Externalizable {

    Long getId();

    void setId(long id);

    List<I18NText> getDocumentation();

    void setDocumentation(List<I18NText> documentation);

    List<OrganizationalEntity> getPotentialOwners();

    void setPotentialOwners(List<OrganizationalEntity> potentialOwners);

}
