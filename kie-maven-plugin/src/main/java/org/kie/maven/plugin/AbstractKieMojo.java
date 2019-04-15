/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.kie.maven.plugin;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

import org.apache.maven.plugin.AbstractMojo;
import org.kie.maven.plugin.metadata.ImageMetaData;

import com.fasterxml.jackson.databind.ObjectMapper;

public abstract class AbstractKieMojo extends AbstractMojo {

    protected final static String LABEL_PREFIX = "org.kie/";
    
    private ObjectMapper mapper = new ObjectMapper();
    
    protected void setSystemProperties(Map<String, String> properties) {

        if (properties != null) {
            getLog().debug("Additional system properties: " + properties);
            for (Map.Entry<String, String> property : properties.entrySet()) {
                System.setProperty(property.getKey(), property.getValue());
            }
            getLog().debug("Configured system properties were successfully set.");
        }
    }
        
    protected void writeLabelsImageMetadata(String target, Map<String, String> labels) {
     
        try {
            Path imageMetaDataFile = Paths.get(target, "image_metadata.json");
            ImageMetaData imageMetadata = null;
            if (Files.exists(imageMetaDataFile)) {
                // read the file to merge the content
                imageMetadata =  mapper.readValue(imageMetaDataFile.toFile(), ImageMetaData.class);
            } else {
                imageMetadata = new ImageMetaData();            
            }
            imageMetadata.add(labels);
            
            Files.write(imageMetaDataFile, mapper.writerWithDefaultPrettyPrinter().writeValueAsString(imageMetadata).getBytes());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
               
    }

}
