package org.drools.verifier.components;

import org.drools.verifier.report.components.Cause;
import org.drools.verifier.report.components.CauseType;

/**
 *
 * @author Toni Rikkola
 */
public class Pattern extends PatternComponent
    implements
    Cause {

    private static final long     serialVersionUID = 5852308145251025423L;

    private String                objectTypeGuid;
    private String                name;
    private VerifierComponentType sourceType       = VerifierComponentType.UNKNOWN;
    private String                sourceGuid;

    private boolean               isPatternNot     = false;
    private boolean               isPatternExists  = false;
    private boolean               isPatternForall  = false;

    public CauseType getCauseType() {
        return CauseType.PATTERN;
    }

    public boolean isPatternNot() {
        return isPatternNot;
    }

    public void setPatternNot(boolean isNot) {
        this.isPatternNot = isNot;
    }

    public boolean isPatternExists() {
        return isPatternExists;
    }

    public void setPatternExists(boolean isExists) {
        this.isPatternExists = isExists;
    }

    public String getObjectTypeGuid() {
        return objectTypeGuid;
    }

    public void setObjectTypeGuid(String guid) {
        this.objectTypeGuid = guid;
    }

    public boolean isPatternForall() {
        return isPatternForall;
    }

    public void setPatternForall(boolean isForall) {
        this.isPatternForall = isForall;
    }

    public String getSourceGuid() {
        return sourceGuid;
    }

    public void setSourceGuid(String sourceGuid) {
        this.sourceGuid = sourceGuid;
    }

    public VerifierComponentType getSourceType() {
        return sourceType;
    }

    public void setSourceType(VerifierComponentType sourceType) {
        this.sourceType = sourceType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Pattern, name: " + name;
    }

    public VerifierComponentType getVerifierComponentType() {
        return VerifierComponentType.PATTERN;
    }

}
