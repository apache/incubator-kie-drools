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
package org.kie.dmn.core.util;

import org.kie.dmn.api.core.DMNMessage;
import org.kie.dmn.api.core.DMNMessage.Severity;
import org.kie.dmn.api.feel.runtime.events.FEELEvent;
import org.kie.dmn.core.api.DMNMessageManager;
import org.kie.dmn.core.util.Msg.Message;
import org.kie.dmn.core.util.Msg.Message0;
import org.kie.dmn.core.util.Msg.Message1;
import org.kie.dmn.core.util.Msg.Message2;
import org.kie.dmn.core.util.Msg.Message3;
import org.kie.dmn.core.util.Msg.Message4;
import org.kie.dmn.model.api.DMNModelInstrumentedBase;
import org.slf4j.Logger;

public final class MsgUtil {

    public static String createMessage( Message0 message) {
        return MsgUtil.buildMessage(message);
    }
    public static String createMessage( Message1 message, Object p1) {
        return MsgUtil.buildMessage(message, p1);
    }
    public static String createMessage( Message2 message, Object p1, Object p2) {
        return MsgUtil.buildMessage(message, p1, p2);
    }
    public static String createMessage( Message3 message, Object p1, Object p2, Object p3) {
        return MsgUtil.buildMessage(message, p1, p2, p3);
    }
    public static String createMessage( Message4 message, Object p1, Object p2, Object p3, Object p4) {
        return MsgUtil.buildMessage(message, p1, p2, p3, p4);
    }
    
    private static String buildMessage( Message message, Object... params ) {
        return String.format( message.getMask(), params );
    }

    public static DMNMessage reportMessage(Logger logger, Severity severity, DMNModelInstrumentedBase source, DMNMessageManager result, Throwable exception, FEELEvent event, Message0 template) {
        String message = createMessage( template );
        return logMessage( logger, severity, source, result, exception, event, template, message );
    }

    public static DMNMessage reportMessage(Logger logger, Severity severity, DMNModelInstrumentedBase source, DMNMessageManager result, Throwable exception, FEELEvent event, Message1 template, Object p1) {
        String message = createMessage( template, p1 );
        return logMessage( logger, severity, source, result, exception, event, template, message );
    }

    public static DMNMessage reportMessage(Logger logger, Severity severity, DMNModelInstrumentedBase source, DMNMessageManager result, Throwable exception, FEELEvent event, Message2 template, Object p1, Object p2) {
        String message = createMessage( template, p1, p2);
        return logMessage( logger, severity, source, result, exception, event, template, message );
    }

    public static DMNMessage reportMessage(Logger logger, Severity severity, DMNModelInstrumentedBase source, DMNMessageManager result, Throwable exception, FEELEvent event, Message3 template, Object p1, Object p2, Object p3) {
        String message = createMessage( template, p1, p2, p3);
        return logMessage( logger, severity, source, result, exception, event, template, message );
    }

    public static DMNMessage reportMessage(Logger logger, Severity severity, DMNModelInstrumentedBase source, DMNMessageManager result, Throwable exception, FEELEvent event, Message4 template, Object p1, Object p2, Object p3, Object p4) {
        String message = createMessage( template, p1, p2, p3, p4);
        return logMessage( logger, severity, source, result, exception, event, template, message );
    }

    private static DMNMessage logMessage(Logger logger, Severity severity, DMNModelInstrumentedBase source, DMNMessageManager result, Throwable exception, FEELEvent event, Message template, String message) {
        switch ( severity ) {
            case ERROR: logger.error( message ); break;
            case WARN: logger.warn( message ); break;
            default: logger.info( message );
        }
        if (logger.isDebugEnabled() && exception != null) {
            logger.debug(message, exception);
        }
        if( event != null ) {
            return result.addMessage(
                    severity,
                    message,
                    template.getType(),
                    source,
                    event );
        } else {
            return result.addMessage(
                    severity,
                    message,
                    template.getType(),
                    source,
                    exception );
        }
    }

    private MsgUtil() {
        // Constructing instances is not allowed for this class
    }

    public static String clipString(String source, int maxChars) {
        return org.kie.dmn.feel.util.MsgUtil.clipString(source, maxChars);
    }
}
