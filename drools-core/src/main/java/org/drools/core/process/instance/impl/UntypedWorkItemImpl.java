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

package org.drools.core.process.instance.impl;

import java.io.Serializable;
import java.util.Map;

import org.apache.commons.beanutils.BeanMap;
import org.drools.core.process.instance.TypedWorkItem;
import org.drools.core.process.instance.WorkItem;

/**
 * An implementation of an untyped WorkItem, that delegates to
 * a TypedWorkItemImpl.
 *
 * For compatibility reasons, TypedWorkItem may be wrapped in an UntypedWorkItemImpl,
 * behaving like a WorkItem (returning maps of params, results).
 *
 * Internally, it uses Apache Commons BeanMap to keep in sync the
 * map key/value pairs, and the underlying bean implementation.
 *
 * This is not publicly instantiable.
 */
class UntypedWorkItemImpl implements WorkItem,
                                            Serializable {

    private static final long serialVersionUID = 510l;

    private final TypedWorkItem<?, ?> workItem;

    UntypedWorkItemImpl(TypedWorkItem<?, ?> workItem) {
        this.workItem = workItem;
    }

    TypedWorkItem<?, ?> asTypedWorkItem() {
        return workItem;
    }

    @Override
    public org.kie.api.runtime.process.WorkItem asWorkItem() {
        return this;
    }

    @Override
    public void setId(long id) {
        workItem.setId(id);
    }

    @Override
    public void setName(String name) {
        workItem.setName(name);
    }

    @Override
    public void setParameter(String name, Object value) {
        beanMapOf(workItem.getParameters()).put(name, value);
    }

    public void setParameters(Map<String, Object> parameters) {
        beanMapOf(workItem.getParameters()).putAll(parameters);
    }

    public void setResults(Map<String, Object> results) {
        beanMapOf(workItem.getResults()).putAll(results);
    }

    @Override
    public void setState(int state) {
        workItem.setState(state);
    }

    @Override
    public void setProcessInstanceId(long processInstanceId) {
        workItem.setProcessInstanceId(processInstanceId);
    }

    @Override
    public void setDeploymentId(String deploymentId) {
        workItem.setDeploymentId(deploymentId);
    }

    @Override
    public void setNodeInstanceId(long deploymentId) {
        workItem.setNodeInstanceId(deploymentId);
    }

    @Override
    public void setNodeId(long deploymentId) {
        workItem.setNodeId(deploymentId);
    }

    @Override
    public String getDeploymentId() {
        return workItem.getDeploymentId();
    }

    @Override
    public long getNodeInstanceId() {
        return workItem.getNodeInstanceId();
    }

    @Override
    public long getNodeId() {
        return workItem.getNodeId();
    }

    @Override
    public long getId() {
        return workItem.getId();
    }

    @Override
    public String getName() {
        return workItem.getName();
    }

    @Override
    public int getState() {
        return workItem.getState();
    }

    @Override
    public Object getParameter(String name) {
        return getParameters().get(name);
    }

    @Override
    public Map<String, Object> getParameters() {
        return beanMapOf(workItem.getParameters());
    }

    @Override
    public Object getResult(String name) {
        return getResults().get(name);
    }

    @Override
    public Map<String, Object> getResults() {
        return upcast(new BeanMap(workItem.getResults()));
    }

    @Override
    public long getProcessInstanceId() {
        return workItem.getProcessInstanceId();
    }

    private Map<String, Object> beanMapOf(Object bean) {
        return upcast(new BeanMap(bean));
    }

    private Map<String, Object> upcast(Map beanMap) {
        return beanMap;
    }
}
