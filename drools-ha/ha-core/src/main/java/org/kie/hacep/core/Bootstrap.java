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
package org.kie.hacep.core;

import java.util.Arrays;

import org.kie.hacep.Config;
import org.kie.hacep.EnvConfig;

import org.kie.hacep.core.infra.consumer.ConsumerController;
import org.kie.hacep.core.infra.election.LeaderElection;
import org.kie.remote.impl.producer.Producer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Here is where we start all the services needed by the POD
 */
public class Bootstrap {

    private static final Logger logger = LoggerFactory.getLogger(Bootstrap.class);
    private static Producer eventProducer;
    private static ConsumerController consumerController;
    private static CoreKube coreKube;

    public static void startEngine(EnvConfig envConfig) {
        //order matter
        checkKJarVersion(envConfig);
        if(!envConfig.isUnderTest()) {
            coreKube = new CoreKube(envConfig.getNamespace(),
                                    null);
        }
        eventProducer = startProducer(envConfig);
        startConsumers(envConfig, eventProducer);
        if(!envConfig.isUnderTest()) {
            leaderElection();
        }
        GlobalStatus.nodeReady = true;
        logger.info("CONFIGURE on start engine:{}", envConfig);
    }

    public static void stopEngine() {
        logger.info("Stop engine");
        if(coreKube != null && coreKube.getLeaderElection() != null) {
            LeaderElection leadership = coreKube.getLeaderElection();
            try {
                leadership.stop();
            } catch (Exception e) {
                GlobalStatus.nodeLive = false;
                throw new RuntimeException(e.getMessage(), e);
            }
            logger.info("Stop leaderElection");
        }
        if (consumerController != null) {
            consumerController.stop();
        }
        logger.info("Stop consumerController");
        if (eventProducer != null) {
            eventProducer.stop();
        }
        logger.info("Stop eventProducer");
        eventProducer = null;
        consumerController = null;
        GlobalStatus.nodeLive = false;
    }

    // only for tests
    public static ConsumerController getConsumerController() {
        return consumerController;
    }

    private static void leaderElection() {
        LeaderElection leadership = coreKube.getLeaderElection();
        coreKube.getLeaderElection().addCallbacks(Arrays.asList( consumerController.getCallback()));
        try {
            leadership.start();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    private static Producer startProducer(EnvConfig envConfig) {
        Producer producer = Producer.get( envConfig.isLocal() );
        producer.start(Config.getProducerConfig("EventProducer"));
        return producer;
    }

    private static void startConsumers(EnvConfig envConfig, Producer producer) {
        consumerController = new ConsumerController(envConfig, producer);
        consumerController.start();
    }

    private static void checkKJarVersion(EnvConfig envConfig){
        if(envConfig.isUpdatableKJar()){
            String gav = envConfig.getKJarGAV();
            if(gav == null){
                throw new RuntimeException("The KJar GAV is missing and must be in the format groupdID:artifactID:version");
            }
            String parts[]= gav.split(":");
            if(parts.length != 3){
                throw new RuntimeException("The KJar GAV must be in the format groupdID:artifactID:version");
            }
        }
    }
}