package org.drools.lang;

import java.util.Collections;
import java.util.LinkedList;

/**
 * Class that represents a DroolsLanguage sentence. To be used by IDE.
 * 
 * @author porcelli
 */
@SuppressWarnings("unchecked")
public class DroolsSentence {

    /**
     * sentence type
     */
    private DroolsSentenceType type = DroolsSentenceType.RULE;
    /**
     * linked list that stores DroolsTokens and Locations
     */
    private LinkedList content = new LinkedList();
    /**
     * start char offset
     */
    private int startOffset = -1;
    /**
     * end char offset
     */
    private int endOffset = -1;

    /**
     * getter of sentence type
     *
     * @return sentence type
     * @see DroolsSentenceType
     */
    public DroolsSentenceType getType() {
        return type;
    }

    /**
     * setter of sentence type
     *
     * @param type
     *            sentence type
     * @see DroolsSentenceType
     */
    public void setType(DroolsSentenceType type) {
        this.type = type;
    }

    /**
     * getter for start char offset
     *
     * @return start char offset
     */
    public int getStartOffset() {
        return startOffset;
    }

    /**
     * setter for start char offset
     *
     * @param startOffset
     *            start char offset
     */
    public void setStartOffset(int startOffset) {
        this.startOffset = startOffset;
    }

    /**
     * getter for end char offset
     *
     * @return end char offset
     */
    public int getEndOffset() {
        return endOffset;
    }

    /**
     * setter for end char offset
     *
     * @param endOffset
     *            end char offset
     */
    public void setEndOffset(int endOffset) {
        this.endOffset = endOffset;
    }

    /**
     * getter of sentence content
     *
     * @return linked list that stores DroolsTokens and Locations
     */
    public LinkedList getContent() {
        return content;
    }

    /**
     * Reverses the content linked list
     */
    public void reverseContent() {
        Collections.reverse(content);
    }

    /**
     * Add a token to the content and sets char offset info
     *
     * @param token
     *            token to be stored
     */
    public void addContent(DroolsToken token) {
        if (startOffset == -1) {
        	startOffset = token.getStartIndex();
        }
        endOffset = token.getStopIndex();
        this.content.add(token);
    }

    /**
     * Add a location to the content
     *
     * @param contextInfo
     *            location identifier
     * @see Location
     */
    public void addContent(int contextInfo) {
        this.content.add(contextInfo);
    }
}
