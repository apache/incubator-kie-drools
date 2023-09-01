package org.drools.reliability.test.smoke;

import org.drools.reliability.test.BeforeAllMethodExtension;
import org.drools.reliability.test.ReliabilityTestBasics;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.condition.EnabledIf;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.kie.api.runtime.conf.PersistedSessionOption;
import org.test.domain.Person;

import static org.assertj.core.api.Assertions.assertThat;

@Disabled("Base class for other smoke tests")
@ExtendWith(BeforeAllMethodExtension.class)
public class BaseSmokeTest extends ReliabilityTestBasics {

    private static final String BASIC_RULE =
            "import " + Person.class.getCanonicalName() + ";" +
            "global java.util.List results;" +
            "rule X when\n" +
            "  $s: String()\n" +
            "  $p: Person( getName().startsWith($s) )\n" +
            "then\n" +
            "  results.add( $p.getName() );\n" +
            "end";

    @ParameterizedTest
    @MethodSource("strategyProviderStoresOnlyWithExplicitSafepoints")
    void insertFailoverInsertFire_shouldRecoverFromFailover(PersistedSessionOption.PersistenceStrategy persistenceStrategy, PersistedSessionOption.SafepointStrategy safepointStrategy) {
        createSession(BASIC_RULE, persistenceStrategy, safepointStrategy);

		insert("M");
		insertMatchingPerson("Matching Person One", 37);

        //-- Assume JVM down here. Fail-over to other JVM or rebooted JVM
        //-- ksession and kbase are lost. CacheManager is recreated. Client knows only "id"
        failover();

        restoreSession(BASIC_RULE, persistenceStrategy, safepointStrategy);

		insertNonMatchingPerson("Toshiya", 35);
        insertMatchingPerson("Matching Person Two", 40);

		fireAllRules();

		assertThat(getResults()).containsExactlyInAnyOrder("Matching Person One", "Matching Person Two");
    }
}
