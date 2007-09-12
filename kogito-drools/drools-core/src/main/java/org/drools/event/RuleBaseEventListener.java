package org.drools.event;

import java.util.EventListener;

public interface RuleBaseEventListener
    extends
    EventListener {

    /**
     * Method called before a new package is added to the rule base
     * @param event
     */
    void beforePackageAdded(BeforePackageAddedEvent event);

    /**
     * Method called after a new package is added to the rule base
     * @param event
     */
    void afterPackageAdded(AfterPackageAddedEvent event);

    /**
     * Method called before a package is removed from the rule base
     * @param event
     */
    void beforePackageRemoved(BeforePackageRemovedEvent event);

    /**
     * Method called after a package is removed from the rule base
     * @param event
     */
    void afterPackageRemoved(AfterPackageRemovedEvent event);

    /**
     * Method called before a new rule is added to the rule base
     * @param event
     */
    void beforeRuleAdded(BeforeRuleAddedEvent event);

    /**
     * Method called after a new rule is added to the rule base
     * @param event
     */
    void afterRuleAdded(AfterRuleAddedEvent event);

    /**
     * Method called before a rule is removed from the rule base
     * @param event
     */
    void beforeRuleRemoved(BeforeRuleRemovedEvent event);

    /**
     * Method called after a rule is removed from the rule base
     * @param event
     */
    void afterRuleRemoved(AfterRuleRemovedEvent event);

}
