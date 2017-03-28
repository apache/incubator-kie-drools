package org.kie.dmn.core.util;

import org.kie.dmn.api.core.DMNMessage;
import org.kie.dmn.api.feel.runtime.events.FEELEvent;
import org.kie.dmn.core.api.DMNMessageManager;
import org.kie.dmn.core.impl.DMNResultImpl;
import org.kie.dmn.core.util.Msg.Message;
import org.kie.dmn.core.util.Msg.Message0;
import org.kie.dmn.core.util.Msg.Message1;
import org.kie.dmn.core.util.Msg.Message2;
import org.kie.dmn.core.util.Msg.Message3;
import org.kie.dmn.core.util.Msg.Message4;
import org.kie.dmn.api.core.DMNMessage.Severity;
import org.kie.dmn.model.v1_1.DMNElement;
import org.kie.dmn.model.v1_1.DMNModelInstrumentedBase;
import org.slf4j.Logger;

public class MsgUtil {

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

}
