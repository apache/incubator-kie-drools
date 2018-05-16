package org.drools.core.base.accumulators;

import org.drools.core.base.accumulators.VarianceAccumulateFunction.VarianceData;
import org.junit.Test;
import static org.junit.Assert.*;

public class VarianceNaNTest {
	
    @Test
    public void shouldNotProduceNaNAfterBackout(){
        VarianceAccumulateFunction varianceAccumulateFunction = new VarianceAccumulateFunction();
        VarianceData data = varianceAccumulateFunction.createContext();
        varianceAccumulateFunction.init(data);
		
        //Before being initialized result is NaN.
        assertEquals(Double.NaN, varianceAccumulateFunction.getResult(data), 0);

        Double value = 1.5;
		
        //With single value variance should be 0
        varianceAccumulateFunction.accumulate(data, value);		
        assertEquals(0.0d, varianceAccumulateFunction.getResult(data), .001d);
		
        //should be back to NaN after backout
        varianceAccumulateFunction.reverse(data, value);
        assertEquals(Double.NaN, varianceAccumulateFunction.getResult(data), 0);
		
        //should be zero after adding number back
        varianceAccumulateFunction.accumulate(data, value);		
        assertEquals(0.0d, varianceAccumulateFunction.getResult(data), .001d);
    }
}
