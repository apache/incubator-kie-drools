package org.nexb.easyeclipse.drools.deployer.actions;

/**
 * Commit it as a final resource (for deployment).
 * @author Michael Neale
 */
public class CommitAsFinal extends SetSvnPropertyAndDeployAction {

    public String getPropertyName() {
        return "drools:status";
    }

    public String getPropertyValue() {
        return "production";
    }

}
