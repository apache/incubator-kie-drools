package org.kie.efesto.common.api.identifiers;

/**
 * Useful for testing. Creates Components reflectively.
 */
public class ReflectiveAppRoot extends AppRoot {
    public ReflectiveAppRoot(String name) {
        super(name);
    }

    public ReflectiveAppRoot() {
        super("efesto-app");
    }

    @Override
    public <T extends ComponentRoot> T get(Class<T> providerId) {
        try {
            return providerId.getConstructor().newInstance();
        } catch (ReflectiveOperationException e) {
            throw new IllegalArgumentException(e);
        }
    }
}
