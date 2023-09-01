package org.drools.codegen.common.context;

public class SpringBootDroolsModelBuildContext extends AbstractDroolsModelBuildContext {

    public static final String CONTEXT_NAME = "Spring";
    public static final String SPRING_REST = "org.springframework.web.bind.annotation.RestController";
    public static final String SPRING_DI = "org.springframework.beans.factory.annotation.Autowired";

    protected SpringBootDroolsModelBuildContext(SpringBootKogitoBuildContextBuilder builder) {
        super(builder, /* new SpringDependencyInjectionAnnotator(), new SpringRestAnnotator(),*/ CONTEXT_NAME);
    }

    public static Builder builder() {
        return new SpringBootKogitoBuildContextBuilder();
    }

    protected static class SpringBootKogitoBuildContextBuilder extends AbstractBuilder {

        protected SpringBootKogitoBuildContextBuilder() {
        }

        @Override
        public SpringBootDroolsModelBuildContext build() {
            return new SpringBootDroolsModelBuildContext(this);
        }

        @Override
        public String toString() {
            return SpringBootDroolsModelBuildContext.CONTEXT_NAME;
        }
    }
}