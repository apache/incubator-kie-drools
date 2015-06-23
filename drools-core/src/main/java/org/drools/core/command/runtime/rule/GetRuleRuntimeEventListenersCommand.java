/*
 * Copyright 2015 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.drools.core.command.runtime.rule;

import org.drools.core.command.impl.GenericCommand;
import org.drools.core.command.impl.KnowledgeCommandContext;
import org.kie.api.event.rule.RuleRuntimeEventListener;
import org.kie.api.runtime.KieSession;
import org.kie.internal.command.Context;

import java.util.Collection;

public class GetRuleRuntimeEventListenersCommand
    implements
    GenericCommand<Collection<RuleRuntimeEventListener>> {

    public Collection<RuleRuntimeEventListener> execute(Context context) {
        KieSession ksession = ((KnowledgeCommandContext) context).getKieSession();
        return ksession.getRuleRuntimeEventListeners();
    }

    public String toString() {
        return "session.getRuleRuntimeEventListeners();";
    }
}
