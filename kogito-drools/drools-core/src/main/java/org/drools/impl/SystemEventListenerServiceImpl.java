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

package org.drools.impl;

import org.drools.SystemEventListener;
import org.drools.SystemEventListenerService;
import org.drools.agent.impl.PrintStreamSystemEventListener;
import org.drools.core.util.DelegatingSystemEventListener;

public class SystemEventListenerServiceImpl implements SystemEventListenerService{
    
    private DelegatingSystemEventListener    listener = new DelegatingSystemEventListener( new PrintStreamSystemEventListener() );
    
    public SystemEventListener getSystemEventListener() {
        return this.listener;
    }

    public void setSystemEventListener(SystemEventListener listener) {
        this.listener.setSystemEventListener( listener );
    }

}
