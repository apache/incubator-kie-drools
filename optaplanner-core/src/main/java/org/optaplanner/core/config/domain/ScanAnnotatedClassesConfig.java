/*
 * Copyright 2015 JBoss Inc
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

package org.optaplanner.core.config.domain;

import java.util.ArrayList;
import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;
import org.optaplanner.core.api.domain.solution.Solution;
import org.optaplanner.core.config.util.ConfigUtils;
import org.optaplanner.core.impl.domain.solution.descriptor.SolutionDescriptor;

@XStreamAlias("scanAnnotatedClasses")
public class ScanAnnotatedClassesConfig {

    @XStreamImplicit(itemFieldName = "packageInclude")
    private List<String> packageIncludeList = null;

    public List<String> getPackageIncludeList() {
        return packageIncludeList;
    }

    public void setPackageIncludeList(List<String> packageIncludeList) {
        this.packageIncludeList = packageIncludeList;
    }

    // ************************************************************************
    // Builder methods
    // ************************************************************************

    public SolutionDescriptor buildSolutionDescriptor() {
        Class<? extends Solution> solutionClass = null;
        List<Class<?>> entityClassList = null;
        // TODO


        return SolutionDescriptor.buildSolutionDescriptor(solutionClass, entityClassList);
    }

    public void inherit(ScanAnnotatedClassesConfig inheritedConfig) {
        packageIncludeList = ConfigUtils.inheritMergeableListProperty(
                packageIncludeList, inheritedConfig.getPackageIncludeList());
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + (packageIncludeList == null ? "" : packageIncludeList) + ")";
    }

}
