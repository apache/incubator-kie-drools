package org.drools.compiler.io.memory;

import org.drools.compiler.io.Path;

public class MemoryPath implements Path {
    private String path;

    public MemoryPath(String path) {
        this.path = path;
    }
    
    public String toPortableString() {
        return path;
    }
 
    public String toRelativePortableString(Path basePath) {
        String otherPath =  basePath.toPortableString();
        String[] otherPathSplit = otherPath.split( "/" );
        String [] pathSplit = path.split( "/" );

        // find path divergence point
        int i = 0;
        for( ; i < pathSplit.length && i < otherPathSplit.length ; i++ ) {
            if ( pathSplit[i].equals( otherPathSplit[i] ) ) {
                continue;
            } else {
                // divergence
                break;
            }
        }
        
        String str = "";
        if ( otherPathSplit.length <= i ) {
            // path is a subpath
            // now go down to file
            
            boolean first = true;
            for ( int j = i; j < pathSplit.length; j++ ) {
                if ( !first ) {
                    str = str + "/";    
                }
                str = str  + pathSplit[j];
                first = false;
            }             
        } else {
            // we need to backup "../"
            boolean first = true;
            for ( int j = i; j < otherPathSplit.length; j++ ) {
                if ( !first ) {
                    str = str + "/";    
                }
                str = str + "..";
                first = false;                
            }
            
            // now go down to file
            for ( int j = i; j < pathSplit.length; j++ ) {
                str = str +  "/" + pathSplit[j];            
            }            
        }
        
        return str;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((path == null) ? 0 : path.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if ( this == obj ) return true;
        if ( obj == null ) return false;
        if ( getClass() != obj.getClass() ) return false;
        MemoryPath other = (MemoryPath) obj;
        if ( path == null ) {
            if ( other.path != null ) return false;
        } else if ( !path.equals( other.path ) ) return false;
        return true;
    }

    @Override
    public String toString() {
        return "MemoryPath [path=" + path + "]";
    }
    
}
