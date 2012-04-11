/*
 * Copyright 2012 JBoss Inc
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
