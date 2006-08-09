package org.nexb.easyeclipse.drools.deployer.actions;

/**
 * Commit it with a non final status
 * @author Michael Neale
 *
 */
public class CommitAsNonFinal extends SetSvnPropertyAndDeployAction {

    public String getPropertyName() {
        return "drools:status";
    }

    public String getPropertyValue() {
        return "draft";
    }

}
