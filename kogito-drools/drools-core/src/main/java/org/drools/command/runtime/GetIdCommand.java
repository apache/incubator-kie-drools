/*
 * Copyright 2010 JBoss Inc
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

package org.drools.command.runtime;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

import org.drools.command.Context;
import org.drools.command.impl.GenericCommand;
import org.drools.command.impl.KnowledgeCommandContext;
import org.drools.impl.StatefulKnowledgeSessionImpl;
import org.drools.runtime.StatefulKnowledgeSession;

@XmlAccessorType(XmlAccessType.NONE)
public class GetIdCommand
    implements
    GenericCommand<Integer> {

    private static final long serialVersionUID = 510l;

    
    public GetIdCommand() {
    }

    public Integer execute(Context context) {
        StatefulKnowledgeSession ksession = ((KnowledgeCommandContext) context).getStatefulKnowledgesession();
        return ((StatefulKnowledgeSessionImpl)ksession).getId();
    }

    public String toString() {
        return "session.getId( );";
    }
}
