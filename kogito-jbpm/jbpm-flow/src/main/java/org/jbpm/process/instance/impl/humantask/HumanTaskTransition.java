/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.jbpm.process.instance.impl.humantask;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.kie.kogito.MapOutput;
import org.kie.kogito.auth.IdentityProvider;
import org.kie.kogito.auth.SecurityPolicy;
import org.kie.kogito.process.workitem.Policy;
import org.kie.kogito.process.workitem.Transition;

/**
 * Human task dedicated transition what uses <code>Map</code> of objects to be
 * associated with work item - human task work item.
 *
 */
public class HumanTaskTransition implements Transition<Map<String, Object>> {

    private String phase;
    private Map<String, Object> data;
    private List<Policy<?>> policies = new ArrayList<>();
    
    public static HumanTaskTransition withModel(String phase, MapOutput data, Policy<?>... policies) {
        return new HumanTaskTransition(phase, data.toMap(), policies);
    }

    public static HumanTaskTransition withoutModel(String phase, Policy<?>... policies) {
        return new HumanTaskTransition(phase, Collections.emptyMap(), policies);
    }

    public HumanTaskTransition(String phase) {
        this(phase, Collections.emptyMap());
    }
    
    public HumanTaskTransition(String phase, Map<String, Object> data, IdentityProvider identity) {
        this(phase, data, SecurityPolicy.of(identity));
    }
    
    public HumanTaskTransition(String phase, Map<String, Object> data, Policy<?>... policies) {
        this.phase = phase;
        this.data = data;
        for (Policy<?> policy : policies) {
            this.policies.add(policy);
        }
    }

    @Override
    public String phase() {
        return phase;
    }

    @Override
    public Map<String, Object> data() {
        return data;
    }

    @Override
    public List<Policy<?>> policies() {
        return policies;
    }
}
