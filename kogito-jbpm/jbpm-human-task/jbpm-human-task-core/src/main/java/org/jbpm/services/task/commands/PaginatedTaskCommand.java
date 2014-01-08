/*
 * Copyright 2014 JBoss by Red Hat.
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

package org.jbpm.services.task.commands;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;

/**
 *
 * @author salaboy
 */

public abstract class PaginatedTaskCommand<T> extends TaskCommand<T> {
    @XmlElement(name="offset")
    @XmlSchemaType(name="int")
    protected int offset;
    
    @XmlElement(name="count")
    @XmlSchemaType(name="int")
    protected int count;

    public PaginatedTaskCommand() {
    }
    
    
    
}
