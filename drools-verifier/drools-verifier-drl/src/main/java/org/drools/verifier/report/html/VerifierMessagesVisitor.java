package org.drools.verifier.report.html;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.drools.verifier.data.VerifierData;
import org.drools.verifier.report.components.Cause;
import org.drools.verifier.report.components.VerifierMessage;
import org.drools.verifier.report.components.VerifierMessageBase;
import org.drools.verifier.report.components.VerifierRangeCheckMessage;
import org.mvel2.templates.TemplateRuntime;

class VerifierMessagesVisitor extends ReportVisitor {

    private static final String VERIFIER_MESSAGES_TEMPLATE = "verifierMessages.htm";
    private static final String VERIFIER_MESSAGE_TEMPLATE = "verifierMessage.htm";

    public static final String NOTES = "Notes";
    public static final String WARNINGS = "Warnings";
    public static final String ERRORS = "Errors";

    public static String visitVerifierMessagesCollection(String title,
            Collection<VerifierMessageBase> messages, VerifierData data) {
        Map<String, Object> map = new HashMap<>();
        Collection<String> messageTemplates = new ArrayList<>();
        String myTemplate = readFile(VERIFIER_MESSAGES_TEMPLATE);

        for (VerifierMessageBase message : messages) {
            messageTemplates.add(visitVerifierMessage(message, data));
        }

        map.put("title", title);
        map.put("messages", messageTemplates);

        return String.valueOf(TemplateRuntime.eval(myTemplate, map));
    }

    public static String visitVerifierMessage(VerifierMessageBase message,
            VerifierData data) {
        if (message instanceof VerifierRangeCheckMessage) {
            return visitVerifierMessage((VerifierRangeCheckMessage) message,
                    data);
        } else if (message instanceof VerifierMessage) {
            return visitVerifierMessage((VerifierMessage) message);
        }

        return null;
    }

    public static String visitVerifierMessage(
            VerifierRangeCheckMessage message, VerifierData data) {

        return MissingRangesReportVisitor.visitRangeCheckMessage(
                UrlFactory.THIS_FOLDER, message, data);
    }

    public static String visitVerifierMessage(VerifierMessage message) {

        Map<String, Object> map = new HashMap<>();
        Collection<String> causeUrls = new ArrayList<>();
        String myTemplate = readFile(VERIFIER_MESSAGE_TEMPLATE);

        // Solve the url's to causes if there is any.
        for (Cause cause : message.getCauses()) {
            causeUrls.add(UrlFactory.getUrl(cause));
        }

        map.put("title", message.getSeverity());
        map.put("reason", message.getFaulty());
        map.put("message", message.getMessage());
        map.put("causes", causeUrls);

        return String.valueOf(TemplateRuntime.eval(myTemplate, map));
    }
}
