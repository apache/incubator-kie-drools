package org.kie.kogito.codegen.io;

import java.io.File;
import java.nio.file.Paths;

import org.junit.jupiter.api.Test;
import org.kie.api.io.Resource;

import static org.assertj.core.api.Assertions.assertThat;

class CollectedResourceTest {

    @Test
    void shouldNotContainDirectories() {
        assertThat(
                CollectedResource.fromDirectory(Paths.get("src/main/resources"))
                        .stream()
                        .map(CollectedResource::resource)
                        .map(Resource::getSourcePath)
                        .map(File::new)
                        .filter(File::isDirectory)
                        .count()).isZero();
    }
}