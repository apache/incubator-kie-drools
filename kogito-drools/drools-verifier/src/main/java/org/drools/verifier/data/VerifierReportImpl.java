package org.drools.verifier.data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.drools.verifier.report.components.Gap;
import org.drools.verifier.report.components.MissingNumberPattern;
import org.drools.verifier.report.components.RangeCheckCause;
import org.drools.verifier.report.components.Severity;
import org.drools.verifier.report.components.VerifierMessageBase;

/**
 * 
 * @author Toni Rikkola
 */
class VerifierReportImpl
    implements
    VerifierReport {
    private static final long                       serialVersionUID               = -6207688526236713721L;

    private Map<String, Gap>                        gapsById                       = new TreeMap<String, Gap>();
    private DataTree<String, Gap>                   gapsByFieldId                  = new DataTree<String, Gap>();
    private Map<String, MissingNumberPattern>       missingNumberPatternsById      = new TreeMap<String, MissingNumberPattern>();
    private DataTree<String, MissingNumberPattern>  missingNumberPatternsByFieldId = new DataTree<String, MissingNumberPattern>();

    private List<VerifierMessageBase>               messages                       = new ArrayList<VerifierMessageBase>();
    private DataTree<Severity, VerifierMessageBase> messagesBySeverity             = new DataTree<Severity, VerifierMessageBase>();

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
        Collection<VerifierMessageBase> result = messagesBySeverity.getBranch( severity );

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
        gapsByFieldId.put( gap.getField().getGuid(),
                           gap );
    }

    public void remove(Gap gap) {
        gapsById.remove( gap.getGuid() );

        gapsByFieldId.remove( gap.getField().getGuid(),
                              gap );
    }

    public Collection<Gap> getGapsByFieldId(String fieldId) {
        return gapsByFieldId.getBranch( fieldId );
    }

    public Collection<RangeCheckCause> getRangeCheckCauses() {
        Collection<RangeCheckCause> result = new ArrayList<RangeCheckCause>();

        result.addAll( gapsById.values() );
        result.addAll( missingNumberPatternsById.values() );

        return result;
    }

    public void add(MissingNumberPattern missingNumberPattern) {
        missingNumberPatternsById.put( missingNumberPattern.getGuid(),
                                       missingNumberPattern );

        // Put by field id.
        missingNumberPatternsByFieldId.put( missingNumberPattern.getField().getGuid(),
                                            missingNumberPattern );
    }

    public Collection<RangeCheckCause> getRangeCheckCausesByFieldId(String id) {
        Collection<RangeCheckCause> result = new ArrayList<RangeCheckCause>();

        result.addAll( gapsByFieldId.getBranch( id ) );

        result.addAll( missingNumberPatternsByFieldId.getBranch( id ) );

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

    public Collection<Gap> getGapsByFieldId(int fieldId) {
        // TODO Auto-generated method stub
        return null;
    }

    public Collection<RangeCheckCause> getRangeCheckCausesByFieldId(int id) {
        // TODO Auto-generated method stub
        return null;
    }
}
