/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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
package org.kie.hacep.consumer;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.function.Consumer;
import java.util.function.Function;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.api.KieServices;
import org.kie.api.builder.ReleaseId;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.EntryPoint;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.api.runtime.rule.QueryResults;
import org.kie.hacep.EnvConfig;
import org.kie.hacep.core.KieSessionContext;
import org.kie.hacep.core.infra.SessionSnapshooter;
import org.kie.hacep.util.ConsumerUtilsCore;
import org.kie.remote.CommonConfig;
import org.kie.remote.DroolsExecutor;
import org.kie.remote.RemoteFactHandle;
import org.kie.remote.command.DeleteCommand;
import org.kie.remote.command.EventInsertCommand;
import org.kie.remote.command.FactCountCommand;
import org.kie.remote.command.FireAllRulesCommand;
import org.kie.remote.command.FireUntilHaltCommand;
import org.kie.remote.command.GetKJarGAVCommand;
import org.kie.remote.command.GetObjectCommand;
import org.kie.remote.command.HaltCommand;
import org.kie.remote.command.InsertCommand;
import org.kie.remote.command.ListObjectsCommand;
import org.kie.remote.command.ListObjectsCommandClassType;
import org.kie.remote.command.ListObjectsCommandNamedQuery;
import org.kie.remote.command.RemoteCommand;
import org.kie.remote.command.SnapshotOnDemandCommand;
import org.kie.remote.command.UpdateCommand;
import org.kie.remote.command.UpdateKJarCommand;
import org.kie.remote.impl.RemoteFactHandleImpl;
import org.kie.remote.impl.producer.Producer;
import org.kie.remote.message.ControlMessage;
import org.kie.remote.message.FactCountMessage;
import org.kie.remote.message.FireAllRuleMessage;
import org.kie.remote.message.GetKJarGAVMessage;
import org.kie.remote.message.GetObjectMessage;
import org.kie.remote.message.ListKieSessionObjectMessage;
import org.kie.remote.message.ResultMessage;

import org.kie.remote.message.UpdateKJarMessage;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class CommandHandlerTest {

    protected static final EnvConfig envConfig = EnvConfig.getDefaultEnvConfig();
    protected static final int fireAllRule = 4;
    protected static final long factCount = fireAllRule;
    protected static final String myObject = "myObject";
    protected static final String myEntryPoint = "myEntryPoint";
    protected static final String namedQuery = "namedQuery";
    protected static final String objectName = "objectName";
    protected static final String kJarGAV = "org.kie:fake-jar:0.1-SNAPSHOT";
    protected static final RemoteFactHandle remoteFactHandle = new RemoteFactHandleImpl(myObject);
    private static final Logger logger = LoggerFactory.getLogger(CommandHandlerTest.class);
    @Mock
    protected KieSessionContext kieSessionContextMock;

    @Mock
    protected Producer producerMock;

    @Mock
    protected SessionSnapshooter sessionSnapshooterMock;

    @Mock
    protected KieSession kieSessionMock;

    @Mock
    protected EntryPoint entryPointMock;

    @Mock
    protected FactHandlesManager factHandlesManagerMock;

    @Mock
    protected FactHandle factHandleMock;

    @Mock
    protected QueryResults queryResultsMock;

    @Mock
    protected ReleaseId releaseIdMock;

    @Mock
    protected KieServices kieServicesMock;

    @Mock
    protected KieContainer kieContainerMock;

    @Captor
    protected ArgumentCaptor<ResultMessage<Object>> messageArgumentCaptor;

    protected CommandHandler commandHandler;

    @Before
    public void initTest() {
        when(kieSessionContextMock.getKieSession()).thenReturn(kieSessionMock);
        when(kieSessionContextMock.getFhManager()).thenReturn(factHandlesManagerMock);
        when(kieSessionMock.fireAllRules()).thenReturn(fireAllRule);
        when(kieSessionMock.getFactCount()).thenReturn(factCount);
        when(kieSessionMock.getEntryPoint(anyString())).thenReturn(entryPointMock);
        when(kieSessionMock.getQueryResults(anyString())).thenReturn(queryResultsMock);
        when(kieSessionContextMock.getKjarGAVUsed()).thenReturn(Optional.of(kJarGAV));
        when(queryResultsMock.iterator()).thenReturn(Collections.emptyIterator());
        doReturn(Collections.singletonList(myObject)).when(entryPointMock).getObjects();
        doReturn(Collections.singletonList(myObject)).when(kieSessionMock).getObjects(any());
        when(kieSessionMock.getObject(any())).thenReturn(myObject);
        when(factHandlesManagerMock.mapRemoteFactHandle(any(RemoteFactHandle.class))).thenReturn(factHandleMock);
        commandHandler = new CommandHandler(kieSessionContextMock,
                                            envConfig,
                                            producerMock,
                                            sessionSnapshooterMock,
                                            new ConsumerUtilsCore() {
                                                @Override
                                                public ControlMessage getLastEvent(String topic,
                                                                                   Integer pollTimeout) {
                                                    return new ControlMessage();
                                                }

                                                @Override
                                                public ControlMessage getLastEvent(String topic,
                                                                                   Properties properties,
                                                                                   Integer pollTimeout) {
                                                    return new ControlMessage();
                                                }
                                            });
        DroolsExecutor.setAsLeader();
    }

    @After
    public void endTest() {
        DroolsExecutor.setAsLeader();
    }

    @Test
    public void visitFireAllRulesCommand() {
        FireAllRulesCommand command = new FireAllRulesCommand();

        executeAndVerifyResponseMessage(command,
                                        commandHandler::visit,
                                        FireAllRuleMessage.class,
                                        result -> result.equals(new Long(fireAllRule)));

        reset(producerMock);
        reset(kieSessionMock);
        DroolsExecutor.setAsReplica();

        executeAndVerify(command,
                         commandHandler::visit,
                         () -> verify(kieSessionMock,
                                      times(1)).fireAllRules());
        logger.info("FireAllRulesCommand:{}", command.toString());
    }

    @Test
    public void visitFireUntilHaltCommand() {
        FireUntilHaltCommand command = new FireUntilHaltCommand();
        executeAndVerify(command,
                         commandHandler::visit,
                         () -> assertTrue(commandHandler.isFiringUntilHalt()));
        logger.info("FireUntilHaltCommand:{}", command.toString());
    }

    @Test
    public void visitHaltCommand() {
        HaltCommand command = new HaltCommand();
        executeAndVerify(command,
                         commandHandler::visit,
                         () -> assertFalse(commandHandler.isFiringUntilHalt()));
        logger.info("HaltCommand:{}", command.toString());
    }

    @Test
    public void visitInsertCommand() {
        InsertCommand command = new InsertCommand(remoteFactHandle, myEntryPoint);
        executeAndVerify(command,
                         commandHandler::visit,
                         () -> {
                             verify(factHandlesManagerMock,
                                    times(1)).registerHandle(eq(remoteFactHandle), any(FactHandle.class));
                             verify(entryPointMock,
                                    times(1)).insert(eq(myObject));
                         });
        logger.info("InsertCommand:{}", command.toString());
    }

    @Test
    public void visitEventInsertCommand() {
        EventInsertCommand command = new EventInsertCommand(myObject, myEntryPoint);
        executeAndVerify(command,
                         commandHandler::visit,
                         () -> verify(entryPointMock, times(1)).insert(eq(myObject)));
        logger.info("EventInsertCommand:{}", command.toString());
    }

    @Test
    public void visitDeleteCommand() {
        DeleteCommand command = new DeleteCommand(remoteFactHandle, myEntryPoint);
        executeAndVerify(command,
                         commandHandler::visit,
                         () -> {
                             verify(kieSessionMock, times(1)).getEntryPoint(eq(myEntryPoint));
                             verify(entryPointMock, times(1)).delete(eq(factHandleMock));
                         });
        logger.info("DeleteCommand:{}", command.toString());
    }

    @Test
    public void visitUpdateCommand() {
        UpdateCommand command = new UpdateCommand(remoteFactHandle, myObject, myEntryPoint);
        executeAndVerify(command,
                         commandHandler::visit,
                         () -> {
                             verify(kieSessionMock, times(1)).getEntryPoint(eq(myEntryPoint));
                             verify(entryPointMock,
                                    times(1)).update(eq(factHandleMock), eq(myObject));
                         });
        logger.info("UpdateCommand:{}", command.toString());
    }

    @Test
    public void visitListObjectsCommand() {
        ListObjectsCommand command = new ListObjectsCommand(myEntryPoint);
        executeAndVerifyResponseMessage(command,
                                        commandHandler::visit,
                                        ListKieSessionObjectMessage.class,
                                        result -> myObject.equals(((List) result).get(0)));
        logger.info("ListObjectsCommand:{}", command.toString());
    }

    @Test
    public void visitListObjectsCommandClassType() {
        ListObjectsCommandClassType command = new ListObjectsCommandClassType(myEntryPoint, String.class);
        executeAndVerifyResponseMessage(command,
                                        commandHandler::visit,
                                        ListKieSessionObjectMessage.class,
                                        result -> myObject.equals(((List) result).get(0)));
        logger.info("ListObjectsCommandClassType:{} ListKieSessionObjectMessage:{}", command.toString());
    }

    @Test
    public void visitGetObjectCommand() {
        GetObjectCommand command = new GetObjectCommand(remoteFactHandle);
        executeAndVerifyResponseMessage(command,
                                        commandHandler::visit,
                                        GetObjectMessage.class,
                                        myObject::equals);
        logger.info("GetObjectCommand:{} GetObjectMessage:{}", command.toString(), GetObjectMessage.class.toString());
    }

    @Test
    public void visitListObjectsCommandNamedQuery() {
        ListObjectsCommandNamedQuery command = new ListObjectsCommandNamedQuery(myEntryPoint, namedQuery, objectName);
        executeAndVerifyResponseMessage(command,
                                        commandHandler::visit,
                                        ListKieSessionObjectMessage.class,
                                        result -> ((List) result).size() == 0);
        logger.info("ListObjectsCommandNamedQuery:{}", command.toString());
    }

    @Test
    public void visitFactCountCommand() {
        FactCountCommand command = new FactCountCommand(myEntryPoint);
        executeAndVerifyResponseMessage(command,
                                        commandHandler::visit,
                                        FactCountMessage.class,
                                        result -> new Long(factCount).equals(result));
        logger.info("FactCountCommand:{}", command.toString());
    }

    @Test
    public void visitSnapshotOnDemandCommand() {
        SnapshotOnDemandCommand command = new SnapshotOnDemandCommand();
        executeAndVerify(command,
                         commandHandler::visit,
                         () -> {
                             verify(sessionSnapshooterMock,
                                    times(1))
                                     .serialize(eq(kieSessionContextMock),
                                                anyString(),
                                                anyLong());
                         });
        logger.info("SnapshotOnDemandCommand:{}", command.toString());
    }

    @Test
    public void visitGetKJarGAVCommand() {
        GetKJarGAVCommand command = new GetKJarGAVCommand(myEntryPoint);
        executeAndVerifyResponseMessage(command,
                                        commandHandler::visit,
                                        GetKJarGAVMessage.class,
                                        result -> kJarGAV.equals(result));
        logger.info("GetKJarGAVCommand:{}", command.toString());
    }

    @Test
    public void visitUpdateKJarCommand() {
        envConfig.withUpdatableKJar("true");
        when(kieSessionContextMock.getKjarGAVUsed()).thenReturn(Optional.of(kJarGAV));
        when(kieSessionContextMock.getKieContainer()).thenReturn(kieContainerMock);
        when(kieServicesMock.newReleaseId("org.kie",
                                          "fake-jar",
                                          "0.1-SNAPSHOT")).thenReturn(releaseIdMock);
        UpdateKJarCommand command = new UpdateKJarCommand(kJarGAV);
        executeAndVerifyResponseMessage(command,
                                        commandHandler::visit,
                                        UpdateKJarMessage.class,
                                        result -> Boolean.TRUE);
        logger.info("UpdateKJarCommand:{}", command.toString());
    }

    private <T extends RemoteCommand> void executeAndVerify(T command,
                                                            Consumer<T> consumer,
                                                            Runnable assertions) {
        internalExecuteAndVerify(false,
                                 command,
                                 consumer,
                                 null,
                                 result -> {
                                     assertions.run();
                                     return true;
                                 });
    }

    private <T extends RemoteCommand> void executeAndVerifyResponseMessage(T command,
                                                                           Consumer<T> consumer,
                                                                           Class<? extends ResultMessage<?>> expectedMessageClass,
                                                                           Function<Object, Boolean> checkOnMessage) {
        internalExecuteAndVerify(true,
                                 command,
                                 consumer,
                                 expectedMessageClass,
                                 checkOnMessage);
    }

    private <T extends RemoteCommand> void internalExecuteAndVerify(boolean hasReturnMessage,
                                                                    T command,
                                                                    Consumer<T> consumer,
                                                                    Class<? extends ResultMessage<?>> expectedMessageClass,
                                                                    Function<Object, Boolean> predicate) {

        consumer.accept(command);

        if (hasReturnMessage) {
            verify(producerMock,
                   times(1))
                    .produceSync(eq(CommonConfig.DEFAULT_KIE_SESSION_INFOS_TOPIC),
                                 eq(command.getId()),
                                 messageArgumentCaptor.capture());

            ResultMessage value = messageArgumentCaptor.getValue();

            // if command has a return message, it should not performed on replica
            // (FireAllRulesCommand and UpdateKjarCommand are an exception)
            assertFalse(command.isPermittedForReplicas() && !((command instanceof FireAllRulesCommand) || (command instanceof UpdateKJarCommand)));
            assertTrue(expectedMessageClass.isAssignableFrom(value.getClass()));
            assertEquals(command.getId(), value.getId());
            assertTrue(predicate.apply(value.getResult()));
        } else {
            verify(producerMock,
                   never())
                    .produceSync(eq(CommonConfig.DEFAULT_KIE_SESSION_INFOS_TOPIC),
                                 eq(command.getId()),
                                 messageArgumentCaptor.capture());

            assertTrue(command.isPermittedForReplicas() || command instanceof SnapshotOnDemandCommand);
            assertTrue(predicate.apply(null));
        }
    }

}