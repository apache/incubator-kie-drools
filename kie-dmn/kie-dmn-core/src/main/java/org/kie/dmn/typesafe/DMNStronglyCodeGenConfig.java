package org.kie.dmn.typesafe;


public class DMNStronglyCodeGenConfig {

    private boolean withJacksonAnnotation = false;
    private boolean withMPOpenApiAnnotation = false;

    public boolean isWithJacksonAnnotation() {
        return withJacksonAnnotation;
    }

    public void setWithJacksonAnnotation(boolean withJacksonAnnotation) {
        this.withJacksonAnnotation = withJacksonAnnotation;
    }

    public boolean isWithMPOpenApiAnnotation() {
        return withMPOpenApiAnnotation;
    }

    public void setWithMPOpenApiAnnotation(boolean withMPOpenApiAnnotation) {
        this.withMPOpenApiAnnotation = withMPOpenApiAnnotation;
    }
}
