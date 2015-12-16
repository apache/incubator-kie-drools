/*
 * Copyright 2011 Red Hat, Inc. and/or its affiliates.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.drools.workbench.models.commons.backend;

/**
 * Interface for utility classes handling upgrades to Models. This may be
 * required as the Model changes to support different (future) requirements.
 * 
 * @param <T>
 *            The return model type after upgrade
 * @param <V>
 *            The source model type
 */
public interface IUpgradeHelper<T, V> {

    T upgrade( V model );

}
