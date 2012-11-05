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
package org.droolsjbpm.services.impl.model;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 *
 * @author salaboy
 */
@Entity
public class ProcessInputDesc implements Serializable {
    @Id
    @GeneratedValue()
    private long pk;
   
    private String processName;
    
    private String input;

    public ProcessInputDesc() {
    }

    public ProcessInputDesc(String processName, String input) {
        this.processName = processName;
        this.input = input;
    }

    public ProcessInputDesc(String input) {
        this.input = input;
    }
    
    


    public String getProcessName() {
        return processName;
    }

    public String getInput() {
        return input;
    }

    @Override
    public String toString() {
        return "ProcessInputDesc{" + "pk=" + pk + ", processName=" + processName + ", input=" + input + '}';
    }

    
    
    
    
}
