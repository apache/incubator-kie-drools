package org.drools.compiler.io;

public interface Path {
    String toPortableString();
    
    String toRelativePortableString(Path path);
}
