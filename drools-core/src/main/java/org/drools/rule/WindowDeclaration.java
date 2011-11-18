/*
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

package org.drools.rule;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import org.drools.definition.KnowledgeDefinition;

/**
 * The window declaration defines a window that can be used by patterns
 * in rules
 */
public class WindowDeclaration extends BaseAnnotatedAsset
    implements
    KnowledgeDefinition,
    Externalizable {
    
    private String               name;
    private Pattern              pattern;

    public WindowDeclaration() {
    }

    public WindowDeclaration(String name) {
        this.name = name;
    }

    public void readExternal(ObjectInput in) throws IOException,
                                            ClassNotFoundException {
        this.name = (String) in.readObject();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject( name );
    }
    
    public String getName() {
        return name;
    }
    
    public void setName( String name ) {
        this.name = name;
    }

    public Pattern getPattern() {
        return pattern;
    }

    public void setPattern( Pattern pattern ) {
        this.pattern = pattern;
    }

}
