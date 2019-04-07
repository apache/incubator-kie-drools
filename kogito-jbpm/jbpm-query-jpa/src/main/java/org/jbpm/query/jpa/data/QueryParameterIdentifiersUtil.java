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

package org.jbpm.query.jpa.data;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.TreeMap;

import org.kie.internal.query.QueryParameterIdentifiers;

public class QueryParameterIdentifiersUtil {

    public static Map<Integer, String> getQueryParameterIdNameMap() { 
        Field [] fields = QueryParameterIdentifiers.class.getDeclaredFields();
        Map<Integer, String> idMap = new TreeMap<Integer, String>();
        for( Field field : fields ) { 
            Object objVal;
            try { 
                objVal = field.get(null);
            } catch( Exception e ) { 
                throw new IllegalStateException("Unable to get static value from field " 
                        + QueryParameterIdentifiers.class.getName() + "." + field.getName());
            }
            if( ! (objVal instanceof String) ) { 
               continue; 
            }
            String val = (String) objVal;
            Integer idVal;
            try { 
               idVal = Integer.valueOf(val);
            } catch( Exception e ) { 
                continue;
            }
           idMap.put(idVal, field.getName());
        }
        return idMap;
    }
}
