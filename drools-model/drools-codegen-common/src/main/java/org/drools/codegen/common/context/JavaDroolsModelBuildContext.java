package org.drools.codegen.common.context;

public class JavaDroolsModelBuildContext extends AbstractDroolsModelBuildContext {

    public static final String CONTEXT_NAME = "Java";

    protected JavaDroolsModelBuildContext(JavaKogitoBuildContextBuilder builder) {
        super(builder, CONTEXT_NAME);
    }

    public static Builder builder() {
        return new JavaKogitoBuildContextBuilder();
    }

    protected static class JavaKogitoBuildContextBuilder extends AbstractBuilder {

        protected JavaKogitoBuildContextBuilder() {
        }

        @Override
        public JavaDroolsModelBuildContext build() {
            return new JavaDroolsModelBuildContext(this);
        }

        @Override
        public String toString() {
            return JavaDroolsModelBuildContext.CONTEXT_NAME;
        }
    }
}