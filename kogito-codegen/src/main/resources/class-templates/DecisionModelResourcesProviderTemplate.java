public class DecisionModelResourcesProvider implements org.kie.internal.decision.DecisionModelResourcesProvider {

    private final static boolean IS_NATIVE_IMAGE = System.getProperty("org.graalvm.nativeimage.imagecode") != null;

    // See https://issues.redhat.com/browse/KOGITO-3330
    private static java.io.InputStreamReader readResource(java.io.InputStream stream) {
        if (!IS_NATIVE_IMAGE) {
            return new java.io.InputStreamReader(stream);
        }

        try {
            byte[] bytes = stream.readAllBytes();
            java.io.ByteArrayInputStream byteArrayInputStream = new java.io.ByteArrayInputStream(bytes);
            return new java.io.InputStreamReader(byteArrayInputStream);
        } catch (java.io.IOException e) {
            throw new java.io.UncheckedIOException(e);
        }
    }

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
