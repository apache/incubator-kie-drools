public class DecisionModels implements org.kie.kogito.decision.DecisionModels {

    private final static boolean IS_NATIVE_IMAGE = System.getProperty("org.graalvm.nativeimage.imagecode") != null;

    private final static java.util.function.Function<java.lang.String, org.kie.api.runtime.KieRuntimeFactory> kieRuntimeFactoryFunction = PredictionModels.kieRuntimeFactoryFunction;
    private final static org.kie.dmn.api.core.DMNRuntime dmnRuntime = org.kie.kogito.dmn.DMNKogito.createGenericDMNRuntime(kieRuntimeFactoryFunction);
    private final static org.kie.kogito.ExecutionIdSupplier execIdSupplier = null;

    public DecisionModels(org.kie.kogito.Application app) {
        app.config().decision().decisionEventListeners().listeners().forEach(dmnRuntime::addListener);
    }

    public org.kie.kogito.decision.DecisionModel getDecisionModel(java.lang.String namespace, java.lang.String name) {
        return new org.kie.kogito.dmn.DmnDecisionModel(dmnRuntime, namespace, name, execIdSupplier);
    }

    private static java.io.InputStreamReader readResource(java.io.InputStream stream) {
        if (!IS_NATIVE_IMAGE) {
            return new java.io.InputStreamReader(stream);
        }

        try {
            byte[] bytes = org.drools.core.util.IoUtils.readBytesFromInputStream(stream);
            java.io.ByteArrayInputStream byteArrayInputStream = new java.io.ByteArrayInputStream(bytes);
            return new java.io.InputStreamReader(byteArrayInputStream);
        } catch (java.io.IOException e) {
            throw new java.io.UncheckedIOException(e);
        }
    }

}
