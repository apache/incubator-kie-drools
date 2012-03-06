/*
 * Copyright 2012 JBoss Inc
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

package org.drools;

public class Father {
    
    private String name;
    private Father father;

    public Father(String name) {
        this.name = name;
    }

    public Father getFather() {
        return father;
    }

    public void setFather(Father father) {
        this.father = father;
    }

    public Father getPatriarch() {
        Father elder = father;
        while (elder != null) {
            if (elder == this) {
                throw new IllegalStateException("Impossible state"); // fail fast during infinite loop
            }
            elder = elder.getFather();
        }
        return elder;
    }

    @Override
    public String toString() {
        return name;
    }

}
