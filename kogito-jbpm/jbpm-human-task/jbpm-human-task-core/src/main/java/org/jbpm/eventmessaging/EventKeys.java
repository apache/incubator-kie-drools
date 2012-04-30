/**
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

package org.jbpm.eventmessaging;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EventKeys {
    private Map<EventKey, List<EventTriggerTransport>> keys;
    
    public EventKeys() {
        keys = new HashMap<EventKey, List<EventTriggerTransport>>();
    }
    
    public void register(EventKey key, EventTriggerTransport target) {
        List<EventTriggerTransport> list = keys.get( key);
        if ( list == null ) {
            list = new ArrayList<EventTriggerTransport>();
            keys.put( key, list );
        }
        list.add( target );
    }
    
    public void unregister(EventKey key, EventTriggerTransport target) {
        List<EventTriggerTransport> list = keys.get( key);
        if ( list != null ) {
            list.remove( target );
        }
    }
    
    public List<EventTriggerTransport> getTargets(EventKey key) {
        return keys.get(  key  );
    }
    
    public List<EventTriggerTransport> removeKey(EventKey key) {
        return keys.remove( key );
    }    
    
    
}
