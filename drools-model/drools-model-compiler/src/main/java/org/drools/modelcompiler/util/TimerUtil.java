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
package org.drools.modelcompiler.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.util.Map;

import org.drools.base.base.ValueResolver;
import org.drools.base.reteoo.BaseTuple;
import org.drools.base.rule.Declaration;
import org.drools.base.time.TimeUtils;
import org.drools.core.time.TimerExpression;
import org.drools.core.time.impl.CronExpression;
import org.drools.util.ClassUtils;
import org.drools.util.DateUtils;

public class TimerUtil {

    public static boolean validateTimer(String timerString) {
        int colonPos = timerString.indexOf( ":" );
        int semicolonPos = timerString.indexOf( ";" );
        String protocol = colonPos < 0 ? "int" : timerString.substring( 0, colonPos );
        String body = timerString.substring( colonPos + 1, semicolonPos > 0 ? semicolonPos : timerString.length() ).trim();

        switch (protocol) {
            case "int":
                String[] times = body.trim().split( "\\s" );
                long delay = 0;
                long period = 0;

                if ( times.length > 2 ) {
                    return false;
                }

                try {
                    if ( times.length == 1 ) {
                        // only defines a delay
                        delay = TimeUtils.parseTimeString( times[0] );
                    } else {
                        // defines a delay and a period for intervals
                        delay = TimeUtils.parseTimeString( times[0] );
                        period = TimeUtils.parseTimeString( times[1] );
                    }
                } catch (RuntimeException e) {
                    return false;
                }

                return true;
            case "cron":
                try {
                    new CronExpression( body );
                } catch ( ParseException e ) {
                    return false;
                }
                return true;
            case "expr":
                return true;
        }

        return false;
    }

    public static TimerExpression buildTimerExpression( String expression, Map<String, Declaration> decls ) {
        if (expression == null) {
            return null;
        }

        try {
            // try to parse it as a long constant
            return new ConstantTimerExpression( TimeUtils.parseTimeString( expression ) );
        } catch (Exception e) {
            // ignore
        }

        try {
            // try to parse it as a Date constant
            return new ConstantTimerExpression( DateUtils.parseDate( expression ).getTime() );
        } catch (Exception e) {
            // ignore
        }

        Declaration declaration = decls.get( expression );
        if (declaration != null) {
            return new DeclarationTimerExpression( declaration );
        }

        int dotPos = expression.indexOf( '.' );
        if (dotPos > 0) {
            String declName = expression.substring( 0, dotPos );
            declaration = decls.get(declName );
            if (declaration != null) {
                return new FieldTimerExpression( declaration, expression.substring( dotPos+1 ) );
            }
        }

        throw new UnsupportedOperationException("Invalid timer expression: " + expression);
    }

    private static class DeclarationTimerExpression implements TimerExpression {
        private final Declaration declaration;

        private DeclarationTimerExpression( Declaration declaration ) {
            this.declaration = declaration;
        }
        @Override

        public Declaration[] getDeclarations() {
            return new Declaration[] { declaration };
        }

        @Override
        public Object getValue(BaseTuple leftTuple, Declaration[] declrs, ValueResolver valueResolver) {
            return declrs[0].getValue( valueResolver, leftTuple );
        }
    }

    private static class FieldTimerExpression implements TimerExpression {
        private final Declaration declaration;
        private final Method method;

        private FieldTimerExpression( Declaration declaration, String field ) {
            this.declaration = declaration;
            method = ClassUtils.getGetterMethod( declaration.getDeclarationClass(), field );
        }
        @Override

        public Declaration[] getDeclarations() {
            return new Declaration[] { declaration };
        }

        @Override
        public Object getValue(BaseTuple leftTuple, Declaration[] declrs, ValueResolver valueResolver) {
            try {
                return method.invoke( declrs[0].getValue( valueResolver, leftTuple ) );
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException( e );
            }
        }
    }

    private static class ConstantTimerExpression implements TimerExpression {
        private final long value;

        private ConstantTimerExpression( long value ) {
            this.value = value;
        }
        @Override

        public Declaration[] getDeclarations() {
            return new Declaration[0];
        }

        @Override
        public Object getValue(BaseTuple leftTuple, Declaration[] declrs, ValueResolver valueResolver) {
            return value;
        }
    }
}
