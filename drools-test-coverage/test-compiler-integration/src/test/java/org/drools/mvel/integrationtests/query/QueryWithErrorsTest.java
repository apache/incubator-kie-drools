package org.drools.mvel.integrationtests.query;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.stream.Stream;

import org.drools.testcoverage.common.util.KieBaseTestConfiguration;
import org.drools.testcoverage.common.util.KieUtil;
import org.drools.testcoverage.common.util.TestParametersUtil2;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.Message;

public class QueryWithErrorsTest {

    public static Stream<KieBaseTestConfiguration> parameters() {
        return TestParametersUtil2.getKieBaseCloudConfigurations(true).stream();
    }

    @ParameterizedTest(name = "KieBase type={0}")
    @MethodSource("parameters")
    public void testQueryWithIncompatibleArgs(KieBaseTestConfiguration kieBaseTestConfiguration) {
        String drl ="""
        		global java.util.List list;
                
                query 
        			foo(String $s, String $s, String $s)
        		end
        		
        		rule React
        		when
        			$i : Integer()
        			foo($i, $x, $i ;)
        		then
        		end
        		""";

        KieBuilder kieBuilder = KieUtil.getKieBuilderFromDrls(kieBaseTestConfiguration, false, drl);
        List<Message> errors = kieBuilder.getResults().getMessages(Message.Level.ERROR);
        assertThat(errors).hasSize(2);
    }
    

    @ParameterizedTest(name = "KieBase type={0}")
    @MethodSource("parameters")
    public void testQueryWithSyntaxError(KieBaseTestConfiguration kieBaseTestConfiguration) {
        String drl = """
        		global java.util.List list;
        		query 
        			foo(Integer $i) 
        		end
        		rule React
        		when
        			$i : Integer()
        			foo($i)  // missing ";" should result in 1 compilation error
        		then
        		end
        		""";

        KieBuilder kieBuilder = KieUtil.getKieBuilderFromDrls(kieBaseTestConfiguration, false, drl);
        List<Message> errors = kieBuilder.getResults().getMessages(Message.Level.ERROR);
        assertThat(errors).hasSize(1);
    }
    


    @ParameterizedTest(name = "KieBase type={0}")
    @MethodSource("parameters")
    public void testQueryWithWrongParamNumber(KieBaseTestConfiguration kieBaseTestConfiguration) {
        String drl = """
    			global java.util.List list;
    			query foo(Integer $i) 
                 
    			end
    			rule React
    			when
    				$i : Integer()
    				$j : Integer()
    				foo($i, $j ;)
    			then
    			end
                 """;

        KieBuilder kieBuilder = KieUtil.getKieBuilderFromDrls(kieBaseTestConfiguration, false, drl);
        List<Message> errors = kieBuilder.getResults().getMessages(Message.Level.ERROR);
        assertThat(errors).hasSize(1);
    }


    @ParameterizedTest(name = "KieBase type={0}")
    @MethodSource("parameters")
    public void testNotExistingDeclarationInQuery(KieBaseTestConfiguration kieBaseTestConfiguration) {
        // DROOLS-414
        String drl ="""
        		import org.drools.compiler.Person;
        		global java.util.List persons;

        		query checkLength(String $s, int $l)
        		    $s := String(length == $l)
        		end

        		rule R when
					$i : Integer()
					$p : Person()
					checkLength($p.name, 1 + $x + $p.age;)
        		then
        		    persons.add($p);
        		end\n"
        		""";

        KieBuilder kieBuilder = KieUtil.getKieBuilderFromDrls(kieBaseTestConfiguration, false, drl);
        List<Message> errors = kieBuilder.getResults().getMessages(Message.Level.ERROR);
        assertThat(errors).as("Should have an error").isNotEmpty();
    }


}
