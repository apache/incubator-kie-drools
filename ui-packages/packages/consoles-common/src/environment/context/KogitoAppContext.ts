/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
import React, { useContext } from 'react';
import { User, UserContext } from '../auth';

export interface AppContext {
  getCurrentUser(): User;
  readonly userContext: UserContext;
}

export class AppContextImpl implements AppContext {
  public readonly userContext: UserContext;

  constructor(userSystem: UserContext) {
    this.userContext = userSystem;
  }

  getCurrentUser(): User {
    return this.userContext.getCurrentUser();
  }
}

const KogitoAppContext = React.createContext<AppContext>(null);

export default KogitoAppContext;

export const useKogitoAppContext = () =>
  useContext<AppContext>(KogitoAppContext);
