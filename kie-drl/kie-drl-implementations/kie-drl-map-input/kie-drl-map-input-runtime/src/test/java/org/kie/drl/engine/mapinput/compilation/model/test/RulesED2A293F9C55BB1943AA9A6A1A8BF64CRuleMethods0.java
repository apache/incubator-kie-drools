/**
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
package org.kie.drl.engine.mapinput.compilation.model.test;

import org.drools.modelcompiler.dsl.pattern.D;
import org.drools.model.Index.ConstraintType;

import static org.kie.drl.engine.mapinput.compilation.model.test.RulesED2A293F9C55BB1943AA9A6A1A8BF64C.*;

public class RulesED2A293F9C55BB1943AA9A6A1A8BF64CRuleMethods0 {

    /**
     * Rule name: SmallDepositApprove
     */
    public static org.drools.model.Rule rule_SmallDepositApprove() {
        final org.drools.model.Variable<org.kie.drl.engine.mapinput.compilation.model.test.LoanApplication> var_$l = D.declarationOf(org.kie.drl.engine.mapinput.compilation.model.test.LoanApplication.class,
                                                                                                        DomainClassesMetadataED2A293F9C55BB1943AA9A6A1A8BF64C.org_kie_kogito_legacy_LoanApplication_Metadata_INSTANCE,
                                                                                                        "$l");
        final org.drools.model.BitMask mask_$l = org.drools.model.BitMask.getPatternMask(DomainClassesMetadataED2A293F9C55BB1943AA9A6A1A8BF64C.org_kie_kogito_legacy_LoanApplication_Metadata_INSTANCE,
                                                                                         "approved");
        org.drools.model.Rule rule = D.rule("org.kie.drl.engine.mapinput.compilation.model.test",
                                            "SmallDepositApprove")
                                      .build(D.pattern(var_$l).expr("GENERATED_085F0B863E90347D117BD907694A122E",
                                                                    org.kie.drl.engine.mapinput.compilation.model.test.P33.LambdaPredicate3384BFD77A71291E75C8C73A492233E3.INSTANCE,
                                                                    D.alphaIndexedBy(int.class,
                                                                                     ConstraintType.GREATER_OR_EQUAL,
                                                                                     -1,
                                                                                     org.kie.drl.engine.mapinput.compilation.model.test.PB2.LambdaExtractorB2483B164D7AAF9439F4B88741DDDF9E.INSTANCE,
                                                                                     20),
                                                                    D.reactOn("applicant")).expr("GENERATED_D4A4CE61FBC80961F803B73B57D90DBA",
                                                                                                 org.kie.drl.engine.mapinput.compilation.model.test.P89.LambdaPredicate896F205BB6DAC489283E534C8D4BF758.INSTANCE,
                                                                                                 D.alphaIndexedBy(int.class,
                                                                                                                  ConstraintType.LESS_THAN,
                                                                                                                  DomainClassesMetadataED2A293F9C55BB1943AA9A6A1A8BF64C.org_kie_kogito_legacy_LoanApplication_Metadata_INSTANCE.getPropertyIndex("deposit"),
                                                                                                                  org.kie.drl.engine.mapinput.compilation.model.test.PA3.LambdaExtractorA32B8CB1183D0F49BCC4780851D79C38.INSTANCE,
                                                                                                                  1000),
                                                                                                 D.reactOn("deposit")).expr("GENERATED_5FE6707E25EC9EB52DDE48F023A79872",
                                                                                                                            org.kie.drl.engine.mapinput.compilation.model.test.P4C.LambdaPredicate4C9797236624848F80F1DAA0797F33AF.INSTANCE,
                                                                                                                            D.alphaIndexedBy(int.class,
                                                                                                                                             ConstraintType.LESS_OR_EQUAL,
                                                                                                                                             DomainClassesMetadataED2A293F9C55BB1943AA9A6A1A8BF64C.org_kie_kogito_legacy_LoanApplication_Metadata_INSTANCE.getPropertyIndex("amount"),
                                                                                                                                             org.kie.drl.engine.mapinput.compilation.model.test.PE7.LambdaExtractorE7AC7861C0CAFC6F617FD43B3B32B4DC.INSTANCE,
                                                                                                                                             2000),
                                                                                                                            D.reactOn("amount")),
                                             D.on(var_$l).execute(org.kie.drl.engine.mapinput.compilation.model.test.PE0.LambdaConsequenceE0E2A00590319D790395C8D009E4D36A.INSTANCE));
        return rule;
    }

    /**
     * Rule name: SmallDepositReject
     */
    public static org.drools.model.Rule rule_SmallDepositReject() {
        final org.drools.model.Variable<org.kie.drl.engine.mapinput.compilation.model.test.LoanApplication> var_$l = D.declarationOf(org.kie.drl.engine.mapinput.compilation.model.test.LoanApplication.class,
                                                                                                        DomainClassesMetadataED2A293F9C55BB1943AA9A6A1A8BF64C.org_kie_kogito_legacy_LoanApplication_Metadata_INSTANCE,
                                                                                                        "$l");
        final org.drools.model.BitMask mask_$l = org.drools.model.BitMask.getPatternMask(DomainClassesMetadataED2A293F9C55BB1943AA9A6A1A8BF64C.org_kie_kogito_legacy_LoanApplication_Metadata_INSTANCE,
                                                                                         "approved");
        org.drools.model.Rule rule = D.rule("org.kie.drl.engine.mapinput.compilation.model.test",
                                            "SmallDepositReject")
                                      .build(D.pattern(var_$l).expr("GENERATED_085F0B863E90347D117BD907694A122E",
                                                                    org.kie.drl.engine.mapinput.compilation.model.test.P33.LambdaPredicate3384BFD77A71291E75C8C73A492233E3.INSTANCE,
                                                                    D.alphaIndexedBy(int.class,
                                                                                     ConstraintType.GREATER_OR_EQUAL,
                                                                                     -1,
                                                                                     org.kie.drl.engine.mapinput.compilation.model.test.PB2.LambdaExtractorB2483B164D7AAF9439F4B88741DDDF9E.INSTANCE,
                                                                                     20),
                                                                    D.reactOn("applicant")).expr("GENERATED_D4A4CE61FBC80961F803B73B57D90DBA",
                                                                                                 org.kie.drl.engine.mapinput.compilation.model.test.P89.LambdaPredicate896F205BB6DAC489283E534C8D4BF758.INSTANCE,
                                                                                                 D.alphaIndexedBy(int.class,
                                                                                                                  ConstraintType.LESS_THAN,
                                                                                                                  DomainClassesMetadataED2A293F9C55BB1943AA9A6A1A8BF64C.org_kie_kogito_legacy_LoanApplication_Metadata_INSTANCE.getPropertyIndex("deposit"),
                                                                                                                  org.kie.drl.engine.mapinput.compilation.model.test.PA3.LambdaExtractorA32B8CB1183D0F49BCC4780851D79C38.INSTANCE,
                                                                                                                  1000),
                                                                                                 D.reactOn("deposit")).expr("GENERATED_5F3F8C5544DE152E5587B244B3282FFF",
                                                                                                                            org.kie.drl.engine.mapinput.compilation.model.test.PF2.LambdaPredicateF2B64823F29DA45122941E0AD245653F.INSTANCE,
                                                                                                                            D.alphaIndexedBy(int.class,
                                                                                                                                             ConstraintType.GREATER_THAN,
                                                                                                                                             DomainClassesMetadataED2A293F9C55BB1943AA9A6A1A8BF64C.org_kie_kogito_legacy_LoanApplication_Metadata_INSTANCE.getPropertyIndex("amount"),
                                                                                                                                             org.kie.drl.engine.mapinput.compilation.model.test.PE7.LambdaExtractorE7AC7861C0CAFC6F617FD43B3B32B4DC.INSTANCE,
                                                                                                                                             2000),
                                                                                                                            D.reactOn("amount")),
                                             D.on(var_$l).execute(org.kie.drl.engine.mapinput.compilation.model.test.P5F.LambdaConsequence5F2293C183CB858F420C12848B4E6D9C.INSTANCE));
        return rule;
    }

    /**
     * Rule name: LargeDepositApprove
     */
    public static org.drools.model.Rule rule_LargeDepositApprove() {
        final org.drools.model.Variable<org.kie.drl.engine.mapinput.compilation.model.test.LoanApplication> var_$l = D.declarationOf(org.kie.drl.engine.mapinput.compilation.model.test.LoanApplication.class,
                                                                                                        DomainClassesMetadataED2A293F9C55BB1943AA9A6A1A8BF64C.org_kie_kogito_legacy_LoanApplication_Metadata_INSTANCE,
                                                                                                        "$l");
        final org.drools.model.BitMask mask_$l = org.drools.model.BitMask.getPatternMask(DomainClassesMetadataED2A293F9C55BB1943AA9A6A1A8BF64C.org_kie_kogito_legacy_LoanApplication_Metadata_INSTANCE,
                                                                                         "approved");
        org.drools.model.Rule rule = D.rule("org.kie.drl.engine.mapinput.compilation.model.test",
                                            "LargeDepositApprove")
                                      .build(D.pattern(var_$l).expr("GENERATED_085F0B863E90347D117BD907694A122E",
                                                                    org.kie.drl.engine.mapinput.compilation.model.test.P33.LambdaPredicate3384BFD77A71291E75C8C73A492233E3.INSTANCE,
                                                                    D.alphaIndexedBy(int.class,
                                                                                     ConstraintType.GREATER_OR_EQUAL,
                                                                                     -1,
                                                                                     org.kie.drl.engine.mapinput.compilation.model.test.PB2.LambdaExtractorB2483B164D7AAF9439F4B88741DDDF9E.INSTANCE,
                                                                                     20),
                                                                    D.reactOn("applicant")).expr("GENERATED_DFF94DC5B427C8A70F9D2E3416B5A4A6",
                                                                                                 org.kie.drl.engine.mapinput.compilation.model.test.P2F.LambdaPredicate2F3B4F1D1FFEB290777A54C8F3D34978.INSTANCE,
                                                                                                 D.alphaIndexedBy(int.class,
                                                                                                                  ConstraintType.GREATER_OR_EQUAL,
                                                                                                                  DomainClassesMetadataED2A293F9C55BB1943AA9A6A1A8BF64C.org_kie_kogito_legacy_LoanApplication_Metadata_INSTANCE.getPropertyIndex("deposit"),
                                                                                                                  org.kie.drl.engine.mapinput.compilation.model.test.PA3.LambdaExtractorA32B8CB1183D0F49BCC4780851D79C38.INSTANCE,
                                                                                                                  1000),
                                                                                                 D.reactOn("deposit")).expr("GENERATED_60F555374A3640A442D6728EF49A5C1A",
                                                                                                                            var_maxAmount,
                                                                                                                            org.kie.drl.engine.mapinput.compilation.model.test.PF3.LambdaPredicateF388D9370A499303354D5F588D65FFF8.INSTANCE,
                                                                                                                            D.reactOn("amount")),
                                             D.on(var_$l).execute(org.kie.drl.engine.mapinput.compilation.model.test.PE0.LambdaConsequenceE0E2A00590319D790395C8D009E4D36A.INSTANCE));
        return rule;
    }

    /**
     * Rule name: LargeDepositReject
     */
    public static org.drools.model.Rule rule_LargeDepositReject() {
        final org.drools.model.Variable<org.kie.drl.engine.mapinput.compilation.model.test.LoanApplication> var_$l = D.declarationOf(org.kie.drl.engine.mapinput.compilation.model.test.LoanApplication.class,
                                                                                                        DomainClassesMetadataED2A293F9C55BB1943AA9A6A1A8BF64C.org_kie_kogito_legacy_LoanApplication_Metadata_INSTANCE,
                                                                                                        "$l");
        final org.drools.model.BitMask mask_$l = org.drools.model.BitMask.getPatternMask(DomainClassesMetadataED2A293F9C55BB1943AA9A6A1A8BF64C.org_kie_kogito_legacy_LoanApplication_Metadata_INSTANCE,
                                                                                         "approved");
        org.drools.model.Rule rule = D.rule("org.kie.drl.engine.mapinput.compilation.model.test",
                                            "LargeDepositReject")
                                      .build(D.pattern(var_$l).expr("GENERATED_085F0B863E90347D117BD907694A122E",
                                                                    org.kie.drl.engine.mapinput.compilation.model.test.P33.LambdaPredicate3384BFD77A71291E75C8C73A492233E3.INSTANCE,
                                                                    D.alphaIndexedBy(int.class,
                                                                                     ConstraintType.GREATER_OR_EQUAL,
                                                                                     -1,
                                                                                     org.kie.drl.engine.mapinput.compilation.model.test.PB2.LambdaExtractorB2483B164D7AAF9439F4B88741DDDF9E.INSTANCE,
                                                                                     20),
                                                                    D.reactOn("applicant")).expr("GENERATED_DFF94DC5B427C8A70F9D2E3416B5A4A6",
                                                                                                 org.kie.drl.engine.mapinput.compilation.model.test.P2F.LambdaPredicate2F3B4F1D1FFEB290777A54C8F3D34978.INSTANCE,
                                                                                                 D.alphaIndexedBy(int.class,
                                                                                                                  ConstraintType.GREATER_OR_EQUAL,
                                                                                                                  DomainClassesMetadataED2A293F9C55BB1943AA9A6A1A8BF64C.org_kie_kogito_legacy_LoanApplication_Metadata_INSTANCE.getPropertyIndex("deposit"),
                                                                                                                  org.kie.drl.engine.mapinput.compilation.model.test.PA3.LambdaExtractorA32B8CB1183D0F49BCC4780851D79C38.INSTANCE,
                                                                                                                  1000),
                                                                                                 D.reactOn("deposit")).expr("GENERATED_7B1A62418028A12A33424E33756EBC32",
                                                                                                                            var_maxAmount,
                                                                                                                            org.kie.drl.engine.mapinput.compilation.model.test.PC9.LambdaPredicateC91E5C2471BC7923781356677C372303.INSTANCE,
                                                                                                                            D.reactOn("amount")),
                                             D.on(var_$l).execute(org.kie.drl.engine.mapinput.compilation.model.test.P5F.LambdaConsequence5F2293C183CB858F420C12848B4E6D9C.INSTANCE));
        return rule;
    }

    /**
     * Rule name: NotAdultApplication
     */
    public static org.drools.model.Rule rule_NotAdultApplication() {
        final org.drools.model.Variable<org.kie.drl.engine.mapinput.compilation.model.test.LoanApplication> var_$l = D.declarationOf(org.kie.drl.engine.mapinput.compilation.model.test.LoanApplication.class,
                                                                                                        DomainClassesMetadataED2A293F9C55BB1943AA9A6A1A8BF64C.org_kie_kogito_legacy_LoanApplication_Metadata_INSTANCE,
                                                                                                        "$l");
        final org.drools.model.BitMask mask_$l = org.drools.model.BitMask.getPatternMask(DomainClassesMetadataED2A293F9C55BB1943AA9A6A1A8BF64C.org_kie_kogito_legacy_LoanApplication_Metadata_INSTANCE,
                                                                                         "approved");
        org.drools.model.Rule rule = D.rule("org.kie.drl.engine.mapinput.compilation.model.test",
                                            "NotAdultApplication")
                                      .build(D.pattern(var_$l).expr("GENERATED_DAB05A3BAEDCE7704CAAE1D58603B01B",
                                                                    org.kie.drl.engine.mapinput.compilation.model.test.PA2.LambdaPredicateA2AD3A5DB0C892BF59F8EDAD4B47E88C.INSTANCE,
                                                                    D.alphaIndexedBy(int.class,
                                                                                     ConstraintType.LESS_THAN,
                                                                                     -1,
                                                                                     org.kie.drl.engine.mapinput.compilation.model.test.PB2.LambdaExtractorB2483B164D7AAF9439F4B88741DDDF9E.INSTANCE,
                                                                                     20),
                                                                    D.reactOn("applicant")),
                                             D.on(var_$l).execute(org.kie.drl.engine.mapinput.compilation.model.test.P5F.LambdaConsequence5F2293C183CB858F420C12848B4E6D9C.INSTANCE));
        return rule;
    }

    /**
     * Rule name: CollectApprovedApplication
     */
    public static org.drools.model.Rule rule_CollectApprovedApplication() {
        final org.drools.model.Variable<org.kie.drl.engine.mapinput.compilation.model.test.LoanApplication> var_$l = D.declarationOf(org.kie.drl.engine.mapinput.compilation.model.test.LoanApplication.class,
                                                                                                        DomainClassesMetadataED2A293F9C55BB1943AA9A6A1A8BF64C.org_kie_kogito_legacy_LoanApplication_Metadata_INSTANCE,
                                                                                                        "$l");
        org.drools.model.Rule rule = D.rule("org.kie.drl.engine.mapinput.compilation.model.test",
                                            "CollectApprovedApplication")
                                      .build(D.pattern(var_$l).expr("GENERATED_004102FA4C92E274FDE04C218F8E4593",
                                                                    org.kie.drl.engine.mapinput.compilation.model.test.PD9.LambdaPredicateD9AE0C5DE12003E037A99BF48F72D864.INSTANCE,
                                                                    D.reactOn("approved")),
                                             D.on(var_approvedApplications,
                                                  var_$l).execute(org.kie.drl.engine.mapinput.compilation.model.test.P57.LambdaConsequence5740B486CC8DAC375E93235CC2B0815D.INSTANCE));
        return rule;
    }
}
