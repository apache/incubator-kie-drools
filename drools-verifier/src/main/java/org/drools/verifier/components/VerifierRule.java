package org.drools.verifier.components;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.drools.verifier.components.Consequence.ConsequenceType;
import org.drools.verifier.report.components.Cause;

/**
 *
 * @author Toni Rikkola
 */
public class VerifierRule extends PackageComponent
    implements
    Cause {

    private Map<String, String>       attributes      = new HashMap<String, String>();

    private String                    consequencePath;
    private ConsequenceType           consequenceType;
    private int                       lineNumber;

    private int                       packageId;

    private String                    name;

    private Collection<String>        header          = new ArrayList<String>();

    private Collection<String>        lhsRows         = new ArrayList<String>();

    private Collection<String>        rhsRows         = new ArrayList<String>();

    private String                    description;

    private Map<String, String>       metadata        = new HashMap<String, String>();

    private Collection<String>        commentMetadata = new ArrayList<String>();

    private Map<String, List<String>> otherInfo       = new HashMap<String, List<String>>();

    private int                       offset          = 0;

    public VerifierRule(RulePackage rulePackage) {
        super( rulePackage );
    }

    @Override
    public String getPath() {
        return String.format( "%s.rule[name=%s]",
                              getPackagePath(),
                              getName() );
    }

    public int getOffset() {
        offset++;
        return offset % 2;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setAttributes(Map<String, String> attributes) {
        this.attributes = attributes;
    }

    public Map<String, String> getAttributes() {
        return attributes;
    }

    public String getConsequencePath() {
        return consequencePath;
    }

    public void setConsequencePath(String consequencePath) {
        this.consequencePath = consequencePath;
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
        return "Rule '" + getName() + "'";
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
