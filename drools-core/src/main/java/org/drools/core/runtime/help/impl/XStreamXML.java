/*
 * Copyright 2010 JBoss Inc
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

package org.drools.core.runtime.help.impl;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.converters.collections.AbstractCollectionConverter;
import com.thoughtworks.xstream.io.ExtendedHierarchicalStreamWriterHelper;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import org.drools.core.QueryResultsImpl;
import org.drools.core.base.ClassObjectType;
import org.drools.core.base.DroolsQuery;
import org.drools.core.command.runtime.BatchExecutionCommandImpl;
import org.drools.core.command.runtime.GetGlobalCommand;
import org.drools.core.command.runtime.SetGlobalCommand;
import org.drools.core.command.runtime.process.AbortWorkItemCommand;
import org.drools.core.command.runtime.process.CompleteWorkItemCommand;
import org.drools.core.command.runtime.process.SignalEventCommand;
import org.drools.core.command.runtime.process.StartProcessCommand;
import org.drools.core.command.runtime.rule.AgendaGroupSetFocusCommand;
import org.drools.core.command.runtime.rule.ClearActivationGroupCommand;
import org.drools.core.command.runtime.rule.ClearAgendaCommand;
import org.drools.core.command.runtime.rule.ClearAgendaGroupCommand;
import org.drools.core.command.runtime.rule.ClearRuleFlowGroupCommand;
import org.drools.core.command.runtime.rule.DeleteCommand;
import org.drools.core.command.runtime.rule.FireAllRulesCommand;
import org.drools.core.command.runtime.rule.GetObjectCommand;
import org.drools.core.command.runtime.rule.GetObjectsCommand;
import org.drools.core.command.runtime.rule.InsertElementsCommand;
import org.drools.core.command.runtime.rule.InsertObjectCommand;
import org.drools.core.command.runtime.rule.ModifyCommand;
import org.drools.core.command.runtime.rule.QueryCommand;
import org.drools.core.common.DefaultFactHandle;
import org.drools.core.rule.Declaration;
import org.drools.core.runtime.impl.ExecutionResultImpl;
import org.drools.core.runtime.rule.impl.FlatQueryResults;
import org.drools.core.spi.ObjectType;
import org.kie.api.command.Command;
import org.kie.api.command.Setter;
import org.kie.api.runtime.ExecutionResults;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.api.runtime.rule.QueryResults;
import org.kie.api.runtime.rule.QueryResultsRow;
import org.kie.internal.command.CommandFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class XStreamXML {
    public static volatile boolean SORT_MAPS = false;

    public static XStream newXStreamMarshaller(XStream xstream) {
        XStreamHelper.setAliases( xstream );

        xstream.processAnnotations( BatchExecutionCommandImpl.class );
        xstream.addImplicitCollection( BatchExecutionCommandImpl.class,
                                       "commands" );

        xstream.registerConverter( new AgendaGroupSetFocusConverter( xstream ) );
        xstream.registerConverter( new ClearActivationGroupConverter( xstream ) );
        xstream.registerConverter( new ClearAgendaConverter( xstream ) );
        xstream.registerConverter( new ClearAgendaGroupConverter( xstream ) );
        xstream.registerConverter( new ClearRuleFlowGroupConverter( xstream ) );
        xstream.registerConverter( new DeleteConverter( xstream ) );
        xstream.registerConverter( new InsertConverter( xstream ) );
        xstream.registerConverter( new ModifyConverter( xstream ) );
        xstream.registerConverter( new GetObjectConverter( xstream ) );
        xstream.registerConverter( new InsertElementsConverter( xstream ) );
        xstream.registerConverter( new FireAllRulesConverter( xstream ) );
        xstream.registerConverter( new StartProcessConvert( xstream ) );
        xstream.registerConverter( new SignalEventConverter( xstream ) );
        xstream.registerConverter( new CompleteWorkItemConverter( xstream ) );
        xstream.registerConverter( new AbortWorkItemConverter( xstream ) );
        xstream.registerConverter( new QueryConverter( xstream ) );
        xstream.registerConverter( new SetGlobalConverter( xstream ) );
        xstream.registerConverter( new GetGlobalConverter( xstream ) );
        xstream.registerConverter( new GetObjectsConverter( xstream ) );
        xstream.registerConverter( new BatchExecutionResultConverter( xstream ) );
        xstream.registerConverter( new QueryResultsConverter( xstream ) );
        xstream.registerConverter( new FactHandleConverter( xstream ) );

        return xstream;
    }

    public static class InsertConverter extends AbstractCollectionConverter
        implements
        Converter {

        public InsertConverter(XStream xstream) {
            super( xstream.getMapper() );
        }

        public void marshal(Object object,
                            HierarchicalStreamWriter writer,
                            MarshallingContext context) {
            InsertObjectCommand cmd = (InsertObjectCommand) object;
            if ( cmd.getOutIdentifier() != null ) {
                writer.addAttribute( "out-identifier",
                                     cmd.getOutIdentifier() );

                writer.addAttribute( "return-object",
                                     Boolean.toString( cmd.isReturnObject() ) );

                writer.addAttribute( "entry-point",
                                     cmd.getEntryPoint() );

            }
            writeItem( cmd.getObject(),
                       context,
                       writer );
        }

        public Object unmarshal(HierarchicalStreamReader reader,
                                UnmarshallingContext context) {
            String identifierOut = reader.getAttribute( "out-identifier" );
            String returnObject = reader.getAttribute( "return-object" );
            String entryPoint = reader.getAttribute( "entry-point" );

            reader.moveDown();
            Object object = readItem( reader,
                                      context,
                                      null );
            reader.moveUp();
            InsertObjectCommand cmd = new InsertObjectCommand( object );
            if ( identifierOut != null ) {
                cmd.setOutIdentifier( identifierOut );
                if ( returnObject != null ) {
                    cmd.setReturnObject( Boolean.parseBoolean( returnObject ) );
                }
            }
            if ( entryPoint != null ) {
                cmd.setEntryPoint( entryPoint );
            }
            return cmd;
        }

        public boolean canConvert(Class clazz) {
            return clazz.equals( InsertObjectCommand.class );
        }

    }

    public static class FactHandleConverter
        implements
        Converter {

        public FactHandleConverter(XStream xstream) {
        }

        public boolean canConvert(Class aClass) {
            return FactHandle.class.isAssignableFrom( aClass );
        }

        public void marshal(Object object,
                            HierarchicalStreamWriter writer,
                            MarshallingContext marshallingContext) {
            FactHandle fh = (FactHandle) object;
            //writer.startNode("fact-handle");
            writer.addAttribute(
                    "external-form",
                    fh.toExternalForm() );
            //writer.endNode();
        }

        public Object unmarshal(HierarchicalStreamReader hierarchicalStreamReader,
                                UnmarshallingContext unmarshallingContext) {
            throw new UnsupportedOperationException( "Unable to unmarshal fact handles." );
        }
    }

    public static class ModifyConverter
        implements
        Converter {

        public ModifyConverter(XStream xstream) {
        }

        public void marshal(Object object,
                            HierarchicalStreamWriter writer,
                            MarshallingContext context) {
            ModifyCommand cmd = (ModifyCommand) object;

            writer.addAttribute( "fact-handle",
                                 cmd.getFactHandle().toExternalForm() );

            for ( Setter setter : cmd.getSetters() ) {
                writer.startNode( "set" );
                writer.addAttribute( "accessor",
                                     setter.getAccessor() );
                writer.addAttribute( "value",
                                     setter.getValue() );
                writer.endNode();
            }
        }

        public Object unmarshal(HierarchicalStreamReader reader,
                                UnmarshallingContext context) {
            FactHandle factHandle = DefaultFactHandle.createFromExternalFormat( reader.getAttribute( "fact-handle" ) );

            List<Setter> setters = new ArrayList();
            while ( reader.hasMoreChildren() ) {
                reader.moveDown();
                Setter setter = CommandFactory.newSetter( reader.getAttribute( "accessor" ),
                                                          reader.getAttribute( "value" ) );
                setters.add( setter );
                reader.moveUp();
            }

            return CommandFactory.newModify( factHandle,
                                             setters );
        }

        public boolean canConvert(Class clazz) {
            return clazz.equals( ModifyCommand.class );
        }

    }

    public static class DeleteConverter
            extends AbstractCollectionConverter
        implements
        Converter {

        public DeleteConverter(XStream xstream) {
            super( xstream.getMapper() );
        }

        public void marshal(Object object,
                            HierarchicalStreamWriter writer,
                            MarshallingContext context) {
            DeleteCommand cmd = (DeleteCommand) object;

            writer.addAttribute( "fact-handle",
                                 cmd.getFactHandle().toExternalForm() );
        }

        public Object unmarshal(HierarchicalStreamReader reader,
                                UnmarshallingContext context) {
            FactHandle factHandle = DefaultFactHandle.createFromExternalFormat( reader.getAttribute( "fact-handle" ) );

            return CommandFactory.newDelete( factHandle );
        }

        public boolean canConvert(Class clazz) {
            return clazz.equals( DeleteCommand.class );
        }
    }

    public static class InsertElementsConverter extends AbstractCollectionConverter
        implements
        Converter {

        public InsertElementsConverter(XStream xstream) {
            super( xstream.getMapper() );
        }

        public void marshal(Object object,
                            HierarchicalStreamWriter writer,
                            MarshallingContext context) {
            InsertElementsCommand cmd = (InsertElementsCommand) object;

            if ( cmd.getOutIdentifier() != null ) {
                writer.addAttribute( "out-identifier",
                                     cmd.getOutIdentifier() );

                writer.addAttribute( "return-objects",
                                     Boolean.toString( cmd.isReturnObject() ) );

                writer.addAttribute( "entry-point",
                                     cmd.getEntryPoint() );

            }

            for ( Object element : cmd.getObjects() ) {
                writeItem( element,
                           context,
                           writer );
            }
        }

        public Object unmarshal(HierarchicalStreamReader reader,
                                UnmarshallingContext context) {
            String identifierOut = reader.getAttribute( "out-identifier" );
            String returnObject = reader.getAttribute( "return-objects" );
            String entryPoint = reader.getAttribute( "entry-point" );

            List objects = new ArrayList();
            while ( reader.hasMoreChildren() ) {
                reader.moveDown();
                Object object = readItem( reader,
                                          context,
                                          null );
                reader.moveUp();
                objects.add( object );
            }

            InsertElementsCommand cmd = new InsertElementsCommand( objects );
            if ( identifierOut != null ) {
                cmd.setOutIdentifier( identifierOut );
                if ( returnObject != null ) {
                    cmd.setReturnObject( Boolean.parseBoolean( returnObject ) );
                }
            }
            if ( entryPoint != null ) {
                cmd.setEntryPoint( entryPoint );
            }
            return cmd;
        }

        public boolean canConvert(Class clazz) {
            return clazz.equals( InsertElementsCommand.class );
        }

    }

    public static class SetGlobalConverter extends AbstractCollectionConverter
        implements
        Converter {

        public SetGlobalConverter(XStream xstream) {
            super( xstream.getMapper() );
        }

        public void marshal(Object object,
                            HierarchicalStreamWriter writer,
                            MarshallingContext context) {
            SetGlobalCommand cmd = (SetGlobalCommand) object;

            writer.addAttribute( "identifier",
                                 cmd.getIdentifier() );

            if ( cmd.getOutIdentifier() != null ) {
                writer.addAttribute( "out-identifier",
                                     cmd.getOutIdentifier() );
            }

            writeItem( cmd.getObject(),
                       context,
                       writer );
        }

        public Object unmarshal(HierarchicalStreamReader reader,
                                UnmarshallingContext context) {
            String identifier = reader.getAttribute( "identifier" );
            String outString = reader.getAttribute( "out" );
            boolean out = false;
            if ( outString != null ) {
                out = Boolean.valueOf( outString );
            }
            String identifierOut = reader.getAttribute( "out-identifier" );

            reader.moveDown();
            Object object = readItem( reader,
                                      context,
                                      null );
            reader.moveUp();
            SetGlobalCommand cmd = new SetGlobalCommand( identifier,
                                                         object );
            if ( identifierOut != null ) {
                cmd.setOutIdentifier( identifierOut );
            } else if ( out ) {
                cmd.setOutIdentifier( identifier );
            }
            return cmd;
        }

        public boolean canConvert(Class clazz) {
            return clazz.equals( SetGlobalCommand.class );
        }
    }

    public static class GetObjectConverter extends AbstractCollectionConverter
        implements
        Converter {

        public GetObjectConverter(XStream xstream) {
            super( xstream.getMapper() );
        }

        public void marshal(Object object,
                            HierarchicalStreamWriter writer,
                            MarshallingContext context) {
            GetObjectCommand cmd = (GetObjectCommand) object;

            writer.addAttribute( "fact-handle",
                                 cmd.getFactHandle().toExternalForm() );

            if ( cmd.getOutIdentifier() != null ) {
                writer.addAttribute( "out-identifier",
                                     cmd.getOutIdentifier() );
            }
        }

        public Object unmarshal(HierarchicalStreamReader reader,
                                UnmarshallingContext context) {
            FactHandle factHandle = DefaultFactHandle.createFromExternalFormat( reader.getAttribute( "fact-handle" ) );
            String identifierOut = reader.getAttribute( "out-identifier" );

            GetObjectCommand cmd = new GetObjectCommand( factHandle );
            if ( identifierOut != null ) {
                cmd.setOutIdentifier( identifierOut );
            }
            return cmd;
        }

        public boolean canConvert(Class clazz) {
            return clazz.equals( GetObjectCommand.class );
        }
    }

    public static class GetGlobalConverter extends AbstractCollectionConverter
        implements
        Converter {

        public GetGlobalConverter(XStream xstream) {
            super( xstream.getMapper() );
        }

        public void marshal(Object object,
                            HierarchicalStreamWriter writer,
                            MarshallingContext context) {
            GetGlobalCommand cmd = (GetGlobalCommand) object;

            writer.addAttribute( "identifier",
                                 cmd.getIdentifier() );

            if ( cmd.getOutIdentifier() != null ) {
                writer.addAttribute( "out-identifier",
                                     cmd.getOutIdentifier() );
            }
        }

        public Object unmarshal(HierarchicalStreamReader reader,
                                UnmarshallingContext context) {
            String identifier = reader.getAttribute( "identifier" );
            String identifierOut = reader.getAttribute( "out-identifier" );

            GetGlobalCommand cmd = new GetGlobalCommand( identifier );
            if ( identifierOut != null ) {
                cmd.setOutIdentifier( identifierOut );
            }
            return cmd;
        }

        public boolean canConvert(Class clazz) {
            return clazz.equals( GetGlobalCommand.class );
        }
    }

    public static class GetObjectsConverter extends AbstractCollectionConverter
        implements
        Converter {

        public GetObjectsConverter(XStream xstream) {
            super( xstream.getMapper() );
        }

        public void marshal(Object object,
                            HierarchicalStreamWriter writer,
                            MarshallingContext context) {
            GetObjectsCommand cmd = (GetObjectsCommand) object;

            if ( cmd.getOutIdentifier() != null ) {
                writer.addAttribute( "out-identifier",
                                     cmd.getOutIdentifier() );
            }
        }

        public Object unmarshal(HierarchicalStreamReader reader,
                                UnmarshallingContext context) {
            String identifierOut = reader.getAttribute( "out-identifier" );

            GetObjectsCommand cmd = new GetObjectsCommand();
            if ( identifierOut != null ) {
                cmd.setOutIdentifier( identifierOut );
            }
            return cmd;
        }

        public boolean canConvert(Class clazz) {
            return clazz.equals( GetObjectsCommand.class );
        }
    }

    public static class FireAllRulesConverter extends AbstractCollectionConverter
        implements
        Converter {

        public FireAllRulesConverter(XStream xstream) {
            super( xstream.getMapper() );
        }

        public void marshal(Object object,
                            HierarchicalStreamWriter writer,
                            MarshallingContext context) {
            FireAllRulesCommand cmd = (FireAllRulesCommand) object;

            if ( cmd.getMax() != -1 ) {
                writer.addAttribute( "max",
                                     Integer.toString( cmd.getMax() ) );
            }

            if ( cmd.getOutIdentifier() != null ) {
                writer.addAttribute( "out-identifier",
                                     cmd.getOutIdentifier() );
            }
        }

        public Object unmarshal(HierarchicalStreamReader reader,
                                UnmarshallingContext context) {
            String max = reader.getAttribute( "max" );
            String outIdentifier = reader.getAttribute( "out-identifier" );

            FireAllRulesCommand cmd = null;

            if ( max != null ) {
                cmd = new FireAllRulesCommand( Integer.parseInt( max ) );
            } else {
                cmd = new FireAllRulesCommand();
            }

            if ( outIdentifier != null ) {
                cmd.setOutIdentifier( outIdentifier );
            }

            return cmd;
        }

        public boolean canConvert(Class clazz) {
            return clazz.equals( FireAllRulesCommand.class );
        }
    }

    public static class QueryConverter extends AbstractCollectionConverter
        implements
        Converter {

        public QueryConverter(XStream xstream) {
            super( xstream.getMapper() );
        }

        public void marshal(Object object,
                            HierarchicalStreamWriter writer,
                            MarshallingContext context) {
            QueryCommand cmd = (QueryCommand) object;
            writer.addAttribute( "out-identifier",
                                 cmd.getOutIdentifier() );
            writer.addAttribute( "name",
                                 cmd.getName() );
            if ( cmd.getArguments() != null ) {
                for ( Object arg : cmd.getArguments() ) {
                    writeItem( arg,
                               context,
                               writer );
                }
            }
        }

        public Object unmarshal(HierarchicalStreamReader reader,
                                UnmarshallingContext context) {
            List<String> outs = new ArrayList<String>();

            // Query cmd = null;
            String outIdentifier = reader.getAttribute( "out-identifier" );
            String name = reader.getAttribute( "name" );
            List<Object> args = new ArrayList<Object>();
            while ( reader.hasMoreChildren() ) {
                reader.moveDown();
                Object arg = readItem( reader,
                                       context,
                                       null );
                args.add( arg );
                reader.moveUp();
            }
            QueryCommand cmd = new QueryCommand( outIdentifier,
                                                 name,
                                                 args.toArray( new Object[args.size()] ) );

            return cmd;
        }

        public boolean canConvert(Class clazz) {
            return clazz.equals( QueryCommand.class );
        }
    }

    public static class StartProcessConvert extends AbstractCollectionConverter
        implements
        Converter {

        public StartProcessConvert(XStream xstream) {
            super( xstream.getMapper() );
        }

        public void marshal(Object object,
                            HierarchicalStreamWriter writer,
                            MarshallingContext context) {
            StartProcessCommand cmd = (StartProcessCommand) object;
            writer.addAttribute( "processId",
                                 cmd.getProcessId() );
            if ( cmd.getOutIdentifier() != null ) {
                writer.addAttribute( "out-identifier",
                                     cmd.getOutIdentifier() );
            }

            for ( Entry<String, Object> entry : cmd.getParameters().entrySet() ) {
                writer.startNode( "parameter" );
                writer.addAttribute( "identifier",
                                     entry.getKey() );
                writeItem( entry.getValue(),
                           context,
                           writer );
                writer.endNode();
            }
        }

        public Object unmarshal(HierarchicalStreamReader reader,
                                UnmarshallingContext context) {
            String processId = reader.getAttribute( "processId" );
            String outIdentifier = reader.getAttribute( "out-identifier" );

            HashMap<String, Object> params = new HashMap<String, Object>();
            while ( reader.hasMoreChildren() ) {
                reader.moveDown();
                String identifier = reader.getAttribute( "identifier" );
                reader.moveDown();
                Object value = readItem( reader,
                                         context,
                                         null );
                reader.moveUp();
                params.put( identifier,
                            value );
                reader.moveUp();
            }
            StartProcessCommand cmd = new StartProcessCommand();
            cmd.setProcessId( processId );
            cmd.setParameters( params );
            cmd.setOutIdentifier( outIdentifier );

            return cmd;
        }

        public boolean canConvert(Class clazz) {
            return clazz.equals( StartProcessCommand.class );
        }
    }

    public static class SignalEventConverter extends AbstractCollectionConverter
        implements
        Converter {

        public SignalEventConverter(XStream xstream) {
            super( xstream.getMapper() );
        }

        public void marshal(Object object,
                            HierarchicalStreamWriter writer,
                            MarshallingContext context) {
            SignalEventCommand cmd = (SignalEventCommand) object;
            long processInstanceId = cmd.getProcessInstanceId();
            String eventType = cmd.getEventType();
            Object event = cmd.getEvent();

            if ( processInstanceId != -1 ) {
                writer.addAttribute( "process-instance-id",
                                     Long.toString( processInstanceId ) );
            }

            writer.addAttribute( "event-type",
                                 eventType );

            writeItem( event,
                       context,
                       writer );
        }

        public Object unmarshal(HierarchicalStreamReader reader,
                                UnmarshallingContext context) {
            String processInstanceId = reader.getAttribute( "process-instance-id" );
            String eventType = reader.getAttribute( "event-type" );

            reader.moveDown();
            Object event = readItem( reader,
                                     context,
                                     null );
            reader.moveUp();

            Command cmd;
            if ( processInstanceId != null ) {
                cmd = CommandFactory.newSignalEvent( Long.parseLong( processInstanceId ),
                                                     eventType,
                                                     event );
            } else {
                cmd = CommandFactory.newSignalEvent( eventType,
                                                     event );
            }

            return cmd;
        }

        public boolean canConvert(Class clazz) {
            return clazz.equals( SignalEventCommand.class );
        }

    }

    public static class CompleteWorkItemConverter extends AbstractCollectionConverter
        implements
        Converter {

        public CompleteWorkItemConverter(XStream xstream) {
            super( xstream.getMapper() );
        }

        public void marshal(Object object,
                            HierarchicalStreamWriter writer,
                            MarshallingContext context) {
            CompleteWorkItemCommand cmd = (CompleteWorkItemCommand) object;
            writer.addAttribute( "id",
                                 Long.toString( cmd.getWorkItemId() ) );

            for ( Entry<String, Object> entry : cmd.getResults().entrySet() ) {
                writer.startNode( "result" );
                writer.addAttribute( "identifier",
                                     entry.getKey() );
                writeItem( entry.getValue(),
                           context,
                           writer );
                writer.endNode();
            }
        }

        public Object unmarshal(HierarchicalStreamReader reader,
                                UnmarshallingContext context) {
            String id = reader.getAttribute( "id" );

            Map<String, Object> results = new HashMap<String, Object>();
            while ( reader.hasMoreChildren() ) {
                reader.moveDown();
                String identifier = reader.getAttribute( "identifier" );
                reader.moveDown();
                Object value = readItem( reader,
                                         context,
                                         null );
                reader.moveUp();
                results.put( identifier,
                             value );
                reader.moveUp();
            }

            Command cmd = CommandFactory.newCompleteWorkItem(
                    Long.parseLong( id ),
                    results );

            return cmd;
        }

        public boolean canConvert(Class clazz) {
            return clazz.equals( CompleteWorkItemCommand.class );
        }
    }

    public static class AbortWorkItemConverter extends AbstractCollectionConverter
        implements
        Converter {

        public AbortWorkItemConverter(XStream xstream) {
            super( xstream.getMapper() );
        }

        public void marshal(Object object,
                            HierarchicalStreamWriter writer,
                            MarshallingContext context) {
            AbortWorkItemCommand cmd = (AbortWorkItemCommand) object;
            writer.addAttribute( "id",
                                 Long.toString( cmd.getWorkItemId() ) );
        }

        public Object unmarshal(HierarchicalStreamReader reader,
                                UnmarshallingContext context) {
            String id = reader.getAttribute( "id" );
            Command cmd = CommandFactory.newAbortWorkItem( Long.parseLong( id ) );

            return cmd;
        }

        public boolean canConvert(Class clazz) {
            return clazz.equals( AbortWorkItemCommand.class );
        }
    }

    public static class BatchExecutionResultConverter extends AbstractCollectionConverter
        implements
        Converter {

        public BatchExecutionResultConverter(XStream xstream) {
            super( xstream.getMapper() );
        }

        public void marshal(Object object,
                            HierarchicalStreamWriter writer,
                            MarshallingContext context) {
            ExecutionResults result = (ExecutionResults) object;

            Collection<String> identifiers = result.getIdentifiers();
            // this gets sorted, otherwise unit tests will not pass
            if ( SORT_MAPS ) {
                String[] array = identifiers.toArray( new String[identifiers.size()]);
                Arrays.sort(array);
                identifiers = Arrays.asList(array);
            }

            for ( String identifier : identifiers ) {
                writer.startNode( "result" );
                writer.addAttribute( "identifier",
                                     identifier );
                Object value = result.getValue( identifier );
                if ( value instanceof org.kie.api.runtime.rule.QueryResults ) {
                    String name = mapper().serializedClass( FlatQueryResults.class );
                    ExtendedHierarchicalStreamWriterHelper.startNode( writer,
                                                                      name,
                                                                      FlatQueryResults.class );
                    context.convertAnother( value );
                    writer.endNode();
                } else {
                    writeItem( value,
                               context,
                               writer );
                }
                writer.endNode();
            }

            Collection<String> handles = ((ExecutionResultImpl) result).getFactHandles().keySet();
            // this gets sorted, otherwise unit tests will not pass
            if (SORT_MAPS) {
                String[] array = handles.toArray( new String[handles.size()]);
                Arrays.sort(array);
                handles = Arrays.asList(array);
            }

            for ( String identifier : handles ) {
                Object handle = result.getFactHandle( identifier );
                if ( handle instanceof FactHandle ) {
                    writer.startNode( "fact-handle" );
                    writer.addAttribute( "identifier",
                                         identifier );
                    writer.addAttribute( "external-form",
                                         ((FactHandle) handle).toExternalForm() );

                    writer.endNode();
                } else if ( handle instanceof Collection ) {
                    writer.startNode( "fact-handles" );
                    writer.addAttribute( "identifier",
                                         identifier );
                    for ( FactHandle factHandle : (Collection<FactHandle>) handle ) {
                        writer.startNode( "fact-handle" );
                        writer.addAttribute( "external-form",
                                             ((FactHandle) factHandle).toExternalForm() );
                        writer.endNode();
                    }

                    writer.endNode();
                }

            }
        }

        public Object unmarshal(HierarchicalStreamReader reader,
                                UnmarshallingContext context) {
            ExecutionResultImpl result = new ExecutionResultImpl();
            Map results = result.getResults();
            Map facts = result.getFactHandles();

            while ( reader.hasMoreChildren() ) {
                reader.moveDown();
                if ( reader.getNodeName().equals( "result" ) ) {
                    String identifier = reader.getAttribute( "identifier" );
                    reader.moveDown();
                    Object value = readItem( reader,
                                             context,
                                             null );
                    results.put( identifier,
                                 value );
                    reader.moveUp();
                    reader.moveUp();
                } else if ( reader.getNodeName().equals( "fact-handle" ) ) {
                    String identifier = reader.getAttribute( "identifier" );
                    facts.put( identifier,
                               DefaultFactHandle.createFromExternalFormat( reader.getAttribute( "external-form" ) ) );
                } else if ( reader.getNodeName().equals( "fact-handles" ) ) {
                    String identifier = reader.getAttribute( "identifier" );
                    List<FactHandle> list = new ArrayList<FactHandle>();
                    while ( reader.hasMoreChildren() ) {
                        reader.moveDown();
                        list.add( DefaultFactHandle.createFromExternalFormat( reader.getAttribute( "external-form" ) ) );
                        reader.moveUp();
                    }
                    facts.put( identifier,
                               list );
                } else {
                    throw new IllegalArgumentException( "Element '" + reader.getNodeName() + "' is not supported here" );
                }
            }

            return result;
        }

        public boolean canConvert(Class clazz) {
            return ExecutionResults.class.isAssignableFrom( clazz );
        }
    }

    public static class QueryResultsConverter extends AbstractCollectionConverter
        implements
        Converter {

        public QueryResultsConverter(XStream xstream) {
            super( xstream.getMapper() );
        }

        public void marshal(Object object,
                            HierarchicalStreamWriter writer,
                            MarshallingContext context) {
            QueryResults results = (QueryResults) object;

            // write out identifiers
            List<String> originalIds = Arrays.asList( results.getIdentifiers() );
            List<String> actualIds = new ArrayList();
            if ( results instanceof QueryResultsImpl) {
                for ( String identifier : originalIds ) {
                    // we don't want to marshall the query parameters
                    Declaration declr = ((QueryResultsImpl) results).getDeclarations(0).get( identifier );
                    ObjectType objectType = declr.getPattern().getObjectType();
                    if ( objectType instanceof ClassObjectType ) {
                        if ( ((ClassObjectType) objectType).getClassType() == DroolsQuery.class ) {
                            continue;
                        }
                    }
                    actualIds.add( identifier );
                }
            }

            String[] identifiers = actualIds.toArray( new String[actualIds.size()] );

            writer.startNode( "identifiers" );
            for ( int i = 0; i < identifiers.length; i++ ) {
                writer.startNode( "identifier" );
                writer.setValue( identifiers[i] );
                writer.endNode();
            }
            writer.endNode();

            for ( QueryResultsRow result : results ) {
                writer.startNode( "row" );
                for ( int i = 0; i < identifiers.length; i++ ) {
                    Object value = result.get( identifiers[i] );
                    FactHandle factHandle = result.getFactHandle( identifiers[i] );
                    writeItem( value,
                               context,
                               writer );
                    writer.startNode( "fact-handle" );
                    writer.addAttribute( "external-form",
                                         ((FactHandle) factHandle).toExternalForm() );
                    writer.endNode();
                }
                writer.endNode();
            }
        }

        public Object unmarshal(HierarchicalStreamReader reader,
                                UnmarshallingContext context) {
            reader.moveDown();
            List<String> list = new ArrayList<String>();
            while ( reader.hasMoreChildren() ) {
                reader.moveDown();
                list.add( reader.getValue() );
                reader.moveUp();
            }
            reader.moveUp();

            HashMap<String, Integer> identifiers = new HashMap<String, Integer>();
            for ( int i = 0; i < list.size(); i++ ) {
                identifiers.put( list.get( i ),
                                 i );
            }

            ArrayList<ArrayList<Object>> results = new ArrayList();
            ArrayList<ArrayList<FactHandle>> resultHandles = new ArrayList();
            while ( reader.hasMoreChildren() ) {
                reader.moveDown();
                ArrayList objects = new ArrayList();
                ArrayList<FactHandle> handles = new ArrayList();
                while ( reader.hasMoreChildren() ) {
                    reader.moveDown();
                    Object object = readItem( reader,
                                              context,
                                              null );
                    reader.moveUp();

                    reader.moveDown();
                    FactHandle handle = DefaultFactHandle.createFromExternalFormat( reader.getAttribute( "external-form" ) );
                    reader.moveUp();

                    objects.add( object );
                    handles.add( handle );
                }
                results.add( objects );
                resultHandles.add( handles );
                reader.moveUp();
            }

            return new FlatQueryResults( identifiers,
                                         results,
                                         resultHandles );
        }

        public boolean canConvert(Class clazz) {
            return QueryResults.class.isAssignableFrom( clazz );
        }
    }

    public static class AgendaGroupSetFocusConverter extends AbstractCollectionConverter
            implements
            Converter {

        public AgendaGroupSetFocusConverter(XStream xstream) {
            super( xstream.getMapper() );
        }

        public void marshal(Object object,
                            HierarchicalStreamWriter writer,
                            MarshallingContext context) {
            AgendaGroupSetFocusCommand cmd = (AgendaGroupSetFocusCommand) object;

            writer.addAttribute( "name",
                                 cmd.getName() );
        }

        public Object unmarshal(HierarchicalStreamReader reader,
                                UnmarshallingContext context) {
            String name = reader.getAttribute( "name" );

            AgendaGroupSetFocusCommand cmd = new AgendaGroupSetFocusCommand( name );
            return cmd;
        }

        public boolean canConvert(Class clazz) {
            return clazz.equals( AgendaGroupSetFocusCommand.class );
        }
    }

    public static class ClearActivationGroupConverter extends AbstractCollectionConverter
            implements
            Converter {

        public ClearActivationGroupConverter(XStream xstream) {
            super( xstream.getMapper() );
        }

        public void marshal(Object object,
                            HierarchicalStreamWriter writer,
                            MarshallingContext context) {
            ClearActivationGroupCommand cmd = (ClearActivationGroupCommand) object;

            writer.addAttribute( "name",
                                 cmd.getName() );
        }

        public Object unmarshal(HierarchicalStreamReader reader,
                                UnmarshallingContext context) {
            String name = reader.getAttribute( "name" );

            ClearActivationGroupCommand cmd = new ClearActivationGroupCommand( name );
            return cmd;
        }

        public boolean canConvert(Class clazz) {
            return clazz.equals( ClearActivationGroupCommand.class );
        }
    }

    public static class ClearAgendaConverter extends AbstractCollectionConverter
            implements
            Converter {

        public ClearAgendaConverter(XStream xstream) {
            super( xstream.getMapper() );
        }

        public void marshal(Object object,
                            HierarchicalStreamWriter writer,
                            MarshallingContext context) {
            ClearAgendaCommand cmd = (ClearAgendaCommand) object;
        }

        public Object unmarshal(HierarchicalStreamReader reader,
                                UnmarshallingContext context) {
            ClearAgendaCommand cmd = new ClearAgendaCommand( );
            return cmd;
        }

        public boolean canConvert(Class clazz) {
            return clazz.equals( ClearAgendaCommand.class );
        }
    }

    public static class ClearAgendaGroupConverter extends AbstractCollectionConverter
            implements
            Converter {

        public ClearAgendaGroupConverter(XStream xstream) {
            super( xstream.getMapper() );
        }

        public void marshal(Object object,
                            HierarchicalStreamWriter writer,
                            MarshallingContext context) {
            ClearAgendaGroupCommand cmd = (ClearAgendaGroupCommand) object;

            writer.addAttribute( "name",
                                 cmd.getName() );
        }

        public Object unmarshal(HierarchicalStreamReader reader,
                                UnmarshallingContext context) {
            String name = reader.getAttribute( "name" );

            ClearAgendaGroupCommand cmd = new ClearAgendaGroupCommand( name );
            return cmd;
        }

        public boolean canConvert(Class clazz) {
            return clazz.equals( ClearAgendaGroupCommand.class );
        }
    }

    public static class ClearRuleFlowGroupConverter extends AbstractCollectionConverter
            implements
            Converter {

        public ClearRuleFlowGroupConverter(XStream xstream) {
            super( xstream.getMapper() );
        }

        public void marshal(Object object,
                            HierarchicalStreamWriter writer,
                            MarshallingContext context) {
            ClearRuleFlowGroupCommand cmd = (ClearRuleFlowGroupCommand) object;

            writer.addAttribute( "name",
                                 cmd.getName() );
        }

        public Object unmarshal(HierarchicalStreamReader reader,
                                UnmarshallingContext context) {
            String name = reader.getAttribute( "name" );

            ClearRuleFlowGroupCommand cmd = new ClearRuleFlowGroupCommand( name );
            return cmd;
        }

        public boolean canConvert(Class clazz) {
            return clazz.equals( ClearRuleFlowGroupCommand.class );
        }
    }

}
