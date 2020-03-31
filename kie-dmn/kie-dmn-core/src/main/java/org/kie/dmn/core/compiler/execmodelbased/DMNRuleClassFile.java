package org.kie.dmn.core.compiler.execmodelbased;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.drools.core.util.IoUtils;

public class DMNRuleClassFile {

    public final static String RULE_CLASS_FILE_NAME = "META-INF/kie/dmn";
    private ClassLoader classLoader;
    private List<String> classFile;

    public DMNRuleClassFile(ClassLoader classLoader) {
        this.classLoader = classLoader;
        if (this.classLoader == null) {
            classFile = Collections.emptyList();
        }
    }

    public boolean hasCompiledClasses() {
        if (classFile == null) {
            classFile = getClassFile();
        }
        return classFile.size() > 0;
    }

    public List<String> getClassFile() {
        if (classFile == null) {
            try {
                InputStream resourceAsStream = classLoader.getResourceAsStream(RULE_CLASS_FILE_NAME);
                if (resourceAsStream != null) {
                    String allClasses = new String(IoUtils.readBytesFromInputStream(resourceAsStream));
                    classFile = Arrays.asList(allClasses.split("\\n"));
                } else {
                    classFile = new ArrayList<>();
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        return classFile;
    }

    public Optional<String> getCompiledClass(String className) {
        return getClassFile().stream().filter(ms -> ms.equals(className)).findFirst();
    }
}
