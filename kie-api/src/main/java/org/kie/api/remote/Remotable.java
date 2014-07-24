package org.kie.api.remote;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * This annotation should be used by users to specify (user-defined) classes that 
 * should be available to remote services (REST, JMS, WS) when using a workbench. 
 * <br> 
 * A user will use this annotation on a class that's either part of a kjar or deployment
 * or on a class that is a dependency of the aforementioned kjar or deployment. When this deployment
 * is deployed, the workbench will scan the deployment and the dependency tree of the deployment
 * for classes marked with this annotation. 
 * <br>
 * While classes at the top level in the deployment itself marked with the appropriate JAXB annotations
 * are always available for use with the remote services (regardless of whether the classes
 * are annotated with the {@code @Remotable} annotation, in the dependency tree of the deployment, 
 * only classes marked with this annotation (and with the appropriate JAXB annotations) 
 * will be able to be used with the workbench's remote services.
 */

@Inherited
@Documented
@Retention(RUNTIME)
@Target({ElementType.TYPE})
public @interface Remotable {

}
