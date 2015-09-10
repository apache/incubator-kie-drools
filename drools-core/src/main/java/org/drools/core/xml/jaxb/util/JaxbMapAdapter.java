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

package org.drools.core.xml.jaxb.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import org.drools.core.QueryResultsImpl;
import org.drools.core.runtime.rule.impl.FlatQueryResults;


public class JaxbMapAdapter extends XmlAdapter<JaxbStringObjectPair[], Map<String,Object>> {

    private static final JaxbUnknownAdapter unknownAdapter = new JaxbUnknownAdapter();
    
    @Override
    public JaxbStringObjectPair[] marshal(Map<String, Object> value) throws Exception {
        if (value == null || value.isEmpty()) {
            return new JaxbStringObjectPair[0];
        }

        List<JaxbStringObjectPair> ret = new ArrayList<JaxbStringObjectPair>(value.size());
        for (Map.Entry<String, Object> entry : value.entrySet()) {
            Object obj = entry.getValue();
            Class<? extends Object> vClass = obj.getClass();

            if ( obj instanceof QueryResultsImpl) {
                obj = new FlatQueryResults( (QueryResultsImpl)obj );
            } else if (!JaxbListWrapper.class.equals(vClass)) {
                obj = unknownAdapter.marshal(obj);
            }
            ret.add(new JaxbStringObjectPair(entry.getKey(), obj));
        }

        return ret.toArray(new JaxbStringObjectPair[value.size()]);
    }

    @Override
    public Map<String, Object> unmarshal(JaxbStringObjectPair[] value) throws Exception {
        Map<String, Object> r = new LinkedHashMap<String, Object>();
        for( JaxbStringObjectPair p : value ) {
            if ( p.getValue() instanceof JaxbListWrapper) {
                r.put(p.getKey(), unknownAdapter.unmarshal(p.getValue()));
            } else {
                r.put(p.getKey(), p.getValue());
            }
        }
        return r;
    }

}
