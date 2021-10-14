/*
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
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

package org.kie.internal.task.api.model;

import java.io.Externalizable;

public interface ContentData extends Externalizable {

    AccessType getAccessType();

    void setAccessType(AccessType accessType);

    String getType();

    void setType(String type);

    byte[] getContent();

    void setContent(byte[] content);
    
    Object getContentObject();
    
    void setContentObject(Object object);
}
