package org.drools.event.knowledgebase;

import java.util.EventListener;

public interface KnowledgeBaseEventListener
    extends
    EventListener {
    /**
     * Method called before a new package is added to the rule base
     * @param event
     */
    void beforeKnowledgePackageAdded(BeforeKnowledgePackageAddedEvent event);

    /**
     * Method called after a new package is added to the rule base
     * @param event
     */
    void afterKnowledgePackageAdded(AfterKnowledgePackageAddedEvent event);

    /**
     * Method called before a package is removed from the rule base
     * @param event
     */
    void beforeKnowledgePackageRemoved(BeforeKnowledgePackageRemovedEvent event);

    /**
     * Method called after a package is removed from the rule base
     * @param event
     */
    void afterKnowledgePackageRemoved(AfterKnowledgePackageRemovedEvent event);

    /**
     * Method called before a rule base is locked
     * @param event
     */
    void beforeKnowledgeBaseLocked(BeforeKnowledgeBaseLockedEvent event);

    /**
     * Method called after a rule base is locked
     * @param event
     */
    void afterKnowledgeBaseLocked(AfterKnowledgeBaseLockedEvent event);

    /**
     * Method called before a rule base is unlocked
     * @param event
     */
    void beforeKnowledgeBaseUnlocked(BeforeKnowledgeBaseUnlockedEvent event);

    /**
     * Method called after a rule base is unlocked
     * @param event
     */
    void afterKnowledgeBaseUnlocked(AfterKnowledgeBaseUnlockedEvent event);

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

    /**
     * Method called before a function is removed from the rule base
     * @param event
     */
    void beforeFunctionRemoved(BeforeFunctionRemovedEvent event);

    /**
     * Method called after a function is removed from the rule base
     * @param event
     */
    void afterFunctionRemoved(AfterFunctionRemovedEvent event);
}
