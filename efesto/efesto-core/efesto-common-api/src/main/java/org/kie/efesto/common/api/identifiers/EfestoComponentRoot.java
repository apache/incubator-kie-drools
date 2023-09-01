package org.kie.efesto.common.api.identifiers;

/**
 * Efesto-specific root path of a component.
 * <p>
 * Those are the top-level chidlren of <code>EfestoAppRoot</code>,
 * the efesto-specific subclass of <code>AppRoot</code>
 */
public interface EfestoComponentRoot extends ComponentRoot {

    <T extends ComponentRoot> T get(Class<T> providerId);
}
