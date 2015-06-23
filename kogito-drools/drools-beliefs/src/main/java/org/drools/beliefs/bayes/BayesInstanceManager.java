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

package org.drools.beliefs.bayes;

import org.kie.api.KieBase;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class BayesInstanceManager {
    private KieBase kBase;

    private ConcurrentHashMap<String, BayesInstance> instances;

    public BayesInstanceManager() {
        instances  = new ConcurrentHashMap<String, BayesInstance>();
    }

    public Map<String, BayesInstance> getInstances() {
        return instances;
    }

    public BayesInstance getBayesInstance( String pkgName, String name ) {

//        instances.

        return null;
    }


}
