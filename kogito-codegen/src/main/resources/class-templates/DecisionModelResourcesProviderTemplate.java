public class DecisionModelResourcesProvider implements org.kie.internal.decision.DecisionModelResourcesProvider {

    private final static java.util.List<org.kie.internal.decision.DecisionModelResource> resources = getResources();

    @Override
    public java.util.List<org.kie.internal.decision.DecisionModelResource> get() {
        return this.resources;
    }

    private final static java.util.List<org.kie.internal.decision.DecisionModelResource> getResources() {
        java.util.List<org.kie.internal.decision.DecisionModelResource> resourcePaths = new java.util.ArrayList<>();
        return resourcePaths;
    }
}
