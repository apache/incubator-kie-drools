package org.jbpm.document.service.impl;

import org.jbpm.document.Document;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class DocumentImpl implements Document {

    private String identifier = "";
    private String name;
    private String link = "";
    private long size;
    private Date lastModified;
    private byte[] content;
    private Map<String, String> attributes;

    public DocumentImpl() {
    }

    public DocumentImpl(String identifier, String name, long size, Date lastModified) {
        this.identifier = identifier;
        this.name = name;
        this.size = size;
        this.lastModified = lastModified;
        attributes = new HashMap<String, String>();
    }

    public DocumentImpl(String name, long size, Date lastModified) {
        this.name = name;
        this.size = size;
        this.lastModified = lastModified;
        attributes = new HashMap<String, String>();
    }

    public DocumentImpl( String identifier, String name, long size, Date lastModified, String link ) {
        this.identifier = identifier;
        this.name = name;
        this.size = size;
        this.lastModified = lastModified;
        this.link = link;
        attributes = new HashMap<String, String>();
    }

    @Override
    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    @Override
    public String getIdentifier() {
        return identifier;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setLink(String link) {
        this.link = link;
    }

    @Override
    public String getLink() {
        return this.link;
    }

    @Override
    public void setSize(long size) {
        this.size = size;
    }

    @Override
    public long getSize() {
        return size;
    }

    @Override
    public void setLastModified(Date lastModified) {
        this.lastModified = lastModified;
    }

    @Override
    public Date getLastModified() {
        return lastModified;
    }

    @Override
    public String getAttribute(String attributeName) {
        return attributes.get(attributeName);
    }

    @Override
    public void addAttribute(String attributeName, String attributeValue) {
        attributes.put(attributeName, attributeValue);
    }

    @Override
    public void setAttributes(Map<String, String> attributes) {
        this.attributes = attributes;
    }

    @Override
    public Map<String, String> getAttributes() {
        return attributes;
    }

    @Override
    public void setContent(byte[] content) {
        this.content = content;
    }

    @Override
    public byte[] getContent() {
        return content;
    }

    @Override
    public String toString() {
        SimpleDateFormat sdf = new SimpleDateFormat( DOCUMENT_DATE_PATTERN );
        return  name + PROPERTIES_SEPARATOR + size + PROPERTIES_SEPARATOR + sdf.format( lastModified ) + PROPERTIES_SEPARATOR + link ;
    }
}