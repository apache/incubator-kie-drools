/*
 * Copyright 2010 JBoss Inc
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

package org.drools.verifier.report.components;

import java.util.Arrays;
import java.util.Collection;

/**
 * Pattern, rule or similar that is always satisfied.
 * 
 * @author trikkola
 * 
 */
public class AlwaysTrue
    implements
    Reason,
    Cause {

    private static int              index = 0;

    private final String            path  = String.valueOf( index++ );

    private final Cause             impactedComponent;

    private final Collection<Cause> causes;

    /**
     * 
     * @param cause
     *            Component that is always satisfied.
     */
    public AlwaysTrue(Cause cause,
                      Collection<Cause> causes) {
        this.impactedComponent = cause;
        this.causes = causes;
    }

    public AlwaysTrue(Cause cause,
                      Cause... causes) {
        this.impactedComponent = cause;
        this.causes = Arrays.asList( causes );
    }

    public ReasonType getReasonType() {
        return ReasonType.ALWAYS_TRUE;
    }

    public String getPath() {
        return path;
    }

    public Cause getCause() {
        return impactedComponent;
    }

    public Collection<Cause> getCauses() {
        return causes;
    }

}
