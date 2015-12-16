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

import org.kie.api.runtime.rule.FactHandle;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.event.*;
import org.drools.core.spi.PropagationContext;
import org.kie.api.runtime.rule.Match;
import org.kie.internal.event.rule.RuleEventListener;

import java.util.Iterator;

public class RuleEventListenerSupport extends AbstractEventSupport<RuleEventListener> {

//    public void onMatch(Match match) {
//        final Iterator<RuleEventListener> iter = getEventListenersIterator();
//        while ( iter.hasNext() ) {
//            RuleEventListener listener = iter.next();
//            listener.onMatch(match);
//        }
//    }
//
//    public void onReMatch(Match match) {
//        final Iterator<RuleEventListener> iter = getEventListenersIterator();
//        while ( iter.hasNext() ) {
//            RuleEventListener listener = iter.next();
//            listener.onReMatch(match);
//        }
//    }
//
//    public void onUnMatch(Match match) {
//        final Iterator<RuleEventListener> iter = getEventListenersIterator();
//        while ( iter.hasNext() ) {
//            RuleEventListener listener = iter.next();
//            listener.onUnMatch(match);
//        }
//    }
//
//    public void onFiring(Match match) {
//        final Iterator<RuleEventListener> iter = getEventListenersIterator();
//        while ( iter.hasNext() ) {
//            RuleEventListener listener = iter.next();
//            listener.onFiring(match);
//        }
//    }
//
//    public void onFired(Match match) {
//        final Iterator<RuleEventListener> iter = getEventListenersIterator();
//        while ( iter.hasNext() ) {
//            RuleEventListener listener = iter.next();
//            listener.onFired(match);
//        }
//    }

    public void reset() {
        this.clear();
    }
}
