package org.drools.scenariosimulation.backend.util;

import java.io.IOException;
import java.util.Optional;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Class used to provide JSON common utils
 */
public class JsonUtils {

    private JsonUtils() {
        // Not instantiable
    }

    /**
     * This method aim is to to evaluate if any possible String is a valid json or not.
     * Given a json in String format, it try to convert it in a <code>JsonNode</code>. In case of success, i.e.
     * the given string is a valid json, it put the <code>JsonNode</code> in a <code>Optional</code>. An empty
     * <code>Optional</code> is passed otherwise.
     * @param json
     * @return
     */
    public static Optional<JsonNode> convertFromStringToJSONNode(String json) {
        if (json == null || json.isEmpty()) {
           return Optional.empty();
        }
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(json);
            return Optional.of(jsonNode);
        } catch (JsonParseException e) {
            return Optional.empty();
        } catch (IOException e) {
            throw new IllegalArgumentException("Generic error during json parsing: " + json, e);
        }
    }
}
