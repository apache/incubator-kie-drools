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

package org.drools.core.event;

import org.kie.api.runtime.rule.Match;
import org.kie.internal.event.rule.RuleEventListener;

public class RuleEventListenerSupport extends AbstractEventSupport<RuleEventListener> {

    public void onBeforeMatchFire(Match match) {
        notifyAllListeners( l -> l.onBeforeMatchFire(match) );
    }

    public void onAfterMatchFire(Match match) {
        notifyAllListeners( l -> l.onAfterMatchFire(match) );
    }

    public void onDeleteMatch(Match match) {
        notifyAllListeners( l -> l.onDeleteMatch(match) );
    }

    public void onUpdateMatch(Match match) {
        notifyAllListeners( l -> l.onUpdateMatch(match) );
    }
}
