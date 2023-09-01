package org.kie.dmn.openapi;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JacksonUtils {

    private static final Logger LOG = LoggerFactory.getLogger(JacksonUtils.class);

    public static void printoutJSON(Object tree) {
        if (LOG.isDebugEnabled()) {
            ObjectMapper mapper = new ObjectMapper();
            try {
                LOG.debug(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(tree));
            } catch (Exception e) {
                LOG.error("error with Jackson serialization", e);
            }
        }
    }

    private JacksonUtils() {
        // no constructor for utility classes.
    }
}
