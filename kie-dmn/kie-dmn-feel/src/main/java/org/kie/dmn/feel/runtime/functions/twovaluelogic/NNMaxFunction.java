package org.kie.dmn.feel.runtime.functions.twovaluelogic;

import org.kie.dmn.api.feel.runtime.events.FEELEvent.Severity;
import org.kie.dmn.feel.runtime.events.InvalidParametersEvent;
import org.kie.dmn.feel.runtime.functions.BaseFEELFunction;
import org.kie.dmn.feel.runtime.functions.FEELFnResult;
import org.kie.dmn.feel.runtime.functions.ParameterName;

import java.util.Arrays;
import java.util.List;

public class NNMaxFunction
        extends BaseFEELFunction {

    public static final NNMaxFunction INSTANCE = new NNMaxFunction();

    public NNMaxFunction() {
        super( "nn max" );
    }

    public FEELFnResult<Object> invoke(@ParameterName("list") List list) {
        if ( list == null || list.isEmpty() ) {
            return FEELFnResult.ofResult( null );
        } else {
            try {
                Comparable max = null;
                for( int i = 0; i < list.size(); i++ ) {
                    Comparable candidate = (Comparable) list.get( i );
                    if( candidate == null ) {
                        continue;
                    } else if( max == null ) {
                        max = candidate;
                    } else if( max.compareTo( candidate ) < 0 ) {
                        max = candidate;
                    }
                }
                return FEELFnResult.ofResult( max );
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
