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

package org.drools.core.command.runtime.process;

import org.drools.core.command.impl.ExecutableCommand;
import org.drools.core.command.impl.RegistryContext;
import org.drools.core.xml.jaxb.util.JaxbMapAdapter;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.Context;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.HashMap;
import java.util.Map;

@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
public class CompleteWorkItemCommand implements ExecutableCommand<Void> {

    @XmlAttribute(name="id", required = true)
    private Long workItemId;
    
    @XmlJavaTypeAdapter(JaxbMapAdapter.class)
    @XmlElement(name="result")
    private Map<String, Object> results = new HashMap<String, Object>();

    public CompleteWorkItemCommand() {}

    public CompleteWorkItemCommand(long workItemId) {
        this.workItemId = workItemId;
    }

    public CompleteWorkItemCommand(long workItemId, Map<String, Object> results) {
        this(workItemId);
        this.results = results;
    }

    public long getWorkItemId() {
        return workItemId;
    }

    public void setWorkItemId(long workItemId) {
        this.workItemId = workItemId;
    }

    public Map<String, Object> getResults() {
        return results;
    }

    public void setResults(Map<String, Object> results) {
        this.results = results;
    }

    public Void execute(Context context) {
        KieSession ksession = ((RegistryContext) context).lookup( KieSession.class );
        ksession.getWorkItemManager().completeWorkItem(workItemId, results);
        return null;
    }

    public String toString() {
        String result = "session.getWorkItemManager().completeWorkItem(" + workItemId + ", [";
        if (results != null) {
            int i = 0;
            for (Map.Entry<String, Object> entry: results.entrySet()) {
                if (i++ > 0) {
                    result += ", ";
                }
                result += entry.getKey() + "=" + entry.getValue();
            }
        }
        result += "]);";
        return result;
    }

}
