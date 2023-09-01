package org.drools.verifier.components;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.drools.drl.ast.descr.PackageDescr;
import org.drools.verifier.data.VerifierComponent;

public class RulePackage extends VerifierComponent<PackageDescr> {

    private int                       offset    = 0;
    private String                    name;
    private Set<VerifierRule>         rules     = new HashSet<>();
    private List<String>              globals   = new ArrayList<>();
    private String                    description;
    private List<String>              metadata  = new ArrayList<>();
    private Map<String, List<String>> otherInfo = new HashMap<>();

    
    public RulePackage(PackageDescr descr) {
       super(descr);
    }
    public int getOffset() {
        offset++;
        return offset % 2;
    }

    @Override
    public String getPath() {
        return String.format( "package[@name=%s]",
                              getName() );
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<VerifierRule> getRules() {
        return rules;
    }

    public void setRules(Set<VerifierRule> rules) {
        this.rules = rules;
    }

    public VerifierComponentType getVerifierComponentType() {
        return VerifierComponentType.RULE_PACKAGE;
    }

    public List<String> getGlobals() {
        return globals;
    }

    public List<String> getMetadata() {
        return metadata;
    }

    public Map<String, List<String>> getOtherInfo() {
        return otherInfo;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
