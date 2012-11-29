package org.kie.builder.impl;

import org.kie.builder.KieJar;

import java.io.File;

public interface InternalKieJar extends KieJar {

    File asFile();
}
