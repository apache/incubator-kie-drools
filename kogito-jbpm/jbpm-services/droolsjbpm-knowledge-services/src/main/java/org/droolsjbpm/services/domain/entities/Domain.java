/*
 * Copyright 2013 JBoss by Red Hat.
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
package org.droolsjbpm.services.domain.entities;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

/**
 *
 * @author salaboy
 */
@Entity
@Table(name="Domain")
@SequenceGenerator(name="domainIdSeq", sequenceName="DOMAIN_ID_SEQ", allocationSize=1)
public class Domain implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator="domainIdSeq")
    private long id;
    
    private String name;
    
    @ManyToOne(cascade = CascadeType.ALL)
    private Organization organization;
    @OneToMany(cascade = CascadeType.ALL, targetEntity=RuntimeId.class)
    @JoinColumn(name = "Runtime_Id", nullable = true)
    private List<RuntimeId> runtimes = Collections.emptyList();

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Organization getOrganization() {
        return organization;
    }

    public void setOrganization(Organization organization) {
        this.organization = organization;
    }

    public List<RuntimeId> getRuntimes() {
        return runtimes;
    }

    public void setRuntimes(List<RuntimeId> runtimes) {
        this.runtimes = runtimes;
    }
    
    
    
    
}
