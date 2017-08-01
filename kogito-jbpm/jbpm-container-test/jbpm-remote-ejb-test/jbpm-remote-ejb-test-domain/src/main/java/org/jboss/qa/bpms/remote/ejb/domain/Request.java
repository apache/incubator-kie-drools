/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jboss.qa.bpms.remote.ejb.domain;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Request implements Serializable {

    /**
     * Default ID.
     */
    private static final long serialVersionUID = 1L;

    @XmlAttribute(required = true)
    private String id;
    
    @XmlAttribute(required = true)
    private String personId;
    private Integer amount;
    private boolean valid;
    private String invalidReason;
    private boolean canceled;

    public Request() {
        // default required for JAXB
    }

    public Request(String id) {
        this.id = id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public String getPersonId() {
        return personId;
    }

    public void setPersonId(String personId) {
        this.personId = personId;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public void setInvalid(String reason) {
        this.valid = false;
        this.invalidReason = reason;
    }

    public boolean isValid() {
        return valid;
    }

    public void setValid() {
        this.valid = true;
    }

    public String getInvalidReason() {
        return invalidReason;
    }

    public boolean isCanceled() {
        return canceled;
    }

    public void setCanceled(boolean canceled) {
        this.canceled = canceled;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Request) {
            return this.id.equals(((Request) o).id);
        }
        return false;
    }

    public int hashCode() {
        return this.id.hashCode();
    }

}

