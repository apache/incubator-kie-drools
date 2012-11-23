package org.drools.scanner;

import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class XmlPomParser implements PomParser {
    public List<DependencyDescriptor> getPomDirectDependencies() {
        File pomfile = new File("pom.xml");
        Model model = null;
        try {
            FileReader reader = new FileReader(pomfile);
            model = new MavenXpp3Reader().read(reader);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (XmlPullParserException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        model.setPomFile(pomfile);

        List<DependencyDescriptor> deps = new ArrayList<DependencyDescriptor>();
        MavenProject project = new MavenProject(model);
        for (Dependency dep : project.getDependencies()) {
            DependencyDescriptor depDescr = new DependencyDescriptor(dep, project.getProperties());
            if (depDescr.isValid()) {
                deps.add(depDescr);
            }
        }
        return deps;
    }
}
