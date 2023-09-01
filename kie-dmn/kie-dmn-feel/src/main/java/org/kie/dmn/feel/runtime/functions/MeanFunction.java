package org.kie.dmn.feel.runtime.functions;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import org.kie.dmn.api.feel.runtime.events.FEELEvent;
import org.kie.dmn.api.feel.runtime.events.FEELEvent.Severity;
import org.kie.dmn.feel.runtime.events.InvalidParametersEvent;
import org.kie.dmn.feel.util.EvalHelper;

public class MeanFunction
        extends BaseFEELFunction {

    private SumFunction sum = new SumFunction();

    public MeanFunction() {
        super( "mean" );
    }

    public FEELFnResult<BigDecimal> invoke(@ParameterName( "list" ) List list) {
        if ( list == null ) {
            return FEELFnResult.ofError(new InvalidParametersEvent(Severity.ERROR, "list", "cannot be null"));
        }

        FEELFnResult<BigDecimal> s = sum.invoke( list );
        
        Function<FEELEvent, FEELFnResult<BigDecimal>> ifLeft = (event) -> {
            return FEELFnResult.ofError(new InvalidParametersEvent(Severity.ERROR, "list", "unable to sum the elements which is required to calculate the mean"));
        };
        
        Function<BigDecimal, FEELFnResult<BigDecimal>> ifRight = (sum) -> {
            try {
                return FEELFnResult.ofResult( sum.divide( BigDecimal.valueOf( list.size() ), MathContext.DECIMAL128 ) );
            } catch (Exception e) {
                return FEELFnResult.ofError( new InvalidParametersEvent(Severity.ERROR, "unable to perform division to calculate the mean", e) );
            }
        };
        
        return s.cata(ifLeft, ifRight);
    }

    public FEELFnResult<BigDecimal> invoke(@ParameterName( "list" ) Number single) {
        if ( single == null ) {
            return FEELFnResult.ofError(new InvalidParametersEvent(Severity.ERROR, "single", "the single value list cannot be null"));
        }

        if( single instanceof BigDecimal ) {
            return FEELFnResult.ofResult((BigDecimal) single );
        } 
        BigDecimal result = EvalHelper.getBigDecimalOrNull( single );
        if ( result != null ) {
            return FEELFnResult.ofResult( result );
        } else {
            return FEELFnResult.ofError(new InvalidParametersEvent(Severity.ERROR, "list", "single element in list is not a number"));
        }
    }

    public FEELFnResult<BigDecimal> invoke(@ParameterName( "n" ) Object[] list) {
        if ( list == null ) { 
            // Arrays.asList does not accept null as parameter
            return FEELFnResult.ofError(new InvalidParametersEvent(Severity.ERROR, "n", "cannot be null"));
        }
        
        return invoke( Arrays.asList( list ) );
    }
}
