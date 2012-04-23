package org.drools.games.pong;

import org.drools.definition.type.Position;

public class KeyPressed {
    
    @Position(0)
    private String keyText;

    public KeyPressed(String keyText) {
        this.keyText = keyText;
    }
    
    public String getKeyText() {
        return keyText;
    }

    public void setKeyText(String keyText) {
        this.keyText = keyText;
    }

    @Override
    public String toString() {
        return "KeyPressed [keyText=" + keyText + "]";
    }        
    
    
}
