/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.drools.compiler.kproject.models;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.drools.core.BeliefSystemType;
import org.kie.api.builder.model.ChannelModel;
import org.kie.api.builder.model.FileLoggerModel;
import org.kie.api.builder.model.KieBaseModel;
import org.kie.api.builder.model.KieSessionModel;
import org.kie.api.builder.model.ListenerModel;
import org.kie.api.builder.model.WorkItemHandlerModel;
import org.kie.api.runtime.conf.BeliefSystemTypeOption;
import org.kie.api.runtime.conf.ClockTypeOption;

public class KieSessionModelImpl
        implements
        KieSessionModel {
    private String                           name;

    private KieSessionType                   type =  KieSessionType.STATEFUL;

    private ClockTypeOption                  clockType = ClockTypeOption.REALTIME;

    private BeliefSystemTypeOption           beliefSystem = BeliefSystemTypeOption.get(BeliefSystemType.SIMPLE.toString());

    private String                           scope;

    private KieBaseModelImpl                 kBase;

    private final List<ListenerModel>        listeners = new ArrayList<>();
    private final List<WorkItemHandlerModel> wihs = new ArrayList<>();
    private final List<ChannelModel>         channels = new ArrayList<>();
    private       Map<String, String>        calendars;

    private boolean                          isDefault = false;

    private String                           consoleLogger;

    private FileLoggerModel                  fileLogger;

    private boolean                          directFiring = false;

    private boolean                          threadSafe = true;

    private boolean                          accumulateNullPropagation = false;

    public KieSessionModelImpl() { }

    public KieSessionModelImpl(KieBaseModelImpl kBase, String name) {
        this.kBase = kBase;
        this.name = name;
    }
    
    public KieBaseModelImpl getKieBaseModel() {
        return kBase;
    }
    public boolean isDefault() {
        return isDefault;
    }
    
    public void setKBase(KieBaseModel kieBaseModel) {
        this.kBase = (KieBaseModelImpl) kieBaseModel;
    }

    public KieSessionModel setDefault( boolean isDefault) {
        this.isDefault = isDefault;
        return this;
    }

    @Override
    public boolean isDirectFiring() {
        return directFiring;
    }

    @Override
    public KieSessionModel setDirectFiring( boolean directFiring ) {
        this.directFiring = directFiring;
        return this;
    }


    @Override
    public boolean isThreadSafe() {
        return threadSafe;
    }

    @Override
    public KieSessionModel setThreadSafe( boolean threadSafe ) {
        this.threadSafe = threadSafe;
        return this;
    }

    @Override
    public boolean isAccumulateNullPropagation() {
        return accumulateNullPropagation;
    }

    @Override
    public KieSessionModel setAccumulateNullPropagation(boolean accumulateNullPropagation) {
        this.accumulateNullPropagation = accumulateNullPropagation;
        return this;
    }

    /* (non-Javadoc)
     * @see org.kie.kproject.KieSessionModel#getName()
     */
    public String getName() {
        return name;
    }

    public KieSessionModel setNameForUnmarshalling(String name) {
        this.name = name;
        return this;
    }

    /* (non-Javadoc)
     * @see org.kie.kproject.KieSessionModel#getType()
     */
    public KieSessionType getType() {
        return type;
    }

    /* (non-Javadoc)
     * @see org.kie.kproject.KieSessionModel#setType(java.lang.String)
     */
    public KieSessionModel setType(KieSessionType type) {
        this.type = type;
        return this;
    }

    /* (non-Javadoc)
     * @see org.kie.kproject.KieSessionModel#getClockType()
     */
    public ClockTypeOption getClockType() {
        return clockType;
    }

    /* (non-Javadoc)
     * @see org.kie.kproject.KieSessionModel#setClockType(org.kie.api.runtime.conf.ClockTypeOption)
     */
    public KieSessionModel setClockType(ClockTypeOption clockType) {
        this.clockType = clockType;
        return this;
    }

    public BeliefSystemTypeOption getBeliefSystem() {
        return beliefSystem;
    }

    public KieSessionModel setBeliefSystem(BeliefSystemTypeOption beliefSystem) {
        this.beliefSystem = beliefSystem;
        return this;
    }

    @Override
    public KieSessionModel setScope(String scope) {
        this.scope = scope;
        return this;
    }

    @Override
    public String getScope() {
        return this.scope;
    }    

    public ListenerModel newListenerModel(String type, ListenerModel.Kind kind) {
        ListenerModelImpl listenerModel = new ListenerModelImpl(this, type, kind);
        listeners.add(listenerModel);
        return listenerModel;
    }

    public List<ListenerModel> getListenerModels() {
        return listeners;
    }

    public List<ListenerModel> getListenerModels(ListenerModel.Kind kind) {
        List<ListenerModel> listeners = new ArrayList<>();
        for (ListenerModel listener : getListenerModels()) {
            if (listener.getKind() == kind) {
                listeners.add(listener);
            }
        }
        return listeners;
    }

    public void addListenerModel(ListenerModel listener) {
        listeners.add(listener);
    }

    public WorkItemHandlerModel newWorkItemHandlerModel(String name, String type) {
        WorkItemHandlerModelImpl wihModel = new WorkItemHandlerModelImpl(this, name, type);
        wihs.add(wihModel);
        return wihModel;
    }

    public List<WorkItemHandlerModel> getWorkItemHandlerModels() {
        return wihs;
    }

    public void addWorkItemHandelerModel(WorkItemHandlerModel wih) {
        wihs.add(wih);
    }
    
    public ChannelModel newChannelModel(String name, String type) {
        ChannelModelImpl channelModel = new ChannelModelImpl(this, name, type);
        channels.add(channelModel);
        return channelModel;
    }
    
    public List<ChannelModel> getChannelModels() {
        return channels;
    }

    public void addChannelModel(ChannelModel channel) {
        channels.add(channel);
    }

    public KieSessionModel addCalendar(String name, String type) {
        if (calendars == null) {
            calendars = new HashMap<>();
        }
        calendars.put(name, type);
        return this;
    }

    public Map<String, String> getCalendars() {
        return calendars == null ? Collections.emptyMap() : calendars;
    }

    public String getConsoleLogger() {
        return consoleLogger;
    }

    public KieSessionModel setConsoleLogger(String consoleLogger) {
        this.consoleLogger = consoleLogger;
        return this;
    }

    public FileLoggerModel getFileLogger() {
        return fileLogger;
    }

    public KieSessionModel setFileLogger(String fileName) {
        this.fileLogger = new FileLoggerModelImpl(fileName);
        return this;
    }

    public KieSessionModel setFileLogger(String fileName, int interval, boolean threaded) {
        this.fileLogger = new FileLoggerModelImpl(fileName, interval, threaded);
        return this;
    }

    public void setFileLogger(FileLoggerModel fileLogger) {
        this.fileLogger = fileLogger;
    }

    public void setCalendars(Map<String, String> calendars) {
        this.calendars = calendars;
    }

    @Override
    public String toString() {
        return "KieSessionModelImpl{" +
                "name='" + name + '\'' +
                ", type=" + type +
                ", clockType=" + clockType +
                ", kBase=" + kBase.getName() +
                ", isDefault=" + isDefault +
                ", threadSafe=" + threadSafe +
                '}';
    }
}
