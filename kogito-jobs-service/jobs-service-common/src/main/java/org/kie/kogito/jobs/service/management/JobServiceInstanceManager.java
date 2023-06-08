/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.kogito.jobs.service.management;

import java.time.OffsetDateTime;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.reactive.messaging.spi.Connector;
import org.kie.kogito.jobs.service.model.JobServiceManagementInfo;
import org.kie.kogito.jobs.service.repository.JobServiceManagementRepository;
import org.kie.kogito.jobs.service.utils.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.quarkus.runtime.ShutdownEvent;
import io.quarkus.runtime.StartupEvent;
import io.smallrye.mutiny.Uni;
import io.smallrye.reactive.messaging.kafka.KafkaConnector;
import io.vertx.mutiny.core.TimeoutStream;
import io.vertx.mutiny.core.Vertx;

@ApplicationScoped
public class JobServiceInstanceManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(JobServiceInstanceManager.class);

    @ConfigProperty(name = "kogito.jobs-service.management.heartbeat.interval-in-seconds", defaultValue = "1")
    long heardBeatIntervalInSeconds;

    @ConfigProperty(name = "kogito.jobs-service.management.leader-check.interval-in-seconds", defaultValue = "1")
    long leaderCheckIntervalInSeconds;

    @ConfigProperty(name = "kogito.jobs-service.management.heartbeat.expiration-in-seconds", defaultValue = "10")
    long heartbeatExpirationInSeconds;

    @ConfigProperty(name = "kogito.jobs-service.management.heartbeat.management-id", defaultValue = "kogito-jobs-service-leader")
    String leaderManagementId;

    @Inject
    @Connector(value = "smallrye-kafka")
    KafkaConnector kafkaConnector;

    @Inject
    Event<MessagingChangeEvent> messagingChangeEventEvent;

    @Inject
    Vertx vertx;

    @Inject
    JobServiceManagementRepository repository;

    private TimeoutStream checkLeader;

    private TimeoutStream heartbeat;

    private final AtomicReference<JobServiceManagementInfo> currentInfo = new AtomicReference<>();

    private final AtomicBoolean leader = new AtomicBoolean(false);

    void startup(@Observes StartupEvent startupEvent) {
        buildAndSetInstanceInfo();

        //background task for leader check, it will be started after the first tryBecomeLeader() execution
        checkLeader = vertx.periodicStream(TimeUnit.SECONDS.toMillis(leaderCheckIntervalInSeconds))
                .handler(id -> tryBecomeLeader(currentInfo.get(), checkLeader, heartbeat)
                        .subscribe().with(i -> LOGGER.trace("Leader check completed"),
                                ex -> LOGGER.error("Error checking Leader", ex)))
                .pause();

        //background task for heartbeat will be started when become leader
        heartbeat = vertx.periodicStream(TimeUnit.SECONDS.toMillis(heardBeatIntervalInSeconds))
                .handler(t -> heartbeat(currentInfo.get())
                        .subscribe().with(i -> LOGGER.trace("Heartbeat completed {}", currentInfo.get()),
                                ex -> LOGGER.error("Error on heartbeat {}", currentInfo.get(), ex)))
                .pause();

        //initial leader check
        tryBecomeLeader(currentInfo.get(), checkLeader, heartbeat)
                .subscribe().with(i -> LOGGER.info("Initial leader check completed"),
                        ex -> LOGGER.error("Error on initial check leader", ex));
    }

    private void disableCommunication() {
        //disable consuming events
        kafkaConnector.getConsumerChannels().stream().forEach(c -> kafkaConnector.getConsumer(c).pause());

        //disable producing events
        messagingChangeEventEvent.fire(new MessagingChangeEvent(false));

        LOGGER.warn("Disabled communication not leader instance");
    }

    private void enableCommunication() {
        //enable consuming events
        kafkaConnector.getConsumerChannels().stream().forEach(c -> kafkaConnector.getConsumer(c).resume());

        //enable producing events
        messagingChangeEventEvent.fire(new MessagingChangeEvent(true));

        LOGGER.info("Enabled communication for leader instance");
    }

    void onShutdown(@Observes ShutdownEvent event) {
        shutdown();
    }

    void onReleaseLeader(@Observes ReleaseLeaderEvent event) {
        shutdown();
    }

    private void shutdown() {
        release(currentInfo.get())
                .onItem().invoke(i -> checkLeader.cancel())
                .onItem().invoke(i -> heartbeat.cancel())
                .subscribe().with(i -> LOGGER.info("Shutting down leader instance check"),
                        ex -> LOGGER.error("Shutdown error", ex));
    }

    protected boolean isLeader() {
        return leader.get();
    }

    protected Uni<JobServiceManagementInfo> tryBecomeLeader(JobServiceManagementInfo info, TimeoutStream checkLeader, TimeoutStream heartbeat) {
        LOGGER.debug("Try to become Leader");
        return repository.getAndUpdate(info.getId(), c -> {
            final OffsetDateTime currentTime = DateUtil.now().toOffsetDateTime();
            if (Objects.isNull(c) || Objects.isNull(c.getToken()) || Objects.equals(c.getToken(), info.getToken()) || Objects.isNull(c.getLastHeartbeat())
                    || c.getLastHeartbeat().isBefore(currentTime.minusSeconds(heartbeatExpirationInSeconds))) {
                //old instance is not active
                info.setLastHeartbeat(currentTime);
                LOGGER.info("SET Leader {}", info);
                leader.set(true);
                enableCommunication();
                heartbeat.resume();
                checkLeader.pause();
                return info;
            } else {
                if (isLeader()) {
                    LOGGER.info("Not Leader");
                    leader.set(false);
                    disableCommunication();
                }
                //stop heartbeats if running
                heartbeat.pause();
                //guarantee the stream is running if not leader
                checkLeader.resume();
            }
            return null;
        });
    }

    protected Uni<Void> release(JobServiceManagementInfo info) {
        return repository.set(new JobServiceManagementInfo(info.getId(), null, null))
                .onItem().invoke(this::disableCommunication)
                .onItem().invoke(i -> leader.set(false))
                .onItem().invoke(i -> LOGGER.info("Leader instance released"))
                .onFailure().invoke(ex -> LOGGER.error("Error releasing leader"))
                .replaceWithVoid();
    }

    protected Uni<JobServiceManagementInfo> heartbeat(JobServiceManagementInfo info) {
        if (isLeader()) {
            return repository.heartbeat(info);
        }
        return Uni.createFrom().nullItem();
    }

    private void buildAndSetInstanceInfo() {
        currentInfo.set(new JobServiceManagementInfo(leaderManagementId, generateToken(), DateUtil.now().toOffsetDateTime()));
        LOGGER.info("Current Job Service Instance {}", currentInfo.get());
    }

    private String generateToken() {
        return UUID.randomUUID().toString();
    }

    protected JobServiceManagementInfo getCurrentInfo() {
        return currentInfo.get();
    }

    protected TimeoutStream getCheckLeader() {
        return checkLeader;
    }

    protected TimeoutStream getHeartbeat() {
        return heartbeat;
    }
}
