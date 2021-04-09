/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.core.impl.score.stream.drools;

import org.drools.core.common.AgendaItem;
import org.kie.api.runtime.rule.Match;
import org.kie.internal.event.rule.RuleEventListener;
import org.optaplanner.core.impl.score.inliner.UndoScoreImpacter;

final class OptaPlannerRuleEventListener implements RuleEventListener {

    @Override
    public void onUpdateMatch(Match match) {
        undoPreviousMatch(match);
    }

    @Override
    public void onDeleteMatch(Match match) {
        undoPreviousMatch(match);
    }

    private static void undoPreviousMatch(Match match) {
        AgendaItem<?> agendaItem = (AgendaItem<?>) match;
        UndoScoreImpacter callback = (UndoScoreImpacter) agendaItem.getCallback();
        callback.run();
        agendaItem.setCallback(null);
    }

}
