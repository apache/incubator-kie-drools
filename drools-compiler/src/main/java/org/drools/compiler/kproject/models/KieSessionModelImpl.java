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

package org.drools.compiler.kproject.models;

import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import org.drools.core.BeliefSystemType;
import org.drools.core.util.AbstractXStreamConverter;
import org.kie.api.builder.model.FileLoggerModel;
import org.kie.api.builder.model.KieBaseModel;
import org.kie.api.builder.model.KieSessionModel;
import org.kie.api.builder.model.ListenerModel;
import org.kie.api.builder.model.WorkItemHandlerModel;
import org.kie.api.runtime.conf.BeliefSystemTypeOption;
import org.kie.api.runtime.conf.ClockTypeOption;

import java.util.ArrayList;
import java.util.List;

public class KieSessionModelImpl
        implements
        KieSessionModel {
    private String                           name;

    private KieSessionType                   type =  KieSessionType.STATEFUL;

    private ClockTypeOption                  clockType = ClockTypeOption.get( "realtime" );

    private BeliefSystemTypeOption           beliefSystem = BeliefSystemTypeOption.get(BeliefSystemType.SIMPLE.toString());

    private String                           scope = "javax.enterprise.context.ApplicationScoped";

    private KieBaseModelImpl                 kBase;

    private final List<ListenerModel>        listeners = new ArrayList<ListenerModel>();
    private final List<WorkItemHandlerModel> wihs = new ArrayList<WorkItemHandlerModel>();

    private boolean                          isDefault = false;

    private String                           consoleLogger;

    private FileLoggerModel                  fileLogger;

    private KieSessionModelImpl() { }

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


    public KieSessionModel setDefault(boolean isDefault) {
        this.isDefault = isDefault;
        return this;
    }

    /* (non-Javadoc)
     * @see org.kie.kproject.KieSessionModel#getName()
     */
    public String getName() {
        return name;
    }

    public KieSessionModel setName(String name) {
        kBase.changeKSessionName(this, this.name, name);
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

    private List<ListenerModel> getListenerModels(ListenerModel.Kind kind) {
        List<ListenerModel> listeners = new ArrayList<ListenerModel>();
        for (ListenerModel listener : getListenerModels()) {
            if (listener.getKind() == kind) {
                listeners.add(listener);
            }
        }
        return listeners;
    }

    private void addListenerModel(ListenerModel listener) {
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

    private void addWorkItemHandelerModel(WorkItemHandlerModel wih) {
        wihs.add(wih);
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

    @Override
    public String toString() {
        return "KieSessionModel [name=" + name + ", clockType=" + clockType + "]";
    }

    public static class KSessionConverter extends AbstractXStreamConverter {

        public KSessionConverter() {
            super(KieSessionModelImpl.class);
        }

        public void marshal(Object value, HierarchicalStreamWriter writer, MarshallingContext context) {
            KieSessionModelImpl kSession = (KieSessionModelImpl) value;
            writer.addAttribute("name", kSession.getName());
            writer.addAttribute("type", kSession.getType().toString().toLowerCase() );
            writer.addAttribute( "default", Boolean.toString(kSession.isDefault()) );
            if (kSession.getClockType() != null) {
                writer.addAttribute("clockType", kSession.getClockType().getClockType());
            }
            if ( kSession.getBeliefSystem() != null ) {
                writer.addAttribute( "beliefSystem", kSession.getBeliefSystem().getBeliefSystemType().toLowerCase() );
            }
            if (kSession.getScope() != null) {
                writer.addAttribute("scope", kSession.getScope() );
            }
            if (kSession.getConsoleLogger() != null) {
                writer.startNode("consoleLogger");
                if (kSession.getConsoleLogger().length() > 0) {
                    writer.addAttribute("name", kSession.getConsoleLogger());
                }
                writer.endNode();
            }
            if (kSession.getFileLogger() != null) {
                writer.startNode("fileLogger");
                writer.addAttribute("file", kSession.getFileLogger().getFile());
                writer.addAttribute("threaded", "" + kSession.getFileLogger().isThreaded());
                writer.addAttribute("interval", "" + kSession.getFileLogger().getInterval());
                writer.endNode();
            }

            writeObjectList(writer, context, "workItemHandlers", "workItemHandler", kSession.getWorkItemHandlerModels());

            if (!kSession.getListenerModels().isEmpty()) {
                writer.startNode("listeners");
                for (ListenerModel listener : kSession.getListenerModels(ListenerModel.Kind.RULE_RUNTIME_EVENT_LISTENER)) {
                    writeObject(writer, context, listener.getKind().toString(), listener);
                }
                for (ListenerModel listener : kSession.getListenerModels(ListenerModel.Kind.AGENDA_EVENT_LISTENER)) {
                    writeObject(writer, context, listener.getKind().toString(), listener);
                }
                for (ListenerModel listener : kSession.getListenerModels(ListenerModel.Kind.PROCESS_EVENT_LISTENER)) {
                    writeObject(writer, context, listener.getKind().toString(), listener);
                }
                writer.endNode();
            }
        }

        public Object unmarshal(HierarchicalStreamReader reader, final UnmarshallingContext context) {
            final KieSessionModelImpl kSession = new KieSessionModelImpl();
            kSession.name = reader.getAttribute("name");
            kSession.setDefault( "true".equals(reader.getAttribute( "default" )) );

            String kSessionType = reader.getAttribute("type");
            kSession.setType(kSessionType != null ? KieSessionType.valueOf( kSessionType.toUpperCase() ) : KieSessionType.STATEFUL);

            String clockType = reader.getAttribute("clockType");
            if (clockType != null) {
                kSession.setClockType(ClockTypeOption.get(clockType));
            }

            String beliefSystem = reader.getAttribute( "beliefSystem" );
            if ( beliefSystem != null ) {
                kSession.setBeliefSystem( BeliefSystemTypeOption.get( beliefSystem ) );
            }

            String scope = reader.getAttribute("scope");
            if (scope != null) {
                kSession.setScope( scope );
            }            
            
            readNodes( reader, new AbstractXStreamConverter.NodeReader() {
                public void onNode(HierarchicalStreamReader reader,
                                   String name,
                                   String value) {
                    if ("listeners".equals( name )) {
                        while (reader.hasMoreChildren()) {
                            reader.moveDown();
                            String nodeName = reader.getNodeName();
                            ListenerModelImpl listener = readObject(reader, context, ListenerModelImpl.class);
                            listener.setKSession( kSession );
                            listener.setKind(ListenerModel.Kind.fromString(nodeName));
                            kSession.addListenerModel(listener);
                            reader.moveUp();
                        }
                    } else if ( "workItemHandlers".equals( name ) ) {
                        List<WorkItemHandlerModelImpl> wihs = readObjectList(reader, context, WorkItemHandlerModelImpl.class);
                        for (WorkItemHandlerModelImpl wih : wihs) {
                            wih.setKSession( kSession );
                            kSession.addWorkItemHandelerModel(wih);
                        }
                    } else if ( "consoleLogger".equals( name ) ) {
                        String consoleLogger = reader.getAttribute("name");
                        kSession.setConsoleLogger(consoleLogger == null ? "" : consoleLogger);
                    } else if ( "fileLogger".equals( name ) ) {
                        FileLoggerModelImpl fileLoggerModel = new FileLoggerModelImpl( reader.getAttribute("file") );
                        try {
                            fileLoggerModel.setInterval( Integer.parseInt(reader.getAttribute("interval")) );
                        } catch (Exception e) { }
                        try {
                            fileLoggerModel.setThreaded( Boolean.parseBoolean(reader.getAttribute("threaded")) );
                        } catch (Exception e) { }
                        kSession.fileLogger = fileLoggerModel;
                    }
                }
            } );
            return kSession;
        }
    }

}
