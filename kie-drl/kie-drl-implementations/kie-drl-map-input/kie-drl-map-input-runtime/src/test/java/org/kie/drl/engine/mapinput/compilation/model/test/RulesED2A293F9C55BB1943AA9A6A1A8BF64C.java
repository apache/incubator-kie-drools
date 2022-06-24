package org.kie.drl.engine.mapinput.compilation.model.test;

import org.drools.modelcompiler.dsl.pattern.D;

public class RulesED2A293F9C55BB1943AA9A6A1A8BF64C implements org.drools.model.Model {

    public final static java.time.format.DateTimeFormatter DATE_TIME_FORMATTER = new java.time.format.DateTimeFormatterBuilder().parseCaseInsensitive().appendPattern(DateUtils.getDateFormatMask()).toFormatter(java.util.Locale.ENGLISH);

    @Override
    public String getName() {
        return "org.kie.drl.engine.mapinput.compilation.model.test";
    }

    @Override
    public java.util.List<org.drools.model.EntryPoint> getEntryPoints() {
        return java.util.Collections.emptyList();
    }

    @Override
    public java.util.List<org.drools.model.Global> getGlobals() {
        return globals;
    }

    @Override
    public java.util.List<org.drools.model.TypeMetaData> getTypeMetaDatas() {
        return typeMetaDatas;
    }

    public static final org.drools.model.Global<Integer> var_maxAmount = D.globalOf(Integer.class,
                                                                                              "org.kie.drl.engine.mapinput.compilation.model.test",
                                                                                              "maxAmount");

    public static final org.drools.model.Global<java.util.List> var_approvedApplications = D.globalOf(java.util.List.class,
                                                                                                      "org.kie.drl.engine.mapinput.compilation.model.test",
                                                                                                      "approvedApplications");

    java.util.List<org.drools.model.Global> globals = new java.util.ArrayList<>();

    java.util.List<org.drools.model.TypeMetaData> typeMetaDatas = java.util.Collections.emptyList();

    /**
     * With the following expression ID:
     * org.drools.modelcompiler.builder.generator.DRLIdGenerator@63071652
     */
    @Override
    public java.util.List<org.drools.model.Rule> getRules() {
        return rules;
    }

    public java.util.List<org.drools.model.Rule> getRulesList() {
        return java.util.Arrays.asList(RulesED2A293F9C55BB1943AA9A6A1A8BF64CRuleMethods0.rule_SmallDepositApprove(),
                                       RulesED2A293F9C55BB1943AA9A6A1A8BF64CRuleMethods0.rule_SmallDepositReject(),
                                       RulesED2A293F9C55BB1943AA9A6A1A8BF64CRuleMethods0.rule_LargeDepositApprove(),
                                       RulesED2A293F9C55BB1943AA9A6A1A8BF64CRuleMethods0.rule_LargeDepositReject(),
                                       RulesED2A293F9C55BB1943AA9A6A1A8BF64CRuleMethods0.rule_NotAdultApplication(),
                                       RulesED2A293F9C55BB1943AA9A6A1A8BF64CRuleMethods0.rule_CollectApprovedApplication());
    }

    java.util.List<org.drools.model.Rule> rules = getRulesList();

    @Override
    public java.util.List<org.drools.model.Query> getQueries() {
        return java.util.Collections.emptyList();
    }

    {
        globals.add(var_maxAmount);
        globals.add(var_approvedApplications);
    }
}
