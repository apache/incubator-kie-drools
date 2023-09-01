package org.kie.pmml.commons;

import org.kie.pmml.commons.model.HasSourcesMap;

/**
 * Interface used to decouple <code>PMMLCompilerService</code> from <code>KiePMMLDroolsModelWithSources</code>
 */
public interface HasRule extends HasSourcesMap {

    String getPkgUUID();

    Object getPackageDescr();
}
