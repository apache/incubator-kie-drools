/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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
package org.jbpm.process.instance.impl.humantask;

import java.time.Duration;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.Period;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.kie.kogito.jobs.DurationExpirationTime;
import org.kie.kogito.jobs.ExactExpirationTime;
import org.kie.kogito.jobs.ExpirationTime;

public class DeadlineHelper {

    private DeadlineHelper() {
    }

    private static final Pattern deadLineSeparatorPattern = Pattern.compile("\\^");
    private static final Pattern listSeparatorPattern = Pattern.compile(",");

    public static ExpirationTime getExpirationTime(ScheduleInfo info) {
        ExpirationTime expirationTime;
        int numRepetitions = info.getNumRepetitions();
        if (numRepetitions == 0) {
            // not repeatable duration and not repeatable exact date
            expirationTime = info.getEndDate() == null ? DurationExpirationTime.after(getDuration(info))
                    : ExactExpirationTime.of(info.getEndDate());
        } else if (info.getStartDate() != null) {
            long duration = info.getEndDate() == null ? getDuration(info)
                    : ChronoUnit.MILLIS.between(info
                            .getStartDate(), info.getEndDate());
            // startDate/duration and startDate/endDate
            expirationTime = DurationExpirationTime.repeat(Instant.now().until(info.getStartDate(), ChronoUnit.MILLIS),
                    duration, numRepetitions);

        } else if (info.getEndDate() != null) {
            // duration/endDate
            if (numRepetitions <= 0) {
                // if number of repetitions is infinite, since there is a limit, it is not really infinite
                numRepetitions = (int) (Instant.now().until(info.getEndDate(), ChronoUnit.MILLIS) / getDuration(info));
            }
            expirationTime = numRepetitions <= 1 ? ExactExpirationTime.of(info.getEndDate())
                    : DurationExpirationTime
                            .repeat(Instant.now().until(info.getEndDate().minus(info.getDuration().multipliedBy(
                                    numRepetitions)), ChronoUnit.MILLIS),
                                    getDuration(info),
                                    numRepetitions);
        } else {
            // repeatable timer with duration
            long duration = getDuration(info);
            expirationTime = DurationExpirationTime.repeat(duration, duration, numRepetitions);
        }
        return expirationTime;
    }

    public static Collection<DeadlineInfo<Reassignment>> parseReassignments(Object text) {
        return parseDeadlines(text, DeadlineHelper::parseReassigment, DeadlineHelper::getReassignmentSchedule);
    }

    public static Collection<DeadlineInfo<Map<String, Object>>> parseDeadlines(Object text) {
        return parseDeadlines(text, DeadlineHelper::asMap, DeadlineHelper::getSchedulesInfo);
    }

    private static <T> Collection<DeadlineInfo<T>> parseDeadlines(Object text,
            Function<String, T> notificationFunction,
            Function<String, Collection<ScheduleInfo>> scheduleFunction) {
        return text == null ? Collections.emptySet()
                : deadLineSeparatorPattern.splitAsStream(text.toString().trim()).map(
                        t -> parseDeadline(t, notificationFunction, scheduleFunction)).collect(Collectors.toSet());
    }

    private static <T> DeadlineInfo<T> parseDeadline(String text,
            Function<String, T> notificationFunction,
            Function<String, Collection<ScheduleInfo>> scheduleFunction) {
        text = text.trim();
        if (text.startsWith("[") && text.endsWith("]")) {
            DeadlineInfo<T> deadline = new DeadlineInfo<>();
            int indexOf = text.indexOf("]@[");
            deadline.setNotification(notificationFunction.apply(text.substring(1, indexOf).trim()));
            deadline.setScheduleInfo(scheduleFunction.apply(text.substring(indexOf + 3, text.length() - 1).trim()));
            return deadline;
        }
        throw new IllegalArgumentException("Invalid formar for dead line expression " + text);
    }

    private static Collection<ScheduleInfo> getReassignmentSchedule(String timeStr) {
        ScheduleInfo info = new ScheduleInfo();
        if (!timeStr.startsWith("PT")) {
            timeStr = "PT" + timeStr;
        }
        info.setDuration(Duration.parse(timeStr));
        return Collections.singletonList(info);
    }

    private static Collection<ScheduleInfo> getSchedulesInfo(String timeStr) {
        Collection<ScheduleInfo> schedules = new ArrayList<>();
        for (String item : timeStr.split(",")) {
            schedules.add(getScheduleInfo(item));
        }
        return schedules;
    }

    private static ScheduleInfo getScheduleInfo(String timeStr) {
        ScheduleInfo scheduleInfo = new ScheduleInfo();
        String[] dateComponents = timeStr.split("/");
        if (dateComponents[0].startsWith("R")) {
            int numRepetitions = numRepetitions(dateComponents[0]);
            scheduleInfo.setNumRepetitions(numRepetitions);
            if (isDuration(dateComponents[1])) {
                //Rx/duration
                scheduleInfo.setDuration(parseDuration(dateComponents[1]));
                if (dateComponents.length == 3) {
                    //Rx/duration/endate
                    scheduleInfo.setEndDate(ZonedDateTime.parse(dateComponents[2]));
                }

            } else {
                scheduleInfo.setStartDate(ZonedDateTime.parse(dateComponents[1]));
                if (isDuration(dateComponents[2])) {
                    //Rx/startDate/duration
                    scheduleInfo.setDuration(parseDuration(dateComponents[2]));
                } else {
                    // Rx/startDate/endDate
                    scheduleInfo.setEndDate(ZonedDateTime.parse(dateComponents[2]));
                }
            }

        } else if (isDuration(dateComponents[0])) {
            // not repeatable duration
            scheduleInfo.setDuration(parseDuration(dateComponents[0]));
        } else {
            // not repeatable exact date
            scheduleInfo.setEndDate(ZonedDateTime.parse(dateComponents[0]));
        }
        return scheduleInfo;
    }

    static Duration parseDuration(String text) {
        int indexOf = text.indexOf('T');
        Duration result;
        if (indexOf == 1) {
            result = Duration.parse(text);
        } else if (indexOf == -1) {
            result = getDuration(Period.parse(text), Duration.ZERO);
        } else {
            result = getDuration(Period.parse(text.substring(0, indexOf)),
                    Duration.parse('P' + text.substring(indexOf)));
        }
        return result;
    }

    protected static Duration getDuration(Period p, Duration d) {
        OffsetDateTime now = OffsetDateTime.now();
        return Duration.between(now, now.plus(p).plus(d));
    }

    private static boolean isDuration(String timeStr) {
        return timeStr.startsWith("P");
    }

    private static int numRepetitions(String repetitionStr) {
        return repetitionStr.length() > 1 ? Integer.parseInt(repetitionStr.substring(1)) : -1;
    }

    private static Map<String, Object> asMap(String text) {
        Map<String, Object> result = new HashMap<>();
        for (String str : text.split("\\|")) {
            int indexOf = str.indexOf(':');
            String value = str.substring(indexOf + 1);
            if (!value.trim().isEmpty()) {
                result.put(str.substring(0, indexOf), value);
            }
        }
        return result;
    }

    private static Reassignment parseReassigment(String text) {
        Map<String, Object> map = asMap(text);
        return new Reassignment(parseSet(map.get("users")), parseSet(map.get("groups")));
    }

    private static Set<String> parseSet(Object text) {
        return text instanceof String ? listSeparatorPattern.splitAsStream(text.toString()).collect(Collectors.toSet())
                : Collections.emptySet();
    }

    private static long getDuration(ScheduleInfo info) {
        if (info.getDuration() == null) {
            throw new IllegalArgumentException("Missing duration specification for " + info);
        }
        return info.getDuration().toMillis();
    }
}
