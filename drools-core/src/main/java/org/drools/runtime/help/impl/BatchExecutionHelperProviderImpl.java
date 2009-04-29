package org.drools.runtime.help.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.drools.base.ClassObjectType;
import org.drools.base.DroolsQuery;
import org.drools.command.Command;
import org.drools.command.CommandFactory;
import org.drools.command.Setter;
import org.drools.common.DisconnectedFactHandle;
import org.drools.common.InternalFactHandle;
import org.drools.process.command.CompleteWorkItemCommand;
import org.drools.process.command.FireAllRulesCommand;
import org.drools.process.command.GetGlobalCommand;
import org.drools.process.command.GetObjectCommand;
import org.drools.process.command.GetObjectsCommand;
import org.drools.process.command.InsertElementsCommand;
import org.drools.process.command.InsertObjectCommand;
import org.drools.process.command.ModifyCommand;
import org.drools.process.command.QueryCommand;
import org.drools.process.command.RetractCommand;
import org.drools.process.command.SetGlobalCommand;
import org.drools.process.command.SignalEventCommand;
import org.drools.process.command.StartProcessCommand;
import org.drools.process.command.ModifyCommand.SetterImpl;
import org.drools.rule.Declaration;
import org.drools.runtime.ExecutionResults;
import org.drools.runtime.help.BatchExecutionHelperProvider;
import org.drools.runtime.impl.BatchExecutionImpl;
import org.drools.runtime.impl.BatchExecutionResultImpl;
import org.drools.runtime.rule.FactHandle;
import org.drools.runtime.rule.QueryResults;
import org.drools.runtime.rule.QueryResultsRow;
import org.drools.runtime.rule.impl.FlatQueryResults;
import org.drools.runtime.rule.impl.NativeQueryResults;
import org.drools.spi.ObjectType;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.converters.collections.AbstractCollectionConverter;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.mapper.Mapper;

public class BatchExecutionHelperProviderImpl
    implements
    BatchExecutionHelperProvider {

    public XStream newXStreamMarshaller() {
        ElementNames names = new XmlElementNames();
        // ElementNames names = new JsonElementNames();

        // XStream xstream = new XStream( new JettisonMappedXmlDriver() );
        XStream xstream = new XStream();
        // xstream.setMode( XStream.NO_REFERENCES );
        xstream.processAnnotations( BatchExecutionImpl.class );
        xstream.addImplicitCollection( BatchExecutionImpl.class,
                                       "commands" );

        xstream.alias( "batch-execution",
                       BatchExecutionImpl.class );
        xstream.alias( "insert",
                       InsertObjectCommand.class );
        xstream.alias( "modify",
                       ModifyCommand.class );
        xstream.alias( "retract",
                       RetractCommand.class );
        xstream.alias( "insert-elements",
                       InsertElementsCommand.class );
        xstream.alias( "start-process",
                       StartProcessCommand.class );
        xstream.alias( "signal-event",
                       SignalEventCommand.class );
        xstream.alias( "complete-work-item",
                       CompleteWorkItemCommand.class );
        xstream.alias( "set-global",
                       SetGlobalCommand.class );
        xstream.alias( "get-global",
                       GetGlobalCommand.class );
        xstream.alias( "get-object",
                       GetObjectCommand.class );
        xstream.alias( "get-objects",
                       GetObjectsCommand.class );
        xstream.alias( "execution-results",
                       BatchExecutionResultImpl.class );
        xstream.alias( "fire-all-rules",
                       FireAllRulesCommand.class );
        xstream.alias( "query",
                       QueryCommand.class );
        xstream.alias( "query-results",
                       FlatQueryResults.class );
        xstream.alias( "query-results",
                       NativeQueryResults.class );

        xstream.registerConverter( new InsertConverter( xstream.getMapper() ) );
        xstream.registerConverter( new RetractConverter( xstream.getMapper() ) );
        xstream.registerConverter( new ModifyConverter( xstream.getMapper() ) );
        xstream.registerConverter( new GetObjectConverter( xstream.getMapper() ) );
        xstream.registerConverter( new InsertElementsConverter( xstream.getMapper() ) );
        xstream.registerConverter( new FireAllRulesConverter( xstream.getMapper() ) );
        xstream.registerConverter( new StartProcessConvert( xstream.getMapper() ) );
        xstream.registerConverter( new SignalEventConverter( xstream.getMapper() ) );
        xstream.registerConverter( new CompleteWorkItemConverter( xstream.getMapper() ) );
        xstream.registerConverter( new QueryConverter( xstream.getMapper() ) );
        xstream.registerConverter( new SetGlobalConverter( xstream.getMapper() ) );
        xstream.registerConverter( new GetGlobalConverter( xstream.getMapper() ) );
        xstream.registerConverter( new GetObjectsConverter( xstream.getMapper() ) );
        xstream.registerConverter( new BatchExecutionResultConverter( xstream.getMapper() ) );
        xstream.registerConverter( new QueryResultsConverter( xstream.getMapper() ) );

        return xstream;
    }

    public static interface ElementNames {
        public String getIn();

        public String getInOut();

        public String getOut();
    }

    public static class JsonElementNames
        implements
        ElementNames {
        private String in    = "in";
        private String inOut = "inOut";
        private String out   = "out";

        public String getIn() {
            return in;
        }

        public String getInOut() {
            return inOut;
        }

        public String getOut() {
            return out;
        }
    }

    public static class XmlElementNames
        implements
        ElementNames {
        private String in    = "in";
        private String inOut = "in-out";
        private String out   = "out";

        public String getIn() {
            return in;
        }

        public String getInOut() {
            return inOut;
        }

        public String getOut() {
            return out;
        }
    }

    public static class InsertConverter extends AbstractCollectionConverter
        implements
        Converter {

        public InsertConverter(Mapper mapper) {
            super( mapper );
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

            }
            writeItem( cmd.getObject(),
                       context,
                       writer );
        }

        public Object unmarshal(HierarchicalStreamReader reader,
                                UnmarshallingContext context) {
            String identifierOut = reader.getAttribute( "out-identifier" );
            String returnObject = reader.getAttribute( "return-object" );

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
            return cmd;
        }

        public boolean canConvert(Class clazz) {
            return clazz.equals( InsertObjectCommand.class );
        }

    }

    public static class ModifyConverter extends AbstractCollectionConverter
        implements
        Converter {

        public ModifyConverter(Mapper mapper) {
            super( mapper );
        }

        public void marshal(Object object,
                            HierarchicalStreamWriter writer,
                            MarshallingContext context) {
            ModifyCommand cmd = (ModifyCommand) object;

            writer.addAttribute( "factHandle",
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
            FactHandle factHandle = new DisconnectedFactHandle( reader.getAttribute( "factHandle" ) );

            List<Setter> setters = new ArrayList();
            while ( reader.hasMoreChildren() ) {
                reader.moveDown();
                Setter setter = CommandFactory.newSetter( reader.getAttribute( "accessor" ),
                                                          reader.getAttribute( "value" ) );
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

    public static class RetractConverter extends AbstractCollectionConverter
        implements
        Converter {

        public RetractConverter(Mapper mapper) {
            super( mapper );
        }

        public void marshal(Object object,
                            HierarchicalStreamWriter writer,
                            MarshallingContext context) {
            RetractCommand cmd = (RetractCommand) object;

            writer.addAttribute( "factHandle",
                                 cmd.getFactHandle().toExternalForm() );
        }

        public Object unmarshal(HierarchicalStreamReader reader,
                                UnmarshallingContext context) {
            FactHandle factHandle = new DisconnectedFactHandle( reader.getAttribute( "factHandle" ) );

            Command cmd = CommandFactory.newRetract( factHandle );

            return cmd;
        }

        public boolean canConvert(Class clazz) {
            return clazz.equals( RetractCommand.class );
        }
    }

    public static class InsertElementsConverter extends AbstractCollectionConverter
        implements
        Converter {

        public InsertElementsConverter(Mapper mapper) {
            super( mapper );
        }

        public void marshal(Object object,
                            HierarchicalStreamWriter writer,
                            MarshallingContext context) {
            InsertElementsCommand cmd = (InsertElementsCommand) object;

            if ( cmd.getOutIdentifier() != null ) {
                writer.addAttribute( "out-identifier",
                                     cmd.getOutIdentifier() );

                writer.addAttribute( "return-object",
                                     Boolean.toString( cmd.isReturnObject() ) );

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
            String returnObject = reader.getAttribute( "return-object" );

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
            return cmd;
        }

        public boolean canConvert(Class clazz) {
            return clazz.equals( InsertElementsCommand.class );
        }

    }

    public static class SetGlobalConverter extends AbstractCollectionConverter
        implements
        Converter {

        public SetGlobalConverter(Mapper mapper) {
            super( mapper );
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
            } else if ( cmd.isOut() ) {
                writer.addAttribute( "out",
                                     Boolean.toString( cmd.isOut() ) );
            }

            writeItem( cmd.getObject(),
                       context,
                       writer );
        }

        public Object unmarshal(HierarchicalStreamReader reader,
                                UnmarshallingContext context) {
            String identifier = reader.getAttribute( "identifier" );
            String out = reader.getAttribute( "out" );
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
            } else if ( out != null ) {
                cmd.setOut( Boolean.parseBoolean( out ) );
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

        public GetObjectConverter(Mapper mapper) {
            super( mapper );
        }

        public void marshal(Object object,
                            HierarchicalStreamWriter writer,
                            MarshallingContext context) {
            GetObjectCommand cmd = (GetObjectCommand) object;

            writer.addAttribute( "factHandle",
                                 cmd.getFactHandle().toExternalForm() );

            if ( cmd.getOutIdentifier() != null ) {
                writer.addAttribute( "out-identifier",
                                     cmd.getOutIdentifier() );
            }
        }

        public Object unmarshal(HierarchicalStreamReader reader,
                                UnmarshallingContext context) {
            FactHandle factHandle = new DisconnectedFactHandle( reader.getAttribute( "factHandle" ) );
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

        public GetGlobalConverter(Mapper mapper) {
            super( mapper );
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

        public GetObjectsConverter(Mapper mapper) {
            super( mapper );
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

        public FireAllRulesConverter(Mapper mapper) {
            super( mapper );
        }

        public void marshal(Object object,
                            HierarchicalStreamWriter writer,
                            MarshallingContext context) {
            FireAllRulesCommand cmd = (FireAllRulesCommand) object;

            if ( cmd.getMax() != -1 ) {
                writer.addAttribute( "max",
                                     Integer.toString( cmd.getMax() ) );
            }
        }

        public Object unmarshal(HierarchicalStreamReader reader,
                                UnmarshallingContext context) {
            String max = reader.getAttribute( "max" );

            FireAllRulesCommand cmd = null;

            if ( max != null ) {
                cmd = new FireAllRulesCommand( Integer.parseInt( max ) );
            } else {
                cmd = new FireAllRulesCommand();
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

        public QueryConverter(Mapper mapper) {
            super( mapper );
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

        public StartProcessConvert(Mapper mapper) {
            super( mapper );
        }

        public void marshal(Object object,
                            HierarchicalStreamWriter writer,
                            MarshallingContext context) {
            StartProcessCommand cmd = (StartProcessCommand) object;
            writer.addAttribute( "processId",
                                 cmd.getProcessId() );

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

            Map<String, Object> params = new HashMap<String, Object>();
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

            return cmd;
        }

        public boolean canConvert(Class clazz) {
            return clazz.equals( StartProcessCommand.class );
        }
    }

    public static class SignalEventConverter extends AbstractCollectionConverter
        implements
        Converter {

        public SignalEventConverter(Mapper mapper) {
            super( mapper );
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

        public CompleteWorkItemConverter(Mapper mapper) {
            super( mapper );
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

            Command cmd = CommandFactory.newCompleteWorkItem(Long.parseLong( id ), results);

            return cmd;
        }

        public boolean canConvert(Class clazz) {
            return clazz.equals( CompleteWorkItemCommand.class );
        }
    }

    public static class BatchExecutionResultConverter extends AbstractCollectionConverter
        implements
        Converter {

        public BatchExecutionResultConverter(Mapper mapper) {
            super( mapper );
        }

        public void marshal(Object object,
                            HierarchicalStreamWriter writer,
                            MarshallingContext context) {
            ExecutionResults result = (ExecutionResults) object;
            for ( String identifier : result.getIdentifiers() ) {
                writer.startNode( "result" );
                writer.addAttribute( "identifier",
                                     identifier );
                Object value = result.getValue( identifier );
                writeItem( value,
                           context,
                           writer );
                writer.endNode();
            }

            for ( String identifier : ((BatchExecutionResultImpl) result).getFacts().keySet() ) {
                writer.startNode( "fact-handle" );
                writer.addAttribute( "identifier",
                                     identifier );
                writer.addAttribute( "externalForm",
                                     ((InternalFactHandle) result.getFactHandle( identifier )).toExternalForm() );
                writer.endNode();
            }
        }

        public Object unmarshal(HierarchicalStreamReader reader,
                                UnmarshallingContext context) {
            BatchExecutionResultImpl result = new BatchExecutionResultImpl();
            Map results = result.getResults();
            Map facts = result.getFacts();

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
                               new DisconnectedFactHandle( reader.getAttribute( "externalForm" ) ) );
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

        public QueryResultsConverter(Mapper mapper) {
            super( mapper );
        }

        public void marshal(Object object,
                            HierarchicalStreamWriter writer,
                            MarshallingContext context) {
            QueryResults results = (QueryResults) object;

            // write out identifiers
            List<String> originalIds = Arrays.asList( results.getIdentifiers() );
            List<String> actualIds = new ArrayList();
            if ( results instanceof NativeQueryResults ) {
                for ( String identifier : originalIds ) {
                    // we don't want to marshall the query parameters
                    Declaration declr = ((NativeQueryResults) results).getDeclarations().get( identifier );
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
                    writeItem( value,
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
                list.add( reader.getValue() );
                reader.moveUp();
            }
            reader.moveUp();

            Map<String, Integer> identifiers = new HashMap<String, Integer>();
            for ( int i = 0; i < list.size(); i++ ) {
                identifiers.put( list.get( i ),
                                 i );
            }

            List results = new ArrayList();
            while ( reader.hasMoreChildren() ) {
                reader.moveDown();
                List objects = new ArrayList();
                while ( reader.hasMoreChildren() ) {
                    reader.moveDown();
                    Object object = readItem( reader,
                                              context,
                                              null );
                    objects.add( object );
                    reader.moveUp();
                }
                results.add( objects );
                reader.moveUp();
            }

            return new FlatQueryResults( identifiers,
                                         results );
        }

        public boolean canConvert(Class clazz) {
            return QueryResults.class.isAssignableFrom( clazz );
        }
    }
}
