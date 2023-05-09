package org.drools.drlonyaml.cli.utils;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.function.Consumer;

public class FileWriteStrategy implements Consumer<String> {
    private File f;
    
    public FileWriteStrategy(File f) {
        this.f = f;
    }
    
    @Override
    public void accept(String t) {
        try {
            Files.write(f.toPath(), t.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
