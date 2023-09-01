package org.drools.scenariosimulation.backend.util;

import java.util.List;

public class ScenarioSimulationServerMessages {

    private ScenarioSimulationServerMessages() {
        // Util class - Not instantiable
    }

    public static final String NULL = "null";

    public static String getFactWithWrongValueExceptionMessage(String factName, Object expectedValue, Object actualValue) {
        if (expectedValue == null) {
            expectedValue = NULL;
        }
        if (actualValue == null) {
            actualValue = NULL;
        }
        return String.format("Failed in \"%s\": The expected value is \"%s\" but the actual one is \"%s\"", factName, expectedValue, actualValue);
    }

    public static String getGenericScenarioExceptionMessage(String exceptionMessage) {
        return  String.format("Failure reason: %s", exceptionMessage);
    }

    public static String getCollectionFactExceptionMessage(String factName, List<String> pathToWrongValue, Object wrongValue) {
        StringBuilder stringBuilder = new StringBuilder("Failed in \"").append(factName).append("\": ");
        if (pathToWrongValue.isEmpty()) {
            stringBuilder.append("Impossible to find elements in the collection to satisfy the conditions.");
        } else {
            if (wrongValue != null) {
                stringBuilder.append("Value \"").append(wrongValue).append("\" is wrong in ");
            } else {
                stringBuilder.append("Wrong in ");
            }
            stringBuilder.append("\"").append(String.join(".", pathToWrongValue)).append("\"");
        }
        return stringBuilder.toString();
    }

    public static String getIndexedScenarioMessage(String assertionError, int index, String scenarioDescription, String fileName) {
        StringBuilder message = new StringBuilder().append("#").append(index);
        if (scenarioDescription != null && !scenarioDescription.isEmpty()) {
            message.append(" ").append(scenarioDescription);
        }
        message.append(": ").append(assertionError);
        if (fileName != null) {
            message.append(" (").append(fileName).append(")");
        }
        return message.toString();
    }

}