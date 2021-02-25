/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import React, { useContext } from 'react';
import { GraphQL } from '@kogito-apps/consoles-common';

export interface ITaskConsoleContext<T> {
  setActiveItem(item: T);
  getActiveItem(): T;
}

export class TaskConsoleContextImpl<T> implements ITaskConsoleContext<T> {
  private item: T;

  getActiveItem(): T {
    return this.item;
  }

  setActiveItem(item: T) {
    this.item = item;
  }
}

const TaskConsoleContext = React.createContext<
  ITaskConsoleContext<GraphQL.UserTaskInstance>
>(null);

export const useTaskConsoleContext = (): ITaskConsoleContext<GraphQL.UserTaskInstance> =>
  useContext<ITaskConsoleContext<GraphQL.UserTaskInstance>>(TaskConsoleContext);

export default TaskConsoleContext;
