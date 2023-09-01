package org.kie.efesto.common.api.identifiers;

/**
 * Root path of an application.
 *
 * An application has a name. Many applications may be created. The name creates
 * a name space for each application, so that components may be individually
 * addressed across application instances.
 *
 * An AppRoot retrieves "components" through {@link ComponentRoot}s.
 * Each component provides a fluent builder for an {@link Id} that is specific
 * to that component.
 */
public abstract class AppRoot extends LocalUriId implements Id {

    private final String name;

    protected AppRoot(String name) {
        super(LocalUri.Root);
        this.name = name;
    }

    /**
     * subclasses should override this using the appropriate
     * DI/ServiceLoading mechanism to allow the pattern <code>appRoot.get(Components.class)...</code>;
     * e.g. <code>appRoot.get(ProcessIds.class).get("my.process.id).tasks().get("my.task")</code>
     */
    abstract public <T extends ComponentRoot> T get(Class<T> providerId);

    /**
     * Name is only used to differentiate multiple applications.
     * Mostly useful in a distributed context, with RemoteIds.
     */
    public String name() {
        return name;
    }

}
