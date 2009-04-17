package org.drools.runtime.pipeline;

import java.util.Properties;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import net.sf.jxls.reader.ReaderBuilder;
import net.sf.jxls.reader.XLSReader;

import org.drools.ProviderInitializationException;
import org.drools.builder.help.KnowledgeBuilderHelper;
import org.drools.io.ResourceFactory;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.StatelessKnowledgeSession;
import org.milyn.Smooks;

import com.thoughtworks.xstream.XStream;

/**
 * <p>
 * The PipelineFactory and associated classes are there to help with the automation of getting information
 * into and out of Drools, especially when using services, such as JMS, and non pojo data sources. Transformers for 
 * Smooks, JAXB, Xstream and Jxls are povided. Smooks is an ETL tooling and can work with a variety of data sources,
 * JAXB is a Java standard aimed at working with XSDs, while XStream is a simple and fast xml serialisation framework and finally
 * Jxls allows for loading of pojos from an excel decision table. minimal information on these technologies will be provided here
 * and it is expected for the user to consult the relevant user guide for each.
 * </p>
 * 
 * <p>
 * Pipeline is not meant as a replacement for products like the more powerful Camel, but is aimed as a complimentary
 * framework that ultimately can be integrated into more powerful pipeline frameworks. Instead it is a simple framework aimed at
 * the specific Drools use cases.
 * </p>
 * 
 * <p>
 * In Drools a pipeline is a series of stages that operate on and propagate a given payload. Typically this starts with a Pipeline instance which is 
 * responsible for taking the payload, creating a PipelineContext for it and propagating that to the first Receiver stage.  Two types of Pipelines
 * are provided, both requiring a different PipelineContexts. StatefulKnowledgeSessionPipeline and StatelessKnowledgeSessionPipeline. Notice that both
 * factory methods take the relevant session as an argument.
 * </p>
 * <pre>
 * Pipeline pipeline = PipelineFactory.newStatefulKnowledgeSessionPipeline( ksession );
 * pipeline.setReceiver( receiver );
 * </pre> 
 * 
 * <p>
 * A pipeline is then made up of a chain of Stages that can implement both the Emitter and the Receiver interfaces. The Emitter interface means the stage
 * can propagate a payload and the Receiver interface means it can receive a payload. This is why the Pipeline interface only implements Emitter and Stage
 * and not Receiver, as it is the first instance in the chain. The Stage interface allows a custom exception handler to be set on the stage.
 * </p>
 * <pre>
 * Transformer transformer = PipelineFactory.newXStreamFromXmlTransformer( xstream );
 * transformer.setStageExceptionHandler( new StageExceptionHandler() { .... } );
 * </pre>
 * 
 * <p>
 * The Transformer interface above extends both Stage, Emitter and Receiver, other than providing those interface methods as a single type, it's other role
 * is that of a marker interface that indicates the role of the instance that implements it. We have several other marker interfaces such as Expression and
 * Action,  both of which also extend Stage, Emitter and Receiver. One of the stages should be responsible for setting a result value on the PipelineContext. It is the role of the ResultHandler interface, that
 * the user implements that is responsible for executing on these results or simply setting them an object that the user can retrieve them from.
 * </p>
 * <pre>
 * ResultHandler resultHandler = new ResultHandlerImpl();
 * pipeline.insert( factHandle, resultHandler );  
 * System.out.println( resultHandler );
 * ...
 * public class ResultHandlerImpl implements ResultHandler {
 *     Object result;
 *
 *     public void handleResult(Object result) {
 *         this.result = result;
 *     }
 *
 *     public Object getResult() {
 *         return this.result;
 *     }
 * }   
 * </pre>
 * 
 * <p>
 * while the above example shows a simple handler that simply assigns the result to a field that the user can access, it could do more complex work
 * like sending the object as a message.
 * </p>
 * 
 * <p>
 * Pipeline is provides an adapter to insert the payload and internally create the correct PipelineContext. Two types of Pipelines
 * are provided, both requiring a different PipelineContext. StatefulKnowledgeSessionPipeline and StatelessKnowledgeSessionPipeline.
 * Pipeline itself implements both Stage and Emitter, this means it's a Stage in a pipeline and emits the payload to a receiver. It does
 * not implement Receiver itself, as it the start adapter for the pipeline. PipelineFactory provides methods to create both of the two
 * Pipeline. StatefulKnowledgeSessionPipeline is constructed as below, with the receiver set
 * </p>
 * 
 * <p>
 * In general it easier to construct the pipelines in reverse, for example the following one handles loading xml data from disk, 
 * transforming it with xstream and then inserting the object:
 * </p
 * <pre>
 * // Make the results, in this case the FactHandles, available to the user 
 * Action executeResultHandler = PipelineFactory.newExecuteResultHandler();
 *
 * // Insert the transformed object into the session associated with the PipelineContext
 * KnowledgeRuntimeCommand insertStage = PipelineFactory.newStatefulKnowledgeSessionInsert();
 * insertStage.setReceiver( executeResultHandler );
 *       
 * // Create the transformer instance and create the Transformer stage, where we are going from Xml to Pojo.
 * XStream xstream = new XStream();
 * Transformer transformer = PipelineFactory.newXStreamFromXmlTransformer( xstream );
 * transformer.setReceiver( insertStage );
 *
 * // Create the start adapter Pipeline for StatefulKnowledgeSessions
 * Pipeline pipeline = PipelineFactory.newStatefulKnowledgeSessionPipeline( ksession );
 * pipeline.setReceiver( transformer );
 *
 * // Instantiate a simple result handler and load and insert the XML
 * ResultHandlerImpl resultHandler = new ResultHandlerImpl();
 * pipeline.insert( ResourceFactory.newClassPathResource( "path/facts.xml", getClass() ),
 *                  resultHandler );
 * </pre>
 * 
 * <p>
 * While the above example is for loading a resource from disk it is also possible to work from a running messaging service. Drools currently
 * provides a single Service for JMS, called JmsMessenger.  Support for other Services will be added later. Below shows part of a unit test which 
 * illustrates part of the JmsMessenger in action
 * </p>
 * 
 * <pre>
 * // as this is a service, it's more likely the results will be logged or sent as a return message 
 * Action resultHandlerStage = PipelineFactory.newExecuteResultHandler();
 *
 * // Insert the transformed object into the session associated with the PipelineContext
 * KnowledgeRuntimeCommand insertStage = PipelineFactory.newStatefulKnowledgeSessionInsert();
 * insertStage.setReceiver( resultHandlerStage );
 *
 * // Create the transformer instance and create the Transformer stage, where we are going from Xml to Pojo. Jaxb needs an array of the available classes
 * JAXBContext jaxbCtx = KnowledgeBuilderHelper.newJAXBContext( classNames,
 *                                                              kbase );
 * Unmarshaller unmarshaller = jaxbCtx.createUnmarshaller();
 * Transformer transformer = PipelineFactory.newJaxbFromXmlTransformer( unmarshaller );
 * transformer.setReceiver( insertStage );
 *
 * // payloads for JMS arrive in a Message wrapper, we need to unwrap this object
 * Action unwrapObjectStage = PipelineFactory.newJmsUnwrapMessageObject();
 * unwrapObjectStage.setReceiver( transformer );
 *
 * // Create the start adapter Pipeline for StatefulKnowledgeSessions
 * Pipeline pipeline = PipelineFactory.newStatefulKnowledgeSessionPipeline( ksession );
 * pipeline.setReceiver( unwrapObjectStage );
 *
 * // Services, like JmsMessenger take a ResultHandlerFactory implementation, this is because a result handler must be created for each incoming message.
 * ResultHandleFactoryImpl factory = new ResultHandleFactoryImpl();
 * Service messenger = PipelineFactory.newJmsMessenger( pipeline,
 *                                                      props,
 *                                                      destinationName,
 *                                                      factory );
 * messenger.start();
 * </pre>
 * 
 * <p>This api is experimental and thus the classes and the interfaces returned are subject to change.</p>
 */

public class PipelineFactory {

    private static CorePipelineProvider       corePipelineProvider;

    private static JaxbTransformerProvider    jaxbPipelineProvider;

    private static SmooksTransformerProvider  smooksPipelineProvider;

    private static XStreamTransformerProvider xstreamPipelineProvider;

    private static JxlsTransformerProvider    jxlsPipelineProvider;

    private static JmsMessengerProvider       jmsMessengerProvider;

    /**
     * Construct a new Pipeline to be used when interacting with a StatefulKnowledgeSession.
     * It assumes that the default entry point will be used for any insertions.
     * 
     * @param ksession
     * @return
     */
    public static Pipeline newStatefulKnowledgeSessionPipeline(StatefulKnowledgeSession ksession) {
        return getCorePipelineProvider().newStatefulKnowledgeSessionPipeline( ksession );
    }

    /**
     * Construct a new Pipeline to be used when interacting with a StatefulKnowledgeSession. The entry point
     * to be used is specified as s String.
     * 
     * @param ksession
     * @param entryPointName
     * @return
     */
    public static Pipeline newStatefulKnowledgeSessionPipeline(StatefulKnowledgeSession ksession,
                                                               String entryPointName) {
        return getCorePipelineProvider().newStatefulKnowledgeSessionPipeline( ksession,
                                                                              entryPointName );
    }

    /**
     * Construct a new Pipeline to be used when interacting with StatelessKnowledgeSessions.
     * @param ksession
     * @return
     */
    public static Pipeline newStatelessKnowledgeSessionPipeline(StatelessKnowledgeSession ksession) {
        return getCorePipelineProvider().newStatelessKnowledgeSessionPipeline( ksession );
    }
    
    
    public static KnowledgeRuntimeCommand newCommandExecutor() {
        return getCorePipelineProvider().newCommandExecutor();
    }    

    
    public static KnowledgeRuntimeCommand newInsertElementsCommand() {
        return getCorePipelineProvider().newInsertElementsCommand();
    }

    public static KnowledgeRuntimeCommand newInsertObjectCommand() {
        return getCorePipelineProvider().newInsertObjectCommand();
    }    
    
    /**
     * Insert the payload into the StatefulKnowledgeSesssion referenced in the context. This stage
     * expects the returned FactHandles to be stored in a HashMap of the PipelineContext result property.
     * @return
     */
    public static KnowledgeRuntimeCommand newStatefulKnowledgeSessionInsert() {
        return getCorePipelineProvider().newStatefulKnowledgeSessionInsert();
    }

    /**
     * The payload here is expected to be a String and the global will be set on the PipelineContext result property. The propagating
     * object will also be switched to the results.
     * @return
     */
    public static KnowledgeRuntimeCommand newStatefulKnowledgeSessionGetGlobal() {
        return getCorePipelineProvider().newStatefulKnowledgeSessionGetGlobal();
    }

    /**
     * Expects the payload to be a Map<String, Object> which it will iterate and set each global on the StatefulKnowledgeSession
     * .
     * @return
     */
    
    public static KnowledgeRuntimeCommand newStatefulKnowledgeSessionSetGlobal() {
        return getCorePipelineProvider().newStatefulKnowledgeSessionSetGlobal();
    }

    /**
     * Expects the payload to be a FactHandle, the associated insert object will be set on the PipelineContext result property and
     * the result itself will also be propagated.
     * @return
     */
    public static KnowledgeRuntimeCommand newStatefulKnowledgeSessionGetObject() {
        return getCorePipelineProvider().newStatefulKnowledgeSessionGetObject();
    }

    /**
     * Expects the payload to be any object, that object will be set as a global using the given identifier.
     * @param identifier
     * @return
     */
    public static KnowledgeRuntimeCommand newStatefulKnowledgeSessionSetGlobal(String identifier) {
        return getCorePipelineProvider().newStatefulKnowledgeSessionSetGlobal( identifier );
    }

    /**
     * The payload is inserted as a Signal of a given event type.
     * 
     * @param eventType
     * @return
     */
    public static KnowledgeRuntimeCommand newStatefulKnowledgeSessionSignalEvent(String eventType) {
        return getCorePipelineProvider().newStatefulKnowledgeSessionSignalEvent( eventType );
    }

    /**
     * The payload is inserted as a Signal of a given event type for a specific process instance.
     * @param eventType
     * @param id
     * @return
     */
    public static KnowledgeRuntimeCommand newStatefulKnowledgeSessionSignalEvent(String eventType,
                                                                                 long id) {
        return getCorePipelineProvider().newStatefulKnowledgeSessionSignalEvent( eventType,
                                                                                 id );
    }

    /**
     * Start a process of the given id. The payload is expected to be a Map and is used for the process variables.
     * @param id
     * @return
     */
    public static KnowledgeRuntimeCommand newStatefulKnowledgeSessionStartProcess(String id) {
        return getCorePipelineProvider().newStatefulKnowledgeSessionStartProcess( id );
    }

    /**
     * This assigns the propagating payload object to the PipelineContext.result property
     * @return
     */
    public static Action newAssignObjectAsResult() {
        return getCorePipelineProvider().newAssignObjectAsResult();
    }

    /**
     * When inserting into a Pipeline a ResultHandler may be passed. This stage will execute the user implemented class.
     * @return
     */
    public static Action newExecuteResultHandler() {
        return getCorePipelineProvider().newExecuteResultHandler();
    }

    /**
     * Create a new MVEL action
     * @param action
     * @return
     */
    public static Action newMvelAction(String action) {
        return getCorePipelineProvider().newMvelAction( action );
    }

    /**
     * Creat a new MVEL expression, the payload will be set to the results of the expression.
     * @param expression
     * @return
     */
    public static Expression newMvelExpression(String expression) {
        return getCorePipelineProvider().newMvelExpression( expression );
    }

    /**
     * Iterates the Iterable object and propagate each element in turn.
     * @return
     */
    public static Splitter newIterateSplitter() {
        return getCorePipelineProvider().newIterateSplitter();
    }

    /**
     * Collect each propagated item into a list, this is used as part of a Splitter.
     * The Join should be set on the splitter using the setJoin method, this allows the Splitter
     * to signal the join when it has propagated all the elements of the Iterable object.
     * @return
     */
    public static Join newListCollectJoin() {
        return getCorePipelineProvider().newListCollectJoin();
    }

    /**
     * Creates a new JmsMessenger which runs as a service in it's own thread. It expects an existing JNDI entry for "ConnectionFactory"
     * Which will be used to create the MessageConsumer which will feed into the specified pipeline.
     * 
     * <pre>
     * // as this is a service, it's more likely the results will be logged or sent as a return message 
     * Action resultHandlerStage = PipelineFactory.newExecuteResultHandler();
     *
     * // Insert the transformed object into the session associated with the PipelineContext
     * KnowledgeRuntimeCommand insertStage = PipelineFactory.newStatefulKnowledgeSessionInsert();
     * insertStage.setReceiver( resultHandlerStage );
     *
     * // Create the transformer instance and create the Transformer stage, where we are going from Xml to Pojo. Jaxb needs an array of the available classes
     * JAXBContext jaxbCtx = KnowledgeBuilderHelper.newJAXBContext( classNames,
     *                                                              kbase );
     * Unmarshaller unmarshaller = jaxbCtx.createUnmarshaller();
     * Transformer transformer = PipelineFactory.newJaxbFromXmlTransformer( unmarshaller );
     * transformer.setReceiver( insertStage );
     *
     * // payloads for JMS arrive in a Message wrapper, we need to unwrap this object
     * Action unwrapObjectStage = PipelineFactory.newJmsUnwrapMessageObject();
     * unwrapObjectStage.setReceiver( transformer );
     *
     * // Create the start adapter Pipeline for StatefulKnowledgeSessions
     * Pipeline pipeline = PipelineFactory.newStatefulKnowledgeSessionPipeline( ksession );
     * pipeline.setReceiver( unwrapObjectStage );
     *
     * // Services, like JmsMessenger take a ResultHandlerFactory implementation, this is because a result handler must be created for each incoming message.
     * ResultHandleFactoryImpl factory = new ResultHandleFactoryImpl();
     * Service messenger = PipelineFactory.newJmsMessenger( pipeline,
     *                                                      props,
     *                                                      destinationName,
     *                                                      factory );
     * messenger.start();
     * </pre>
     * @param pipeline
     * @param properties
     * @param destinationName
     * @param resultHandlerFactory
     * @return
     */
    public static Service newJmsMessenger(Pipeline pipeline,
                                          Properties properties,
                                          String destinationName,
                                          ResultHandlerFactory resultHandlerFactory) {
        return getJmsMessengerProvider().newJmsMessenger( pipeline,
                                                          properties,
                                                          destinationName,
                                                          resultHandlerFactory );
    }

    /**
     * Unwrap the payload from the JMS Message and propagate it as the payload object.
     * @return
     */
    public static Action newJmsUnwrapMessageObject() {
        return getJmsMessengerProvider().newJmsUnwrapMessageObject();
    }

    /**
     * <p>
     * Transforms from Source to Pojo using Smooks, the resulting pojo is set as the propagating object. 
     * </p>
     * 
     * <pre>
     * // Instantiate Smooks with the config...
     * Smooks smooks = new Smooks( getClass().getResourceAsStream( "smooks-config.xml" ) );
     *
     * Transformer transformer = PipelineFactory.newSmooksFromSourceTransformer( smooks,
     *                                                                           "orderItem" );
     * transformer.setReceiver( insertStage );
     * </pre>
     * 
     * @param smooks
     * @param rootId
     * @return
     */
    public static Transformer newSmooksFromSourceTransformer(Smooks smooks,
                                                             String rootId) {
        return getSmooksPipelineProvider().newSmooksFromSourceTransformer( smooks,
                                                                           rootId );
    }

    /**
     * <p>
     * Transforms from Pojo to Source using Smooks, the resulting Source is set as the propagating object
     * </p>
     * 
     * <pre>
     * // Instantiate Smooks with the config...
     * Smooks smooks = new Smooks( getClass().getResourceAsStream( "smooks-config.xml" ) );
     *
     * Transformer transformer = PipelineFactory.newSmooksToSourceTransformer( smooks );
     * transformer.setReceiver( receiver );
     * </pre>
     * 
     * @param smooks
     * @param rootId
     * @return
     */    
    public static Transformer newSmooksToSourceTransformer(Smooks smooks) {
        return getSmooksPipelineProvider().newSmooksToSourceTransformer( smooks );
    }

    /**
     * Transforms from XML to Pojo using JAXB, the resulting pojo is set as the propagating object. 
     * 
     * <pre>
     * JAXBContext jaxbCtx = KnowledgeBuilderHelper.newJAXBContext( classNames,
     *                                                                kbase );
     * Unmarshaller unmarshaller = jaxbCtx.createUnmarshaller();
     * Transformer transformer = PipelineFactory.newJaxbFromXmlTransformer( unmarshaller );
     * transformer.setReceiver( receiver );
     * </pre>
     *
     * Don't forget the XSD model must be generated, using XJC at runtime into the KnowledgeBase first,
     * Using KnowledgeBuilderHelper.addXsdModel:
     * <pre>
     * Options xjcOpts = new Options();
     * xjcOpts.setSchemaLanguage( Language.XMLSCHEMA );
     * KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
     *
     * String[] classNames = KnowledgeBuilderHelper.addXsdModel( ResourceFactory.newClassPathResource( "order.xsd",
     *                                                                                                 getClass() ),
     *                                                           kbuilder,
     *                                                           xjcOpts,
     *                                                           "xsd" );
     * </pre>
     * @param unmarshaller
     * @return
     */
    public static Transformer newJaxbFromXmlTransformer(Unmarshaller unmarshaller) {
        return getJaxbPipelineProvider().newJaxbFromXmlTransformer( unmarshaller );
    }

    /**
     * Transforms from Pojo to XML using JAXB, the resulting XML is set as the propagating object. 
     * <pre>
     *  JAXBContext jaxbCtx = KnowledgeBuilderHelper.newJAXBContext( classNames,
     *                                                               kbase );
     *  Unmarshaller unmarshaller = jaxbCtx.createUnmarshaller();
     *  Transformer transformer = PipelineFactory.newJaxbFromXmlTransformer( unmarshaller );
     *  transformer.setReceiver( receiver );
     *
     * 
     * @param marshaller
     * @return
     */
    public static Transformer newJaxbToXmlTransformer(Marshaller marshaller) {
        return getJaxbPipelineProvider().newJaxbToXmlTransformer( marshaller );
    }

    /**
     * <p>
     * Transforms from XML to Pojo using XStream, the resulting Pojo is set as the propagating object. 
     * </p>
     * 
     * <pre>
     * Marshaller marshaller = jaxbCtx.createMarshaller();
     * Transformer transformer = PipelineFactory.newJaxbToXmlTransformer( marshaller );
     * transformer.setReceiver( receiver );
     * </pre>
     * 
     * <p>
     * The BatchExecutionHelper factory provides a pre-configured XStream instance used for marshalling Commands, specifically
     * the BatchExecutionCommand and the ExecutionResults. It also contains docs on the xml formant and on how to use the pipeline 
     * for marshalling BatchExecutionCommand and ExecutionResults.
     * </p>
     * 
     * @param xstream
     * @return
     */
    public static Transformer newXStreamFromXmlTransformer(XStream xstream) {
        return getXStreamTransformerProvider().newXStreamFromXmlTransformer( xstream );
    }

    /**
     * <p>
     * Transforms from Pojo to XML using XStream, the resulting XML is set as the propagating object. 
     * </p>
     * 
     * <pre>
     * XStream xstream = new XStream();
     * Transformer transformer = PipelineFactory.newXStreamToXmlTransformer( xstream );
     * transformer.setReceiver( receiver );
     * </pre>
     * 
     * <p>
     * The BatchExecutionHelper factory provides a pre-configured XStream instance used for marshalling Commands, specifically
     * the BatchExecutionCommand and the ExecutionResults. It also contains docs on the xml formant and on how to use the pipeline 
     * for marshalling BatchExecutionCommand and ExecutionResults.
     * </p>
     * @param xstream
     * @return
     */    
    public static Transformer newXStreamToXmlTransformer(XStream xstream) {
        return getXStreamTransformerProvider().newXStreamToXmlTransformer( xstream );
    }

    /**
     * Transforms from an Excel spread to a Map of pojos pojos using jXLS, the resulting map is set as the propagating object.
     * You may need to use splitters and MVEL expressions to split up the transformation to insert individual pojos.
     * 
     * Note you must provde an XLSReader, which references the mapping file and also an MVEL string which will instantiate the map.
     * The mvel expression is pre-compiled but executedon each usage of the transformation.
     * 
     * <pre>
     * XLSReader mainReader = ReaderBuilder.buildFromXML( ResourceFactory.newClassPathResource( "departments.xml", getClass() ).getInputStream() );
     * Transformer transformer = PipelineFactory.newJxlsTransformer(mainReader, "[ 'departments' : new java.util.ArrayList(), 'company' : new org.drools.runtime.pipeline.impl.Company() ]");
     * </pre>
     * 
     * @param xlsReader
     * @param text
     * @return
     */
    public static Transformer newJxlsTransformer(XLSReader xlsReader,
                                                 String text) {
        return getJxlsTransformerProvider().newJxlsTransformer( xlsReader,
                                                                text );
    }

    private static synchronized void setCorePipelineProvider(CorePipelineProvider provider) {
        PipelineFactory.corePipelineProvider = provider;
    }

    private static synchronized CorePipelineProvider getCorePipelineProvider() {
        if ( corePipelineProvider == null ) {
            loadCorePipelineProvider();
        }
        return corePipelineProvider;
    }

    private static void loadCorePipelineProvider() {
        try {
            Class<CorePipelineProvider> cls = (Class<CorePipelineProvider>) Class.forName( "org.drools.runtime.pipeline.impl.CorePipelineProviderImpl" );
            setCorePipelineProvider( cls.newInstance() );
        } catch ( Exception e2 ) {
            throw new ProviderInitializationException( "org.drools.runtime.pipeline.impl.CorePipelineProviderImpl could not be set.",
                                                       e2 );
        }
    }

    private static synchronized void setJaxbTransformerProvider(JaxbTransformerProvider provider) {
        PipelineFactory.jaxbPipelineProvider = provider;
    }

    private static synchronized JaxbTransformerProvider getJaxbPipelineProvider() {
        if ( jaxbPipelineProvider == null ) {
            loadJaxbTransformerProvider();
        }
        return jaxbPipelineProvider;
    }

    private static void loadJaxbTransformerProvider() {
        try {
            Class<JaxbTransformerProvider> cls = (Class<JaxbTransformerProvider>) Class.forName( "org.drools.runtime.pipeline.impl.JaxbTransformerProviderImpl" );
            setJaxbTransformerProvider( cls.newInstance() );
        } catch ( Exception e2 ) {
            throw new ProviderInitializationException( "Provider org.drools.runtime.pipeline.impl.JaxbTransformerProviderImpl could not be set.",
                                                       e2 );
        }
    }

    private static synchronized void setSmooksTransformerProvider(SmooksTransformerProvider provider) {
        PipelineFactory.smooksPipelineProvider = provider;
    }

    private static synchronized SmooksTransformerProvider getSmooksPipelineProvider() {
        if ( smooksPipelineProvider == null ) {
            loadSmooksTransformerProvider();
        }
        return smooksPipelineProvider;
    }

    private static void loadSmooksTransformerProvider() {
        try {
            Class<SmooksTransformerProvider> cls = (Class<SmooksTransformerProvider>) Class.forName( "org.drools.runtime.pipeline.impl.SmooksTransformerProviderImpl" );
            setSmooksTransformerProvider( cls.newInstance() );
        } catch ( Exception e2 ) {
            throw new ProviderInitializationException( "Provider org.drools.runtime.pipeline.impl.SmooksTransformerProviderImpl could not be set.",
                                                       e2 );
        }
    }

    private static synchronized void setXStreamTransformerProvider(XStreamTransformerProvider provider) {
        PipelineFactory.xstreamPipelineProvider = provider;
    }

    private static synchronized XStreamTransformerProvider getXStreamTransformerProvider() {
        if ( xstreamPipelineProvider == null ) {
            loadXStreamTransformerProvider();
        }
        return xstreamPipelineProvider;
    }

    private static void loadXStreamTransformerProvider() {
        try {
            Class<XStreamTransformerProvider> cls = (Class<XStreamTransformerProvider>) Class.forName( "org.drools.runtime.pipeline.impl.XStreamTransformerProviderImpl" );
            setXStreamTransformerProvider( cls.newInstance() );
        } catch ( Exception e2 ) {
            throw new ProviderInitializationException( "Provider org.drools.runtime.pipeline.impl.XStreamTransformerProviderImpl could not be set.",
                                                       e2 );
        }
    }

    private static synchronized void setJxlsTransformerProvider(JxlsTransformerProvider provider) {
        PipelineFactory.jxlsPipelineProvider = provider;
    }

    private static synchronized JxlsTransformerProvider getJxlsTransformerProvider() {
        if ( jxlsPipelineProvider == null ) {
            loadJxlsTransformerProvider();
        }
        return jxlsPipelineProvider;
    }

    private static void loadJxlsTransformerProvider() {
        try {
            Class<JxlsTransformerProvider> cls = (Class<JxlsTransformerProvider>) Class.forName( "org.drools.runtime.pipeline.impl.JxlsTransformer$JxlsTransformerProviderImpl" );
            setJxlsTransformerProvider( cls.newInstance() );
        } catch ( Exception e2 ) {
            throw new ProviderInitializationException( "Provider org.drools.runtime.pipeline.impl.JxlsTransformer$JxlsTransformerProviderImpl could not be set.",
                                                       e2 );
        }
    }

    private static synchronized void setJmsMessengerProvider(JmsMessengerProvider provider) {
        PipelineFactory.jmsMessengerProvider = provider;
    }

    private static synchronized JmsMessengerProvider getJmsMessengerProvider() {
        if ( jmsMessengerProvider == null ) {
            loadJmsMessengerProvider();
        }
        return jmsMessengerProvider;
    }

    private static void loadJmsMessengerProvider() {
        try {
            Class<JmsMessengerProvider> cls = (Class<JmsMessengerProvider>) Class.forName( "org.drools.runtime.pipeline.impl.JmsMessengerProviderImpl" );
            setJmsMessengerProvider( cls.newInstance() );
        } catch ( Exception e2 ) {
            throw new ProviderInitializationException( "Provider org.drools.runtime.pipeline.impl.JmsMessengerProviderImpl could not be set.",
                                                       e2 );
        }
    }

}
