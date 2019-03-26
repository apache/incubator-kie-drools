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

package org.jbpm.services.api.admin;

import java.io.Serializable;
import java.util.Date;

/**
 * Single migration step information that comes with
 * message, timestamp and type (INFO, WARN, ERROR)
 *
 */
public interface MigrationEntry extends Serializable {
    
    /**
     * Return timestamp of the entry - when it was created
     * @return
     */
    Date getTimestamp();
    
    /**
     * Returns actual message of the entry
     * @return
     */
    String getMessage();
    
    /**
     * Returns type of the entry - INFO, WARN, ERROR
     * @return
     */
    String getType();
}
