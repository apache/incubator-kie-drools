package org.drools.model.codegen.execmodel;

import java.nio.charset.StandardCharsets;
import java.util.Objects;

import org.drools.util.PortablePath;

public class GeneratedFile {

    public enum Type {
        APPLICATION,
        PROCESS,
        PROCESS_INSTANCE,
        REST,
        RULE,
        DECLARED_TYPE,
        QUERY,
        MODEL,
        CLASS,
        MESSAGE_CONSUMER,
        MESSAGE_PRODUCER,
        PMML;
    }

    private final PortablePath path;
    private final byte[] data;
    private final Type type;

    public GeneratedFile(String path, String data) {
        this(Type.RULE, path, data);
    }

    public GeneratedFile(String path, byte[] data) {
        this(Type.RULE, path, data);
    }

    public GeneratedFile(Type type, String path, String data) {
        this(type, path, data.getBytes(StandardCharsets.UTF_8));
    }

    private GeneratedFile(Type type, String path, byte[] data) {
        this.type = type;
        this.path = PortablePath.of(path);
        this.data = data;
    }

    public byte[] getData() {
        return data;
    }

    public String getPath() {
        return path.asString();
    }

    public PortablePath getKiePath() {
        return path;
    }

    public Type getType() {
        return type;
    }

    @Override
    public String toString() {
        return "GeneratedFile{" +
                "path='" + path.asString() + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        GeneratedFile that = (GeneratedFile) o;
        return path.equals(that.path);
    }

    @Override
    public int hashCode() {
        return Objects.hash(path);
    }
}
