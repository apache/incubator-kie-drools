package org.kie.efesto.common.api.identifiers;

import java.util.Map;

import static org.kie.efesto.common.api.utils.EfestoAppRootHelper.getEfestoComponentRootBySPI;

/**
 * Efesto-specific root path of an application.
 * <p>
 * Its top-level children are <code>EfestoComponentRoot</code> instances, the efesto-specific subclass of
 * <code>ComponentRoot</code>
 *
 * It also implements <code>ComponentRoot</code> so that it can be used as top-level <code>path</code> inside another <code>AppRoot</code>
 */
public final class EfestoAppRoot extends AppRoot implements ComponentRoot {

    public static final String EGESTO_ENGINES = "engines";

    private static final Map<Class<? extends EfestoComponentRoot>, EfestoComponentRoot> INSTANCES;

    static {
        INSTANCES = getEfestoComponentRootBySPI(EfestoComponentRoot.class);
    }

    public EfestoAppRoot() {
        super(EGESTO_ENGINES);
    }

    @Override
    public <T extends ComponentRoot> T get(Class<T> providerId) {
        return (T) INSTANCES.get(providerId);
    }

}
