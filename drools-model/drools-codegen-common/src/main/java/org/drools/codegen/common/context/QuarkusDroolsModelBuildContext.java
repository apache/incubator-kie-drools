package org.drools.codegen.common.context;

public class QuarkusDroolsModelBuildContext extends AbstractDroolsModelBuildContext {

    public static final String CONTEXT_NAME = "Quarkus";
    public static final String QUARKUS_REST = "javax.ws.rs.Path";
    public static final String QUARKUS_DI = "javax.inject.Inject";

    protected QuarkusDroolsModelBuildContext(QuarkusKogitoBuildContextBuilder builder) {
        super(builder, /*new CDIDependencyInjectionAnnotator(), new CDIRestAnnotator(),  */ CONTEXT_NAME);
    }

    public static Builder builder() {
        return new QuarkusKogitoBuildContextBuilder();
    }

    protected static class QuarkusKogitoBuildContextBuilder extends AbstractBuilder {

        protected QuarkusKogitoBuildContextBuilder() {
        }

        @Override
        public QuarkusDroolsModelBuildContext build() {
            return new QuarkusDroolsModelBuildContext(this);
        }

        @Override
        public String toString() {
            return QuarkusDroolsModelBuildContext.CONTEXT_NAME;
        }
    }
}