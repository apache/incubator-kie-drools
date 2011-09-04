/*
 * Copyright 2011 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.factmodel.traits;

import org.drools.factmodel.traits.IThing;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Imp2 {

    private String name;
    private String school;


    private Map<String,Object> __$$dynamic_properties_map$$ = new HashMap<String,Object>();

    public Map<String,Object> getDynamicProperties() {
        return __$$dynamic_properties_map$$;
    }


    private Map<String, ? extends IThing> traits = new HashMap<String, IThing>();

    public Map<String, ? extends IThing> getTraits() {
        return traits;
    }


    private Set field;
    private Map field2;

    public Imp2() {
        field = new HashSet();
        field2 = new HashMap();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSchool() {
        return school;
    }

    public void setSchool(String school) {
        this.school = school;
    }
}
