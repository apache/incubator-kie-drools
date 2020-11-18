/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.examples.machinereassignment.optional.score.drools;

import static java.util.Comparator.comparing;
import static java.util.Comparator.comparingLong;

import java.util.Comparator;
import java.util.Objects;

import org.optaplanner.examples.machinereassignment.domain.MrService;

public class MrServiceMovedProcessesCount implements Comparable<MrServiceMovedProcessesCount> {

    private static final Comparator<MrServiceMovedProcessesCount> COMPARATOR = comparing(
            (MrServiceMovedProcessesCount count) -> count.service, comparingLong(MrService::getId))
                    .thenComparingInt(count -> count.movedProcessesCount);
    private MrService service;
    private int movedProcessesCount;

    public MrServiceMovedProcessesCount(MrService service, int movedProcessesCount) {
        this.service = service;
        this.movedProcessesCount = movedProcessesCount;
    }

    public MrService getService() {
        return service;
    }

    public int getMovedProcessesCount() {
        return movedProcessesCount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final MrServiceMovedProcessesCount other = (MrServiceMovedProcessesCount) o;
        return Objects.equals(service, other.service) &&
                movedProcessesCount == other.movedProcessesCount;
    }

    @Override
    public int hashCode() {
        return Objects.hash(service, movedProcessesCount);
    }

    public Long getServiceId() {
        return service.getId();
    }

    @Override
    public String toString() {
        return service + "=" + movedProcessesCount;
    }

    @Override
    public int compareTo(MrServiceMovedProcessesCount o) {
        return COMPARATOR.compare(this, o);
    }
}
