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

package org.drools.runtime.help.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.drools.command.Command;
import org.drools.command.CommandFactory;
import org.drools.command.Setter;
import org.drools.command.impl.GenericCommand;
import org.drools.command.runtime.BatchExecutionCommandImpl;
import org.drools.command.runtime.GetGlobalCommand;
import org.drools.command.runtime.SetGlobalCommand;
import org.drools.command.runtime.process.AbortWorkItemCommand;
import org.drools.command.runtime.process.CompleteWorkItemCommand;
import org.drools.command.runtime.process.SignalEventCommand;
import org.drools.command.runtime.process.StartProcessCommand;
import org.drools.command.runtime.rule.FireAllRulesCommand;
import org.drools.command.runtime.rule.GetObjectCommand;
import org.drools.command.runtime.rule.GetObjectsCommand;
import org.drools.command.runtime.rule.InsertElementsCommand;
import org.drools.command.runtime.rule.InsertObjectCommand;
import org.drools.command.runtime.rule.ModifyCommand;
import org.drools.command.runtime.rule.QueryCommand;
import org.drools.command.runtime.rule.RetractCommand;
import org.drools.common.DefaultFactHandle;
import org.drools.core.util.StringUtils;
import org.drools.runtime.ExecutionResults;
import org.drools.runtime.impl.ExecutionResultImpl;
import org.drools.runtime.rule.FactHandle;
import org.drools.runtime.rule.QueryResults;
import org.drools.runtime.rule.QueryResultsRow;
import org.drools.runtime.rule.impl.FlatQueryResults;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.converters.collections.AbstractCollectionConverter;
import com.thoughtworks.xstream.converters.reflection.ReflectionProvider;
import com.thoughtworks.xstream.core.util.HierarchicalStreams;
import com.thoughtworks.xstream.io.ExtendedHierarchicalStreamWriterHelper;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.io.json.JettisonMappedXmlDriver;
import com.thoughtworks.xstream.mapper.Mapper;

public class XStreamJSon {
    public static XStream newJSonMarshaller() {
        JettisonMappedXmlDriver jet = new JettisonMappedXmlDriver();
        XStream xstream = new XStream( jet );

        XStreamHelper.setAliases( xstream );

        xstream.alias( "commands",
                       CommandsObjectContainer.class );
        xstream.alias( "objects",
                       ObjectsObjectContainer.class );
        xstream.alias( "item",
                       RowItemContainer.class );
        xstream.alias( "parameters",
                       ParameterContainer.class );
        xstream.alias( "results",
                       WorkItemResultsContainer.class );

        xstream.setMode( XStream.NO_REFERENCES );

        xstream.registerConverter( new JSonFactHandleConverter( xstream ) );
        xstream.registerConverter( new JSonBatchExecutionResultConverter( xstream ) );
        xstream.registerConverter( new JSonInsertConverter( xstream ) );
        xstream.registerConverter( new JSonFireAllRulesConverter( xstream ) );
        xstream.registerConverter( new JSonBatchExecutionCommandConverter( xstream ) );
        xstream.registerConverter( new CommandsContainerConverter( xstream ) );
        xstream.registerConverter( new JSonGetObjectConverter( xstream ) );
        xstream.registerConverter( new JSonRetractConverter( xstream ) );
        xstream.registerConverter( new JSonModifyConverter( xstream ) );
        xstream.registerConverter( new JSonSetGlobalConverter( xstream ) );
        xstream.registerConverter( new JSonInsertElementsConverter( xstream ) );
        xstream.registerConverter( new JSonGetGlobalConverter( xstream ) );
        xstream.registerConverter( new JSonGetObjectsConverter( xstream ) );
        xstream.registerConverter( new JSonQueryConverter( xstream ) );
        xstream.registerConverter( new JSonQueryResultsConverter( xstream ) );
        xstream.registerConverter( new RowItemConverter( xstream ) );
        xstream.registerConverter( new JSonStartProcessConvert( xstream ) );
        xstream.registerConverter( new JSonSignalEventConverter( xstream ) );
        xstream.registerConverter( new JSonCompleteWorkItemConverter( xstream ) );
        xstream.registerConverter( new JSonAbortWorkItemConverter( xstream ) );

        return xstream;
    }

    public static class CommandsContainerConverter extends AbstractCollectionConverter {
        public CommandsContainerConverter(XStream xstream) {
            super( xstream.getMapper() );
        }

        public boolean canConvert(Class type) {
            return CommandsObjectContainer.class.isAssignableFrom( type );

        }

        @Override
        public void marshal(Object object,
                            HierarchicalStreamWriter writer,
                            MarshallingContext context) {
            CommandsObjectContainer container = (CommandsObjectContainer) object;

            writeItem( container.getContainedObject(),
                       context,
                       writer );
        }

        @Override
        public Object unmarshal(HierarchicalStreamReader reader,
                                UnmarshallingContext context) {
            throw new UnsupportedOperationException();
        }

    }

    public static class RowItemConverter extends AbstractCollectionConverter {
        public RowItemConverter(XStream xstream) {
            super( xstream.getMapper() );
        }

        public boolean canConvert(Class type) {
            return RowItemContainer.class.isAssignableFrom( type );

        }

        @Override
        public void marshal(Object object,
                            HierarchicalStreamWriter writer,
                            MarshallingContext context) {
            RowItemContainer container = (RowItemContainer) object;

            writer.startNode( "external-form" );
            writer.setValue( container.getFactHandle().toExternalForm() );
            writer.endNode();

            writer.startNode( "object" );
            writeItem( container.getObject(),
                       context,
                       writer );
            writer.endNode();
        }

        @Override
        public Object unmarshal(HierarchicalStreamReader reader,
                                UnmarshallingContext context) {
            String externalForm = null;
            Object object = null;
            while ( reader.hasMoreChildren() ) {
                reader.moveDown();
                String nodeName = reader.getNodeName();
                if ( "external-form".equals( nodeName ) ) {
                    externalForm = reader.getValue();
                } else if ( "object".equals( nodeName ) ) {
                    reader.moveDown();
                    object = readItem( reader,
                                       context,
                                       null );
                    reader.moveUp();
                }
                reader.moveUp();
            }
            return new RowItemContainer( new DefaultFactHandle( externalForm ),
                                         object );
        }

    }

    //    public static class ModifyEntriesContainerConverter extends AbstractCollectionConverter {
    //        public ModifyEntriesContainerConverter(Mapper mapper) {
    //            super( mapper );
    //        }
    //
    //        public boolean canConvert(Class type) {
    //            return ModifyEntriesContainer.class.isAssignableFrom( type );
    //
    //        }
    //
    //        @Override
    //        public void marshal(Object object,
    //                            HierarchicalStreamWriter writer,
    //                            MarshallingContext context) {
    //            ModifyEntriesContainer container = (ModifyEntriesContainer) object;
    //
    //            writer.startNode( "accessor" );
    //            writer.setValue( container.getAccessor() );
    //            writer.endNode();
    //            
    //            writer.startNode( "value" );
    //            writer.setValue( container.getValue() );
    //            writer.endNode();
    //        }
    //
    //        @Override
    //        public Object unmarshal(HierarchicalStreamReader reader,
    //                                UnmarshallingContext context) {
    //            throw new UnsupportedOperationException();
    //        }
    //
    //    }

    public static class JSonBatchExecutionCommandConverter extends AbstractCollectionConverter
        implements
        Converter {

        public JSonBatchExecutionCommandConverter(XStream xstream) {
            super( xstream.getMapper() );
        }

        public void marshal(Object object,
                            HierarchicalStreamWriter writer,
                            MarshallingContext context) {
            BatchExecutionCommandImpl cmds = (BatchExecutionCommandImpl) object;
            if ( cmds.getLookup() != null ) {
                writer.startNode( "lookup" );
                writer.setValue( cmds.getLookup() );
                writer.endNode();
            }
            List<GenericCommand< ? >> list = cmds.getCommands();

            for ( GenericCommand cmd : list ) {
                writeItem( new CommandsObjectContainer( cmd ),
                           context,
                           writer );
            }
        }

        public Object unmarshal(HierarchicalStreamReader reader,
                                UnmarshallingContext context) {
            List<GenericCommand< ? >> list = new ArrayList<GenericCommand< ? >>();
            String lookup = null;
            while ( reader.hasMoreChildren() ) {
                reader.moveDown();
                if ( "commands".equals( reader.getNodeName() ) ) {
                    while ( reader.hasMoreChildren() ) {
                        reader.moveDown();
                        GenericCommand cmd = (GenericCommand) readItem( reader,
                                                                        context,
                                                                        null );
                        list.add( cmd );
                        reader.moveUp();
                    }
                } else if ( "lookup".equals( reader.getNodeName() ) ) {
                    lookup = reader.getValue();
                } else {
                    throw new IllegalArgumentException( "batch-execution does not support the child element name=''" + reader.getNodeName() + "' value=" + reader.getValue() + "'" );
                }
                reader.moveUp();
            }
            return new BatchExecutionCommandImpl( list,
                                              lookup );
        }

        public boolean canConvert(Class clazz) {
            return clazz.equals( BatchExecutionCommandImpl.class );
        }
    }

    public static class JSonInsertConverter extends BaseConverter
        implements
        Converter {

        public JSonInsertConverter(XStream xstream) {
            super( xstream );
        }

        public void marshal(Object object,
                            HierarchicalStreamWriter writer,
                            MarshallingContext context) {
            InsertObjectCommand cmd = (InsertObjectCommand) object;
            if ( cmd.getOutIdentifier() != null ) {
                writer.startNode( "out-identifier" );
                writer.setValue( cmd.getOutIdentifier() );
                writer.endNode();

                writer.startNode( "return-object" );
                writer.setValue( Boolean.toString( cmd.isReturnObject() ) );
                writer.endNode();
            }
            
            if ( !StringUtils.isEmpty( cmd.getEntryPoint() ) ) {
                writer.startNode( "entry-point" );
                writer.setValue(  cmd.getEntryPoint() );
                writer.endNode();
            }
            writeValue( writer,
                        context,
                        "object",
                        cmd.getObject() );
        }

        public Object unmarshal(HierarchicalStreamReader reader,
                                UnmarshallingContext context) {
            InsertObjectCommand cmd = new InsertObjectCommand();
            while ( reader.hasMoreChildren() ) {
                reader.moveDown();
                String nodeName = reader.getNodeName();
                if ( "out-identifier".equals( nodeName ) ) {
                    cmd.setOutIdentifier( reader.getValue() );
                } else if ( "return-object".equals( nodeName ) ) {
                    cmd.setReturnObject( Boolean.parseBoolean( reader.getValue() ) );
                } else if ( "object".equals( nodeName ) ) {
                    cmd.setObject( readValue( reader,
                                              context,
                                              cmd.getObject(),
                                              "object" ) );
                } else if ( "entry-point".equals( nodeName ) ) {
                    cmd.setEntryPoint( reader.getValue() );
                }
                reader.moveUp();

            }
            return cmd;
        }

        public boolean canConvert(Class clazz) {
            return clazz.equals( InsertObjectCommand.class );
        }

    }

    public static class JSonFactHandleConverter extends AbstractCollectionConverter
        implements
        Converter {
        public JSonFactHandleConverter(XStream xstream) {
            super( xstream.getMapper() );
        }

        public boolean canConvert(Class aClass) {
            return FactHandle.class.isAssignableFrom( aClass );
        }

        public void marshal(Object object,
                            HierarchicalStreamWriter writer,
                            MarshallingContext marshallingContext) {
            FactHandle fh = (FactHandle) object;
            writer.startNode( "external-form" );
            writer.setValue( fh.toExternalForm() );
            writer.endNode();
        }

        public Object unmarshal(HierarchicalStreamReader reader,
                                UnmarshallingContext unmarshallingContext) {
            reader.moveDown();
            DefaultFactHandle factHandle = new DefaultFactHandle( reader.getValue() );
            reader.moveUp();
            return factHandle;
        }
    }

    public static class JSonFireAllRulesConverter extends AbstractCollectionConverter
        implements
        Converter {

        public JSonFireAllRulesConverter(XStream xstream) {
            super( xstream.getMapper() );
        }

        public void marshal(Object object,
                            HierarchicalStreamWriter writer,
                            MarshallingContext context) {
            FireAllRulesCommand cmd = (FireAllRulesCommand) object;

            if ( cmd.getMax() != -1 ) {
                writer.startNode( "max" );
                writer.setValue( Integer.toString( cmd.getMax() ) );
                writer.endNode();
            }
            
            if ( cmd.getOutIdentifier() != null ) {
                writer.startNode( "out-identifier" );
                writer.setValue( cmd.getOutIdentifier() );
                writer.endNode();
            }
        }

        public Object unmarshal(HierarchicalStreamReader reader,
                                UnmarshallingContext context) {
            String max = null;
            String outIdentifier = null;
            while ( reader.hasMoreChildren() ) {
                reader.moveDown();
                if ( "max".equals( reader.getNodeName() ) ) {
                    max = reader.getValue();
                } else if ( "out-identifier".equals( reader.getNodeName() ) ) {
                    outIdentifier = reader.getValue();
                } else {
                    throw new IllegalArgumentException( "fire-all-rules does not support the child element name=''" + reader.getNodeName() + "' value=" + reader.getValue() + "'" );
                }
                reader.moveUp();
            }

            FireAllRulesCommand cmd = null;

            if ( max != null ) {
                cmd = new FireAllRulesCommand( Integer.parseInt( max ) );
            } else {
                cmd = new FireAllRulesCommand();
            }
            if ( outIdentifier != null ) {
                cmd.setOutIdentifier(outIdentifier);
            }
            return cmd;
        }

        public boolean canConvert(Class clazz) {
            return clazz.equals( FireAllRulesCommand.class );
        }
    }

    public static class JSonGetObjectConverter extends AbstractCollectionConverter
        implements
        Converter {

        public JSonGetObjectConverter(XStream xstream) {
            super( xstream.getMapper() );
        }

        public void marshal(Object object,
                            HierarchicalStreamWriter writer,
                            MarshallingContext context) {
            GetObjectCommand cmd = (GetObjectCommand) object;
            writer.startNode( "fact-handle" );
            writer.setValue( cmd.getFactHandle().toExternalForm() );
            writer.endNode();

            if ( cmd.getOutIdentifier() != null ) {
                writer.startNode( "out-identifier" );
                writer.setValue( cmd.getOutIdentifier() );
                writer.endNode();
            }
        }

        public Object unmarshal(HierarchicalStreamReader reader,
                                UnmarshallingContext context) {
            FactHandle factHandle = null;
            String outIdentifier = null;
            while ( reader.hasMoreChildren() ) {
                reader.moveDown();
                String name = reader.getNodeName();
                if ( "fact-handle".equals( name ) ) {
                    factHandle = new DefaultFactHandle( reader.getValue() );
                } else if ( "out-identifier".equals( "out-identifier" ) ) {
                    outIdentifier = reader.getValue();
                }
                reader.moveUp();
            }

            GetObjectCommand cmd = new GetObjectCommand( factHandle );
            if ( outIdentifier != null ) {
                cmd.setOutIdentifier( outIdentifier );
            }
            return cmd;
        }

        public boolean canConvert(Class clazz) {
            return clazz.equals( GetObjectCommand.class );
        }
    }

    public static class JSonRetractConverter extends AbstractCollectionConverter
        implements
        Converter {

        public JSonRetractConverter(XStream xstream) {
            super( xstream.getMapper() );
        }

        public void marshal(Object object,
                            HierarchicalStreamWriter writer,
                            MarshallingContext context) {
            RetractCommand cmd = (RetractCommand) object;
            writer.startNode( "fact-handle" );
            writer.setValue( cmd.getFactHandle().toExternalForm() );
            writer.endNode();
        }

        public Object unmarshal(HierarchicalStreamReader reader,
                                UnmarshallingContext context) {
            reader.moveDown();
            FactHandle factHandle = new DefaultFactHandle( reader.getValue() );
            reader.moveUp();

            Command cmd = CommandFactory.newRetract( factHandle );

            return cmd;
        }

        public boolean canConvert(Class clazz) {
            return clazz.equals( RetractCommand.class );
        }
    }

    public static class JSonModifyConverter extends AbstractCollectionConverter
        implements
        Converter {

        public JSonModifyConverter(XStream xstream) {
            super( xstream.getMapper() );
        }

        public void marshal(Object object,
                            HierarchicalStreamWriter writer,
                            MarshallingContext context) {
            ModifyCommand cmd = (ModifyCommand) object;

            writer.startNode( "fact-handle" );
            writer.setValue( cmd.getFactHandle().toExternalForm() );
            writer.endNode();

            List<Setter> setters = cmd.getSetters();
            for ( Setter setter : setters ) {
                writeItem( setter,
                           context,
                           writer );

            }
        }

        public Object unmarshal(HierarchicalStreamReader reader,
                                UnmarshallingContext context) {
            reader.moveDown();
            FactHandle factHandle = new DefaultFactHandle( reader.getValue() );
            reader.moveUp();

            List<Setter> setters = new ArrayList();
            while ( reader.hasMoreChildren() ) {
                reader.moveDown();

                reader.moveDown();
                String accessor = reader.getValue();
                reader.moveUp();

                reader.moveDown();
                String value = reader.getValue();
                reader.moveUp();

                Setter setter = CommandFactory.newSetter( accessor,
                                                          value );
                setters.add( setter );

                reader.moveUp();
            }

            Command cmd = CommandFactory.newModify( factHandle,
                                                    setters );
            return cmd;
        }

        public boolean canConvert(Class clazz) {
            return clazz.equals( ModifyCommand.class );
        }

    }

    public static class JSonInsertElementsConverter extends AbstractCollectionConverter
        implements
        Converter {

        public JSonInsertElementsConverter(XStream xstream) {
            super( xstream.getMapper() );
        }

        public void marshal(Object object,
                            HierarchicalStreamWriter writer,
                            MarshallingContext context) {
            InsertElementsCommand cmd = (InsertElementsCommand) object;

            if ( cmd.getOutIdentifier() != null ) {
                writer.startNode( "out-identifier" );
                writer.setValue( cmd.getOutIdentifier() );
                writer.endNode();

                writer.startNode( "return-objects" );
                writer.setValue( Boolean.toString( cmd.isReturnObject() ) );
                writer.endNode();

            }
            if ( !StringUtils.isEmpty( cmd.getEntryPoint() ) ) {
                writer.startNode( "entry-point" );
                writer.setValue(  cmd.getEntryPoint() );
                writer.endNode();
            }

            for ( Object element : cmd.getObjects() ) {
                writeItem( new ObjectsObjectContainer( element ),
                           context,
                           writer );
            }
        }

        public Object unmarshal(HierarchicalStreamReader reader,
                                UnmarshallingContext context) {
            List objects = new ArrayList();
            String outIdentifier = null;
            String returnObjects = null;
            String entryPoint = null;
            while ( reader.hasMoreChildren() ) {
                reader.moveDown();
                String nodeName = reader.getNodeName();
                if ( "objects".equals( nodeName ) ) {
                    while ( reader.hasMoreChildren() ) {
                        reader.moveDown();
                        Object o = readItem( reader,
                                             context,
                                             null );
                        objects.add( o );
                        reader.moveUp();
                    }
                } else if ( "out-identifier".equals( nodeName ) ) {
                    outIdentifier = reader.getValue();
                } else if ( "return-objects".equals( nodeName ) ) {
                    returnObjects = reader.getValue();
                } else if ( "entry-point".equals( nodeName ) ) {
                    entryPoint = reader.getValue();
                } else {
                    throw new IllegalArgumentException( "insert-elements does not support the child element name=''" + reader.getNodeName() + "' value=" + reader.getValue() + "'" );
                }
                reader.moveUp();
            }
            InsertElementsCommand cmd = new InsertElementsCommand( objects );
            if ( outIdentifier != null ) {
                cmd.setOutIdentifier( outIdentifier );
                if ( outIdentifier != null ) {
                    cmd.setReturnObject( Boolean.parseBoolean( returnObjects ) );
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

    public static class JSonBatchExecutionResultConverter extends AbstractCollectionConverter
        implements
        Converter {

        public JSonBatchExecutionResultConverter(XStream xstream) {
            super( xstream.getMapper() );
        }

        public void marshal(Object object,
                            HierarchicalStreamWriter writer,
                            MarshallingContext context) {
            ExecutionResults result = (ExecutionResults) object;
            writer.startNode( "results" );
            if ( !result.getIdentifiers().isEmpty() ) {
                for ( String identifier : result.getIdentifiers() ) {
                    writer.startNode( "result" );

                    writer.startNode( "identifier" );
                    writer.setValue( identifier );
                    writer.endNode();

                    writer.startNode( "value" );
                    Object value = result.getValue( identifier );
                    if ( value instanceof org.drools.runtime.rule.QueryResults ) {
                        String name = mapper().serializedClass(FlatQueryResults.class);
                        ExtendedHierarchicalStreamWriterHelper.startNode(writer, name, FlatQueryResults.class);
                        context.convertAnother(value);
                        writer.endNode();
                    } else {
                        writeItem( value,
                                   context,
                                   writer );
                    }
                    writer.endNode();

                    writer.endNode();
                }
            }

            for ( String identifier : ((ExecutionResultImpl) result).getFactHandles().keySet() ) {
                Object handle = result.getFactHandle( identifier );
                if ( handle instanceof FactHandle ) {
                    writer.startNode( "fact-handle" );

                    writer.startNode( "identifier" );
                    writer.setValue( identifier );
                    writer.endNode();

                    writer.startNode( "external-form" );
                    writer.setValue( ((FactHandle) handle).toExternalForm() );
                    writer.endNode();

                    writer.endNode();
                } else if ( handle instanceof Collection ) {
                    writer.startNode( "fact-handles" );

                    writer.startNode( "identifier" );
                    writer.setValue( identifier );
                    writer.endNode();

                    //writer.startNode( "xxx" );
                    for ( FactHandle factHandle : (Collection<FactHandle>) handle ) {
                        writeItem( factHandle.toExternalForm(),
                                   context,
                                   writer );
                    }
                    //writer.endNode();

                    writer.endNode();
                }
            }

            writer.endNode();
        }

        public Object unmarshal(HierarchicalStreamReader reader,
                                UnmarshallingContext context) {
            ExecutionResultImpl result = new ExecutionResultImpl();
            Map results = result.getResults();
            Map facts = result.getFactHandles();

            reader.moveDown();
            if ( "results".equals( reader.getNodeName() ) ) {
                while ( reader.hasMoreChildren() ) {
                    reader.moveDown();

                    if ( reader.getNodeName().equals( "result" ) ) {
                        reader.moveDown();
                        String identifier = reader.getValue();
                        reader.moveUp();

                        reader.moveDown();
                        reader.moveDown();
                        Object value = readItem( reader,
                                                 context,
                                                 null );
                        results.put( identifier,
                                     value );
                        reader.moveUp();
                        reader.moveUp();
                    } else if ( reader.getNodeName().equals( "fact-handle" ) ) {
                        reader.moveDown();
                        String identifier = reader.getValue();
                        reader.moveUp();

                        reader.moveDown();
                        String externalForm = reader.getValue();
                        reader.moveUp();

                        facts.put( identifier,
                                   new DefaultFactHandle( externalForm ) );
                    } else if ( reader.getNodeName().equals( "fact-handles" ) ) {
                        List list = new ArrayList();
                        String identifier = null;
                        while ( reader.hasMoreChildren() ) {
                            reader.moveDown();
                            identifier = reader.getValue();
                            reader.moveUp();
                            while ( reader.hasMoreChildren() ) {
                                reader.moveDown();
                                FactHandle factHandle = new DefaultFactHandle( (String) readItem( reader,
                                                                                                       context,
                                                                                                       null ) );
                                list.add( factHandle );
                                reader.moveUp();
                            }
                        }
                        facts.put( identifier,
                                   list );
                    } else {
                        throw new IllegalArgumentException( "Element '" + reader.getNodeName() + "' is not supported here" );
                    }
                    reader.moveUp();
                }
            } else {
                throw new IllegalArgumentException( "Element '" + reader.getNodeName() + "' is not supported here" );
            }
            reader.moveUp();

            return result;
        }

        public boolean canConvert(Class clazz) {
            return ExecutionResults.class.isAssignableFrom( clazz );
        }
    }

    public static abstract class BaseConverter {
        protected Mapper             mapper;
        protected ReflectionProvider reflectionProvider;

        public BaseConverter(XStream xstream) {
            this.mapper = xstream.getMapper();
            this.reflectionProvider = xstream.getReflectionProvider();
        }

        protected void writeValue(HierarchicalStreamWriter writer,
                                  MarshallingContext context,
                                  String fieldName,
                                  Object object) {
            writer.startNode( fieldName );
            String name = this.mapper.serializedClass( object.getClass() );
            ExtendedHierarchicalStreamWriterHelper.startNode( writer,
                                                              name,
                                                              Mapper.Null.class );
            context.convertAnother( object );
            writer.endNode();
            writer.endNode();
        }

        protected Object readValue(HierarchicalStreamReader reader,
                                   UnmarshallingContext context,
                                   Object object,
                                   Object fieldName) {
            reader.moveDown();
            Class type = HierarchicalStreams.readClassType( reader,
                                                            this.mapper );
            Object o = context.convertAnother( null,
                                               type );

            reader.moveUp();
            return o;
        }
    }

    public static class JSonSetGlobalConverter extends BaseConverter
        implements
        Converter {

        public JSonSetGlobalConverter(XStream xstream) {
            super( xstream );
        }

        public void marshal(Object object,
                            HierarchicalStreamWriter writer,
                            MarshallingContext context) {
            SetGlobalCommand cmd = (SetGlobalCommand) object;

            writer.startNode( "identifier" );
            writer.setValue( cmd.getIdentifier() );
            writer.endNode();

            if ( cmd.getOutIdentifier() != null ) {
                writer.startNode( "out-identifier" );
                writer.setValue( cmd.getOutIdentifier() );
                writer.endNode();
            } 
            writeValue( writer,
                        context,
                        "object",
                        cmd.getObject() );

        }

        public Object unmarshal(HierarchicalStreamReader reader,
                                UnmarshallingContext context) {
            String identifier = null;
            boolean out = false;
            String outIdentifier = null;
            Object object = null;
            SetGlobalCommand cmd = new SetGlobalCommand();

            while ( reader.hasMoreChildren() ) {
                reader.moveDown();
                String nodeName = reader.getNodeName();
                if ( "identifier".equals( nodeName ) ) {
                    identifier = reader.getValue();
                } else if ( "out".equals( nodeName ) ) {
                    out = Boolean.valueOf(reader.getValue());
                } else if ( "out-identifier".equals( nodeName ) ) {
                    outIdentifier = reader.getValue();
                } else if ( "object".equals( nodeName ) ) {
                    cmd.setObject( readValue( reader,
                                              context,
                                              cmd.getObject(),
                                              "object" ) );
                }
                reader.moveUp();
            }

            cmd.setIdentifier( identifier );

            if ( outIdentifier != null ) {
                cmd.setOutIdentifier( outIdentifier );
            } else if (out) {
                cmd.setOutIdentifier( identifier );
            }
            return cmd;
        }

        public boolean canConvert(Class clazz) {
            return clazz.equals( SetGlobalCommand.class );
        }

    }

    public static class JSonGetGlobalConverter extends BaseConverter
        implements
        Converter {

        public JSonGetGlobalConverter(XStream xstream) {
            super( xstream );
        }

        public void marshal(Object object,
                            HierarchicalStreamWriter writer,
                            MarshallingContext context) {
            GetGlobalCommand cmd = (GetGlobalCommand) object;

            writer.startNode( "identifier" );
            writer.setValue( cmd.getIdentifier() );
            writer.endNode();

            if ( cmd.getOutIdentifier() != null ) {
                writer.startNode( "out-identifier" );
                writer.setValue( cmd.getOutIdentifier() );
                writer.endNode();
            }

        }

        public Object unmarshal(HierarchicalStreamReader reader,
                                UnmarshallingContext context) {
            String identifier = null;
            String outIdentifier = null;
            GetGlobalCommand cmd = new GetGlobalCommand();

            while ( reader.hasMoreChildren() ) {
                reader.moveDown();
                String nodeName = reader.getNodeName();
                if ( "identifier".equals( nodeName ) ) {
                    identifier = reader.getValue();
                } else if ( "out-identifier".equals( nodeName ) ) {
                    outIdentifier = reader.getValue();
                }
                reader.moveUp();
            }

            cmd.setIdentifier( identifier );

            if ( outIdentifier != null ) {
                cmd.setOutIdentifier( outIdentifier );
            }
            return cmd;
        }

        public boolean canConvert(Class clazz) {
            return clazz.equals( GetGlobalCommand.class );
        }
    }

    public static class JSonGetObjectsConverter extends AbstractCollectionConverter
        implements
        Converter {

        public JSonGetObjectsConverter(XStream xstream) {
            super( xstream.getMapper() );
        }

        public void marshal(Object object,
                            HierarchicalStreamWriter writer,
                            MarshallingContext context) {
            GetObjectsCommand cmd = (GetObjectsCommand) object;

            if ( cmd.getOutIdentifier() != null ) {
                writer.startNode( "out-identifier" );
                writer.setValue( cmd.getOutIdentifier() );
                writer.endNode();
            }
        }

        public Object unmarshal(HierarchicalStreamReader reader,
                                UnmarshallingContext context) {
            String outIdentifier = null;
            while ( reader.hasMoreChildren() ) {
                reader.moveDown();
                if ( "out-identifier".equals( reader.getNodeName() ) ) {
                    outIdentifier = reader.getValue();
                }
                reader.moveUp();
            }

            GetObjectsCommand cmd = new GetObjectsCommand();
            if ( outIdentifier != null ) {
                cmd.setOutIdentifier( outIdentifier );
            }
            return cmd;
        }

        public boolean canConvert(Class clazz) {
            return clazz.equals( GetObjectsCommand.class );
        }
    }

    public static class JSonQueryConverter extends AbstractCollectionConverter
        implements
        Converter {

        public JSonQueryConverter(XStream xstream) {
            super( xstream.getMapper() );
        }

        public void marshal(Object object,
                            HierarchicalStreamWriter writer,
                            MarshallingContext context) {
            QueryCommand cmd = (QueryCommand) object;

            writer.startNode( "out-identifier" );
            writer.setValue( cmd.getOutIdentifier() );
            writer.endNode();

            writer.startNode( "name" );
            writer.setValue( cmd.getName() );
            writer.endNode();

            if ( cmd.getArguments() != null && cmd.getArguments().size() > 0 ) {
                writer.startNode( "args" );
                for ( Object arg : cmd.getArguments() ) {
                    writeItem( arg,
                               context,
                               writer );
                }
                writer.endNode();
            }
        }

        public Object unmarshal(HierarchicalStreamReader reader,
                                UnmarshallingContext context) {
            List<String> outs = new ArrayList<String>();

            String outIdentifier = null;
            String name = null;
            List<Object> args = null;
            while ( reader.hasMoreChildren() ) {
                reader.moveDown();
                String nodeName = reader.getNodeName();
                if ( "out-identifier".equals( nodeName ) ) {
                    outIdentifier = reader.getValue();
                } else if ( "name".equals( nodeName ) ) {
                    name = reader.getValue();
                } else if ( "args".equals( nodeName ) ) {
                    args = new ArrayList<Object>();
                    while ( reader.hasMoreChildren() ) {
                        reader.moveDown();
                        Object arg = readItem( reader,
                                               context,
                                               null );
                        args.add( arg );
                        reader.moveUp();
                    }
                }
                reader.moveUp();
            }

            QueryCommand cmd = new QueryCommand( outIdentifier,
                                                 name,
                                                 (args != null) ? args.toArray( new Object[args.size()] ) : null );

            return cmd;
        }

        public boolean canConvert(Class clazz) {
            return clazz.equals( QueryCommand.class );
        }
    }

    public static class JSonQueryResultsConverter extends AbstractCollectionConverter
        implements
        Converter {

        public JSonQueryResultsConverter(XStream xstream) {
            super( xstream.getMapper() );
        }

        public void marshal(Object object,
                            HierarchicalStreamWriter writer,
                            MarshallingContext context) {
            QueryResults results = (QueryResults) object;

            // write out identifiers
            String[] identifiers = results.getIdentifiers();

            writer.startNode( "identifiers" );
            for ( int i = 0; i < identifiers.length; i++ ) {
                writeItem( identifiers[i],
                           context,
                           writer );
            }
            writer.endNode();

            for ( QueryResultsRow result : results ) {
                writer.startNode( "row" );
                for ( int i = 0; i < identifiers.length; i++ ) {
                    Object value = result.get( identifiers[i] );
                    FactHandle factHandle = result.getFactHandle( identifiers[i] );
                    writeItem( new RowItemContainer( factHandle,
                                                     value ),
                               context,
                               writer );
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
                list.add( (String) readItem( reader,
                                             context,
                                             null ) );
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
                    RowItemContainer container = (RowItemContainer) readItem( reader,
                                                                              context,
                                                                              null );

                    objects.add( container.getObject() );
                    handles.add( container.getFactHandle() );
                    reader.moveUp();
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

    public static class JSonStartProcessConvert extends AbstractCollectionConverter
        implements
        Converter {

        public JSonStartProcessConvert(XStream xstream) {
            super( xstream.getMapper() );
        }

        public void marshal(Object object,
                            HierarchicalStreamWriter writer,
                            MarshallingContext context) {
            StartProcessCommand cmd = (StartProcessCommand) object;
            writer.startNode( "process-id" );
            writer.setValue( cmd.getProcessId() );
            writer.endNode();

            for ( Entry<String, Object> entry : cmd.getParameters().entrySet() ) {
                writeItem( new ParameterContainer( entry.getKey(),
                                                   entry.getValue() ),
                           context,
                           writer );
            }
        }

        public Object unmarshal(HierarchicalStreamReader reader,
                                UnmarshallingContext context) {
            reader.moveDown();
            String processId = reader.getValue();
            reader.moveUp();

            HashMap<String, Object> params = new HashMap<String, Object>();
            while ( reader.hasMoreChildren() ) {
                reader.moveDown();
                ParameterContainer parameterContainer = (ParameterContainer) readItem( reader,
                                                                                       context,
                                                                                       null );
                params.put( parameterContainer.getIdentifier(),
                            parameterContainer.getObject() );
                reader.moveUp();
            }

            StartProcessCommand cmd = new StartProcessCommand();
            cmd.setProcessId( processId );
            cmd.setParameters( params );

            return cmd;
        }

        public boolean canConvert(Class clazz) {
            return clazz.equals( StartProcessCommand.class );
        }
    }

    public static class JSonSignalEventConverter extends AbstractCollectionConverter
        implements
        Converter {

        public JSonSignalEventConverter(XStream xstream) {
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
                writer.startNode( "process-instance-id" );
                writer.setValue( Long.toString( processInstanceId ) );
                writer.endNode();
            }

            writer.addAttribute( "event-type",
                                 eventType );

            writer.startNode( "event-type" );
            writer.setValue( eventType );
            writer.endNode();

            writer.startNode( "object" );
            writeItem( event,
                       context,
                       writer );
            writer.endNode();
        }

        public Object unmarshal(HierarchicalStreamReader reader,
                                UnmarshallingContext context) {
            String processInstanceId = null;
            String eventType = null;
            Object event = null;
            while ( reader.hasMoreChildren() ) {
                reader.moveDown();
                String nodeName = reader.getNodeName();
                if ( "process-instance-id".equals( nodeName ) ) {
                    processInstanceId = reader.getValue();
                } else if ( "event-type".equals( nodeName ) ) {
                    eventType = reader.getValue();
                } else if ( "object".equals( nodeName ) ) {
                    reader.moveDown();
                    event = readItem( reader,
                                      context,
                                      null );
                    reader.moveUp();
                }
                reader.moveUp();
            }

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

    public static class JSonCompleteWorkItemConverter extends AbstractCollectionConverter
        implements
        Converter {

        public JSonCompleteWorkItemConverter(XStream xstream) {
            super( xstream.getMapper() );
        }

        public void marshal(Object object,
                            HierarchicalStreamWriter writer,
                            MarshallingContext context) {
            CompleteWorkItemCommand cmd = (CompleteWorkItemCommand) object;

            writer.startNode( "id" );
            writer.setValue( Long.toString( cmd.getWorkItemId() ) );
            writer.endNode();

            for ( Entry<String, Object> entry : cmd.getResults().entrySet() ) {
                writeItem( new WorkItemResultsContainer( entry.getKey(),
                                                         entry.getValue() ),
                           context,
                           writer );
            }
        }

        public Object unmarshal(HierarchicalStreamReader reader,
                                UnmarshallingContext context) {
            String id = null;
            Map<String, Object> results = new HashMap<String, Object>();
            while ( reader.hasMoreChildren() ) {
                reader.moveDown();
                String nodeName = reader.getNodeName();
                if ( "id".equals( nodeName ) ) {
                    id = reader.getValue();
                } else if ( "results".equals( nodeName ) ) {
                    while ( reader.hasMoreChildren() ) {
                        WorkItemResultsContainer res = (WorkItemResultsContainer) readItem( reader,
                                                                                            context,
                                                                                            null );
                        results.put( res.getIdentifier(),
                                     res.getObject() );
                    }
                }
                reader.moveUp();
            }

            return new CompleteWorkItemCommand( Long.parseLong( id ),
                                                results );
        }

        public boolean canConvert(Class clazz) {
            return clazz.equals( CompleteWorkItemCommand.class );
        }
    }

    public static class JSonAbortWorkItemConverter extends AbstractCollectionConverter
        implements
        Converter {

        public JSonAbortWorkItemConverter(XStream xstream) {
            super( xstream.getMapper() );
        }

        public void marshal(Object object,
                            HierarchicalStreamWriter writer,
                            MarshallingContext context) {
            AbortWorkItemCommand cmd = (AbortWorkItemCommand) object;
            writer.startNode( "id" );
            writer.setValue( Long.toString( cmd.getWorkItemId() ) );
            writer.endNode();
        }

        public Object unmarshal(HierarchicalStreamReader reader,
                                UnmarshallingContext context) {
            reader.moveDown();
            String id = reader.getValue();
            reader.moveUp();

            Command cmd = CommandFactory.newAbortWorkItem( Long.parseLong( id ) );

            return cmd;
        }

        public boolean canConvert(Class clazz) {
            return clazz.equals( AbortWorkItemCommand.class );
        }
    }
}
