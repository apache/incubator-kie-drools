/*
 * Copyright 2005 JBoss Inc
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

package org.drools.base;

import org.drools.spi.Activation;
import org.drools.spi.AgendaFilter;

/**
 * Filters activations based on an exact match of a rule name.
 */
public class RuleNameEqualsAgendaFilter
    implements
    AgendaFilter {
    private final String  name;

    private final boolean accept;

    public RuleNameEqualsAgendaFilter(final String name) {
        this( name,
              true );
    }

    public RuleNameEqualsAgendaFilter(final String name,
                                      final boolean accept) {
        this.name = name;
        this.accept = accept;
    }

    public boolean accept(final Activation activation) {
        if ( activation.getRule().getName().equals( this.name ) ) {
            return this.accept;
        } else {
            return !this.accept;
        }
    }
}
