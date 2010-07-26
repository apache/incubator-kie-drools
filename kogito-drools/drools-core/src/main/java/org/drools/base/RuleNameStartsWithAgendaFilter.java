/**
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
 * Filters activations based on a specified rule name prefix.
 * 
 * @author <a href="mailto:simon@redhillconsulting.com.au">Simon Harris </a>
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

    public boolean accept(final Activation activation) {
        if ( activation.getRule().getName().startsWith( this.prefix ) ) {
            return this.accept;
        } else {
            return !this.accept;
        }
    }
}
