/*
 * Copyright 2006 JBoss Inc
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

package org.drools.compiler;

import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.Token;
import org.antlr.runtime.TokenSource;

/**
 * This is a specialized version of CommonTokenStream that allows
 * the parser to switch tokens between channels on the fly.
 * 
 * @author <mailto:tirelli@post.com>Edson Tirelli</mailto>
 */
public class SwitchingCommonTokenStream extends CommonTokenStream {
    
    public SwitchingCommonTokenStream() {
        super();
    }

    public SwitchingCommonTokenStream(TokenSource tokenSource) {
        super(tokenSource);
    }

    public SwitchingCommonTokenStream(TokenSource tokenSource, int channel) {
        super(tokenSource, channel);
    }

    
    /** 
     * @inheritdoc
     */
    protected int skipOffTokenChannels(int i) {
        int n = tokens.size();
        while ( i<n ) {
            Token t = ((Token)tokens.get(i));
            // is there a channel override for token type?
            if ( channelOverrideMap!=null ) {
                Integer channelI = (Integer) channelOverrideMap.get(new Integer(t.getType()));
                if ( channelI!=null ) {
                    t.setChannel(channelI.intValue());
                }
            }
            if( t.getChannel() == channel ) {
                break;
            }
            i++;
        }
        return i;
        
    }

    /** 
     * @inheritdoc
     */
    protected int skipOffTokenChannelsReverse(int i) {
        while ( i>=0 ) {
            Token t = ((Token)tokens.get(i));
            // is there a channel override for token type?
            if ( channelOverrideMap!=null ) {
                Integer channelI = (Integer) channelOverrideMap.get(new Integer(t.getType()));
                if ( channelI!=null ) {
                    t.setChannel(channelI.intValue());
                }
            }
            if( t.getChannel() == channel ) {
                break;
            }
            i--;
        }
        return i;
    }

}
