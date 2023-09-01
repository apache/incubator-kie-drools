package org.kie.drl.engine.runtime.kiesession.local.model;

import org.kie.drl.engine.runtime.model.EfestoInputDrl;
import org.kie.efesto.common.api.identifiers.ModelLocalUriId;

/**
 * <code>EfestoInputDrl</code> specific for local kiesession usage.
 * Its only scope it is to retrieve a <code>KieSession</code> instance
 */
public class EfestoInputDrlKieSessionLocal extends EfestoInputDrl<String> {

    public EfestoInputDrlKieSessionLocal(ModelLocalUriId modelLocalUriId, String inputData) {
        super(modelLocalUriId, inputData);
    }
}
