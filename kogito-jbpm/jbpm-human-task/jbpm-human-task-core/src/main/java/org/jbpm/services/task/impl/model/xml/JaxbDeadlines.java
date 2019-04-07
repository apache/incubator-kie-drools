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

package org.jbpm.services.task.impl.model.xml;

import static org.jbpm.services.task.impl.model.xml.AbstractJaxbTaskObject.unsupported;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Collections;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import org.kie.internal.task.api.model.Deadline;
import org.kie.internal.task.api.model.Deadlines;

/**
 * This is a "dummy" object which does *NOT* contain any information
 */
@XmlType(name="deadlines")
@XmlAccessorType(XmlAccessType.FIELD)
public class JaxbDeadlines implements Deadlines {

    public JaxbDeadlines() { 
       // no-arg constructor for JAXB 
    }
    
    @Override
    public List<Deadline> getStartDeadlines() {
        return Collections.emptyList();
    }

    @Override
    public void setStartDeadlines(List<Deadline> startDeadlines) {
        // no-op
    }

    @Override
    public List<Deadline> getEndDeadlines() {
        return Collections.emptyList();
    }

    @Override
    public void setEndDeadlines(List<Deadline> endDeadlines) {
        // no-op
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        unsupported(Deadlines.class);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        unsupported(Deadlines.class);
    }

}

