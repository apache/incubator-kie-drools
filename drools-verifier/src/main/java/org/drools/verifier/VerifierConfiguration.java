package org.drools.verifier;

import java.util.List;
import java.util.Map;

import org.drools.PropertiesConfiguration;
import org.drools.builder.ResourceType;
import org.drools.io.Resource;

public interface VerifierConfiguration
    extends
    PropertiesConfiguration {

    public final static String VERIFYING_SCOPE_SINGLE_RULE       = "single-rule";
    public final static String VERIFYING_SCOPE_DECISION_TABLE    = "decision-table";
    public final static String VERIFYING_SCOPE_KNOWLEDGE_PACKAGE = "knowledge-package";

    /**
     * Add external analyzing rules to verifier.
     * 
     * @param resource
     *            the Resource to add
     * @param type
     *            the resource type
     */
    Map<Resource, ResourceType> getVerifyingResources();

    /**
     * Set verifying scope.<br>
     * <br>
     * single-rule - Verifies an single rule <br>
     * decision-table - Verifies a decision table <br>
     * knowledge-package - Verifies everything
     * 
     */
    public void addVerifyingScopes(String scope);

    public List<String> getVerifyingScopes();

    public void setAcceptRulesWithoutVerifiyingScope(boolean accept);

    public boolean acceptRulesWithoutVerifiyingScope();
}
