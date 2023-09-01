package org.kie.dmn.core.impl;

import org.kie.dmn.api.core.DMNModel;

/**
 * internal utility class.
 */
public class DMNResultImplFactory {

    public DMNResultImpl newDMNResultImpl(DMNModel model) {
        return new DMNResultImpl(model);
    }
}
