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

package org.kie.kogito.jobs.service.api;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import static org.kie.kogito.jobs.service.api.Retry.DELAY_PROPERTY;
import static org.kie.kogito.jobs.service.api.Retry.DELAY_UNIT_PROPERTY;
import static org.kie.kogito.jobs.service.api.Retry.DURATION_UNIT_PROPERTY;
import static org.kie.kogito.jobs.service.api.Retry.MAX_DURATION_PROPERTY;
import static org.kie.kogito.jobs.service.api.Retry.MAX_RETRIES_PROPERTY;

@Schema(description = "The retry configuration establishes the number of times a failing job execution must be retried before it’s considered as FAILED.")
@JsonPropertyOrder({ MAX_RETRIES_PROPERTY, DELAY_PROPERTY, DELAY_UNIT_PROPERTY, MAX_DURATION_PROPERTY, DURATION_UNIT_PROPERTY })
public class Retry {

    static final String MAX_RETRIES_PROPERTY = "maxRetries";
    static final String DELAY_PROPERTY = "delay";
    static final String DELAY_UNIT_PROPERTY = "delayUnit";
    static final String MAX_DURATION_PROPERTY = "maxDuration";
    static final String DURATION_UNIT_PROPERTY = "durationUnit";

    @Schema(description = "Number of retries to execute in case of failures.", defaultValue = "3")
    private Integer maxRetries = 3;
    @Schema(description = "Time delay between the retries.", defaultValue = "0")
    private Long delay = 0L;
    private TemporalUnit delayUnit = TemporalUnit.MILLIS;
    @Schema(description = "Maximum amount of time to continue retrying if no successful execution was produced.", defaultValue = "180000")
    private Long maxDuration = 180000L;
    private TemporalUnit durationUnit = TemporalUnit.MILLIS;

    public Retry() {
        // Marshalling constructor.
    }

    public Integer getMaxRetries() {
        return maxRetries;
    }

    public void setMaxRetries(Integer maxRetries) {
        this.maxRetries = maxRetries;
    }

    public Long getDelay() {
        return delay;
    }

    public void setDelay(Long delay) {
        this.delay = delay;
    }

    public TemporalUnit getDelayUnit() {
        return delayUnit;
    }

    public void setDelayUnit(TemporalUnit delayUnit) {
        this.delayUnit = delayUnit;
    }

    public Long getMaxDuration() {
        return maxDuration;
    }

    public void setMaxDuration(Long maxDuration) {
        this.maxDuration = maxDuration;
    }

    public TemporalUnit getDurationUnit() {
        return durationUnit;
    }

    public void setDurationUnit(TemporalUnit durationUnit) {
        this.durationUnit = durationUnit;
    }

    @Override
    public String toString() {
        return "Retry{" +
                "maxRetries=" + maxRetries +
                ", delay=" + delay +
                ", delayUnit='" + delayUnit + '\'' +
                ", maxDuration=" + maxDuration +
                ", durationUnit='" + durationUnit + '\'' +
                '}';
    }

    public static Builder builder() {
        return new Builder(new Retry());
    }

    public static class Builder {

        private final Retry retry;

        private Builder(Retry retry) {
            this.retry = retry;
        }

        public Builder maxRetries(Integer maxRetries) {
            retry.setMaxRetries(maxRetries);
            return this;
        }

        public Builder delay(Long delay) {
            retry.setDelay(delay);
            return this;
        }

        public Builder delayUnit(TemporalUnit delayUnit) {
            retry.setDelayUnit(delayUnit);
            return this;
        }

        public Builder maxDuration(Long maxDuration) {
            retry.setMaxDuration(maxDuration);
            return this;
        }

        public Builder durationUnit(TemporalUnit durationUnit) {
            retry.setDurationUnit(durationUnit);
            return this;
        }

        public Retry build() {
            return retry;
        }
    }
}