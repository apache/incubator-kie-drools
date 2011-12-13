/*
 * Copyright 2011 JBoss Inc..
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

package org.drools.command.runtime;

import org.drools.builder.KnowledgeBuilderConfiguration;
import org.drools.command.Context;
import org.drools.command.impl.GenericCommand;

/**
 *
 * @author salaboy
 */
public class KBuilderSetPropertyCommand implements GenericCommand<Void>{
    private String kbuilderConfId;
    private String name;
    private String value;

    public KBuilderSetPropertyCommand(String kbuilderConfId, String name, String value) {
        this.kbuilderConfId = kbuilderConfId;
        this.name = name;
        this.value = value;
    }
    
    
    
    public Void execute(Context context) {
       KnowledgeBuilderConfiguration kconf = (KnowledgeBuilderConfiguration) context.get(kbuilderConfId);
       kconf.setProperty(this.name, this.value);
       return null;
    }

}
