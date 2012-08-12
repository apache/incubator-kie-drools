package org.drools.kproject;

public interface Path {
    String toPortableString();
    
    String toRelativePortableString(Path path);
}
