/*
 * Copyright 2010 salaboy.
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
 * under the License.
 */
package org.drools.core.time;

import org.drools.base.base.ValueResolver;
import org.drools.core.reteoo.BaseTuple;
import org.drools.core.rule.Declaration;
import org.drools.util.DateUtils;

import java.util.Date;

public class TimerExpressionUtil {
    public static long evalTimeExpression(TimerExpression expr, BaseTuple leftTuple, Declaration[] declrs, ValueResolver valueResolver) {
        Object d = expr.getValue( leftTuple,  declrs, valueResolver );
        if ( d instanceof Number ) {
            return ((Number) d).longValue();
        }
        return TimeUtils.parseTimeString( d.toString() );
    }

    public static Date evalDateExpression(TimerExpression expr, BaseTuple leftTuple, Declaration[] declrs, ValueResolver valueResolver) {
        if (expr == null) {
            return null;
        }
        Object d = expr.getValue( leftTuple, declrs, valueResolver );
        if ( d == null ) {
            return null;
        }
        if ( d instanceof Number ) {
            return new Date( ((Number) d).longValue() );
        }
        return DateUtils.parseDate(d.toString());
    }
}
