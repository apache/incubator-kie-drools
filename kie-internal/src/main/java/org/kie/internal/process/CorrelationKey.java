package org.kie.internal.process;

import java.util.List;

public interface CorrelationKey {

    String getName();

    List<CorrelationProperty<?>> getProperties();

    String toExternalForm();
}
