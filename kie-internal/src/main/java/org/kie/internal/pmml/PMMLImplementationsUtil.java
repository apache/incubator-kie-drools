package org.kie.internal.pmml;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class used to provide utility methods to manage implementation to be invoked
 * at runtime
 */
public class PMMLImplementationsUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(PMMLImplementationsUtil.class);

    private static final String JPMML_IMPL = "org.kie.dmn.jpmml.DMNjPMMLInvocationEvaluator";

    private PMMLImplementationsUtil() {
    }

    /**
     * @param classLoader
     * @return <code>true</code> if <b>org.kie.dmn.jpmml.DMNjPMMLInvocationEvaluator</b> is found in the given
     * <code>ClassLoader</code>,
     * <code>false</code> otherwise
     */
    public static boolean isjPMMLAvailableToClassLoader(final ClassLoader classLoader) {
        try {
            classLoader.loadClass(JPMML_IMPL);
            LOGGER.info("jpmml libraries available on classpath");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }
}
