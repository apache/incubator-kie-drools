package org.drools.mvel.compiler.conf;

import org.drools.core.BeliefSystemType;
import org.junit.Before;
import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.runtime.conf.AccumulateNullPropagationOption;
import org.kie.api.runtime.conf.BeliefSystemTypeOption;
import org.kie.api.runtime.KieSessionConfiguration;
import org.kie.api.runtime.conf.ClockTypeOption;

import static org.assertj.core.api.Assertions.assertThat;


public class KnowledgeSessionConfigurationTest {

    private KieSessionConfiguration config;

    /* (non-Javadoc)
     * @see junit.framework.TestCase#setUp()
     */
    @Before
    public void setUp() throws Exception {
        config = KieServices.Factory.get().newKieSessionConfiguration();
    }

    @Test
    public void testClockTypeConfiguration() {
        // setting the option using the type safe method
        config.setOption( ClockTypeOption.PSEUDO );

        // checking the type safe getOption() method
        assertThat(config.getOption(ClockTypeOption.KEY)).isEqualTo(ClockTypeOption.PSEUDO);
        // checking the string based getProperty() method
        assertThat(config.getProperty(ClockTypeOption.PROPERTY_NAME)).isEqualTo("pseudo");

        // setting the options using the string based setProperty() method
        config.setProperty( ClockTypeOption.PROPERTY_NAME,
                            "realtime" );

        // checking the type safe getOption() method
        assertThat(config.getOption(ClockTypeOption.KEY)).isEqualTo(ClockTypeOption.REALTIME);
        // checking the string based getProperty() method
        assertThat(config.getProperty(ClockTypeOption.PROPERTY_NAME)).isEqualTo("realtime");
    }


    @Test
    public void testBeliefSystemType() {
        config.setOption( BeliefSystemTypeOption.get( BeliefSystemType.JTMS.toString() ) );

        assertThat(config.getOption(BeliefSystemTypeOption.KEY)).isEqualTo(BeliefSystemTypeOption.get(BeliefSystemType.JTMS.toString()));

        // checking the string based getProperty() method
        assertThat(config.getProperty(BeliefSystemTypeOption.PROPERTY_NAME)).isEqualTo(BeliefSystemType.JTMS.getId());

        // setting the options using the string based setProperty() method
        config.setProperty( BeliefSystemTypeOption.PROPERTY_NAME,
                            BeliefSystemType.DEFEASIBLE.getId() );

        // checking the type safe getOption() method
        assertThat(config.getOption(BeliefSystemTypeOption.KEY)).isEqualTo(BeliefSystemTypeOption.get(BeliefSystemType.DEFEASIBLE.getId()));
        // checking the string based getProperty() method
        assertThat(config.getProperty(BeliefSystemTypeOption.PROPERTY_NAME)).isEqualTo(BeliefSystemType.DEFEASIBLE.getId());
    }

    @Test
    public void testAccumulateNullPropagation() {
        // false by default
        assertThat(config.getOption(AccumulateNullPropagationOption.KEY)).isEqualTo(AccumulateNullPropagationOption.NO);
        assertThat(config.getProperty(AccumulateNullPropagationOption.PROPERTY_NAME)).isEqualTo("false");

        config.setOption(AccumulateNullPropagationOption.YES);

        assertThat(config.getOption(AccumulateNullPropagationOption.KEY)).isEqualTo(AccumulateNullPropagationOption.YES);

        // checking the string based getProperty() method
        assertThat(config.getProperty(AccumulateNullPropagationOption.PROPERTY_NAME)).isEqualTo("true");

        // setting the options using the string based setProperty() method
        config.setProperty(AccumulateNullPropagationOption.PROPERTY_NAME,
                           "false");

        // checking the type safe getOption() method
        assertThat(config.getOption(AccumulateNullPropagationOption.KEY)).isEqualTo(AccumulateNullPropagationOption.NO);
        // checking the string based getProperty() method
        assertThat(config.getProperty(AccumulateNullPropagationOption.PROPERTY_NAME)).isEqualTo("false");
    }
}
