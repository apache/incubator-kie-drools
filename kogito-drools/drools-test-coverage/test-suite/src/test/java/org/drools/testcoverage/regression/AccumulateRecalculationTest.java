/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.testcoverage.regression;

import org.assertj.core.api.Assertions;
import org.drools.testcoverage.common.model.MyFact;
import org.junit.Test;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.internal.utils.KieHelper;

/**
 * BZ 1190923 reproducer by Hisao Furuich.
 */
public class AccumulateRecalculationTest {

    @Test
    public void testAccumulateWithNoLoop() {
        final String drl =
                "import " + MyFact.class.getCanonicalName() + " ;\n" +
                        "rule RuleAccumulate \n" +
                        "  no-loop true\n" +
                        "when\n" +
                        "  $target: MyFact(name == \"target\");\n" +
                        "  $sum : Integer( ) from accumulate (\n" +
                        "    $v1: MyFact(name str[startsWith] \"src\", currentValue != null),\n" +
                        "    init ( int total = 0; ),\n" +
                        "    action (\n" +
                        "      total = total + $v1.getCurrentValue().intValue();\n" +
                        "      System.out.println(\"action  : plus  \" + $v1.toString() + \" then total=\" + total);\n" +
                        "    ),\n" +
                        "    reverse (\n" +
                        "      total = total - \n" +
                        "        ($v1.getPreviousValue() != null ? $v1.getPreviousValue().intValue() : $v1.getCurrentValue().intValue());\n" +
                        "      System.out.println(\"reverse : minus \" + $v1.toString() + \" then total=\" + total);\n" +
                        "    ),\n" +
                        "    result ( new Integer(total) )\n" +
                        "  );\n" +
                        "then\n" +
                        "  System.out.println(\" Fired!! \" + drools.getRule().getName() + \" total=\" + $sum);\n" +
                        "  $target.setCurrentValue($sum);\n" +
                        "  update($target);\n" +
                        "end\n" +
                        "\n" +
                        "rule RuleFollowing \n" +
                        "when\n" +
                        "  $target: MyFact(name == \"target\");\n" +
                        "then\n" +
                        "  System.out.println(\" Fired!! RuleFollowing target=\" + $target.toString());\n" +
                        "end\n";
        scenario(drl);
    }

    @Test
    public void testAccumulateWithNoLoopBySum() {
        final String drl =
                "import " + MyFact.class.getCanonicalName() + " ;\n" +
                        "rule RuleAccumulateSum \n" +
                        "  no-loop true\n" +
                        "when\n" +
                        "  $target: MyFact(name == \"target\");\n" +
                        "  $sum : Number( ) from accumulate (\n" +
                        "    $v1: MyFact(name str[startsWith] \"src\", $val: currentValue != null),\n" +
                        "    sum( $val )\n" +
                        "  );\n" +
                        "then\n" +
                        "  System.out.println(\" Fired!! \" + drools.getRule().getName() + \" total=\" + $sum);\n" +
                        "  $target.setCurrentValue($sum.intValue());\n" +
                        "  update($target);\n" +
                        "end\n" +
                        "\n" +
                        "rule RuleFollowing \n" +
                        "when\n" +
                        "  $target: MyFact(name == \"target\");\n" +
                        "then\n" +
                        "  System.out.println(\" Fired!! RuleFollowing target=\" + $target.toString());\n" +
                        "end\n";
        scenario(drl);
    }

    @Test
    public void testCollect() {
        final String drl =
                "import java.util.ArrayList;\n" +
                        " import " + MyFact.class.getCanonicalName() + " ;\n" +
                        "rule RuleCollect \n" +
                        "  no-loop true\n" +
                        "when\n" +
                        "  $target: MyFact(name == \"target\");\n" +
                        "  $list : ArrayList( size > 0 ) from collect (\n" +
                        "    $v1: MyFact(name str[startsWith] \"src\", $val: currentValue != null)\n" +
                        "  );\n" +
                        "then\n" +
                        "  int $sum = 0;\n" +
                        "  for (Object obj : $list) {\n" +
                        "    MyFact myFact = (MyFact)obj;\n" +
                        "    $sum = $sum + ((MyFact)obj).getCurrentValue().intValue();\n" +
                        "    System.out.println(\" sum + \" + myFact.toString() + \" total is \" + $sum);\n" +
                        "  }\n" +
                        "  System.out.println(\" Fired!! \" + drools.getRule().getName() + \" total=\" + $sum);\n" +
                        "  $target.setCurrentValue($sum);\n" +
                        "  update($target);\n" +
                        "end\n" +
                        "\n" +
                        "rule RuleFollowing \n" +
                        "when\n" +
                        "  $target: MyFact(name == \"target\");\n" +
                        "then\n" +
                        "  System.out.println(\" Fired!! RuleFollowing target=\" + $target.toString());\n" +
                        "end\n";
        scenario(drl);
    }

    @Test
    public void testWithOutAccumulateCollect() {
        final String drl =
                "import " + MyFact.class.getCanonicalName() + " ;\n" +
                        "rule RuleWithOutAccumulateCollect \n" +
                        "  no-loop true\n" +
                        "when\n" +
                        "  $target: MyFact(name == \"target\");\n" +
                        "  $src1 : MyFact(name str[startsWith] \"src\", currentValue != null);\n" +
                        "  $src2 : MyFact(hashCode() < $src1.hashCode(), name str[startsWith] \"src\", currentValue != null);\n" +
                        "then\n" +
                        "  int $sum = $src1.getCurrentValue().intValue();\n" +
                        "  $sum += $src2.getCurrentValue().intValue();\n" +
                        "  System.out.println(\" Fired!! \" + drools.getRule().getName() + \" total=\" + $sum);\n" +
                        "  $target.setCurrentValue($sum);\n" +
                        "  update($target);\n" +
                        "end\n" +
                        "\n" +
                        "rule RuleFollowing \n" +
                        "when\n" +
                        "  $target: MyFact(name == \"target\");\n" +
                        "then\n" +
                        "  System.out.println(\" Fired!! RuleFollowing target=\" + $target.toString());\n" +
                        "end\n";

        scenario(drl);
    }

    private void scenario(final String drl) {
        final KieSession ksession = new KieHelper().addContent(drl, ResourceType.DRL).build().newKieSession();

        final MyFact fact0 = new MyFact("target", 0);
        final MyFact fact1 = new MyFact("src1", 4);
        final MyFact fact2 = new MyFact("src2", 5);

        final FactHandle fh0 = ksession.insert(fact0);
        final FactHandle fh1 = ksession.insert(fact1);
        final FactHandle fh2 = ksession.insert(fact2);

        int fireCount = ksession.fireAllRules();

        Assertions.assertThat(fact0.getCurrentValue().intValue()).isEqualTo(9);
        Assertions.assertThat(fireCount).isEqualTo(2);

        fact2.setCurrentValue(6);

        ksession.update(fh2, fact2);
        fireCount = ksession.fireAllRules();

        Assertions.assertThat(fact0.getCurrentValue().intValue()).isEqualTo(10);
        Assertions.assertThat(fireCount).isEqualTo(2);
    }
}
