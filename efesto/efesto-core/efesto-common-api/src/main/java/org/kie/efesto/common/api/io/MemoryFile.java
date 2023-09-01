package org.kie.efesto.common.api.io;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.URL;
import java.nio.file.Path;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MemoryFile extends File implements Serializable {

    private static final long serialVersionUID = 1690003941627937413L;

    private static final Logger logger = LoggerFactory.getLogger(MemoryFile.class);
    private String name;
    private transient Path filePath;

    private final URL url;

    private byte[] content;

    public MemoryFile(Path filePath) throws IOException {
        super(filePath.getFileName().toString());
        logger.debug("MemoryFile {}", filePath);
        logger.debug(this.getAbsolutePath());
        this.name = filePath.getFileName().toString();
        this.filePath = filePath;
        url = filePath.toUri().toURL();
        initContent(url);
    }

    public MemoryFile(URL url) throws IOException {
        super(url.getPath());
        logger.debug("MemoryFile {}", url);
        logger.debug(this.getAbsolutePath());
        this.name = url.getFile();
        if (name.contains("/")) {
            name = name.substring(name.lastIndexOf("/") +1 );
        }
        this.url = url;
        initContent(this.url);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean exists() {
        return url != null;
    }

    private void initContent(URL url) throws IOException {
        logger.debug("initContent {}", url);
        if (url != null) {
            try (InputStream input = url.openStream()) {
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                int read;
                byte[] bytes = new byte[1024];
                while ((read = input.read(bytes)) != -1) {
                    out.write(bytes, 0, read);
                }
                content = out.toByteArray();
                out.flush();
                out.close();
            }
        }
    }

    public byte[] getContent() {
        return content;
    }

    @Override
    public boolean canRead() {
        return content != null;
    }

    @Override
    public boolean canWrite() {
        return false;
    }

    @Override
    public long length() {
        return content != null ? content.length : 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        MemoryFile that = (MemoryFile) o;
        return Objects.equals(name, that.name) && Objects.equals(filePath, that.filePath) && Objects.equals(url,
                                                                                                            that.url);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), name, filePath, url);
    }

    @Override
    public String toString() {
        return "MemoryFile{" +
                "name='" + name + '\'' +
                ", filePath=" + filePath +
                ", url=" + url +
                "} " + super.toString();
    }
}
