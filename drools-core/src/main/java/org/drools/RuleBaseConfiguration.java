/*
 * Copyright 2005 JBoss Inc
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

package org.drools;

import java.io.Serializable;

/**
 * RuleBaseConfiguration
 * 
 * A class to store RuleBase related configuration. It must be used at rule base instantiation time
 * or not used at all.
 * This class will automatically load default values from system properties, so if you want to set
 * a default configuration value for all your new rule bases, you can simply set the property as 
 * a System property.
 * 
 * After RuleBase is created, it makes the configuration immutable and there is no way to make it 
 * mutable again. This is to avoid inconsistent behavior inside rulebase.
 * 
 * NOTE: This API is under review and may change in the future.
 *
 * Created: 16/05/2006
 * @author <a href="mailto:tirelli@post.com">Edson Tirelli</a> 
 *
 * @version $Id$
 */

/**
 * drools.removeIdentities = <true|false>
 * drools.shareAlphaNodes  = <true|false>
 * drools.shareBetaNodes = <true|false>
 * drools.alphaMemory <true/false>
 * drools.alphaNodeHashingThreshold = <1...n>
 * drools.compositeKeyDepth  =<1..3>
 * drools.indexLeftBetaMemory = <true/false>
 * drools.indexRightBetaMemory = <true/false>
 * drools.assertBehaviour = <IDENTITY|EQUALITY>
 * drools.logicalOverride = <DISCARD|PRESERVE>
 */
public class RuleBaseConfiguration
    implements
    Serializable {
    private static final long serialVersionUID = 320L;

    private boolean           immutable;

    private boolean           removeIdentities;
    private boolean           shareAlphaNodes;
    private boolean           shareBetaNodes;
    private boolean           alphaMemory;
    private int               alphaNodeHashingThreshold;
    private int               compositeKeyDepth;
    private boolean           indexLeftBetaMemory;
    private boolean           indexRightBetaMemory;
    private AssertBehaviour   assertBehaviour;
    private LogicalOverride   logicalOverride;

    public RuleBaseConfiguration() {
        this.immutable = false;

        setRemoveIdentities( Boolean.valueOf( System.getProperty( "drools.removeIdentities",
                                                                  "false" ) ).booleanValue() );
        
        setAlphaMemory( Boolean.valueOf( System.getProperty( "drools.alphaMemory",
                                                             "false" ) ).booleanValue() );
        
        setShareAlphaNodes( Boolean.valueOf( System.getProperty( "drools.shareAlphaNodes",
                                                                 "true" ) ).booleanValue() );

        setShareBetaNodes( Boolean.valueOf( System.getProperty( "drools.shareBetaNodes",
                                                                "true" ) ).booleanValue() );

        setAlphaNodeHashingThreshold( Integer.parseInt( System.getProperty( "drools.alphaNodeHashingThreshold",
                                                                            "3" ) ) );

        setCompositeKeyDepth( Integer.parseInt( System.getProperty( "drools.compositeKeyDepth",
                                                                    "3" ) ) );

        setIndexLeftBetaMemory( Boolean.valueOf( System.getProperty( "drools.indexLeftBetaMemory",
                                                                     "true" ) ).booleanValue() );
        setIndexRightBetaMemory( Boolean.valueOf( System.getProperty( "drools.indexRightBetaMemory",
                                                                      "true" ) ).booleanValue() );

        setAssertBehaviour( AssertBehaviour.determineAssertBehaviour( System.getProperty( "drools.iassertBehaviour",
                                                                                          "IDENTITY" ) ) );
        setLogicalOverride( LogicalOverride.determineLogicalOverride( System.getProperty( "drools.logicalOverride",
                                                                                          "DISCARD" ) ) );
    }

    /**
     * Makes the configuration object immutable. Once it becomes immutable, 
     * there is no way to make it mutable again. 
     * This is done to keep consistency.
     */
    public void makeImmutable() {
        this.immutable = true;
    }

    /**
     * Returns true if this configuration object is immutable or false otherwise.
     * @return
     */
    public boolean isImmutable() {
        return this.immutable;
    }
        
    
    
    public boolean isRemoveIdentities() {
        return removeIdentities;
    }

    public void setRemoveIdentities(boolean removeIdentities) {
        if ( !this.immutable ) {
            this.removeIdentities = removeIdentities;
        } else {
            throw new UnsupportedOperationException( "Can't set a property after configuration becomes immutable" );
        }
    }

    public boolean isAlphaMemory() {
        return alphaMemory;
    }

    public void setAlphaMemory(boolean alphaMemory) {
        if ( !this.immutable ) {
            this.alphaMemory = alphaMemory;
        } else {
            throw new UnsupportedOperationException( "Can't set a property after configuration becomes immutable" );
        }
    }

    public boolean isShareAlphaNodes() {
        return shareAlphaNodes;
    }

    public void setShareAlphaNodes(boolean shareAlphaNodes) {
        if ( !this.immutable ) {
            this.shareAlphaNodes = shareAlphaNodes;
        } else {
            throw new UnsupportedOperationException( "Can't set a property after configuration becomes immutable" );
        }
    }

    public boolean isShareBetaNodes() {
        return shareBetaNodes;
    }

    public void setShareBetaNodes(boolean shareBetaNodes) {
        if ( !this.immutable ) {
            this.shareBetaNodes = shareBetaNodes;
        } else {
            throw new UnsupportedOperationException( "Can't set a property after configuration becomes immutable" );
        }
    }

    public int getAlphaNodeHashingThreshold() {
        return alphaNodeHashingThreshold;
    }

    public void setAlphaNodeHashingThreshold(int alphaNodeHashingThreshold) {
        if ( !this.immutable ) {
            this.alphaNodeHashingThreshold = alphaNodeHashingThreshold;
        } else {
            throw new UnsupportedOperationException( "Can't set a property after configuration becomes immutable" );
        }
    }

    public AssertBehaviour getAssertBehaviour() {
        return assertBehaviour;
    }

    public void setAssertBehaviour(AssertBehaviour assertBehaviour) {
        if ( !this.immutable ) {
            this.assertBehaviour = assertBehaviour;
        } else {
            throw new UnsupportedOperationException( "Can't set a property after configuration becomes immutable" );
        }
    }

    public int getCompositeKeyDepth() {
        return compositeKeyDepth;
    }

    public void setCompositeKeyDepth(int compositeKeyDepth) {
        if ( !this.immutable ) {
            if ( compositeKeyDepth > 3 ) {
                throw new UnsupportedOperationException( "compositeKeyDepth cannot be greater than 3" );
            }
            this.compositeKeyDepth = compositeKeyDepth;
        } else {
            throw new UnsupportedOperationException( "Can't set a property after configuration becomes immutable" );
        }
    }

    public boolean isIndexLeftBetaMemory() {
        return indexLeftBetaMemory;
    }

    public void setIndexLeftBetaMemory(boolean indexLeftBetaMemory) {
        if ( !this.immutable ) {
            this.indexLeftBetaMemory = indexLeftBetaMemory;
        } else {
            throw new UnsupportedOperationException( "Can't set a property after configuration becomes immutable" );
        }
    }

    public boolean isIndexRightBetaMemory() {
        return indexRightBetaMemory;
    }

    public void setIndexRightBetaMemory(boolean indexRightBetaMemory) {
        if ( !this.immutable ) {
            this.indexRightBetaMemory = indexRightBetaMemory;
        } else {
            throw new UnsupportedOperationException( "Can't set a property after configuration becomes immutable" );
        }
    }

    public LogicalOverride getLogicalOverride() {
        return logicalOverride;
    }

    public void setLogicalOverride(LogicalOverride logicalOverride) {
        if ( !this.immutable ) {
            this.logicalOverride = logicalOverride;
        } else {
            throw new UnsupportedOperationException( "Can't set a property after configuration becomes immutable" );
        }
    }

    public static class AssertBehaviour
        implements
        Serializable {
        private static final long           serialVersionUID = 320L;

        public static final AssertBehaviour IDENTITY         = new AssertBehaviour( 0 );
        public static final AssertBehaviour EQUALITY         = new AssertBehaviour( 1 );

        private int                         value;

        private AssertBehaviour(int value) {
            this.value = value;
        }

        public static AssertBehaviour determineAssertBehaviour(String value) {
            if ( value.equals( "IDENTITY" ) ) {
                return IDENTITY;
            } else if ( value.equals( "EQUALITY" ) ) {
                return EQUALITY;
            } else {
                throw new IllegalArgumentException( "Illegal enum value '" + value + "' for AssertBehaviour" );
            }
        }

        private Object readResolve() throws java.io.ObjectStreamException {
            switch ( this.value ) {
                case 0 :
                    return IDENTITY;
                case 1 :
                    return EQUALITY;
                default :
                    throw new IllegalArgumentException( "Illegal enum value '" + this.value + "' for AssertBehaviour" );
            }
        }
    }

    public static class LogicalOverride
        implements
        Serializable {
        private static final long           serialVersionUID = 320L;

        public static final LogicalOverride PRESERVE         = new LogicalOverride( 0 );
        public static final LogicalOverride DISCARD          = new LogicalOverride( 1 );

        private int                         value;

        private LogicalOverride(int value) {
            this.value = value;
        }

        public static LogicalOverride determineLogicalOverride(String value) {
            if ( value.equals( "PRESERVE" ) ) {
                return PRESERVE;
            } else if ( value.equals( "DISCARD" ) ) {
                return DISCARD;
            } else {
                throw new IllegalArgumentException( "Illegal enum value '" + value + "' for LogicalOverride" );
            }
        }

        private Object readResolve() throws java.io.ObjectStreamException {
            switch ( this.value ) {
                case 0 :
                    return PRESERVE;
                case 1 :
                    return DISCARD;
                default :
                    throw new IllegalArgumentException( "Illegal enum value '" + this.value + "' for LogicalOverride" );
            }
        }
    }

}
