/*
 * Copyright 2010 salaboy.
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
 * under the License.
 */

package org.drools.core.command.impl;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.kie.internal.command.Context;
import org.kie.internal.command.ContextManager;

public class ContextImplWithEviction extends ContextImpl{

    private  Logger log = Logger.getLogger( ContextImplWithEviction.class.getName() );
    
    private Map<String, Long> evictionMap = new ConcurrentHashMap<String, Long>();
    
    private boolean           evictionEnabled = true;
    //I need a way to set up this parameters 
    
    //time to look up for evicted entries
    private long              evictionWakeUpTime = 5 * 60 * 1000 ; // 5 minutes
    //Time that the entry is valid
    private long              entryEvictionTime = 60 * 60 * 1000;  // 1 hour
    
    //private LinkedBlockingQueue<ChangeSet> queue;
    
    public ContextImplWithEviction(String name, ContextManager manager, Context delegate) {
        super(name, manager, delegate);
    }

    public ContextImplWithEviction(String name, ContextManager manager) {
        super(name, manager);
        
    }
    
    @Override
    public Object get(String identifier){
        Object result = super.get(identifier);
        if(evictionEnabled && identifier != null && result != null){
            long currentTimeStamp = System.currentTimeMillis();
            evictionMap.put(identifier, currentTimeStamp);
            log.log(Level.FINE,"Updating key=" +identifier +"@"+super.getName()+":"+currentTimeStamp);
        }
        return result;
    }
    
    @Override
    public void set(String name,
                    Object object) {
        if(evictionEnabled){
            long currentTimeStamp = System.currentTimeMillis();
            evictionMap.put(name, currentTimeStamp );
            log.log(Level.FINE,"Setting key=" +name +"@"+super.getName()+":"+currentTimeStamp);
        }
        super.set(name, object);
    }
    
    public long getEvictionWakeUpTime() {
        return evictionWakeUpTime;
    }

    public Map<String, Long> getEvictionMap() {
        return evictionMap;
    }

    public boolean isEvictionEnabled() {
        return evictionEnabled;
    }

    public void setEvictionEnabled(boolean evictionEnabled) {
        this.evictionEnabled = evictionEnabled;
    }

    public long getEntryEvictionTime() {
        return entryEvictionTime;
    }

    public void setEntryEvictionTime(long entryEvictionTime) {
        this.entryEvictionTime = entryEvictionTime;
    }

    public void setEvictionWakeUpTime(long evictionWakeUpTime) {
        this.evictionWakeUpTime = evictionWakeUpTime;
    }
    

}
