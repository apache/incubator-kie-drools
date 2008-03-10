/*
 * Copyright 2007 JBoss Inc
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
 *
 * Created on Nov 28, 2007
 */
package org.drools.reteoo;

import org.drools.TemporalSession;
import org.drools.common.InternalRuleBase;
import org.drools.concurrent.ExecutorService;
import org.drools.rule.EntryPoint;
import org.drools.temporal.SessionClock;

/**
 * ReteooStatefulTemporalSession implements a temporal enabled session
 * for Reteoo rulebases
 * 
 * @author etirelli
 */
public class ReteooTemporalSession<T extends SessionClock> extends ReteooStatefulSession
    implements
    TemporalSession<T> {

    private static final long serialVersionUID = -2129661675928809928L;

    private T                 sessionClock;

    public ReteooTemporalSession(final int id,
                                 final InternalRuleBase ruleBase,
                                 final ExecutorService executorService,
                                 final T clock) {
        super( id,
               ruleBase,
               executorService );
        this.sessionClock = clock;
    }

    public T getSessionClock() {
        return this.sessionClock;
    }

}
