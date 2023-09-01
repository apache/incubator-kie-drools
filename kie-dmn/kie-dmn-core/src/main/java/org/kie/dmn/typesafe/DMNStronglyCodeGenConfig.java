package org.kie.dmn.typesafe;


public class DMNStronglyCodeGenConfig {

    private boolean withJacksonAnnotation = false;
    private boolean withMPOpenApiAnnotation = false;
    private boolean withIOSwaggerOASv3Annotation = false;

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

    public boolean isWithIOSwaggerOASv3Annotation() {
        return withIOSwaggerOASv3Annotation;
    }

    public void setWithIOSwaggerOASv3Annotation(boolean withIOSwaggerOASV3Annotation) {
        this.withIOSwaggerOASv3Annotation = withIOSwaggerOASV3Annotation;
    }

}
