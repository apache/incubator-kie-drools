package org.kie.internal.runtime.manager.deploy;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.kie.internal.runtime.conf.DeploymentDescriptor;
import org.kie.internal.runtime.conf.MergeMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DeploymentDescriptorManager {

    private static final Logger logger = LoggerFactory.getLogger(DeploymentDescriptorManager.class);
    private static Set<String> locations = Collections.synchronizedSet(new LinkedHashSet<>());

    private String defaultPU;

    public static void addDescriptorLocation(String location) {
        locations.add(location);
    }

    public DeploymentDescriptorManager() {
        this("org.jbpm.persistence.jpa");
    }

    public DeploymentDescriptorManager(String defaultPU) {
        this.defaultPU = defaultPU;
    }

    public DeploymentDescriptor getDefaultDescriptor() {
        List<DeploymentDescriptor> descriptors = new ArrayList<>();
        String defaultDescriptorLocation = System.getProperty("org.kie.deployment.desc.location");
        DeploymentDescriptor defaultDesc = loadDescriptor(defaultDescriptorLocation);
        descriptors.add(defaultDesc != null ? defaultDesc : new DeploymentDescriptorImpl(defaultPU));
        locations.forEach(url -> addDescriptor(descriptors, url));
        return DeploymentDescriptorMerger.merge(descriptors, MergeMode.MERGE_COLLECTIONS);
    }

    private void addDescriptor(List<DeploymentDescriptor> descriptors, String url) {
        DeploymentDescriptor desc = loadDescriptor(url);
        if (desc != null) {
            descriptors.add(desc);
        }
    }

    private DeploymentDescriptor loadDescriptor(String location) {
        try {
            logger.debug("Reading default descriptor from {}", location);
            if (location != null) {
                URL locationURL = getLocationURL(location);
                if (locationURL != null) {
                    return DeploymentDescriptorIO.fromXml(locationURL.openStream());
                }
            }
            return null;
        } catch (IOException e) {
            throw new IllegalArgumentException("Unable to read default deployment descriptor from " + location, e);
        }
    }

    private URL getLocationURL(String location) {
        URL locationUrl;
        if (location.startsWith("classpath:")) {
            String stripedLocation = location.replaceFirst("classpath:", "");
            locationUrl = this.getClass().getResource(stripedLocation);
            if (locationUrl == null) {
                locationUrl = Thread.currentThread().getContextClassLoader().getResource(stripedLocation);
            }
        } else {
            try {
                locationUrl = new URL(location);
            } catch (MalformedURLException e) {
                locationUrl = this.getClass().getResource(location);
                if (locationUrl == null) {
                    locationUrl = Thread.currentThread().getContextClassLoader().getResource(location);
                }
            }
        }
        return locationUrl;
    }
}
