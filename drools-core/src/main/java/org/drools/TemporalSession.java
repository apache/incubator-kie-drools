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
package org.drools;

import org.drools.temporal.SessionClock;

/**
 * A StatefulTemporalSession is a stateful session with
 * additional functionalities for temporal reasoning. 
 * 
 * @author etirelli
 *
 */
public interface TemporalSession<T extends SessionClock> extends StatefulSession {
    
    /**
     * Returns the session clock instance for this session.
     * 
     * The actual session clock implementation is defined by the
     * RuleBaseConfiguration.setSessionClockImpl() call or by
     * setting the corresponding system property.
     */
    public T getSessionClock();
    
}
