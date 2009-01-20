package org.drools.runtime.pipeline;

import java.util.Properties;

import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import net.sf.jxls.reader.XLSReader;

import org.drools.ProviderInitializationException;
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
 * Pipeline is provides an adapter to insert the payload and internal create the correct PipelineContext. Two types of Pipelines
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
 * See StatefullKnowledgeSessionPipeline, StatelessKnowledgeSessionPipeline for more specific information and capabilities on these pipelines.
 * </p>
 * 
 * <p>
 * While the above example is for loading a resource from disk it is also possible to work from a running messaging service. Drools currently
 * provides a single Service for JMS, called JmsMessenger. Other Services will be added later. Look at the JmsMessenger for more details, but below shows
 * part of a unit test:
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
 */

public class PipelineFactory {

    private static CorePipelineProvider       corePipelineProvider;

    private static JaxbTransformerProvider    jaxbPipelineProvider;

    private static SmooksTransformerProvider  smooksPipelineProvider;

    private static XStreamTransformerProvider xstreamPipelineProvider;

    private static JxlsTransformerProvider    jxlsPipelineProvider;

    private static JmsMessengerProvider       jmsMessengerProvider;

    public static Pipeline newStatefulKnowledgeSessionPipeline(StatefulKnowledgeSession ksession) {
        return getCorePipelineProvider().newStatefulKnowledgeSessionPipeline( ksession );
    }

    public static Pipeline newStatefulKnowledgeSessionPipeline(StatefulKnowledgeSession ksession,
                                                               String entryPointName) {
        return getCorePipelineProvider().newStatefulKnowledgeSessionPipeline( ksession,
                                                                              entryPointName );
    }

    public static Pipeline newStatelessKnowledgeSessionPipeline(StatelessKnowledgeSession ksession) {
        return getCorePipelineProvider().newStatelessKnowledgeSessionPipelineImpl( ksession );
    }

    public static KnowledgeRuntimeCommand newStatefulKnowledgeSessionInsert() {
        return getCorePipelineProvider().newStatefulKnowledgeSessionInsert();
    }

    public static KnowledgeRuntimeCommand newStatelessKnowledgeSessionExecute() {
        return getCorePipelineProvider().newStatelessKnowledgeSessionExecute();
    }

    public static KnowledgeRuntimeCommand newStatefulKnowledgeSessionGetGlobal() {
        return getCorePipelineProvider().newStatefulKnowledgeSessionGetGlobal();
    }

    public static KnowledgeRuntimeCommand newStatefulKnowledgeSessionSetGlobal() {
        return getCorePipelineProvider().newStatefulKnowledgeSessionSetGlobal();
    }

    public static KnowledgeRuntimeCommand newStatefulKnowledgeSessionGetObject() {
        return getCorePipelineProvider().newStatefulKnowledgeSessionGetObject();
    }

    public static KnowledgeRuntimeCommand newStatefulKnowledgeSessionSetGlobal(String identifier) {
        return getCorePipelineProvider().newStatefulKnowledgeSessionSetGlobal( identifier );
    }

    public static KnowledgeRuntimeCommand newStatefulKnowledgeSessionSignalEvent(String eventType) {
        return getCorePipelineProvider().newStatefulKnowledgeSessionSignalEvent( eventType );
    }

    public static KnowledgeRuntimeCommand newStatefulKnowledgeSessionSignalEvent(String eventType,
                                                                                 long id) {
        return getCorePipelineProvider().newStatefulKnowledgeSessionSignalEvent( eventType,
                                                                                 id );
    }

    public static KnowledgeRuntimeCommand newStatefulKnowledgeSessionStartProcess(String eventType) {
        return getCorePipelineProvider().newStatefulKnowledgeSessionStartProcess( eventType );
    }

    public static Action newAssignObjectAsResult() {
        return getCorePipelineProvider().newAssignObjectAsResult();
    }

    public static Action newExecuteResultHandler() {
        return getCorePipelineProvider().newExecuteResultHandler();
    }

    public static Action newMvelAction(String action) {
        return getCorePipelineProvider().newMvelAction( action );
    }

    public static Expression newMvelExpression(String expression) {
        return getCorePipelineProvider().newMvelExpression( expression );
    }

    public static Splitter newIterateSplitter() {
        return getCorePipelineProvider().newIterateSplitter();
    }

    public static Join newListCollectJoin() {
        return getCorePipelineProvider().newListCollectJoin();
    }

    public static Service newJmsMessenger(Pipeline pipeline,
                                          Properties properties,
                                          String destinationName,
                                          ResultHandlerFactory resultHandlerFactory) {
        return getJmsMessengerProvider().newJmsMessenger( pipeline,
                                                          properties,
                                                          destinationName,
                                                          resultHandlerFactory );
    }

    public static Action newJmsUnwrapMessageObject() {
        return getJmsMessengerProvider().newJmsUnwrapMessageObject();
    }

    public static Transformer newSmooksFromSourceTransformer(Smooks smooks,
                                                             String rootId) {
        return getSmooksPipelineProvider().newSmooksFromSourceTransformer( smooks,
                                                                           rootId );
    }

    public static Transformer newSmooksToSourceTransformer(Smooks smooks) {
        return getSmooksPipelineProvider().newSmooksToSourceTransformer( smooks );
    }

    public static Transformer newJaxbFromXmlTransformer(Unmarshaller unmarshaller) {
        return getJaxbPipelineProvider().newJaxbFromXmlTransformer( unmarshaller );
    }

    public static Transformer newJaxbToXmlTransformer(Marshaller marshaller) {
        return getJaxbPipelineProvider().newJaxbToXmlTransformer( marshaller );
    }

    public static Transformer newXStreamFromXmlTransformer(XStream xstream) {
        return getXStreamTransformerProvider().newXStreamFromXmlTransformer( xstream );
    }

    public static Transformer newXStreamToXmlTransformer(XStream xstream) {
        return getXStreamTransformerProvider().newXStreamToXmlTransformer( xstream );
    }

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
