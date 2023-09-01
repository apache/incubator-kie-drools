package org.kie.dmn.typesafe;

import org.kie.dmn.api.core.DMNModel;
import org.kie.dmn.feel.codegen.feel11.CodegenStringUtil;

public class DMNTypeSafePackageName {

    public interface Factory {

        DMNTypeSafePackageName create(DMNModel m);
    }

    public static class ModelFactory implements Factory {

        private final String prefix;

        public ModelFactory(String prefix) {
            this.prefix = prefix;
        }

        public ModelFactory() {
            this.prefix = "";
        }

        @Override
        public DMNTypeSafePackageName create(DMNModel model) {
            return new DMNTypeSafePackageName(prefix, model.getNamespace(), model.getName());
        }
    }

    private final String prefix;
    private final String dmnModelNamespace;
    private final String dmnModelName;

    public DMNTypeSafePackageName(String prefix, String dmnModelNamespace, String dmnModelName) {
        this.prefix = prefix;
        this.dmnModelNamespace = dmnModelNamespace;
        this.dmnModelName = dmnModelName;
    }

    public String packageName() {
        return CodegenStringUtil.escapeIdentifier(prefix + dmnModelNamespace + dmnModelName);
    }

    public String appendPackage(String typeName) {
        return packageName() + "." + typeName;
    }

    @Override
    public String toString() {
        return "DMNTypeSafePackageName{" +
                "prefix='" + prefix + '\'' +
                ", dmnModelNamespace='" + dmnModelNamespace + '\'' +
                ", dmnModelName='" + dmnModelName + '\'' +
                '}';
    }
}
