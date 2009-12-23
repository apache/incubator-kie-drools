/*
 *  Copyright 2009 salaboy.
 * 
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 * 
 *       http://www.apache.org/licenses/LICENSE-2.0
 * 
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *  under the License.
 */

package org.drools.vsm.remote;

import java.io.Serializable;
import org.drools.runtime.process.ProcessInstance;

/**
 *
 * @author salaboy
 */
public class ProcessInstanceRemoteClient implements ProcessInstance, Serializable {

    private String processId;
    private long id;
    private String processName;
    private int state;
    private String[] eventTypes;
    public ProcessInstanceRemoteClient() {
    }

    public ProcessInstanceRemoteClient(long id, String processId, String processName, int state) {
        this.processId = processId;
        this.id = id;
        this.processName = processName;
        this.state = state;
    }



    public String getProcessId() {
        return processId;
    }

    public long getId() {
        return id;
    }

    public String getProcessName() {
        return processName;
    }

    public int getState() {
        return state;
    }

    public void signalEvent(String type, Object event) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public String[] getEventTypes() {
        return eventTypes;
    }

}
