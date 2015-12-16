/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.drools.core.util;


public class NumberUtils {

    /**
     * Checks if an addition of 2 longs caused an overflow 
     * @param op1
     * @param op2
     * @param result
     * @return true if an overflow occurred, false otherwise
     */
    public static boolean isAddOverflow( final long op1, final long op2, final long result ) {
        // ((op1^result)&(op2^result))<0) is a shorthand for:
        // ( (op1<0 && op2<0 && result>=0) ||
        //   (op1>0 && op2>0 && result<=0) )
        return (( (op1^result) & (op2^result) ) < 0);
    }
}
