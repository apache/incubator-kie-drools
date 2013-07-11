package org.drools.compiler.compiler;

import java.io.IOException;
import java.util.List;

import org.kie.api.io.Resource;

public interface ProcessBuilder {

    List<DroolsError> addProcessFromXml(Resource resource) throws IOException;

    List<DroolsError> getErrors();
}
