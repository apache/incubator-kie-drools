/*
 * Copyright 2005 Red Hat, Inc. and/or its affiliates.
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

package org.drools.core.base;

import org.drools.core.spi.Activation;
import org.kie.api.runtime.rule.AgendaFilter;
import org.kie.api.runtime.rule.Match;

/**
 * Filters activations based on a specified rule name prefix.
 */
public class RuleNameStartsWithAgendaFilter
    implements
    AgendaFilter {
    private final String  prefix;

    private final boolean accept;

    public RuleNameStartsWithAgendaFilter(final String prefix) {
        this( prefix,
              true );
    }

    public RuleNameStartsWithAgendaFilter(final String prefix,
                                          final boolean accept) {
        this.prefix = prefix;
        this.accept = accept;
    }

    public String getPrefix() {
        return prefix;
    }

    public boolean isAccept() {
        return accept;
    }

    public boolean accept( Match activation ) {
        if ( activation.getRule().getName().startsWith( this.prefix ) ) {
            return this.accept;
        } else {
            return !this.accept;
        }
    }
}
