package org.drools.builder.impl;

import org.drools.KBaseUnit;
import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseConfiguration;
import org.drools.KnowledgeBaseFactory;
import org.drools.builder.CompositeKnowledgeBuilder;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderConfiguration;
import org.drools.builder.KnowledgeBuilderErrors;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.compiler.PackageBuilderConfiguration;
import org.drools.io.ResourceFactory;
import org.drools.kproject.KBase;
import org.drools.kproject.KSession;
import org.drools.runtime.KnowledgeSessionConfiguration;
import org.drools.runtime.StatefulKnowledgeSession;

import java.io.File;
import java.io.InputStream;

import static org.drools.builder.impl.KnowledgeContainerImpl.KBASES_FOLDER;

public class KBaseUnitImpl implements KBaseUnit {

    private final KnowledgeBuilderConfiguration kConf;

    private final KBase kBase;
    private final File sourceFolder;

    private KnowledgeBuilder kbuilder;
    private KnowledgeBase knowledgeBase;

    public KBaseUnitImpl(KnowledgeBuilderConfiguration kConf, KBase kBase) {
        this(kConf, kBase, null);
    }

    KBaseUnitImpl(KnowledgeBuilderConfiguration kConf, KBase kBase, File sourceFolder) {
        this.kConf = kConf;
        this.kBase = kBase;
        this.sourceFolder = sourceFolder;
    }

    public KnowledgeBase getKnowledgeBase() {
        if (knowledgeBase != null) {
            return knowledgeBase;
        }
        KnowledgeBuilder kbuilder = getKBuilder();
        if (kbuilder.hasErrors()) {
            return null;
        }

        knowledgeBase = KnowledgeBaseFactory.newKnowledgeBase( getKnowledgeBaseConfiguration() );
        knowledgeBase.addKnowledgePackages( kbuilder.getKnowledgePackages() );
        return knowledgeBase;
    }

    public String getKBaseName() {
        return kBase.getQName();
    }

    public boolean hasErrors() {
        return getKBuilder().hasErrors();
    }

    public KnowledgeBuilderErrors getErrors() {
        return getKBuilder().getErrors();
    }

    public StatefulKnowledgeSession newStatefulKnowledegSession(String ksessionName) {
        return getKnowledgeBase().newStatefulKnowledgeSession(getKnowledgeSessionConfiguration(ksessionName), null);
    }

    private KnowledgeBuilder getKBuilder() {
        if (kbuilder != null) {
            return kbuilder;
        }

        kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder(kConf);
        CompositeKnowledgeBuilder ckbuilder = kbuilder.batch();

        for (String kBaseFile : kBase.getFiles()) {
            buildKBaseFile(ckbuilder, kBase, kBaseFile);
        }

        ckbuilder.build();
        return kbuilder;
    }

    private void buildKBaseFile(CompositeKnowledgeBuilder ckbuilder, KBase kBase, String kBaseFile) {
        if (sourceFolder != null) {
            File file = new File(sourceFolder, kBase.getQName() + "/" + kBaseFile);
            ckbuilder.add(ResourceFactory.newFileResource(file), ResourceType.determineResourceType(file.getName()));
        } else {
            String file = KBASES_FOLDER + "/" + kBase.getQName() + "/" + kBaseFile;
            InputStream ruleStream = ((PackageBuilderConfiguration)kConf).getClassLoader().getResourceAsStream(file);
            ckbuilder.add(ResourceFactory.newInputStreamResource(ruleStream), ResourceType.determineResourceType(file));
        }
    }

    private KSession getKSession(String ksessionName) {
        KSession kSession = kBase.getKSessions().get(ksessionName);
        if (kSession == null) {
            throw new RuntimeException("Unknown Knowledge Session: " + ksessionName + " in Knowledge Base: " + getKBaseName());
        }
        return kSession;
    }

    private KnowledgeBaseConfiguration getKnowledgeBaseConfiguration() {
        KnowledgeBaseConfiguration kbConf = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
        kbConf.setOption(kBase.getEqualsBehavior());
        kbConf.setOption(kBase.getEventProcessingMode());
        return kbConf;
    }

    private KnowledgeSessionConfiguration getKnowledgeSessionConfiguration(String ksessionName) {
        KSession kSession = getKSession(ksessionName);
        KnowledgeSessionConfiguration ksConf = KnowledgeBaseFactory.newKnowledgeSessionConfiguration();
        ksConf.setOption(kSession.getClockType());
        return ksConf;
    }
}