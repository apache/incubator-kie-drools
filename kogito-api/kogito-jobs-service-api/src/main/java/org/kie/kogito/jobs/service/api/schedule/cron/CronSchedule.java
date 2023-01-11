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

package org.kie.kogito.jobs.service.api.schedule.cron;

import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.kie.kogito.jobs.service.api.Schedule;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import static org.kie.kogito.jobs.service.api.schedule.cron.CronSchedule.EXPRESSION_PROPERTY;
import static org.kie.kogito.jobs.service.api.schedule.cron.CronSchedule.TIME_ZONE_PROPERTY;

@Schema(description = "Cron schedules establishes that a job must be executed with a periodicity based on a unix like crontab expression.",
        allOf = { Schedule.class },
        requiredProperties = { EXPRESSION_PROPERTY })
@JsonPropertyOrder({ EXPRESSION_PROPERTY, TIME_ZONE_PROPERTY })
public class CronSchedule extends Schedule {

    static final String EXPRESSION_PROPERTY = "expression";
    static final String TIME_ZONE_PROPERTY = "timeZone";

    @Schema(description = "Cron expression for the job execution, for more information <a href=\"https://en.wikipedia.org/wiki/Cron#Overview\" target=\"_blank\">see</a>.")
    private String expression;
    @Schema(description = "Time zone for the cron programming, for example \"Europe/Madrid\". For more information <a href=\"https://en.wikipedia.org/wiki/List_of_tz_database_time_zones\" target=\"_blank\">see</a>.")
    private String timeZone;

    public String getExpression() {
        return expression;
    }

    public void setExpression(String expression) {
        this.expression = expression;
    }

    public String getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }

    public CronSchedule() {
        // Marshalling constructor.
    }

    @Override
    public String toString() {
        return "CronSchedule{" +
                "expression='" + expression + '\'' +
                ", timeZone='" + timeZone + '\'' +
                '}';
    }

    public static Builder builder() {
        return new Builder(new CronSchedule());
    }

    public static class Builder {

        private final CronSchedule schedule;

        private Builder(CronSchedule schedule) {
            this.schedule = schedule;
        }

        public Builder expression(String expression) {
            schedule.setExpression(expression);
            return this;
        }

        public Builder timeZone(String timeZone) {
            schedule.setTimeZone(timeZone);
            return this;
        }

        public CronSchedule build() {
            return schedule;
        }
    }
}
