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

package org.jbpm.task.event.entity;

import java.io.Externalizable;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
@Entity
@DiscriminatorValue(value="us")
public class TaskUserEvent extends TaskEvent implements Externalizable {

    TaskUserEvent() {
        super();
    }
    
    TaskUserEvent(long taskId, String userId, int sessionId) {
        super( taskId, sessionId );
        this.userId = userId;
    }
    
    public String getUserId() {
        return userId;
    }
    
}
