package org.kie.maven.plugin;

import org.codehaus.classworlds.ClassRealm;
import org.codehaus.plexus.component.annotations.Component;
import org.codehaus.plexus.component.configurator.AbstractComponentConfigurator;
import org.codehaus.plexus.component.configurator.ComponentConfigurationException;
import org.codehaus.plexus.component.configurator.ComponentConfigurator;
import org.codehaus.plexus.component.configurator.ConfigurationListener;
import org.codehaus.plexus.component.configurator.converters.composite.ObjectWithFieldsConverter;
import org.codehaus.plexus.component.configurator.converters.special.ClassRealmConverter;
import org.codehaus.plexus.component.configurator.expression.ExpressionEvaluationException;
import org.codehaus.plexus.component.configurator.expression.ExpressionEvaluator;
import org.codehaus.plexus.configuration.PlexusConfiguration;
import org.codehaus.plexus.logging.Logger;
import org.codehaus.plexus.logging.console.ConsoleLogger;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * A custom ComponentConfigurator which adds the project's runtime classpath elements
 * to the
 */
@Component(role=ComponentConfigurator.class, hint="include-project-dependencies")
public class IncludeProjectDependenciesComponentConfigurator extends AbstractComponentConfigurator {

   private static final Logger LOGGER = new ConsoleLogger(Logger.LEVEL_DEBUG, "Configurator");

   public void configureComponent( Object component, PlexusConfiguration configuration,
                                   ExpressionEvaluator expressionEvaluator, ClassRealm containerRealm,
                                   ConfigurationListener listener )
         throws ComponentConfigurationException {

      addProjectDependenciesToClassRealm(expressionEvaluator, containerRealm);

      converterLookup.registerConverter( new ClassRealmConverter( containerRealm ) );

      ObjectWithFieldsConverter converter = new ObjectWithFieldsConverter();

      converter.processConfiguration( converterLookup, component, containerRealm.getClassLoader(), configuration,
            expressionEvaluator, listener );
   }

   private void addProjectDependenciesToClassRealm(ExpressionEvaluator expressionEvaluator, ClassRealm containerRealm) throws ComponentConfigurationException {
      List<String> runtimeClasspathElements;
      try {
         //noinspection unchecked
         runtimeClasspathElements = (List<String>) expressionEvaluator.evaluate("${project.runtimeClasspathElements}");
      } catch (ExpressionEvaluationException e) {
         throw new ComponentConfigurationException("There was a problem evaluating: ${project.runtimeClasspathElements}", e);
      }

      // Add the project dependencies to the ClassRealm
      final URL[] urls = buildURLs(runtimeClasspathElements);
      for (URL url : urls) {
         containerRealm.addConstituent(url);
      }
   }

   private URL[] buildURLs(List<String> runtimeClasspathElements) throws ComponentConfigurationException {
      // Add the projects classes and dependencies
      List<URL> urls = new ArrayList<URL>(runtimeClasspathElements.size());
      for (String element : runtimeClasspathElements) {
         try {
            final URL url = new File(element).toURI().toURL();
            urls.add(url);
            if (LOGGER.isDebugEnabled()) {
               LOGGER.debug("Added to project class loader: " + url);
            }
         } catch (MalformedURLException e) {
            throw new ComponentConfigurationException("Unable to access project dependency: " + element, e);
         }
      }

      // Add the plugin's dependencies (so Trove stuff works if Trove isn't on
      return urls.toArray(new URL[urls.size()]);
   }

}
