/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.constraint.streams.drools;

import java.util.Arrays;
import java.util.Objects;

import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.AgendaFilter;
import org.optaplanner.constraint.streams.common.ConstraintStreamSession;
import org.optaplanner.constraint.streams.common.inliner.AbstractScoreInliner;
import org.optaplanner.core.api.score.Score;

public final class SessionDescriptor<Score_ extends Score<Score_>> implements ConstraintStreamSession<Score_> {

    private final KieSession session;
    private final AgendaFilter agendaFilter;
    private final AbstractScoreInliner<Score_> scoreInliner;

    public SessionDescriptor(KieSession session, AbstractScoreInliner<Score_> scoreInliner, AgendaFilter agendaFilter) {
        this.session = Objects.requireNonNull(session);
        this.scoreInliner = Objects.requireNonNull(scoreInliner);
        this.agendaFilter = agendaFilter;
    }

    /**
     * @return never null
     */
    public KieSession getSession() {
        return session;
    }

    /**
     * Used to obtain the latest {@link Score} and related information from the session returned by {@link #getSession()}.
     *
     * @return never null
     */
    public AbstractScoreInliner<Score_> getScoreInliner() {
        return scoreInliner;
    }

    /**
     * The purpose of the agenda filter is to determine which rules should run.
     * The agenda filter will prevent rules from firing whose constraint weights are set to zero.
     * Always call {@link KieSession#fireAllRules(AgendaFilter)} on the session returned by {@link #getSession()}
     * with this filter, never without it.
     *
     * @return null when there are no disabled rules
     */
    public AgendaFilter getAgendaFilter() {
        return agendaFilter;
    }

    @Override
    public AbstractScoreInliner<Score_> runAndClose(Object... facts) {
        Arrays.stream(facts).forEach(session::insert);
        session.fireAllRules();
        return scoreInliner;
    }

}
