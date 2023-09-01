package org.kie.pmml.compiler.api.provider;

import java.util.List;

import org.dmg.pmml.Model;
import org.kie.pmml.commons.model.KiePMMLModel;

/**
 * Actual implementation is required to retrieve a
 * <code>List&lt;ModelImplementationProvider&gt;</code> out from the classes found in the classpath
 */
public interface ModelImplementationProviderFinder {

    /**
     * Retrieve all the <code>ModelImplementationProvider</code> implementations in the classpath
     * @param refresh pass <code>true</code> to reload classes from classpath; <code>false</code> to use cached ones
     * @return
     */
    <T extends Model, E extends KiePMMLModel> List<ModelImplementationProvider<T, E>> getImplementations(boolean refresh);
}
