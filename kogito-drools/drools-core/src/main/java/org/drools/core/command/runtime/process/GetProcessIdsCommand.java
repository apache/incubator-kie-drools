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

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.drools.core.command.impl.GenericCommand;
import org.drools.core.command.impl.KnowledgeCommandContext;
import org.kie.api.definition.process.Process;
import org.kie.internal.command.Context;

@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
public class GetProcessIdsCommand
    implements
    GenericCommand<List<String>> {

    public List<String> execute(Context context) {
    	List<String> result = new ArrayList<String>();
        for (Process p: ((KnowledgeCommandContext) context).getKieSession().getKieBase().getProcesses()) {
        	result.add(p.getId());
        }
        return result;
    }

    public String toString() {
        return "session.getKieBase().getProcesses();";
    }

}
