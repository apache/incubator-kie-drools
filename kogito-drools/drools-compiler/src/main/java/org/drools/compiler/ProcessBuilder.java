package org.drools.compiler;

import java.io.IOException;
import java.util.List;

import org.drools.io.Resource;

public interface ProcessBuilder {

    List<DroolsError> addProcessFromXml(Resource resource) throws IOException;

}
