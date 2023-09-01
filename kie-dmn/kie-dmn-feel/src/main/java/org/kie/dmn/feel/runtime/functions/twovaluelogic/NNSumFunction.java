package org.kie.dmn.feel.runtime.functions.twovaluelogic;

import org.kie.dmn.api.feel.runtime.events.FEELEvent.Severity;
import org.kie.dmn.feel.runtime.events.InvalidParametersEvent;
import org.kie.dmn.feel.runtime.functions.BaseFEELFunction;
import org.kie.dmn.feel.runtime.functions.FEELFnResult;
import org.kie.dmn.feel.runtime.functions.ParameterName;
import org.kie.dmn.feel.util.EvalHelper;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

public class NNSumFunction
        extends BaseFEELFunction {

    public static final NNSumFunction INSTANCE = new NNSumFunction();

    public NNSumFunction() {
        super( "nn sum" );
    }

    public FEELFnResult<BigDecimal> invoke(@ParameterName("list") List list) {
        if ( list == null ) {
            return FEELFnResult.ofResult(null);
        }
        BigDecimal sum = BigDecimal.ZERO;
        for ( Object element : list ) {
            if( element == null ) {
                continue;
            } else if ( element instanceof BigDecimal ) {
                sum = sum.add( (BigDecimal) element );
            } else if ( element instanceof Number ) {
                BigDecimal value = EvalHelper.getBigDecimalOrNull( element );
                if (value == null) {
                    return FEELFnResult.ofError(new InvalidParametersEvent(Severity.ERROR, "list", "an element in the list is not suitable for the sum"));
                } else {
                    sum = sum.add( value );
                }
            } else {
                return FEELFnResult.ofError(new InvalidParametersEvent(Severity.ERROR, "list", "an element in the list is not a number"));
            }
        }
        return FEELFnResult.ofResult( sum );
    }

    public FEELFnResult<BigDecimal> invoke(@ParameterName("list") Number single) {
        if ( single == null ) { 
            return FEELFnResult.ofResult( null );
        }
        if( single instanceof BigDecimal ) {
            return FEELFnResult.ofResult((BigDecimal) single );
        } 
        BigDecimal result = EvalHelper.getBigDecimalOrNull( single );
        if ( result != null ) {
            return FEELFnResult.ofResult( result );
        } else {
            return FEELFnResult.ofError(new InvalidParametersEvent(Severity.ERROR, "list", "single element in list not a number"));
        }
    }

    public FEELFnResult<BigDecimal> invoke(@ParameterName("n") Object[] list) {
        if ( list == null ) { 
            return FEELFnResult.ofResult(null);
        }
        
        return invoke( Arrays.asList( list ) );
    }
}
