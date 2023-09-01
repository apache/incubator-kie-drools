package org.kie.efesto.compilationmanager.api.model;

import java.util.Map;

public interface EfestoClassesContainer {

    Map<String, byte[]> getCompiledClassesMap();
}
