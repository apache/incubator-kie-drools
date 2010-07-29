package org.drools.persistence.session;

import java.io.Serializable;

/**
 *
 * @author salaboy
 */
public class MyVariableSerializable implements Serializable {

	private static final long serialVersionUID = 510l;
	
	private String text = "";

    public MyVariableSerializable(String string) {
        this.text = string;
    }

    /**
     * @return the text
     */
    public String getText() {
        return text;
    }

    /**
     * @param text the text to set
     */
    public void setText(String text) {
        this.text = text;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final MyVariableSerializable other = (MyVariableSerializable) obj;
        if ((this.text == null) ? (other.text != null) : !this.text.equals(other.text)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 37 * hash + (this.text != null ? this.text.hashCode() : 0);
        return hash;
    }

    public String toString(){
        return "Serializable Variable: "+this.getText();
    }

}
