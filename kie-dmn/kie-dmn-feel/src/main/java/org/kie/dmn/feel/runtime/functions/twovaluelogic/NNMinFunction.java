package org.kie.dmn.feel.runtime.functions.twovaluelogic;

import org.kie.dmn.api.feel.runtime.events.FEELEvent.Severity;
import org.kie.dmn.feel.runtime.events.InvalidParametersEvent;
import org.kie.dmn.feel.runtime.functions.BaseFEELFunction;
import org.kie.dmn.feel.runtime.functions.FEELFnResult;
import org.kie.dmn.feel.runtime.functions.ParameterName;

import java.util.Arrays;
import java.util.List;

public class NNMinFunction
        extends BaseFEELFunction {

    public static final NNMinFunction INSTANCE = new NNMinFunction();

    public NNMinFunction() {
        super( "nn min" );
    }

    public FEELFnResult<Object> invoke(@ParameterName("list") List list) {
        if ( list == null || list.isEmpty() ) {
            return FEELFnResult.ofResult( null );
        } else {
            try {
                Comparable min = null;
                for( int i = 0; i < list.size(); i++ ) {
                    Comparable candidate = (Comparable) list.get( i );
                    if( candidate == null ) {
                        continue;
                    } else if( min == null ) {
                        min = candidate;
                    } else if( min.compareTo( candidate ) > 0 ) {
                        min = candidate;
                    }
                }
                return FEELFnResult.ofResult( min );
            } catch (ClassCastException e) {
                return FEELFnResult.ofError(new InvalidParametersEvent(Severity.ERROR, "list", "contains items that are not comparable"));
            }
        }
    }

    public FEELFnResult<Object> invoke(@ParameterName("c") Object[] list) {
        if ( list == null || list.length == 0 ) {
            return FEELFnResult.ofResult( null );
        }
        
        return invoke( Arrays.asList( list ) );
    }

}
