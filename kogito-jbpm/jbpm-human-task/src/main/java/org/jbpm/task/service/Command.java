/**
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

package org.jbpm.task.service;

import java.io.Serializable;
import java.util.List;

public class Command implements Serializable {
    
    private int id;
    
    private CommandName name;    
    
    private List<Object> arguments;
    
    public Command() {
	}
    
    public Command(int id, CommandName name, List<Object> arguments) {
        super();
        this.id = id;
        this.arguments = arguments;
        this.name = name;
    }
    
    public int getId() {
        return id;
    }



    public void setId(int id) {
        this.id = id;
    }



    public CommandName getName() {
        return name;
    }
    public void setName(CommandName name) {
        this.name = name;
    }
    public List<Object> getArguments() {
        return arguments;
    }
    public void setArguments(List<Object> arguments) {
        this.arguments = arguments;
    }

}
