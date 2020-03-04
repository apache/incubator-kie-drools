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

import java.io.Serializable;
import java.util.Queue;

import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.hacep.EnvConfig;
import org.kie.hacep.core.GlobalStatus;
import org.kie.hacep.core.KieSessionContext;
import org.kie.hacep.core.infra.SessionSnapshooter;
import org.kie.hacep.core.infra.SnapshotInfos;
import org.kie.hacep.core.infra.consumer.ConsumerHandler;
import org.kie.hacep.core.infra.consumer.ItemToProcess;
import org.kie.hacep.core.infra.election.State;
import org.kie.hacep.util.ConsumerUtilsCore;
import org.kie.hacep.util.PrinterUtil;
import org.kie.remote.DroolsExecutor;
import org.kie.remote.command.RemoteCommand;
import org.kie.remote.command.VisitableCommand;
import org.kie.remote.impl.producer.Producer;
import org.kie.remote.message.ControlMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.kie.remote.util.SerializationUtil.deserialize;

public class DroolsConsumerHandler implements ConsumerHandler {

    private static final Logger logger = LoggerFactory.getLogger(DroolsConsumerHandler.class);
    private Logger loggerForTest;
    private Producer producer;
    private SessionSnapshooter sessionSnapShooter;
    private EnvConfig envConfig;
    private KieSessionContext kieSessionContext;
    private CommandHandler commandHandler;
    private SnapshotInfos snapshotInfos;
    private boolean shutdown;

    public DroolsConsumerHandler(Producer producer, EnvConfig envConfig, SessionSnapshooter snapShooter, ConsumerUtilsCore consumerUtilsCore) {
        this.envConfig = envConfig;
        this.sessionSnapShooter = snapShooter;
        initializeKieSessionContext();
        this.producer = producer;
        this.commandHandler = new CommandHandler(this.kieSessionContext, this.envConfig, producer, this.sessionSnapShooter, consumerUtilsCore);
        if (this.envConfig.isUnderTest()) {
            loggerForTest = PrinterUtil.getKafkaLoggerForTest(envConfig);
        }
    }

    private void initializeKieSessionContext() {
        if (this.envConfig.isSkipOnDemandSnapshot()) {// if true we reads the snapshots and wait until the first leaderElectionUpdate
            initializeSessionContextWithSnapshotCheck();
        } else {
            createAndInitializeSessionContextWithoutSnapshot();
        }
    }

    private void initializeSessionContextWithSnapshotCheck() {
        this.snapshotInfos = this.sessionSnapShooter.deserialize();
        /* if the Snapshot is ok we use the KieContainer and KieSession from the infos,
           otherwise (empty system) we create kiecontainer and kieSession from the envConfig's GAV */
        if (this.snapshotInfos != null) {
            initializeSessionContextFromSnapshot();
        } else {
            createAndInitializeSessionContextWithoutSnapshot();
        }
    }

    private void createAndInitializeSessionContextWithoutSnapshot() {
        KieServices srv = KieServices.get();
        if (srv != null) {
            KieContainer kieContainer = KieContainerUtils.getKieContainer(envConfig, srv);
            this.kieSessionContext = new KieSessionContext();
            this.kieSessionContext.init(kieContainer, kieContainer.newKieSession());
        } else {
            throw new IllegalStateException("KieService is null");
        }
    }

    private void initializeSessionContextFromSnapshot() {
        if (this.snapshotInfos.getKieSession() != null) {
            if (logger.isInfoEnabled()) {
                logger.info("Applying snapshot Session");
            }
            this.kieSessionContext = new KieSessionContext();
            this.kieSessionContext.initFromSnapshot(this.snapshotInfos);
        } else {
            throw new IllegalStateException("The Serialized Session isn't present");
        }
    }

    //This is called from the Default KafkaConsumer
    public boolean initializeKieSessionFromSnapshotOnDemand(EnvConfig config, SnapshotInfos snapshotInfos) {
        if (!config.isSkipOnDemandSnapshot()) {// if true we reads the snapshots and wait until the first leaderElectionUpdate
            this.snapshotInfos = snapshotInfos;
            initializeSessionContextFromSnapshot();
            return true;
        }
        return false;
    }

    @Override
    public void process(ItemToProcess item, State state) {
        RemoteCommand command = deserialize((byte[]) item.getObject());
        process(command, state);
    }

    @Override
    public void process(RemoteCommand command, State state) {
        if (envConfig.isUnderTest()) {
            loggerForTest.warn("DroolsConsumerHandler.process Remote command on process:{} state:{}", command, state);
        }
        if (state.equals(State.LEADER)) {
            processCommand(command, state);
            Queue<Serializable> sideEffectsResults = DroolsExecutor.getInstance().getAndReset();
            if (envConfig.isUnderTest()) {
                loggerForTest.warn("DroolsConsumerHandler.process sideEffects:{}", sideEffectsResults);
            }
            ControlMessage newControlMessage = new ControlMessage(command.getId(), sideEffectsResults);
            if (envConfig.isUnderTest()) {
                loggerForTest.warn("DroolsConsumerHandler.process new ControlMessage sent to control topic:{}", newControlMessage);
            }
            producer.produceSync(envConfig.getControlTopicName(), command.getId(), newControlMessage);
            if (envConfig.isUnderTest()) {
                loggerForTest.warn("sideEffectOnLeader:{}", sideEffectsResults);
            }
        } else {
            processCommand(command, state);
        }
    }

    public void processSideEffectsOnReplica(Queue<Serializable> newSideEffects) {
        DroolsExecutor.getInstance().appendSideEffects(newSideEffects);
        if (envConfig.isUnderTest()) {
            loggerForTest.warn("sideEffectOnReplica:{}", newSideEffects);
        }
    }

    @Override
    public void processWithSnapshot(ItemToProcess item, State currentState) {
        if (logger.isInfoEnabled()) {
            logger.info("SNAPSHOT");
        }
        process(item, currentState);
        if (!shutdown) {
            sessionSnapShooter.serialize(this.kieSessionContext, item.getKey(), item.getOffset());
        }
    }

    @Override
    public void stop() {
        shutdown = true;
        if (this.kieSessionContext != null) {
            this.kieSessionContext.getKieSession().dispose();
        }
    }

    private void processCommand(RemoteCommand command, State state) {
        boolean execute = state.equals(State.LEADER) || command.isPermittedForReplicas();
        if (execute) {
            VisitableCommand visitable = (VisitableCommand) command;
            try {
                visitable.accept(commandHandler);
            } catch (Exception e) {
                GlobalStatus.setNodeLive(false);
                throw new IllegalStateException(e.getMessage(), e.getCause());
            }
        }
    }
}