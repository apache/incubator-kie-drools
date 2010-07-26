/**
 * Copyright 2010 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.base.evaluators;

import org.drools.RuntimeDroolsException;
import org.drools.time.TimeUtils;

/**
 * A parameters parser that uses JodaTime for time units parsing.
 *  
 * @author etirelli
 */
public class TimeIntervalParser
    implements
    EvaluatorParametersParser {

    /**
     * @inheritDoc
     * 
     * @see org.drools.base.evaluators.EvaluatorParametersParser#parse(java.lang.String)
     */
    public Long[] parse(String paramText) {
        if ( paramText == null || paramText.trim().length() == 0 ) {
            return new Long[0];
        }
        String[] params = paramText.split( "," );
        Long[] result = new Long[params.length];
        int index = 0;
        for ( String param : params ) {
            String trimmed = param.trim();
            if ( trimmed.length() > 0 ) {
                result[index++] = TimeUtils.parseTimeString( param );
            } else {
                throw new RuntimeDroolsException( "Empty parameters not allowed in: [" + paramText + "]" );
            }
        }
        return result;
    }


}
