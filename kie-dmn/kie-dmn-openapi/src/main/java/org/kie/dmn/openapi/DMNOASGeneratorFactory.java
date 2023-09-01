package org.kie.dmn.openapi;

import java.util.Collection;

import org.kie.dmn.api.core.DMNModel;
import org.kie.dmn.openapi.impl.DMNOASGeneratorImpl;

public class DMNOASGeneratorFactory {

    private DMNOASGeneratorFactory() {
        // no instance of utility classes.
    }

    public static DMNOASGenerator generator(Collection<DMNModel> models) {
        return new DMNOASGeneratorImpl(models, "#/definitions/");
    }

    public static DMNOASGenerator generator(Collection<DMNModel> models, String refPrefix) {
        return new DMNOASGeneratorImpl(models, refPrefix);
    }
}
