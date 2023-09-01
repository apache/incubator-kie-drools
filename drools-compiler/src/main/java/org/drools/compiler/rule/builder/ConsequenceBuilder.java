package org.drools.compiler.rule.builder;

public interface ConsequenceBuilder {

    Long CONSEQUENCE_SERIAL_UID = Long.valueOf(510L);

    void build(final RuleBuildContext context, String name);

}
