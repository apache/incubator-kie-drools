/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.drools.games.pong;

public class PongGame {
    private Player playerOne;
    private Player Playertwo;
    
    public PongGame(Player playerOne,
                    Player playertwo) {        
        this.playerOne = playerOne;
        Playertwo = playertwo;
    }

    public Player getPlayerOne() {
        return playerOne;
    }

    public void setPlayerOne(Player playerOne) {
        this.playerOne = playerOne;
    }

    public Player getPlayertwo() {
        return Playertwo;
    }

    public void setPlayertwo(Player playertwo) {
        Playertwo = playertwo;
    }  
}
