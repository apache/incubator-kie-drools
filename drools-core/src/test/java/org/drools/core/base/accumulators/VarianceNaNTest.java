package org.drools.core.base.accumulators;

import org.drools.core.base.accumulators.VarianceAccumulateFunction.VarianceData;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

public class VarianceNaNTest {
	
    @Test
    public void shouldNotProduceNaNAfterBackout(){
        VarianceAccumulateFunction varianceAccumulateFunction = new VarianceAccumulateFunction();
        VarianceData data = varianceAccumulateFunction.createContext();
        varianceAccumulateFunction.init(data);

        //Before being initialized result is NaN.
        assertThat(varianceAccumulateFunction.getResult(data)).isCloseTo(Double.NaN, within(0.0));

        Double value = 1.5;
		
        //With single value variance should be 0
        varianceAccumulateFunction.accumulate(data, value);
        assertThat(varianceAccumulateFunction.getResult(data)).isCloseTo(0.0d, within(.001d));
		
        //should be back to NaN after backout
        varianceAccumulateFunction.reverse(data, value);
        assertThat(varianceAccumulateFunction.getResult(data)).isCloseTo(Double.NaN, within(0.0));
		
        //should be zero after adding number back
        varianceAccumulateFunction.accumulate(data, value);
        assertThat(varianceAccumulateFunction.getResult(data)).isCloseTo(0.0d, within(.001d));
    }
}
