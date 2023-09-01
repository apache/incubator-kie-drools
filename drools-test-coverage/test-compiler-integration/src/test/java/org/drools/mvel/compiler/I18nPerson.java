package org.drools.mvel.compiler;

import java.io.Serializable;

/**
 * Class with non-ASCII characters.
 */
public class I18nPerson implements Serializable {
    
    private String garçon; // "boy" in French
    private String élève; // "student" in French (creates a weird getter/setter name)
    private String имя; // "name" in Russian
    private String 名称; // "name" in Chinese

    public String getGarçon() {
        return garçon;
    }

    public void setGarçon(String garçon) {
        this.garçon = garçon;
    }

    public String getÉlève() {
        return élève;
    }

    public void setÉlève(String élève) {
        this.élève = élève;
    }

    public String getИмя() {
        return имя;
    }

    public void setИмя(String имя) {
        this.имя = имя;
    }

    public String get名称() {
        return 名称;
    }

    public void set名称(String 名称) {
        this.名称 = 名称;
    }

}
