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

package org.drools.runtime.help;

import com.thoughtworks.xstream.XStream;

/**
 * <p>
 * Provides a configured XStream instance to support the marshalling of BatchExecutions, where the resulting
 * xml can be used as a message format. Configured converters only exist for the commands supported via the 
 * CommandFactory. The user may add other converters for their user objects.
 * </p>
 * 
 * <p>
 * This is very useful for scripting stateless of stateful knowledge sessions, especially when services are involved.
 * </p>
 * 
 * <p>
 * There is current no xsd for schema validation, however we will try to outline the basic format here and the drools-pipeline module
 * has an illustrative unit test in the XStreamBatchExecutionTest unit test. The root element is &lt;batch-execution&gt; and it can contain zero or more
 * commands elements.
 * </p>
 * <pre>
 * &lt;batch-execution&gt;
 * ...
 * &lt;/batch-execution&gt;
 * </pre>
 * 
 * <p>
 * This contains a list of elements that represent commands, the supported commands is limited to those Commands provided by the CommandFactory. The
 * most basic of these is the &lt;insert&gt; element, which inserts objects. The contents of the insert element is the user object, as dictated by XStream.
 * </p>
 * <pre>
 * &lt;batch-execution&gt;
 *     &lt;insert&gt;
 *     ....
 *     &lt;/insert&gt;
 * &lt;/batch-execution&gt;
 * </pre>
 * <p>
 * The insert element supports an 'out-identifier' attribute, this means the inserted object's FactHandle will be returned 
 * and optionally the object itself as part of the <batch-execution-results> payload. To return the object use the attribute 'return-object' which takes a boolean
 * 'true'|'false' value, by default this is true.
 * The 
 * </p>
 * <pre>
 * &lt;batch-execution &gt;
 *     &lt;insert out-identifier='userVar' &gt;
 *     ....
 *     &lt;/insert&gt;
 * &lt;/batch-execution&gt;
 * </pre>
 * 
 * <p>
 * It's also possible to insert a collection of objects using the &lt;insert-elements&gt; element, here each element is inserted in turn. This command also support's an
 * 'out-identifier' attribute which returns the FactHandle's in a Collection, of the same order that the objects where inserted. 'return-object' is also supported to optionally
 * return the inserted objects, again they will be in a collection of the same order they where inserted.
 * The org.domain.UserClass is just an illustrative user object that xstream would serialise. 
 * </p>
 * <pre>
 * &lt;batch-execution&gt;
 *     &lt;insert-elements&gt;
 *         &lt;org.domain.UserClass&gt;
 *         ...
 *         &lt;/org.domain.UserClass&gt;
 *         &lt;org.domain.UserClass&gt;
 *         ...
 *         &lt;/org.domain.UserClass&gt;
 *         &lt;org.domain.UserClass&gt;
 *         ...
 *         &lt;/org.domain.UserClass&gt;
 *     &lt;/insert-elements&gt;
 * &lt;/batch-execution&gt;
 * </pre>
 * 
 * <p>
 * Next there is the &lt;set-global&gt; element, which sets a global for the session. 
 * </p>
 * <pre>
 * &lt;batch-execution&gt;
 *     &lt;set-global identifier='userVar'&gt;
 *         &lt;org.domain.UserClass&gt;
 *         ...
 *         &lt;/org.domain.UserClass&gt;
 *     &lt;/set-global&gt;
 * &lt;/batch-execution&gt;
 * </pre>
 * <p>
 * &lt;set-global&gt; also supports two other optional attributes 'out' and 'out-identifier'. 'out' is a boolean and when set the global will be added to the 
 * &lt;batch-execution-results&g; payload using the name from the 'identifier' attribute. 'out-identifier' works like 'out' but additionally allows you to 
 * override the identifier used in the &lt;batch-execution-results&g; payload.
 * </p>
 * <pre>
 * &lt;batch-execution&gt;
 *     &lt;set-global identifier='userVar1' out='true'&gt;
 *         &lt;org.domain.UserClass&gt;
 *         ...
 *         &lt;/org.domain.UserClass&gt;
 *     &lt;/set-global&gt;
 *     &lt;set-global identifier='userVar2' out-identifier='alternativeUserVar2'&gt;
 *         &lt;org.domain.UserClass&gt;
 *         ...
 *         &lt;/org.domain.UserClass&gt;
 *     &lt;/set-global&gt;
 * &lt;/batch-execution&gt;
 * </pre>
 * <p>
 * There is also a &lt;get-global&gt; element, which has no contents but does support an 'out-identifier' attribute, there is no need for an 'out' attribute
 * as we assume that a &lt;get-global&gt; is always an 'out'.
 * </p>
 * <pre>
 * &lt;batch-execution&gt;
 *     &lt;get-global identifier='userVar1' /&gt;
 *     &lt;get-global identifier='userVar2' out-identifier='alternativeUserVar2'/&gt;
 * &lt;/batch-execution&gt;
 * </pre>
 * 
 * <p>Specific objects can be retrieved using the FactHandle:</p>
 * <pre>
 * &lt;batch-execution&gt;
 *     &lt;get-object out-identifier='outStilton' factHandle='" + factHandle.toExternalForm() + "' /&gt;
 * &lt;/batch-execution&gt;
 * </pre>
 * 
 * <p>
 * While the 'out' attribute is useful in returning specific instances as a result payload, we often wish to run actual querries. Both parameter
 * and parameterless querries are supported. The 'name' attribute is the name of the query to be called, and the 'out-identifier' is the identifier
 * to be used for the query results in the &lt;batch-execution-results&gt; payload.
 * </p>
 * <pre>
 * &lt;batch-execution&gt;
 *     &lt;query out-identifier='cheeses' name='cheeses'/&gt;
 *     &lt;query out-identifier='cheeses2' name='cheesesWithParams'&gt;
 *         &lt;string>stilton&lt;/string&gt;
 *         &lt;string>cheddar&lt;/string&gt;
 *     &lt;/query&gt;;
 * &lt;/batch-execution&gt;
 * </pre>
 * 
 * <p>
 * The &lt;start-process&gt; command is also supported and accepts optional parameters:
 * </p>
 * <pre>
 * &lt;batch-execution&gt;
 *  &lt;startProcess processId='org.drools.actions'&gt;
 *    &lt;parameter identifier='person'&gt;
 *       &lt;org.drools.TestVariable&gt;
 *         &lt;name&gt;John Doe&lt;/name&gt;
 *       &lt;/org.drools.TestVariable&gt;
 *    &lt;/parameter&gt;
 *  &lt;/startProcess&gt;
 * &lt;/batch-execution&gt;
 * </pre>
 * 
 * <p>SignelEvent</p>
 * <pre>
 * &lt;signal-event process-instance-id='1' event-type='MyEvent'&gt;
 *     &lt;string&gt;MyValue&lt;/string&gt;
 * &lt;/signal-event&gt;
 * </pre>
 * 
 * <p>CompleteWorkItem</p>
 * <pre>
 * &lt;complete-work-item id='" + workItem.getId() + "' &gt;
 *    &lt;result identifier='Result'&gt;
 *         &lt;string&gt;SomeOtherString&lt;/string&gt;
 *     &lt;/result&gt;
 * &lt;/complete-work-item>
 * </pre>
 * 
 * <p>AbortWorkItem</p>
 * <pre>
 * &lt;abort-work-item id='21' /&gt;
 * </pre>
 * 
 * <p>
 * Support for more commands will be added over time.
 * </p>
 * 
 * <p>
 * The following is a simple insert batch-execution command:
 * </p>
 * <pre>
 * &lt;batch-execution&gt;
 *   &lt;insert out-identifier='outStilton'&gt;
 *     &lt;org.drools.Cheese&gt;
 *       &lt;type&gt;stilton&lt;/type&gt;
 *       &lt;price&gt;25&lt;/price&gt;
 *       &lt;oldPrice&gt;0&lt;/oldPrice&gt;
 *     &lt;/org.drools.Cheese&gt;
 *   &lt;/insert&gt;
 * &lt;/batch-execution&gt;
 * </pre>
 * 
 * <p>
 * The pipeline can be used to handle this end to end, notice the part where the configured XStream instance is passed "BatchExecutionHelper.newXStreamMarshaller()".
 * This will take a given xml, transform it and then execute it as a BatchExecution Command. Notice the Pipeline also handles the marshalling
 * of the ExecutionResults back out to XML.
 * </p>
 * <pre>
 * Action executeResultHandler = PipelineFactory.newExecuteResultHandler();
 *        
 * Action assignResult = PipelineFactory.newAssignObjectAsResult();
 * assignResult.setReceiver( executeResultHandler );
 *       
 * Transformer outTransformer = PipelineFactory.newXStreamToXmlTransformer( BatchExecutionHelper.newXStreamMarshaller() );
 * outTransformer.setReceiver( assignResult );
 *
 * KnowledgeRuntimeCommand batchExecution = PipelineFactory.newBatchExecutor();
 * batchExecution.setReceiver( outTransformer );
 *
 *
 * Transformer inTransformer = PipelineFactory.newXStreamFromXmlTransformer( BatchExecutionHelper.newXStreamMarshaller() );
 * inTransformer.setReceiver( batchExecution );
 *
 * Pipeline pipeline = PipelineFactory.newStatelessKnowledgeSessionPipeline( ksession );
 * pipeline.setReceiver( inTransformer );
 * </pre>
 * 
 * <p>
 * The results would look like following xml:
 * </p>
 * <pre>
 * &lt;execution-results&gt;
 *   &lt;result identifier='outStilton'&gt;
 *     &lt;org.drools.Cheese&gt;
 *       &lt;type&gt;stilton&lt;/type&gt;
 *       &lt;oldPrice&gt;0&lt;/oldPrice&gt;
 *       &lt;price&gt;30&lt;/price&gt;
 *     &lt;/org.drools.Cheese&gt;
 *   &lt;/result&gt;
 * &lt;/execution-results&gt;
 * </pre>
 * 
 * <p>This api is experimental and thus the classes and the interfaces returned are subject to change.</p>
 *
 */
public class BatchExecutionHelper {
    private static volatile BatchExecutionHelperProvider provider;

    public static XStream newXStreamMarshaller() {
        return getBatchExecutionHelperProvider().newXStreamMarshaller();
    }
    
    public static XStream newJSonMarshaller() {
        return getBatchExecutionHelperProvider().newJSonMarshaller();
    }

    private static synchronized void setBatchExecutionHelperProvider(BatchExecutionHelperProvider provider) {
        BatchExecutionHelper.provider = provider;
    }

    private static synchronized BatchExecutionHelperProvider getBatchExecutionHelperProvider() {
        if ( provider == null ) {
            loadProvider();
        }
        return provider;
    }

    private static void loadProvider() {
        try {
            Class<BatchExecutionHelperProvider> cls = (Class<BatchExecutionHelperProvider>) Class.forName( "org.drools.runtime.help.impl.BatchExecutionHelperProviderImpl" );
            setBatchExecutionHelperProvider( cls.newInstance() );
        } catch ( Exception e2 ) {
            throw new RuntimeException( "Provider org.drools.runtime.help.impl.BatchExecutionHelperProviderImpl could not be set.",
                                                       e2 );
        }
    }
}
