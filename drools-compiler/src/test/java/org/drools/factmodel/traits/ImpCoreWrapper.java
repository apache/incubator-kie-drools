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

import java.util.HashMap;
import java.util.Map;

public class ImpCoreWrapper extends Imp implements CoreWrapper<Imp> {

    private Imp core;
    private Map<String,Object> __$$dynamic_properties_map$$ = new HashMap<String, Object>();
    private Map<String,Class> __$$dynamic_traits_set$$ = new HashMap<String, Class>();

    public Map<String, Object> getDynamicProperties() {
        return __$$dynamic_properties_map$$;
    }

    public Map getTraits() {
        return __$$dynamic_traits_set$$;
    }


    public void init(Imp core) {
        this.core = core;
    }





    public String getSchool() {
        return core.getSchool();
    }

    public void setSchool( String school ) {
        core.setSchool( school );
    }

    public String getName() {
        return core.getName();
    }

    public void setName( String name ) {
        core.setSchool( name );
    }





    public double testMethod( String arg1, int arg2, Object arg3, double arg4 ) {
        return core.testMethod(arg1,arg2, arg3, arg4);
    }


    public boolean equals( Object other ) {
        return core.equals( other );
    }

    public String toString() {
        return core.toString();
    }

    public int hashCode() {
        return core.hashCode();
    }


}
