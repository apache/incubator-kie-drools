/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

package org.kie.internal.runtime.beliefs;

import java.util.HashMap;
import java.util.Map;

public class KieBeliefsImpl implements KieBeliefs{
    private KieBeliefService[] services;

    private Map<String, KieBeliefService> beliefs;

    public KieBeliefsImpl() {
        beliefs = new HashMap<String, KieBeliefService>();
    }

    public Map<String, KieBeliefService> getBeliefs() {
        return this.beliefs;
    }

    public Class getServiceInterface() {
        return KieBeliefService.class;
    }

    public KieBeliefService[] getServices() {
        if ( services == null ) {
            synchronized ( beliefs )  {
                if ( services != null ) {
                    return services;
                }
                int size = beliefs.size();
                services = new KieBeliefService[ size ];
                int i = 0;
                for ( KieBeliefService service : beliefs.values() ) {
                    services[i++] = service;
                }
            }
        }
        return services;
    }
}
