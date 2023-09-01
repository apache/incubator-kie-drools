package org.kie.efesto.common.utils;

public class PackageClassNameUtils {

    private static final String FOLDER_SEPARATOR = "/";

    private static final String WINDOWS_FOLDER_SEPARATOR = "\\";

    private PackageClassNameUtils() {
    }

    /**
     * Method to be used by <b>every</b> KiePMML implementation to retrieve the <b>package</b> name
     * out of the model name
     *
     * @param modelName
     * @return
     */
    public static String getSanitizedPackageName(String modelName) {
        return modelName.replaceAll("[^A-Za-z0-9.]", "").toLowerCase();
    }

    /**
     * Convert the given <code>String</code> in a valid class name (i.e. no dots, no spaces, first letter upper case)
     *
     * @param input
     * @return
     */
    public static String getSanitizedClassName(String input) {
        String upperCasedInput = input.substring(0, 1).toUpperCase() + input.substring(1);
        return upperCasedInput.replaceAll("[^A-Za-z0-9]", "");
    }

    /**
     * Returns an array where the first item is the <b>factory class</b> name and the second item is the <b>package</b> name,
     * built starting from the given <b>sourcePath</b> <code>String</code>
     *
     * @param sourcePath
     * @return
     */
    public static String[] getFactoryClassNamePackageName(String suffix, String sourcePath) {
        sourcePath = sourcePath.replace(WINDOWS_FOLDER_SEPARATOR, FOLDER_SEPARATOR);
        String fileName = sourcePath.substring(sourcePath.lastIndexOf(FOLDER_SEPARATOR) + 1);
        if (fileName.endsWith(suffix)) {
            fileName = fileName.substring(0, fileName.lastIndexOf(suffix) -1);
        }
        String packageName = getSanitizedPackageName(fileName);
        String factoryClassName = getSanitizedClassName(fileName + "Factory");
        return new String[]{factoryClassName, packageName};
    }
}
