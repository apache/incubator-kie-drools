/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

package org.drools.games.adventures;

public class Response {
    private long remoteId;
    private long localId;
    private Object object;
    
    public Response(Object object) {
        this.object = object;
    }
    
    public long getRemoteId() {
        return remoteId;
    }
    
    public void setRemoteId(long id) {
        this.remoteId = id;
    }
    
    public Object getObject() {
        return object;
    }

    public long getLocalId() {
        return localId;
    }

    public void setLocalId(long localId) {
        this.localId = localId;
    }

    public void setObject(Object object) {
        this.object = object;
    }

    @Override
    public String toString() {
        return "Response [remoteId=" + remoteId + ", localId=" + localId + ", object=" + object + "]";
    } 
    
}
