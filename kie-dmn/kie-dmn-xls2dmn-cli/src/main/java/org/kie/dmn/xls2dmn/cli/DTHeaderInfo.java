package org.kie.dmn.xls2dmn.cli;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class DTHeaderInfo {

    private final String sheetName;
    private final List<String> original;
    private final int hIndex;
    private final Collection<String> requiredInput;
    private final Collection<String> requiredDecision;

    public DTHeaderInfo(String sheetName, List<String> original, int hIndex, List<String> requiredInput, List<String> requiredDecision) {
        this.sheetName = sheetName;
        this.original = new ArrayList<>(original);
        this.hIndex = hIndex;
        this.requiredInput = new ArrayList<>(requiredInput);
        this.requiredDecision = new ArrayList<>(requiredDecision);
    }

    @Override
    public String toString() {
        return "DTHeaderInfo [hIndex=" + hIndex + ", original=" + original + ", requiredDecision=" + requiredDecision + ", requiredInput=" + requiredInput + ", sheetName=" + sheetName + "]";
    }

    public String getSheetName() {
        return sheetName;
    }

    public List<String> getOriginal() {
        return original;
    }

    public int gethIndex() {
        return hIndex;
    }

    public Collection<String> getRequiredInput() {
        return requiredInput;
    }

    public Collection<String> getRequiredDecision() {
        return requiredDecision;
    }

}
