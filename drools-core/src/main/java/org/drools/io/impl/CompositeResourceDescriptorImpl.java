/*
 * Copyright 2012 JBoss by Red Hat.
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
package org.drools.io.impl;

import java.util.List;
import org.drools.io.CompositeResourceDescriptor;
import org.drools.io.ResourceDescriptor;

/**
 * Resource Descriptor containing a List of internal Resource Descriptors
 */
public class CompositeResourceDescriptorImpl extends ResourceDescriptorImpl implements CompositeResourceDescriptor{

    private List<ResourceDescriptor> internalResources;

    public List<ResourceDescriptor> getInternalResources() {
        return internalResources;
    }

    public void setInternalResources(List<ResourceDescriptor> internalResources) {
        this.internalResources = internalResources;
    }
    
}
