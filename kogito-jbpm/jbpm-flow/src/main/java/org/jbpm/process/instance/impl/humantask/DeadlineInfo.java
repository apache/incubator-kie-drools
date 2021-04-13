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

import java.io.Serializable;
import java.util.Collection;
import java.util.Objects;

public class DeadlineInfo<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    private Collection<ScheduleInfo> scheduleInfo;
    private T notification;

    public T getNotification() {
        return notification;
    }

    public void setNotification(T notification) {
        this.notification = notification;
    }

    public Collection<ScheduleInfo> getScheduleInfo() {
        return scheduleInfo;
    }

    public void setScheduleInfo(Collection<ScheduleInfo> scheduleInfo) {
        this.scheduleInfo = scheduleInfo;
    }

    @Override
    public int hashCode() {
        return Objects.hash(notification, scheduleInfo);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!(obj instanceof DeadlineInfo))
            return false;
        DeadlineInfo<T> other = (DeadlineInfo<T>) obj;
        return Objects.equals(notification, other.notification) && Objects.equals(scheduleInfo, other.scheduleInfo);
    }

    @Override
    public String toString() {
        return "DeadlineInfo [scheduleInfo=" + scheduleInfo + ", notification=" + notification + "]";
    }
}
