package org.drools.verifier.components;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.drools.verifier.components.Consequence.ConsequenceType;
import org.drools.verifier.report.components.Cause;
import org.drools.verifier.report.components.CauseType;

/**
 *
 * @author Toni Rikkola
 */
public class VerifierRule extends RuleComponent
    implements
    Cause {

    private Map<String, String>       attributes      = new HashMap<String, String>();

    private String                    consequenceGuid;
    private ConsequenceType           consequenceType;
    private int                       lineNumber;

    private int                       packageId;

    private Collection<String>        header          = new ArrayList<String>();

    private Collection<String>        lhsRows         = new ArrayList<String>();

    private Collection<String>        rhsRows         = new ArrayList<String>();

    private String                    description;

    private Map<String, String>       metadata        = new HashMap<String, String>();

    private Collection<String>        commentMetadata = new ArrayList<String>();

    private Map<String, List<String>> otherInfo       = new HashMap<String, List<String>>();

    public CauseType getCauseType() {
        return CauseType.RULE;
    }

    public void setAttributes(Map<String, String> attributes) {
        this.attributes = attributes;
    }

    public Map<String, String> getAttributes() {
        return attributes;
    }

    public String getConsequenceGuid() {
        return consequenceGuid;
    }

    public void setConsequenceGuid(String consequenceGuid) {
        this.consequenceGuid = consequenceGuid;
    }

    public ConsequenceType getConsequenceType() {
        return consequenceType;
    }

    public void setConsequenceType(ConsequenceType consequenceType) {
        this.consequenceType = consequenceType;
    }

    public int getLineNumber() {
        return lineNumber;
    }

    public void setLineNumber(int lineNumber) {
        this.lineNumber = lineNumber;
    }

    @Override
    public String toString() {
        return "Rule '" + getRuleName() + "'";
    }

    public int getPackageId() {
        return packageId;
    }

    public void setPackageId(int packageId) {
        this.packageId = packageId;
    }

    public VerifierComponentType getVerifierComponentType() {
        return VerifierComponentType.RULE;
    }

    public Collection<String> getHeader() {
        return header;
    }

    public Collection<String> getLhsRows() {
        return lhsRows;
    }

    public Collection<String> getRhsRows() {
        return rhsRows;
    }

    public Map<String, String> getMetadata() {
        return metadata;
    }

    public Collection<String> getCommentMetadata() {
        return commentMetadata;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public Map<String, List<String>> getOtherInfo() {
        return otherInfo;
    }
}
