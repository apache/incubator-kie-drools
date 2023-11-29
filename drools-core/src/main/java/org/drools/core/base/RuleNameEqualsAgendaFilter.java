/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.drools.core.base;

import java.io.Serializable;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlRootElement;

import org.kie.api.runtime.rule.AgendaFilter;
import org.kie.api.runtime.rule.Match;

/**
 * Filters activations based on an exact match of a rule name.
 */
@XmlRootElement(name="rule-name-equals-agenda-filter")
@XmlAccessorType(XmlAccessType.NONE)
public class RuleNameEqualsAgendaFilter
    implements
    AgendaFilter, Serializable {

    @XmlAttribute
    private String  name;

    @XmlAttribute
    private boolean accept;

    public RuleNameEqualsAgendaFilter() {
    }

    public RuleNameEqualsAgendaFilter(final String name) {
        this( name,
              true );
    }

    public RuleNameEqualsAgendaFilter(final String name,
                                      final boolean accept) {
        this.name = name;
        this.accept = accept;
    }

    public String getName() {
        return name;
    }

    public boolean isAccept() {
        return accept;
    }

    public boolean accept( Match activation ) {
        if ( activation.getRule().getName().equals( this.name ) ) {
            return this.accept;
        } else {
            return !this.accept;
        }
    }
}
