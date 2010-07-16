/**
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

package org.drools.verifier.data;

import java.util.Collection;

import org.drools.verifier.report.components.Gap;
import org.drools.verifier.report.components.MissingNumberPattern;
import org.drools.verifier.report.components.MissingRange;
import org.drools.verifier.report.components.Severity;
import org.drools.verifier.report.components.VerifierMessageBase;

/**
 * 
 * @author Toni Rikkola
 */
public interface VerifierReport {

    public void add(Gap gap);

    public void remove(Gap gap);

    public void add(MissingNumberPattern missingNumberPattern);

    public VerifierData getVerifierData(VerifierData data);

    public VerifierData getVerifierData();

    public Collection<MissingRange> getRangeCheckCauses();

    public Collection<Gap> getGapsByFieldId(String fieldId);

    public void add(VerifierMessageBase note);

    /**
     * Return all the items that have given severity value.
     * 
     * @param severity
     *            Severity level of item.
     * @return Collection of items or an empty list if none was found.
     */
    public Collection<VerifierMessageBase> getBySeverity(Severity severity);

    public Collection<MissingRange> getRangeCheckCausesByFieldPath(String path);

}
