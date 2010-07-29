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


/**
 * A class for the default package name configuration.
 * 
 * @author etirelli
 */
public class DefaultPackageNameOption implements SingleValueKnowledgeBuilderOption {

    private static final long serialVersionUID = 510l;

    /**
     * The property name for the default package name
     */
    public static final String PROPERTY_NAME = "drools.defaultPackageName";
    
    /**
     * package name
     */
    private final String packageName;
    
    /**
     * Private constructor to enforce the use of the factory method
     * @param packageName
     */
    private DefaultPackageNameOption( String packageName ) {
        this.packageName = packageName;
    }
    
    /**
     * This is a factory method for this DefaultPackageName configuration.
     * The factory method is a best practice for the case where the 
     * actual object construction is changed in the future.
     * 
     * @param packageName the name of the package to be configured as default
     * 
     * @return the actual type safe default package name configuration.
     */
    public static DefaultPackageNameOption get( String packageName ) {
        return new DefaultPackageNameOption( packageName );
    }
    
    /**
     * {@inheritDoc}
     */
    public String getPropertyName() {
        return PROPERTY_NAME;
    }
    
    /**
     * Returns the name of the package configured as default
     * 
     * @return
     */
    public String getPackageName() {
        return packageName;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((packageName == null) ? 0 : packageName.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if ( this == obj ) return true;
        if ( obj == null ) return false;
        if ( getClass() != obj.getClass() ) return false;
        DefaultPackageNameOption other = (DefaultPackageNameOption) obj;
        if ( packageName == null ) {
            if ( other.packageName != null ) return false;
        } else if ( !packageName.equals( other.packageName ) ) return false;
        return true;
    }
    
    @Override
    public String toString() {
        return "DefaultPackageNameOption( name="+packageName+" )";
    }
}
