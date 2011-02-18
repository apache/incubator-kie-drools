/**
 * Copyright 2010 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.builder.conf;

import java.io.File;

/**
 * A class for the dump directory configuration. I.e., for the directory
 * where Drools will dump generated code for debugging purposes. If this
 * option is not set, drools will generate and compile all code in memory.
 * 
 * @author etirelli
 */
public class DumpDirOption implements SingleValueKnowledgeBuilderOption {

    private static final long serialVersionUID = 510l;

    /**
     * The property name for the drools dump directory configuration
     */
    public static final String PROPERTY_NAME = "drools.dump.dir";
    
    /**
     * directory reference
     */
    private final File dir;
    
    /**
     * Private constructor to enforce the use of the factory method
     * @param dir directory to set
     */
    private DumpDirOption( File dir ) {
        this.dir = dir;
    }
    
    /**
     * This is a factory method for this DumpDirectoryOption configuration.
     * The factory method is a best practice for the case where the 
     * actual object construction is changed in the future.
     * 
     * @param dir the directory to which drools will dump files
     * 
     * @return the actual type safe dump directory configuration.
     */
    public static DumpDirOption get( File dir ) {
        return new DumpDirOption( dir );
    }
    
    /**
     * {@inheritDoc}
     */
    public String getPropertyName() {
        return PROPERTY_NAME;
    }
    
    /**
     * Returns the directory to which drools should dump generated files
     * 
     * @return
     */
    public File getDirectory() {
        return dir;
    }

    @Override
    public String toString() {
        return "DumpDirOption( directory="+((dir == null) ? "" : dir.toString())+" )";
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((dir == null) ? 0 : dir.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if ( this == obj ) return true;
        if ( obj == null ) return false;
        if ( getClass() != obj.getClass() ) return false;
        DumpDirOption other = (DumpDirOption) obj;
        if ( dir == null ) {
            if ( other.dir != null ) return false;
        } else if ( !dir.equals( other.dir ) ) return false;
        return true;
    }

}
