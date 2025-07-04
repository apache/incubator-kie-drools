/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.drools.mvel.integrationtests;

import java.io.IOException;
import java.util.Collection;
import java.util.stream.Stream;

import org.drools.drl.parser.DroolsParserException;
import org.drools.kiesession.rulebase.InternalKnowledgeBase;
import org.drools.core.impl.RuleBaseFactory;
import org.drools.testcoverage.common.util.KieBaseTestConfiguration;
import org.drools.testcoverage.common.util.KieBaseUtil;
import org.drools.testcoverage.common.util.KieUtil;
import org.drools.testcoverage.common.util.TestParametersUtil2;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.kie.api.KieBase;
import org.kie.api.KieBaseConfiguration;
import org.kie.api.builder.KieModule;
import org.kie.api.conf.SequentialOption;
import org.kie.api.definition.KiePackage;
import org.kie.api.runtime.KieSession;
import org.kie.internal.builder.KnowledgeBuilderConfiguration;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.internal.builder.conf.DefaultDialectOption;
import org.kie.internal.conf.ShareAlphaNodesOption;
import org.kie.internal.conf.ShareBetaNodesOption;

import static org.assertj.core.api.Assertions.assertThat;

public class FailureOnRemovalTest {

    private static final String  LS                   = System.getProperty( "line.separator" );
    private static final String  PACKAGE              = "failure_on_removal";
    private static final String  RULE_1               = "rule_1";
    private static final String  RULE_2               = "rule_2";
    private static final String  RULE_3               = "rule_3";
    private static final boolean SHARE_BETA_NODES     = true;
    private static final boolean NOT_SHARE_BETA_NODES = false;

    public static Stream<KieBaseTestConfiguration> parameters() {
        return TestParametersUtil2.getKieBaseCloudConfigurations(true).stream();
    }

    @ParameterizedTest(name = "KieBase type={0}")
    @MethodSource("parameters")
    public void testWithBetaNodeSharing(KieBaseTestConfiguration kieBaseTestConfiguration) throws Exception {
        runTest(kieBaseTestConfiguration, SHARE_BETA_NODES );
    }

    @ParameterizedTest(name = "KieBase type={0}")
    @MethodSource("parameters")
    public void testWithoutBetaNodeSharing(KieBaseTestConfiguration kieBaseTestConfiguration) throws Exception {
        runTest(kieBaseTestConfiguration, NOT_SHARE_BETA_NODES );
    }

    private void runTest(KieBaseTestConfiguration kieBaseTestConfiguration, boolean shareBetaNodes) throws Exception {
        InternalKnowledgeBase kbase = (InternalKnowledgeBase) createKnowledgeBase(kieBaseTestConfiguration, shareBetaNodes);
        Collection<KiePackage> rule1 = compileRule(kieBaseTestConfiguration, RULE_1 );
        kbase.addPackages( rule1 );

        // we need to add at least two rules. Test will not fail with only one rule.
        Collection<KiePackage> rule2 = compileRule(kieBaseTestConfiguration, RULE_2 );
        kbase.addPackages( rule2 );

        kbase.removeRule( PACKAGE,
                          RULE_1 );
        
        KieSession ksession = kbase.newKieSession();
        int fired = ksession.fireAllRules();
        ksession.dispose();

        assertThat(fired).isEqualTo(1);

        Collection<KiePackage> rule3 = compileRule(kieBaseTestConfiguration, RULE_3 );
        kbase.addPackages( rule3 );
    }

    private Collection<KiePackage> compileRule(KieBaseTestConfiguration kieBaseTestConfiguration, String name) throws DroolsParserException,
                                                                 IOException {
        String drl = getDrl( name );
        System.out.println(drl);
        return KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, drl).getKiePackages();
    }

    private KnowledgeBuilderConfiguration createKnowledgeBuilderConfiguration() {
        KnowledgeBuilderConfiguration kconf = KnowledgeBuilderFactory.newKnowledgeBuilderConfiguration( null, getClass().getClassLoader() );
        kconf.setOption( DefaultDialectOption.get( "java" ) );
        return kconf;
    }

    private KieBase createKnowledgeBase(KieBaseTestConfiguration kieBaseTestConfiguration, boolean shareBetaNodes) {
        KieBaseConfiguration ruleBaseConfiguration = createKnowledgeBaseConfiguration( shareBetaNodes );
        final KieModule kieModule = KieUtil.getKieModuleFromResources("test", kieBaseTestConfiguration);
        return KieBaseUtil.newKieBaseFromReleaseId(kieModule.getReleaseId(), ruleBaseConfiguration);
    }

    private KieBaseConfiguration createKnowledgeBaseConfiguration(boolean shareBetaNodes) {
        KieBaseConfiguration kconf = RuleBaseFactory.newKnowledgeBaseConfiguration();
        kconf.setOption( SequentialOption.NO );
        kconf.setOption( ShareAlphaNodesOption.YES );
        kconf.setOption( shareBetaNodes ? ShareBetaNodesOption.YES : ShareBetaNodesOption.NO );
        return kconf;
    }

    private String getDrl(String name) {
        return new StringBuffer( "package " ).append( PACKAGE ).append( LS ).append( "rule '" ).append( name ).append( '\'' ).append( LS ).append( "when" ).append( LS ).append( "eval (true)" ).append( LS ).append( "then" ).append( LS ).append( "end" ).append( LS ).toString();
    }
}
