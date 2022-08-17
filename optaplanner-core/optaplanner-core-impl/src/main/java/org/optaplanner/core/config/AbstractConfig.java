package org.optaplanner.core.config;

import java.util.function.Consumer;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

/**
 * A config class is a user-friendly, validating configuration class that maps XML input.
 * It builds the runtime impl classes (which are optimized for scalability and performance instead).
 * <p>
 * A config class should adhere to "configuration by exception" in its XML/JSON input/output,
 * so all non-static fields should be null by default.
 * Using the config class to build a runtime class, must not alter the config class's XML/JSON output.
 *
 * @param <Config_> the same class as the implementing subclass
 */
@XmlAccessorType(XmlAccessType.FIELD) // Applies to all subclasses.
public abstract class AbstractConfig<Config_ extends AbstractConfig<Config_>> {

    /**
     * Inherits each property of the {@code inheritedConfig} unless that property (or a semantic alternative)
     * is defined by this instance (which overwrites the inherited behaviour).
     * <p>
     * After the inheritance, if a property on this {@link AbstractConfig} composition is replaced,
     * it should not affect the inherited composition instance.
     *
     * @param inheritedConfig never null
     * @return this
     */
    public abstract Config_ inherit(Config_ inheritedConfig);

    /**
     * Typically implemented by constructing a new instance and calling {@link #inherit(AbstractConfig)} on it.
     *
     * @return new instance
     */
    public abstract Config_ copyConfig();

    /**
     * Call the class visitor on each (possibly null) Class instance provided to this config by the user
     * (including those provided in child configs).
     * Required to create the bean factory in Quarkus.
     *
     * @param classVisitor The visitor of classes, never null. Can accept null instances of Class.
     */
    public abstract void visitReferencedClasses(Consumer<Class<?>> classVisitor);

    @Override
    public String toString() {
        return getClass().getSimpleName() + "()";
    }

}
