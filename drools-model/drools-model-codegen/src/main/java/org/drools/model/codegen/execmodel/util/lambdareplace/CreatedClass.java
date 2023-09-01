package org.drools.model.codegen.execmodel.util.lambdareplace;

import java.util.Objects;

public class CreatedClass {

    private final String contents;
    private final String className;
    private final String packageName;
    private final String canonicalName;
    private final String sourcePath;

    public CreatedClass(String contents, String className, String packageName) {
        this.className = className;
        this.packageName = packageName;
        this.contents = contents;
        this.canonicalName = packageName + "." + className;
        this.sourcePath = this.canonicalName.replace('.', '/') + ".java";
    }

    public String getClassNameWithPackage() {
        return canonicalName;
    }

    public String getClassNamePath() {
        return sourcePath;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        CreatedClass that = (CreatedClass) o;
        return contents.equals(that.contents) &&
                className.equals(that.className) &&
                packageName.equals(that.packageName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(contents, className, packageName);
    }

    public String getContents() {
        return contents;
    }
}
