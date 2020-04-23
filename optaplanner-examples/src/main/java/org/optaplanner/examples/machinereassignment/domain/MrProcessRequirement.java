/*
 * Copyright 2011 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.examples.machinereassignment.domain;

import org.optaplanner.examples.common.domain.AbstractPersistable;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("MrProcessRequirement")
public class MrProcessRequirement extends AbstractPersistable {

    private MrProcess process;
    private MrResource resource;

    private long usage;

    public MrProcessRequirement() {
    }

    public MrProcessRequirement(MrProcess process, MrResource resource, long usage) {
        this.process = process;
        this.resource = resource;
        this.usage = usage;
    }

    public MrProcessRequirement(long id, MrProcess process, MrResource resource, long usage) {
        super(id);
        this.process = process;
        this.resource = resource;
        this.usage = usage;
    }

    public MrProcess getProcess() {
        return process;
    }

    public void setProcess(MrProcess process) {
        this.process = process;
    }

    public MrResource getResource() {
        return resource;
    }

    public void setResource(MrResource resource) {
        this.resource = resource;
    }

    public long getUsage() {
        return usage;
    }

    public void setUsage(long usage) {
        this.usage = usage;
    }

}
