package org.drools.rule.builder;

public interface ConsequenceBuilder {

    Long CONSEQUENCE_SERIAL_UID = new Long(510L);

    void build(final RuleBuildContext context, String name);

}