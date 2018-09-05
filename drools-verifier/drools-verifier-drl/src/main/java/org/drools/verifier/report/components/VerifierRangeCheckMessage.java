/*
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

public class VerifierRangeCheckMessage extends VerifierMessageBase {
    private static final long        serialVersionUID = 510l;

    private Collection<MissingRange> causes;

    public VerifierRangeCheckMessage(Severity severity,
                                     Cause faulty,
                                     String message,
                                     Collection<MissingRange> causes) {
        super( new HashMap<String, String>(),
               severity,
               MessageType.RANGE_CHECK,
               faulty,
               message );

        this.causes = causes;
    }

    public Collection<MissingRange> getMissingRanges() {
        return causes;
    }

    public Collection<Cause> getCauses() {
        Collection<Cause> causes = new ArrayList<Cause>();
        for ( Cause cause : this.causes ) {
            causes.add( cause );
        }
        return causes;
    }

    public void setCauses(Collection<MissingRange> reasons) {
        this.causes = reasons;
    }
}
