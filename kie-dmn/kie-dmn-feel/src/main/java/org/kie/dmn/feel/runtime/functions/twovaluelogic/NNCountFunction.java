/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
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
