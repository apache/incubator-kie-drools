/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jbpm.bpmn2.handler;

import java.text.MessageFormat;
import java.util.*;

import org.kie.api.runtime.process.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class is a {@link WorkItemHandler} implementation that is meant to wrap
 * <i>other</i> {@link WorkItemHandler} implementations.
 * 
 * </p>When an exception is thrown by the wrapped {@link WorkItemHandler}
 * instance, it's added to a list of {@link WorkItemExceptionInfo} instances
 * that contain as much information as possible about the exception, the
 * {@link WorkItem} that caused the exception and the {@link ProcessInstance} id
 * of the process in which the exception was thrown.
 * <ul>
 * <li>See the {@link WorkItemExceptionInfo} class for more information.</li>
 * <li>The list of {@link WorkItemExceptionInfo} classes is available via the
 * {@link LoggingTaskHandlerDecorator#getWorkItemExceptionInfoList()} method.</li>
 * </ul>
 * 
 * </p>After the exception info has been saved, this class then logs a message
 * the appropriate information via {@link Logger#warn(String)}. The message
 * logged is configurable: see
 * {@link LoggingTaskHandlerDecorator#setLoggedMessageFormat(String)} for more
 * information.
 * 
 * </p>This class is thread-safe, although it does not take any responsibility
 * for the {@link WorkItemHandler} that it wraps. If you are using this with
 * multiple threads, please make sure the the {@link WorkItemHandler} instance
 * wrapped is also thread-safe.
 */
public class LoggingTaskHandlerDecorator extends AbstractExceptionHandlingTaskHandler {

    private static final Logger logger = LoggerFactory.getLogger(LoggingTaskHandlerDecorator.class);
    private int loggedExceptionsLimit = 100;
    private Queue<WorkItemExceptionInfo> exceptionInfoList = new ArrayDeque<WorkItemExceptionInfo>(loggedExceptionsLimit);

    private String configuredMessage = "{0} thrown when work item {1} ({2}) was {3}ed in process instance {4}.";
    private List<InputParameter> configuredInputList = new ArrayList<InputParameter>();
    private boolean printStackTrace = true;

    /**
     * Constructs an {@link LoggingTaskHandlerDecorator} instance that wraps a
     * created instance of the {@link WorkItemHandler} class given. This
     * instance will only keep the given number of {@link WorkItemExceptionInfo}
     * instances instead of the default 100.
     * 
     * @param originalTaskHandlerClass
     * @param logLimit
     */
    public LoggingTaskHandlerDecorator(Class<? extends WorkItemHandler> originalTaskHandlerClass, int logLimit) {
        super(originalTaskHandlerClass);
        initializeExceptionInfoList(logLimit);
    }

    /**
     * Constructs an {@link LoggingTaskHandlerDecorator} instance that wraps a
     * created instance of the {@link WorkItemHandler} class given. Only
     * information about the last 100 exceptions will be held in the list
     * available from
     * {@link LoggingTaskHandlerDecorator#getWorkItemExceptionInfoList()};
     * 
     * @param originalTaskHandlerClass
     */
    public LoggingTaskHandlerDecorator(Class<? extends WorkItemHandler> originalTaskHandlerClass) {
        super(originalTaskHandlerClass);
    }

    /**
     * Constructs a {@link LoggingTaskHandlerDecorator} instance that wraps the
     * given {@link WorkItemHandler} instance. This instance will only keep a
     * refere
     * 
     * @param originalTaskHandler
     */
    public LoggingTaskHandlerDecorator(WorkItemHandler originalTaskHandler) {
        super(originalTaskHandler);
    }

    /**
     * Sets the {@link MessageFormat} string to be used to format the log
     * messages. If this method is used, it's a good idea to also use the
     * {@link LoggingTaskHandlerDecorator#setLoggedMessageInput(List)} method.
     * 
     * </p>The default {@link MessageFormat} string used is one of the
     * following:
     * 
     * </p>If the {@link WorkItemHandler} is a {@link ServiceTaskHandler} (that
     * is used with <code>&lt;serviceTask&gt;</code> nodes), then the format is:
     * <ul>
     * <code>{0}.{1} threw {2} when {3}ing work item {4} in process instance {5}.</code>
     * </ul>
     * <ol start="0">
     * <li>The name of the interface used for the &lt;serviceTask&gt;</li>
     * <li>The name of the operation used for the &lt;serviceTask&gt;</li>
     * <li>The simple name of the class of the exception thrown</li>
     * <li>"excut" or "abort" depending on the WorkItemHandler method called</li>
     * <li>The work item id</li>
     * <li>The process instance id</li>
     * </ol>
     * 
     * </p>For all other {@link WorkItemHandler} implementations, the format is:
     * <ul>
     * <code>{0} thrown when work item {1} ({2}) was {3}ed in process instance {4}.</code>
     * </ul>
     * where the parameters are the following:
     * <ol start="0">
     * <li>The (simple) class name of the exception</li>
     * <li>The work item id</li>
     * <li>The name of the work item</li>
     * <li>"excut" or "abort" depending on the WorkItemHandler method called</li>
     * <li>The process instance id</li>
     * </ol>
     * 
     * @param logMessageFormat
     *            The format to use for logged messages.
     */
    public synchronized void setLoggedMessageFormat(String logMessageFormat) {
        this.configuredMessage = logMessageFormat;
    }

    /**
     * Sets the list of parameter types used for the log message format that is set in 
     * {@link LoggingTaskHandlerDecorator#setLoggedMessageFormat(String)}. 
     * 
     * </p>The order of the {@link InputParameter} value in the list corresponds to the {@link MessageFormat} number 
     * used in the String given to {@link LoggingTaskHandlerDecorator#setLoggedMessageFormat(String)}. 
     * 
     * </p>See {@link InputParameter} for more information.
     * 
     * @param inputParameterList
     */
    public synchronized void setLoggedMessageInput(List<InputParameter> inputParameterList) {
        this.configuredInputList = inputParameterList;
    }

    public synchronized void setLoggedExceptionInfoListSize(int loggedExceptionInfoListSize) {
        initializeExceptionInfoList(loggedExceptionInfoListSize);
    }

    public synchronized void setPrintStackTrace(boolean printStackTrace) {
        this.printStackTrace = printStackTrace;
    }

    private void initializeExceptionInfoList(int listSize) {
        this.loggedExceptionsLimit = listSize;
        Queue<WorkItemExceptionInfo> newExceptionInfoList = new ArrayDeque<WorkItemExceptionInfo>(loggedExceptionsLimit + 1);
        newExceptionInfoList.addAll(exceptionInfoList);
        this.exceptionInfoList = newExceptionInfoList;
    }

    public synchronized List<WorkItemExceptionInfo> getWorkItemExceptionInfoList() {
        return new ArrayList<WorkItemExceptionInfo>(exceptionInfoList);
    }

    @Override
    public synchronized void handleExecuteException(Throwable cause, WorkItem workItem, WorkItemManager manager) {
        if (exceptionInfoList.size() == this.loggedExceptionsLimit) {
            exceptionInfoList.poll();
        }
        exceptionInfoList.add(new WorkItemExceptionInfo(workItem, cause, true));
        logMessage(true, workItem, cause);
    }

    @Override
    public synchronized void handleAbortException(Throwable cause, WorkItem workItem, WorkItemManager manager) {
        if (exceptionInfoList.size() == this.loggedExceptionsLimit) {
            exceptionInfoList.poll();
        }
        exceptionInfoList.add(new WorkItemExceptionInfo(workItem, cause, false));
        logMessage(false, workItem, cause);
    }

    private void logMessage(boolean onExecute, WorkItem workItem, Throwable cause) {
        String handlerMethodStem = "execut";
        if (!onExecute) {
            handlerMethodStem = "abort";
        }

        if( cause instanceof WorkItemHandlerRuntimeException ) { 
            cause = cause.getCause();
        }
        
        List<String> inputList = new ArrayList<String>();
        if (configuredInputList.isEmpty()) {
            
            if (workItem.getParameter("Interface") != null) {
                configuredMessage = "{0}.{1} threw {2} when {3}ing work item {4} in process instance {5}.";
                inputList.add((String) workItem.getParameter("Interface"));
                inputList.add((String) workItem.getParameter("Operation"));
                inputList.add(cause.getClass().getSimpleName());
                inputList.add(handlerMethodStem);
                inputList.add(String.valueOf(workItem.getId()));
                inputList.add(String.valueOf(workItem.getProcessInstanceId()));

            } else {
                // {0} thrown when work item {1} ({2}) was {3}ed in process instance {4}.
                inputList.add(cause.getClass().getSimpleName());
                inputList.add(String.valueOf(workItem.getId()));
                inputList.add(workItem.getName());
                inputList.add(handlerMethodStem);
                inputList.add(String.valueOf(workItem.getProcessInstanceId()));
            }

        } else {
            for (InputParameter inputType : configuredInputList) {
                switch (inputType) {
                case EXCEPTION_CLASS:
                    inputList.add(cause.getClass().getSimpleName());
                    break;
                case WORK_ITEM_HANDLER_TYPE:
                    inputList.add(getOriginalTaskHandler().getClass().getSimpleName());
                    break;
                case WORK_ITEM_METHOD:
                    inputList.add(onExecute ? "execut" : "abort");
                    break;
                case WORK_ITEM_ID:
                    inputList.add(String.valueOf(workItem.getId()));
                    break;
                case WORK_ITEM_NAME:
                    inputList.add(workItem.getName());
                    break;
                case WORK_ITEM_PARAMETERS:
                    StringBuilder parameters = new StringBuilder();
                    for (String param : workItem.getParameters().keySet()) {
                        parameters.append(param + " : " + workItem.getParameters().get(param) + ", ");
                    }
                    inputList.add(parameters.substring(0, parameters.length() - 2));
                    break;
                case PROCESS_INSTANCE_ID:
                    inputList.add(String.valueOf(workItem.getProcessInstanceId()));
                    break;
                case SERVICE:
                    inputList.add((String) workItem.getParameter("Interface"));
                    break;
                case OPERATION:
                    inputList.add((String) workItem.getParameter("Operation"));
                    break;
                }
            }
        }

        String message = MessageFormat.format(configuredMessage, inputList.toArray());

        if (printStackTrace) {
            logger.warn(message, cause);
        } else {
            logger.warn(message);
        }
    }

    public class WorkItemExceptionInfo {

        private final Throwable cause;
        private final Date timeThrown;
        private final boolean onExecute;

        private final long processInstanceId;
        private final long workItemId;
        private final String workItemName;
        private final Map<String, Object> workItemParameters;

        public WorkItemExceptionInfo(WorkItem workItem, Throwable cause, boolean onExecute) {
            this.timeThrown = new Date();
            this.cause = cause;
            this.onExecute = onExecute;

            this.processInstanceId = workItem.getProcessInstanceId();

            this.workItemId = workItem.getId();
            this.workItemName = workItem.getName();
            this.workItemParameters = Collections.unmodifiableMap(workItem.getParameters());
        }

        public Throwable getException() {
            return cause;
        }

        public Date getTimeThrown() {
            return timeThrown;
        }

        public boolean onExecute() {
            return onExecute;
        }

        public long getProcessInstanceId() {
            return processInstanceId;
        }

        public long getWorkItemId() {
            return workItemId;
        }

        public String getWorkItemName() {
            return workItemName;
        }

        public Map<String, Object> getWorkItemParameters() {
            return workItemParameters;
        }
    }

    /**
     * Type of input parameter that will be used in the {@link MessageFormat} string set in 
     * {@link LoggingTaskHandlerDecorator#setLoggedMessageFormat(String)}.
     * 
     * <p>Work items are referred to in the following table, are {@link WorkItem} instances
     * that were being processed when the exception was thrown. 
     * </p>The following values can be used:<table valign='top'>
     *   <tr>
     * <td><code>WORK_ITEM_ID</code></td>
     * <td>The work item id</td> 
     *   </tr><tr>
     * <td><code>WORK_ITEM_NAME</code></td>
     * <td>The work item name</td> 
     *   </tr><tr>
     * <td><code>WORK_ITEM_METHOD</code></td>
     * <td>Either "execut" (without an 'e') or "abort" depending what was being done with the work item.</td> 
     *   </tr><tr>
     * <td><code>WORK_ITEM_HANDLER_TYPE</code></td>
     * <td>The class name of the {@link WorkItemHandler} implementation.</td>  
     *   </tr><tr>
     * <td><code>WORK_ITEM_PARAMETERS</code></td>
     * <td>A list of the parameters present in the {@link WorkItem}</td> 
     *   </tr><tr>
     * <td><code>SERVICE</code></td>
     * <td>If the work item was being processed as part of a &lt;serviceTask&gt;, then this is the name of the class or service being called. Null otherwise.</td> 
     *   </tr><tr>
     * <td><code>OPERATION</code></td>
     * <td>If the work item was being processed as part of a &lt;serviceTask&gt;, then this is the name of the method or service operation being called. Null otherwise.</td> 
     *   </tr><tr>
     * <td><code>PROCESS_INSTANCE_ID</code></td>
     * <td>The process instance id in which the exception occurred.</td>
     *   </tr><tr>
     * <td><code>EXCEPTION_CLASS</code></td>
     * <td>The class of the exception thrown.</td>
     *   </tr>
     * </table>
     */
    public enum InputParameter {
        WORK_ITEM_ID, WORK_ITEM_NAME, 
        WORK_ITEM_METHOD, WORK_ITEM_HANDLER_TYPE,  
        WORK_ITEM_PARAMETERS, 
        
        SERVICE, OPERATION, 
        
        PROCESS_INSTANCE_ID, EXCEPTION_CLASS;
    }

}
