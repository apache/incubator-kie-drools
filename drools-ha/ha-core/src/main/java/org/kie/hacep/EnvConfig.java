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
package org.kie.hacep;

import java.time.Duration;
import java.util.Optional;

import org.kie.hacep.util.PrinterLogImpl;
import org.kie.remote.CommonConfig;

public final class EnvConfig {

    private String namespace;
    private String eventsTopicName;
    private String controlTopicName;
    private String snapshotTopicName;
    private String kieSessionInfosTopicName;
    private String printerType;
    private int iterationBetweenSnapshot = Config.DEFAULT_ITERATION_BETWEEN_SNAPSHOT;
    private int pollTimeout = 1000;
    private int pollSnapshotTimeout = 1;
    private int maxSnapshotRequestAttempts = 10;
    private boolean skipOnDemanSnapshot;
    private long maxSnapshotAge;
    private boolean test;
    private boolean local;
    private PollUnit pollUnit, pollUnitSnapshot;
    private Duration pollDuration, pollSnapshotDuration;
    private final static String sec ="sec";
    private final static String millisec ="millisec";
    private boolean updatableKJar;
    private String kjarGAV;//groupid:artifactid:version


    private EnvConfig() { }

    public static EnvConfig getDefaultEnvConfig() {
        return anEnvConfig().
                withNamespace(Optional.ofNullable(System.getenv(Config.NAMESPACE)).orElse(CommonConfig.DEFAULT_NAMESPACE)).
                withControlTopicName(Optional.ofNullable(System.getenv(Config.DEFAULT_CONTROL_TOPIC)).orElse(Config.DEFAULT_CONTROL_TOPIC)).
                withEventsTopicName(Optional.ofNullable(System.getenv(CommonConfig.DEFAULT_EVENTS_TOPIC)).orElse(CommonConfig.DEFAULT_EVENTS_TOPIC)).
                withSnapshotTopicName(Optional.ofNullable(System.getenv(Config.DEFAULT_SNAPSHOT_TOPIC)).orElse(Config.DEFAULT_SNAPSHOT_TOPIC)).
                withKieSessionInfosTopicName(Optional.ofNullable(System.getenv(CommonConfig.DEFAULT_KIE_SESSION_INFOS_TOPIC)).orElse(CommonConfig.DEFAULT_KIE_SESSION_INFOS_TOPIC)).
                withPrinterType(Optional.ofNullable(System.getenv(Config.DEFAULT_PRINTER_TYPE)).orElse(PrinterLogImpl.class.getName())).
                withPollTimeout(Optional.ofNullable(System.getenv(Config.POLL_TIMEOUT)).orElse(String.valueOf(Config.DEFAULT_POLL_TIMEOUT))).
                withPollTimeUnit(Optional.ofNullable(System.getenv(Config.POLL_TIMEOUT_UNIT)).orElse(millisec)).
                withPollSnapshotTimeout(Optional.ofNullable(System.getenv(Config.POLL_TIMEOUT_SNAPSHOT)).orElse(String.valueOf(Config.DEFAULT_POLL_SNAPSHOT_TIMEOUT))).
                withPollSnapshotTimeUnit(Optional.ofNullable(System.getenv(Config.POLL_TIMEOUT_UNIT_SNAPSHOT)).orElse(sec)).
                skipOnDemandSnapshot(Optional.ofNullable(System.getenv(Config.SKIP_ON_DEMAND_SNAPSHOT)).orElse(Boolean.FALSE.toString())).
                withIterationBetweenSnapshot(Optional.ofNullable(System.getenv(Config.ITERATION_BETWEEN_SNAPSHOT)).orElse(String.valueOf(Config.DEFAULT_ITERATION_BETWEEN_SNAPSHOT))).
                withMaxSnapshotAgeSeconds(Optional.ofNullable(System.getenv(Config.MAX_SNAPSHOT_AGE)).orElse(Config.DEFAULT_MAX_SNAPSHOT_AGE_SEC)).
                withMaxSnapshotRequestAttempts(Optional.ofNullable(System.getenv(Config.MAX_SNAPSHOT_REQUEST_ATTEMPTS)).orElse(Config.DEFAULT_MAX_SNAPSHOT_REQUEST_ATTEMPTS)).
                withUpdatableKJar(Optional.ofNullable(System.getenv(Config.UPDATABLE_KJAR)).orElse(Boolean.FALSE.toString())).
                withKJarGAV(Optional.ofNullable(System.getenv(Config.KJAR_GAV)).orElse(null)).
                underTest(Optional.ofNullable(System.getenv(Config.UNDER_TEST)).orElse(Config.TEST));
    }

    public static EnvConfig anEnvConfig() {
        return new EnvConfig();
    }

    public EnvConfig withNamespace(String namespace) {
        this.namespace = namespace;
        return this;
    }

    public EnvConfig withEventsTopicName(String eventsTopicName) {
        this.eventsTopicName = eventsTopicName;
        return this;
    }

    public EnvConfig withControlTopicName(String controlTopicName) {
        this.controlTopicName = controlTopicName;
        return this;
    }

    public EnvConfig withSnapshotTopicName(String snapshotTopicName) {
        this.snapshotTopicName = snapshotTopicName;
        return this;
    }

    public EnvConfig withKieSessionInfosTopicName(String kieSessionInfosTopicName) {
        this.kieSessionInfosTopicName = kieSessionInfosTopicName;
        return this;
    }

    public EnvConfig withPrinterType(String printerType) {
        this.printerType = printerType;
        return this;
    }

    public EnvConfig withPollTimeout(String pollTimeout) {
        this.pollTimeout = Integer.valueOf(pollTimeout);
        return this;
    }

    public EnvConfig withPollSnapshotTimeout(String pollSnapshotTimeout) {
        this.pollSnapshotTimeout = Integer.valueOf(pollSnapshotTimeout);
        return this;
    }

    public EnvConfig withIterationBetweenSnapshot(String iterationBetweenSnapshot) {
        this.iterationBetweenSnapshot = Integer.valueOf(iterationBetweenSnapshot);
        return this;
    }

    public EnvConfig underTest(String underTest) {
        return underTest(Boolean.valueOf(underTest));
    }

    public EnvConfig underTest(boolean underTest) {
        this.test = underTest;
        return this;
    }

    public EnvConfig local(boolean local) {
        this.local = local;
        return this;
    }

    public EnvConfig skipOnDemandSnapshot(String skipOnDemandSnapshoot) {
        this.skipOnDemanSnapshot = Boolean.valueOf(skipOnDemandSnapshoot);
        return this;
    }

    public EnvConfig withMaxSnapshotAgeSeconds(String maxSnapshotAge) {
        this.maxSnapshotAge = Long.valueOf(maxSnapshotAge);
        return this;
    }

    public EnvConfig withMaxSnapshotRequestAttempts(String maxSnapshotRequestAttempts) {
        this.maxSnapshotRequestAttempts = Integer.parseInt(maxSnapshotRequestAttempts);
        return this;
    }

    public EnvConfig withPollTimeUnit(String pollTimeUnit){
        switch (pollTimeUnit){
            case millisec:
                this.pollUnit = PollUnit.MILLISECOND;
                this.pollDuration = Duration.ofMillis(pollTimeout);
                break;
            case sec:
                this.pollUnit = PollUnit.SECOND;
                this.pollDuration = Duration.ofSeconds(pollTimeout);
                break;
            default:
                throw new RuntimeException("No pollTimeUnit provided");
        }
        return this;
    }

    public EnvConfig withPollSnapshotTimeUnit(String pollSnapshotTimeUnit){
        switch (pollSnapshotTimeUnit){
            case millisec:
                this.pollUnitSnapshot = PollUnit.MILLISECOND;
                this.pollSnapshotDuration = Duration.ofMillis(pollSnapshotTimeout);
                break;
            case sec:
                this.pollUnitSnapshot = PollUnit.SECOND;
                this.pollSnapshotDuration = Duration.ofSeconds(pollSnapshotTimeout);
                break;
            default:
                throw new RuntimeException("No pollSnapshotTimeUnit provided");
        }
        return this;
    }

    public EnvConfig withUpdatableKJar(String updatableKJar){
        this.updatableKJar = Boolean.valueOf(updatableKJar);
        return this;
    }

    public EnvConfig withKJarGAV(String kjarGAV){
        this.kjarGAV = kjarGAV;
        return this;
    }

    public EnvConfig clone() {
        EnvConfig envConfig = new EnvConfig();
        envConfig.eventsTopicName = this.eventsTopicName;
        envConfig.namespace = this.namespace;
        envConfig.controlTopicName = this.controlTopicName;
        envConfig.snapshotTopicName = this.snapshotTopicName;
        envConfig.kieSessionInfosTopicName = this.kieSessionInfosTopicName;
        envConfig.printerType = this.printerType;
        envConfig.test = this.test;
        envConfig.local = this.local;
        envConfig.pollTimeout = this.pollTimeout;
        envConfig.pollSnapshotTimeout = this.pollSnapshotTimeout;
        envConfig.iterationBetweenSnapshot = this.iterationBetweenSnapshot;
        envConfig.skipOnDemanSnapshot = this.skipOnDemanSnapshot;
        envConfig.maxSnapshotAge = this.maxSnapshotAge;
        envConfig.maxSnapshotRequestAttempts = this.maxSnapshotRequestAttempts;
        envConfig.pollUnit = this.pollUnit;
        envConfig.pollUnitSnapshot = this.pollUnitSnapshot;
        envConfig.updatableKJar = this.updatableKJar;
        envConfig.kjarGAV = this.kjarGAV;
        return envConfig;
    }

    public String getNamespace() {
        return namespace;
    }

    public String getEventsTopicName() {
        return eventsTopicName;
    }

    public String getControlTopicName() {
        return controlTopicName;
    }

    public String getSnapshotTopicName() {
        return snapshotTopicName;
    }

    public String getKieSessionInfosTopicName() {
        return kieSessionInfosTopicName;
    }

    public String getPrinterType() {
        return printerType;
    }

    public boolean isUnderTest() {
        return test;
    }

    public int getPollTimeout() {
        return pollTimeout;
    }

    public int getPollSnapshotTimeout() {
        return pollSnapshotTimeout;
    }

    public int getIterationBetweenSnapshot() {
        return iterationBetweenSnapshot;
    }

    public boolean isSkipOnDemandSnapshot() {
        return skipOnDemanSnapshot;
    }

    public long getMaxSnapshotAge() {
        return maxSnapshotAge;
    }

    public boolean isLocal() {
        return local;
    }

    public int getMaxSnapshotRequestAttempts() {
        return maxSnapshotRequestAttempts;
    }

    public PollUnit getPollUnit() { return pollUnit; }

    public PollUnit getPollSnapshotUnit() { return pollUnitSnapshot; }

    public Duration getPollDuration(){ return pollDuration; }

    public Duration getPollSnapshotDuration(){ return pollSnapshotDuration; }

    public boolean isUpdatableKJar(){ return updatableKJar;}

    public String getKJarGAV(){ return kjarGAV;}


    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("EnvConfig{");
        sb.append("namespace='").append(namespace).append('\'');
        sb.append(", eventsTopicName='").append(eventsTopicName).append('\'');
        sb.append(", controlTopicName='").append(controlTopicName).append('\'');
        sb.append(", snapshotTopicName='").append(snapshotTopicName).append('\'');
        sb.append(", kieSessionInfosTopicName='").append(kieSessionInfosTopicName).append('\'');
        sb.append(", printerType='").append(printerType).append('\'');
        sb.append(", pollTimeUnit='").append(pollUnit).append('\'');
        sb.append(", pollTimeout='").append(pollTimeout).append('\'');
        sb.append(", pollDuration='").append(pollDuration).append('\'');
        sb.append(", pollSnapshotUnit='").append(pollUnitSnapshot).append('\'');
        sb.append(", pollSnapshotDuration='").append(pollSnapshotDuration).append('\'');
        sb.append(", pollSnapshotTimeout='").append(pollSnapshotTimeout).append('\'');
        sb.append(", iterationBetweenSnapshot='").append(iterationBetweenSnapshot).append('\'');
        sb.append(", skipOnDemanSnapshot='").append(skipOnDemanSnapshot).append('\'');
        sb.append(", maxSnapshotAge='").append(maxSnapshotAge).append('\'');
        sb.append(", maxSnapshotRequestAttempts='").append(maxSnapshotRequestAttempts).append('\'');
        sb.append(", updatableKJar='").append(updatableKJar).append('\'');
        sb.append(", kjarGAV='").append(kjarGAV).append('\'');
        sb.append(", underTest='").append(test).append('\'');
        sb.append('}');
        return sb.toString();
    }
}

