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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.drools.core.spi.Activation;
import org.kie.api.runtime.rule.AgendaFilter;
import org.kie.api.runtime.rule.Match;

/**
 * Filters activations based on a specified regular expression.
 */
public class RuleNameMatchesAgendaFilter
    implements
    AgendaFilter {
    private final Pattern pattern;

    private final boolean accept;

    public RuleNameMatchesAgendaFilter(final String regexp) {
        this( regexp,
              true );
    }

    public RuleNameMatchesAgendaFilter(final String regexp,
                                 final boolean accept) {
        this.pattern = Pattern.compile( regexp );
        this.accept = accept;
    }

    public Pattern getPattern() {
        return pattern;
    }

    public boolean isAccept() {
        return accept;
    }

    public boolean accept( Match activation ) {
        Matcher matcher = pattern.matcher( activation.getRule().getName() );
        if ( matcher.matches() ) {
            return this.accept;
        } else {
            return !this.accept;
        }
    }
}
