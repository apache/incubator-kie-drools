/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jbpm.services.task.impl.model;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Lob;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.kie.internal.task.api.model.InternalI18NText;

@Entity
@Table(name="I18NText",
       indexes = {@Index(name = "IDX_I18NText_SubjId",  columnList="Task_Subjects_Id"),
                  @Index(name = "IDX_I18NText_NameId",  columnList="Task_Names_Id"),
                  @Index(name = "IDX_I18NText_DescrId",  columnList="Task_Descriptions_Id"),
                  @Index(name = "IDX_I18NText_ReassignId",  columnList="Reassignment_Documentation_Id"),
                  @Index(name = "IDX_I18NText_NotSubjId",  columnList="Notification_Subjects_Id"),
                  @Index(name = "IDX_I18NText_NotNamId",  columnList="Notification_Names_Id"),
                  @Index(name = "IDX_I18NText_NotDocId",  columnList="Notification_Documentation_Id"),
                  @Index(name = "IDX_I18NText_NotDescrId",  columnList="Notification_Descriptions_Id"),
                  @Index(name = "IDX_I18NText_DeadDocId", columnList="Deadline_Documentation_Id")})
@SequenceGenerator(name="i18nTextIdSeq", sequenceName="I18NTEXT_ID_SEQ", allocationSize=1)
public class I18NTextImpl implements InternalI18NText {
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator="i18nTextIdSeq")
    @Column(name = "id")
    private Long   id = null;

    private String language;

    private String shortText;

    @Lob @Column(length=65535)
    private String text;

    public I18NTextImpl() {

    }
    
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeLong( id );
        if( language == null ) { 
            language = "";
        }
        out.writeUTF( language );
        
        if( shortText == null ) {
            shortText = "";
        }
        out.writeUTF( shortText );
        
        if( text == null ) { 
            text = "";
        }
        out.writeUTF( text );        
    }
    
    public void readExternal(ObjectInput in) throws IOException,
                                            ClassNotFoundException {
        id = in.readLong();
        language = in.readUTF();
        shortText = in.readUTF();
        text = in.readUTF();        
    }

    public I18NTextImpl(String language,
                    String text) {
        this.language = language;
        if (text != null && text.length() > 256) {
            this.shortText = text.substring(0, 255);
        } else {
            this.shortText = text;
        }
        this.text = text;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        if (text != null && text.length() > 256) {
            this.shortText = text.substring(0, 255);
        } else {
            this.shortText = text;
        }
        this.text = text;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((language == null) ? 0 : language.hashCode());
        result = prime * result + ((shortText == null) ? 0 : shortText.hashCode());
        result = prime * result + ((text == null) ? 0 : text.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if ( this == obj ) return true;
        if ( obj == null ) return false;
        if ( !(obj instanceof I18NTextImpl) ) return false;
        I18NTextImpl other = (I18NTextImpl) obj;
        if ( language == null ) {
            if ( other.language != null ) return false;
        } else if ( !language.equals( other.language ) ) return false;
        if ( shortText == null ) {
            if ( other.shortText != null ) return false;
        } else if ( !shortText.equals( other.shortText ) ) return false;
        if ( text == null ) {
            if ( other.text != null ) return false;
        } else if ( !text.equals( other.text ) ) return false;
        return true;
    }
    
    public static String getLocalText(List<I18NTextImpl> list, String prefferedLanguage, String defaultLanguage) {
        for ( I18NTextImpl text : list) {
            if ( text.getLanguage().equals( prefferedLanguage )) {
                return text.getText();
            }
        }
        if (  defaultLanguage == null ) {
            for ( I18NTextImpl text : list) {
                if ( text.getLanguage().equals( defaultLanguage )) {
                    return text.getText();
                }
            }    
        }
        return "";
    }


    
}
