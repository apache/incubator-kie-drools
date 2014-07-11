package org.kie.scanner;

import javax.enterprise.context.Dependent;
import javax.inject.Named;

/**
 * A dependent scoped bean with a name. It is not injected anywhere, so should not cause any problems....
 */
@Dependent
@Named("a-name")
public class DependentScopeBean {

}
