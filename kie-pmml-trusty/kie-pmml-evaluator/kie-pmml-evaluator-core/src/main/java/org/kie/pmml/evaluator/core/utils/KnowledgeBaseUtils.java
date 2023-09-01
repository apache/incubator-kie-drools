package org.kie.pmml.evaluator.core.utils;

public class KnowledgeBaseUtils {

//    private static final Logger logger = LoggerFactory.getLogger(KnowledgeBaseUtils.class);
//
//    private KnowledgeBaseUtils() {
//        // Avoid instantiation
//    }
//
//    public static List<KiePMMLModel> getModels(final KieMemoryCompiler.MemoryCompilerClassLoader classLoader) {
//        List<KiePMMLModel> models = new ArrayList<>();
//        KieMemoryCompiler.MemoryCompilerClassLoader.getSystemClassLoader();
//       runtimePackageContainer.getRuntimePackages().forEach(kpkg -> {
//            PMMLPackage pmmlPackage = (PMMLPackage) kpkg.getResourceTypePackages().get(ResourceType.PMML);
//            if (pmmlPackage != null) {
//                models.addAll(pmmlPackage.getAllModels().values());
//            }
//        });
//        return models;
//    }
//
//    public static Optional<KiePMMLModel> getPMMLModel(final RuntimePackageContainer runtimePackageContainer, String
//    modelName) {
//        logger.trace("getModels {} {}", runtimePackageContainer, modelName);
//        return getModels(runtimePackageContainer)
//                .stream()
//                .filter(model -> Objects.equals(modelName, model.getName()))
//                .findFirst();
//    }
}
