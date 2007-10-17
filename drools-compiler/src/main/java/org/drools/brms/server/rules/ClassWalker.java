package org.drools.brms.server.rules;
/*
 * Copyright 2007 Mark Derricutt
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

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;


/**
 * This is a utility to walk a tree of classes (recursively) and return a set
 * of classes that match a package name.
 *
 * This is not currently used, as I can't work out a way to make it work including the dynamically added stuff into the MapBackedClassloader
 * (ie classes added in memory only). It requires some more surgery to cope with this. Was worth a try though !
 * (can be wired into the loadClass method of the SuggestionCompletionLoader).
 *
 * @author Mark Derricutt
 * @author Michael Neale
 */
public class ClassWalker {


    public static Set<Class> findClassesInPackage(String packageName, ClassLoader loader) {

        Set<Class> acceptedClasses = new HashSet<Class>();

        try {
            String packageOnly = packageName;
            boolean recursive = false;
            if (packageName.endsWith(".*")) {
                packageOnly = packageName.substring(0, packageName.lastIndexOf(".*"));
                recursive = true;
            }

            String packageDirName = packageOnly.replace('.', '/');
            Enumeration<URL> dirs = loader.getResources(packageDirName);

            while (dirs.hasMoreElements()) {
                URL url = dirs.nextElement();
                if ("file".equals(url.getProtocol())) {
                    findClassesInDirPackage(packageOnly,
                            URLDecoder.decode(url.getFile(), "UTF-8"),
                            recursive, acceptedClasses, loader
                    );
                } else if ("jar".equals(url.getProtocol())) {
                    JarFile jar = ((JarURLConnection) url.openConnection()).getJarFile();

                    Enumeration<JarEntry> entries = jar.entries();
                    while (entries.hasMoreElements()) {
                        JarEntry entry = entries.nextElement();
                        String name = entry.getName();

                        if (!name.endsWith("/")) {
                            String className = name.replaceAll("/", ".").replaceAll("\\.class", "");
                            checkValidClass(className, acceptedClasses, loader);
                        }
                    }
                }


            }
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        return acceptedClasses;


    }

    private static void checkValidClass(String className, Set<Class> acceptedClasses, ClassLoader loader) {
        try {
            Class classClass = loader.loadClass(className);


                acceptedClasses.add(classClass);


        } catch (ClassNotFoundException e) {
            // e.printStackTrace();
        } catch (NoClassDefFoundError e) {
            //
        }
    }

    private static void findClassesInDirPackage(String packageName,
                                                String packagePath,
                                                final boolean recursive,
                                                Set<Class> classes, ClassLoader loader) {
        File dir = new File(packagePath);

        if (!dir.exists() || !dir.isDirectory()) {
            return;
        }

        File[] dirfiles = dir.listFiles(new FileFilter() {
            public boolean accept(File file) {
                return (recursive && file.isDirectory()) || (file.getName().endsWith(".class"));
            }
        });

        for (File file : dirfiles) {
            if (file.isDirectory()) {
                findClassesInDirPackage(packageName + "." + file.getName(),
                        file.getAbsolutePath(),
                        recursive,
                        classes, loader);
            } else {
                String className = file.getName().substring(0, file.getName().length() - 6);
                checkValidClass(packageName + "." + className, classes, loader);
            }
        }
    }


}

