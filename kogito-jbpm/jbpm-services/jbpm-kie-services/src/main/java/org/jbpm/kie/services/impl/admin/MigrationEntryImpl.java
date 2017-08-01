/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jbpm.kie.services.impl.admin;

import java.io.Serializable;
import java.util.Date;


public class MigrationEntryImpl implements org.jbpm.services.api.admin.MigrationEntry, Serializable {

    private static final long serialVersionUID = 5946077616945343956L;
    private Date timestamp;
    private String message;
    private String type;
    
    public MigrationEntryImpl(Date timestamp, String message, String type) {
        super();
        this.timestamp = timestamp;
        this.message = message;
        this.type = type;
    }

    public Date getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    @Override
    public String getType() {
        return type;
    }
    
    public void setType(String type) {
        this.type = type;
    }

}
