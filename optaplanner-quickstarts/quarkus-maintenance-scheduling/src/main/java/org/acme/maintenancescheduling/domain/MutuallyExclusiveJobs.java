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

package org.acme.maintenancescheduling.domain;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotNull;
import java.util.Arrays;
import java.util.List;

@Entity
public class MutuallyExclusiveJobs extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @NotNull
    private Long id;

    @OneToMany(fetch = FetchType.EAGER)
    private List<MaintenanceJob> mutexJobs;

    public MutuallyExclusiveJobs() {
    }

    public MutuallyExclusiveJobs(MaintenanceJob... mutexJobs) {
        this.mutexJobs = Arrays.asList(mutexJobs);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<MaintenanceJob> getMutexJobs() {
        return mutexJobs;
    }

    public void setMutexJobs(List<MaintenanceJob> mutexJobs) {
        this.mutexJobs = mutexJobs;
    }

    // ************************************************************************
    // Complex methods
    // ************************************************************************

    public boolean isMutuallyExclusive(MaintenanceJob maintenanceJob, MaintenanceJob otherJob) {
        if (mutexJobs.contains(maintenanceJob) && mutexJobs.contains(otherJob)) {
            return true;
        }
        return false;
    }

    @Override
    public String toString() {
        return "MutuallyExclusiveJobs{" +
                "id=" + id +
                ", mutexJobs=" + mutexJobs +
                '}';
    }
}
