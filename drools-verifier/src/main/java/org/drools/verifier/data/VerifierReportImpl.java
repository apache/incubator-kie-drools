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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.drools.verifier.report.components.Gap;
import org.drools.verifier.report.components.MissingNumberPattern;
import org.drools.verifier.report.components.MissingRange;
import org.drools.verifier.report.components.Severity;
import org.drools.verifier.report.components.VerifierMessageBase;

import com.google.common.collect.Multimap;
import com.google.common.collect.TreeMultimap;

/**
 * 
 * @author Toni Rikkola
 */
public class VerifierReportImpl
    implements
    VerifierReport {
    private static final long                       serialVersionUID               = 510l;

    private Map<String, Gap>                        gapsById                       = new TreeMap<String, Gap>();
    private Multimap<String, Gap>                   gapsByFieldId                  = TreeMultimap.create();
    private Map<String, MissingNumberPattern>       missingNumberPatternsById      = new TreeMap<String, MissingNumberPattern>();
    private Multimap<String, MissingNumberPattern>  missingNumberPatternsByFieldId = TreeMultimap.create();

    private List<VerifierMessageBase>               messages                       = new ArrayList<VerifierMessageBase>();
    private Multimap<Severity, VerifierMessageBase> messagesBySeverity             = TreeMultimap.create();

    private VerifierData                            data;

    public VerifierReportImpl(VerifierData data) {
        this.data = data;
    }

    public void add(VerifierMessageBase message) {
        messages.add( message );
        messagesBySeverity.put( message.getSeverity(),
                                message );
    }

    public Collection<VerifierMessageBase> getBySeverity(Severity severity) {
        Collection<VerifierMessageBase> result = messagesBySeverity.get( severity );

        if ( result == null ) {
            return Collections.emptyList();
        } else {
            return result;
        }
    }

    public void add(Gap gap) {
        gapsById.put( gap.getGuid(),
                      gap );

        // Put by field id.
        gapsByFieldId.put( gap.getField().getPath(),
                           gap );
    }

    public void remove(Gap gap) {
        gapsById.remove( gap.getGuid() );

        gapsByFieldId.remove( gap.getField().getPath(),
                              gap );
    }

    public Collection<Gap> getGapsByFieldId(String fieldId) {
        return gapsByFieldId.get( fieldId );
    }

    public Collection<MissingRange> getRangeCheckCauses() {
        Collection<MissingRange> result = new ArrayList<MissingRange>();

        result.addAll( gapsById.values() );
        result.addAll( missingNumberPatternsById.values() );

        return result;
    }

    public void add(MissingNumberPattern missingNumberPattern) {
        missingNumberPatternsById.put( missingNumberPattern.getGuid(),
                                       missingNumberPattern );

        // Put by field id.
        missingNumberPatternsByFieldId.put( missingNumberPattern.getField().getPath(),
                                            missingNumberPattern );
    }

    public Collection<MissingRange> getRangeCheckCausesByFieldPath(String id) {
        Collection<MissingRange> result = new ArrayList<MissingRange>();

        result.addAll( gapsByFieldId.get( id ) );

        result.addAll( missingNumberPatternsByFieldId.get( id ) );

        return result;
    }

    public VerifierData getVerifierData() {
        return data;
    }

    public void setVerifierData(VerifierData data) {
        this.data = data;
    }

    public VerifierData getVerifierData(VerifierData data) {
        return this.data;
    }

}
