/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.drools.core.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.drools.core.impl.InternalRuleBase;
import org.kie.api.KieBase;
import org.kie.api.definition.KiePackage;
import org.kie.api.definition.rule.Rule;

public class DroolsTestUtil {
    public static Map<String, Rule> rulestoMap( Collection<Rule> rules ) {
        Map<String, Rule> ret = new HashMap<>();
        for ( Rule rule : rules ) {
            ret.put( rule.getName(), rule );
        }
        return ret;
    }

    public static Map<String, Rule> rulestoMap( KieBase kbase ) {
        List<Rule> rules = new ArrayList();
        for ( KiePackage pkg : ((InternalRuleBase)kbase).getPackages() ) {
            rules.addAll(pkg.getRules());
        }

        return rulestoMap( rules );
    }

}
