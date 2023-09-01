package org.kie.drl.engine.runtime.kiesession.local.model;

import org.kie.api.runtime.KieSession;
import org.kie.drl.engine.runtime.model.EfestoOutputDrl;
import org.kie.efesto.common.api.identifiers.ModelLocalUriId;

/**
 * <code>EfestoOutputDrl</code> specific for local kiesession usage.
 * Its only scope it is to return a <code>KieSession</code> instance.
 * <p>
 * The returned <code>LocalUri</code> will contain the session id as last element of the path
 */
public class EfestoOutputDrlKieSessionLocal extends EfestoOutputDrl<KieSession> {

    public EfestoOutputDrlKieSessionLocal(ModelLocalUriId modelLocalUriId, KieSession kieSession) {
        super(modelLocalUriId, kieSession);
    }
}
