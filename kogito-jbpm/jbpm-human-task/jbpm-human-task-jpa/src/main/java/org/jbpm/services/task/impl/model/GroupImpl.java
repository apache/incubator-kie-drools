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

package org.jbpm.services.task.impl.model;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import org.kie.api.task.model.Group;

@Entity
@DiscriminatorValue("Group")
public class GroupImpl extends OrganizationalEntityImpl implements Group {

    public GroupImpl() {
        super();
    }

    public GroupImpl(String id) {
        super( id );
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        super.writeExternal( out );

    }

    public void readExternal(ObjectInput in) throws IOException,
                                            ClassNotFoundException {
        super.readExternal( in );
    }

}
