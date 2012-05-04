/*
 * Copyright 2012 JBoss by Red Hat.
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
package org.jbpm.task.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.drools.marshalling.ObjectMarshallingStrategy;


public class ContentMarshallerContext {
    
    private boolean useMarshal = false;

    private List<ObjectMarshallingStrategy> strategies = new ArrayList<ObjectMarshallingStrategy>();
    
    public final Map<Class, ObjectMarshallingStrategy.Context> strategyContext;

    public ContentMarshallerContext() {
        strategyContext = new HashMap<Class, ObjectMarshallingStrategy.Context>();
        
    }
    
    public ContentMarshallerContext(boolean useMarshal) {
        strategyContext = new HashMap<Class, ObjectMarshallingStrategy.Context>();
        this.setUseMarshal(useMarshal);
    }

    public void setUseMarshal(boolean useMarshal) {
        this.useMarshal = useMarshal;
    }

    public boolean isUseMarshal() {
        return useMarshal;
    }

    public void addStrategy(ObjectMarshallingStrategy strategy) {
        this.strategies.add(strategy);
    }
  
    public void setStrategies(List<ObjectMarshallingStrategy> strategies) {
        this.strategies = strategies;
    }

    public List<ObjectMarshallingStrategy> getStrategies() {
        return strategies;
    }
}
