package org.drools.guvnor.client.modeldriven.brl;

import java.io.Serializable;


/**
 * This is the marker interface for portable Ajaxy type objects.
 * This is replaced in the BRMS with a GWT specific one, and only used here so the RuleModel
 * can compile. It does nothing, and is strictly a marker interface only.
 * @author Michael Neale
 *
 */
public interface PortableObject extends Serializable {

}
