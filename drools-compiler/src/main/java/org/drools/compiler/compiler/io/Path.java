package org.drools.compiler.compiler.io;

public interface Path {
    String toPortableString();
    
    String toRelativePortableString(Path path);
}
