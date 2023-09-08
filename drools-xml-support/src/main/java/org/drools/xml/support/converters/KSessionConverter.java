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
package org.drools.xml.support.converters;

import java.util.List;

import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import org.drools.compiler.kproject.KieModuleException;
import org.drools.compiler.kproject.models.ChannelModelImpl;
import org.drools.compiler.kproject.models.FileLoggerModelImpl;
import org.drools.compiler.kproject.models.KieSessionModelImpl;
import org.drools.compiler.kproject.models.ListenerModelImpl;
import org.drools.compiler.kproject.models.WorkItemHandlerModelImpl;
import org.kie.api.builder.model.KieSessionModel;
import org.kie.api.builder.model.ListenerModel;
import org.kie.api.runtime.conf.BeliefSystemTypeOption;
import org.kie.api.runtime.conf.ClockTypeOption;

public class KSessionConverter extends AbstractXStreamConverter {

    public KSessionConverter() {
        super(KieSessionModelImpl.class);
    }

    public void marshal(Object value, HierarchicalStreamWriter writer, MarshallingContext context) {
        KieSessionModelImpl kSession = (KieSessionModelImpl) value;
        writer.addAttribute("name", kSession.getName());
        writer.addAttribute("type", kSession.getType().toString().toLowerCase() );
        writer.addAttribute( "default", Boolean.toString(kSession.isDefault()) );
        writer.addAttribute( "directFiring", Boolean.toString(kSession.isDirectFiring()) );
        writer.addAttribute( "threadSafe", Boolean.toString(kSession.isThreadSafe()) );
        writer.addAttribute( "accumulateNullPropagation", Boolean.toString(kSession.isAccumulateNullPropagation()) );
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
        writeObjectList(writer, context, "channels", "channel", kSession.getChannelModels());
        writeMap(writer, context, "calendars", "calendar", "name", "type", kSession.getCalendars());

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
        String kSessionName = reader.getAttribute("name");
        if (kSessionName.isEmpty()) {
            throw new KieModuleException("ksession name is empty in kmodule.xml");
        }
        kSession.setNameForUnmarshalling( kSessionName );
        kSession.setDefault( "true".equals(reader.getAttribute( "default" )) );
        kSession.setDirectFiring( "true".equals(reader.getAttribute( "directFiring" )) );
        kSession.setThreadSafe( "true".equals(reader.getAttribute( "threadSafe" )) );
        kSession.setAccumulateNullPropagation( "true".equals(reader.getAttribute( "accumulateNullPropagation" )) );

        String kSessionType = reader.getAttribute("type");
        kSession.setType(kSessionType != null ? KieSessionModel.KieSessionType.valueOf( kSessionType.toUpperCase() ) : KieSessionModel.KieSessionType.STATEFUL);

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
                } else if ("calendars".equals(name)) {
                    kSession.setCalendars( readMap(reader, context, "name", "type") );
                } else if ( "channels".equals( name ) ) {
                    List<ChannelModelImpl> channels = readObjectList(reader, context, ChannelModelImpl.class);
                    for (ChannelModelImpl channel : channels) {
                        channel.setKSession( kSession );
                        kSession.addChannelModel(channel);;
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
                    kSession.setFileLogger( fileLoggerModel );
                }
            }
        } );
        return kSession;
    }
}