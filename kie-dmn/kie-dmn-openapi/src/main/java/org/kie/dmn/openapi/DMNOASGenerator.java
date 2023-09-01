package org.kie.dmn.openapi;

import org.kie.dmn.openapi.model.DMNOASResult;

/**
 * Internal utility to generate OpenAPI (OAS) schema and related metadata information
 */
public interface DMNOASGenerator {

    DMNOASResult build();

}
