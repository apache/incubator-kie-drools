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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class JaxbMapAdapter extends XmlAdapter<JaxbStringObjectPair[], Map<String,? extends Object>> {

    protected static Logger logger = LoggerFactory.getLogger(JaxbMapAdapter.class);

    @Override
    public JaxbStringObjectPair[] marshal(Map<String, ? extends Object> map) throws Exception {
        try {
            if (map == null || map.isEmpty()) {
                return new JaxbStringObjectPair[0];
            }

            List<JaxbStringObjectPair> ret = new ArrayList<JaxbStringObjectPair>(map.size());
            for (Map.Entry<String, ? extends Object> entry : map.entrySet()) {
                Object obj = entry.getValue();
                // There's already a @XmlJavaTypeAdapter(JaxbUnknownAdapter.class) anno on the JaxbStringObjectPair.value field
                ret.add(new JaxbStringObjectPair(entry.getKey(), obj));
            }

            return ret.toArray(new JaxbStringObjectPair[map.size()]);
        } catch( Exception e ) {
            // because exceptions are always swallowed by JAXB
            logger.error("Unable to marshall " + map.getClass().getName() + " instance: " + e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public Map<String, Object> unmarshal(JaxbStringObjectPair[] value) throws Exception {
        try {
            Map<String, Object> r = new LinkedHashMap<String, Object>();
            for( JaxbStringObjectPair p : value ) {
                r.put(p.getKey(), p.getValue());
            }
            return r;
        } catch( Exception e ) {
            // because exceptions are always swallowed by JAXB
            logger.error("Unable to *un*marshal " + value.getClass().getName() + " instance: " + e.getMessage(), e);
            throw e;
        }
    }

}
