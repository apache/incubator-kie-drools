/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.drools.core.util;

import org.drools.core.impl.KnowledgeBaseImpl;
import org.kie.api.definition.KiePackage;
import org.kie.api.definition.rule.Rule;
import org.kie.internal.KnowledgeBase;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DroolsTestUtil {
    public static Map<String, Rule> rulestoMap( Collection<Rule> rules ) {
        Map<String, Rule> ret = new HashMap<String, Rule>();
        for ( Rule rule : rules ) {
            ret.put( rule.getName(), rule );
        }
        return ret;
    }

    public static Map<String, Rule> rulestoMap( KnowledgeBase kbase ) {
        List<Rule> rules = new ArrayList();
        for ( KiePackage pkg : ((KnowledgeBaseImpl)kbase).getPackages() ) {
            for ( Rule rule : pkg.getRules() ) {
                rules.add(rule);
            }
        }

        return rulestoMap( rules );
    }

}
