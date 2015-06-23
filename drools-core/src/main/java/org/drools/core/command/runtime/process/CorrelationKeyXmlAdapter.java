/*
 * Copyright 2015 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.drools.core.command.runtime.process;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import org.kie.internal.KieInternalServices;
import org.kie.internal.process.CorrelationKey;
import org.kie.internal.process.CorrelationProperty;

public class CorrelationKeyXmlAdapter extends XmlAdapter<String, CorrelationKey> {

    @Override
    public CorrelationKey unmarshal(String key) throws Exception {
    	List<String> keys = new ArrayList<String>();
    	for (String k: key.split(",")) {
    		keys.add(k);
    	}
        return KieInternalServices.Factory.get()
    		.newCorrelationKeyFactory().newCorrelationKey(keys);
    }

    @Override
    public String marshal(CorrelationKey key) throws Exception {
    	String result = null;
        for (CorrelationProperty<?> p: key.getProperties()) {
        	if (result == null) {
        		result = (String) p.getValue();
        	} else {
        		result += "," + p.getValue();
        	}
        }
        return result;
    }

}
