/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jbpm.process.core.validation;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

import org.jbpm.ruleflow.core.RuleFlowProcess;
import org.jbpm.ruleflow.core.validation.RuleFlowProcessValidator;
import org.kie.api.definition.process.Process;
import org.kie.api.io.Resource;

public class ProcessValidatorRegistry {

    private static ProcessValidatorRegistry instance;
    
    private Map<String, ProcessValidator> defaultValidators = new ConcurrentHashMap<String, ProcessValidator>();    
    private Set<ProcessValidator> additionalValidators = new CopyOnWriteArraySet<ProcessValidator>();
    
    private ProcessValidatorRegistry() {
        defaultValidators.put(RuleFlowProcess.RULEFLOW_TYPE, RuleFlowProcessValidator.getInstance());
    }
    
    public static ProcessValidatorRegistry getInstance() {
        if (instance == null) {
            instance = new ProcessValidatorRegistry();
        }
        
        return instance;
    }
    
    public void registerAdditonalValidator(ProcessValidator validator) {
        this.additionalValidators.add(validator);
    }
    
    public ProcessValidator getValidator(Process process, Resource resource) {
        if (!additionalValidators.isEmpty()) {
            for (ProcessValidator validator : additionalValidators) {
                boolean accepted = validator.accept(process, resource);
                if (accepted) {
                    return validator;
                }
            }
        }
        
        return defaultValidators.get(process.getType());
    }
}
