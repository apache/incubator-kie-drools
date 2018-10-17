package org.kie.dmn.api.core;

import java.util.List;

public interface AfterGeneratingSourcesListener {

    void accept(List<GeneratedSource> generatedSource);

    class GeneratedSource {

        private final String fileName;
        private final String sourceContent;

        public GeneratedSource(String fileName, String sourceContent) {
            this.fileName = fileName;
            this.sourceContent = sourceContent;
        }

        public String getFileName() {
            return fileName;
        }

        public String getSourceContent() {
            return sourceContent;
        }

        @Override
        public String toString() {
            return "GeneratedSource{" +
                    "fileName='" + fileName + '\'' +
                    ", sourceContent='" + sourceContent + '\'' +
                    '}';
        }
    }
}

