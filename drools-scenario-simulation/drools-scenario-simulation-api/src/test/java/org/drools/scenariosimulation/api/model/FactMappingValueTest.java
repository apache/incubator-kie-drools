package org.drools.scenariosimulation.api.model;

import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.drools.scenariosimulation.api.utils.ConstantsHolder.VALUE;

public class FactMappingValueTest {

    private FactMappingValue value;
    
    @Before
    public void setUp() throws Exception {
        value = new FactMappingValue();
    }

    @Test
    public void emptyFactMappingValue() {
        assertThatThrownBy(() -> new FactMappingValue(null, ExpressionIdentifier.DESCRIPTION, null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("FactIdentifier has to be not null");

        assertThatThrownBy(() -> new FactMappingValue(FactIdentifier.DESCRIPTION, null, null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("ExpressionIdentifier has to be not null");
    }

    @Test
    public void resetStatus() {
        value.resetStatus();
        
        assertThat(value.getStatus()).isEqualTo(FactMappingValueStatus.SUCCESS);
        assertThat(value.getExceptionMessage()).isNull();
        assertThat(value.getErrorValue()).isNull();
        assertThat(value.getCollectionPathToValue()).isNull();
    }

    @Test
    public void setErrorValue() {
        value.setErrorValue(VALUE);
        
        assertThat(value.getStatus()).isEqualTo(FactMappingValueStatus.FAILED_WITH_ERROR);
        assertThat(value.getExceptionMessage()).isNull();
        assertThat(value.getCollectionPathToValue()).isNull();
        assertThat(value.getErrorValue()).isEqualTo(VALUE);
    }

    @Test
    public void setExceptionMessage() {
        String exceptionValue = "Exception";
        
        value.setExceptionMessage(exceptionValue);
        
        assertThat(value.getStatus()).isEqualTo(FactMappingValueStatus.FAILED_WITH_EXCEPTION);
        assertThat(value.getExceptionMessage()).isEqualTo(exceptionValue);
        assertThat(value.getErrorValue()).isNull();
        assertThat(value.getCollectionPathToValue()).isNull();
    }

    @Test
    public void setPathToValue() {
        List<String> path = Arrays.asList("Step1", "Step2");
        
        value.setCollectionPathToValue(path);
        
        assertThat(value.getStatus()).isEqualTo(FactMappingValueStatus.FAILED_WITH_ERROR);
        assertThat(value.getExceptionMessage()).isNull();
        assertThat(value.getErrorValue()).isNull();
        assertThat(value.getCollectionPathToValue()).isSameAs(path);
    }
}