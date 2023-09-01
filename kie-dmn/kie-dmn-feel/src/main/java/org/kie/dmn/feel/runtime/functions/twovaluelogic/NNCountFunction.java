package org.kie.dmn.feel.runtime.functions.twovaluelogic;

import org.kie.dmn.feel.runtime.functions.BaseFEELFunction;
import org.kie.dmn.feel.runtime.functions.FEELFnResult;
import org.kie.dmn.feel.runtime.functions.ParameterName;

import java.math.BigDecimal;
import java.util.List;

public class NNCountFunction
        extends BaseFEELFunction {

    public static final NNCountFunction INSTANCE = new NNCountFunction();

    public NNCountFunction() {
        super( "nn count" );
    }

    public FEELFnResult<BigDecimal> invoke(@ParameterName( "list" ) List list) {
        if ( list == null ) {
            return FEELFnResult.ofResult(BigDecimal.ZERO);
        }
        // using raw loop instead of streams for performance
        int count = 0;
        for( int i = 0; i < list.size(); i++ ) {
            if( list.get( i ) != null ) {
                count++;
            }
        }
        return FEELFnResult.ofResult( BigDecimal.valueOf( count ) );
    }

    public FEELFnResult<BigDecimal> invoke(@ParameterName( "c" ) Object[] list) {
        if ( list == null ) {
            return FEELFnResult.ofResult(BigDecimal.ZERO);
        }
        // using raw loop instead of streams for performance
        int count = 0;
        for( int i = 0; i < list.length; i++ ) {
            if( list[ i ] != null ) {
                count++;
            }
        }
        return FEELFnResult.ofResult( BigDecimal.valueOf( count ) );
    }
}
