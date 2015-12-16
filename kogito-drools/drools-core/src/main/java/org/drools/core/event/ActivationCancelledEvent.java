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

import org.drools.core.spi.Activation;
import org.kie.api.event.rule.MatchCancelledCause;

public class ActivationCancelledEvent extends ActivationEvent {
    private MatchCancelledCause cause;
    
    private static final long serialVersionUID = 510l;

    public ActivationCancelledEvent(final Activation activation, MatchCancelledCause cause) {
        super( activation );
        this.cause = cause;
    }
    
    public MatchCancelledCause getCause() {
        return cause;
    }

    public String toString() {
        return "<==[ActivationCancelled(" + getActivation().getActivationNumber() + "): rule=" + getActivation().getRule().getName() + "; tuple=" + getActivation().getTuple() + "]";
        //return "<==[ActivationCancelled: rule=" + getActivation().getRule().getName() + "]";
    }
}
