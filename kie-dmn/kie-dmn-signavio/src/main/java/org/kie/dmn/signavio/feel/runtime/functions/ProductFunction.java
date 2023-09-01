package org.kie.dmn.signavio.feel.runtime.functions;

import org.kie.dmn.api.feel.runtime.events.FEELEvent.Severity;
import org.kie.dmn.feel.runtime.events.InvalidParametersEvent;
import org.kie.dmn.feel.runtime.functions.BaseFEELFunction;
import org.kie.dmn.feel.runtime.functions.FEELFnResult;
import org.kie.dmn.feel.runtime.functions.ParameterName;
import org.kie.dmn.feel.util.EvalHelper;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

public class ProductFunction
        extends BaseFEELFunction {
    public static final ProductFunction INSTANCE = new ProductFunction();

    ProductFunction() {
        super( "product" );
    }

    public FEELFnResult<BigDecimal> invoke(@ParameterName("list") List list) {
        if ( list == null ) {
            return FEELFnResult.ofError(new InvalidParametersEvent(Severity.ERROR, "list", "the list cannot be null"));
        }
        BigDecimal product = list.isEmpty() ? BigDecimal.ZERO : BigDecimal.ONE;
        for ( Object element : list ) {
            if ( element instanceof BigDecimal ) {
                product = product.multiply( (BigDecimal) element );
            } else if ( element instanceof Number ) {
                product = product.multiply( EvalHelper.getBigDecimalOrNull( element ) );
            } else {
                return FEELFnResult.ofError(new InvalidParametersEvent(Severity.ERROR, "list", "an element in the list is not suitable for the product"));
            }
        }
        return FEELFnResult.ofResult( product );
    }

    public FEELFnResult<BigDecimal> invoke(@ParameterName("list") Number single) {
        if ( single == null ) { 
            // Arrays.asList does not accept null as parameter
            return FEELFnResult.ofError(new InvalidParametersEvent(Severity.ERROR, "list", "the single value list cannot be null"));
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
            // Arrays.asList does not accept null as parameter
            return FEELFnResult.ofError(new InvalidParametersEvent(Severity.ERROR, "n", "the single value list cannot be null"));
        }
        
        return invoke( Arrays.asList( list ) );
    }
}
