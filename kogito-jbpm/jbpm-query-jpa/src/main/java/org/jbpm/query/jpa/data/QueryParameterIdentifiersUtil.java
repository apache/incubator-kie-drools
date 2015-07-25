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
