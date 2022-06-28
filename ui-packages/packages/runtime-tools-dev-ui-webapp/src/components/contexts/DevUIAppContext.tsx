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
import { User } from '@kogito-apps/consoles-common';
import { CustomLabels } from '../../api/CustomLabels';
import { DiagramPreviewSize } from '@kogito-apps/process-details/dist/api';

export interface DevUIAppContext {
  isProcessEnabled: boolean;
  isTracingEnabled: boolean;
  getCurrentUser(): User;
  getAllUsers(): User[];
  switchUser(userId: string): void;
  onUserChange(listener: UserChangeListener): UnSubscribeHandler;
  getDevUIUrl(): string;
  getOpenApiPath(): string;
  availablePages?: string[];
  customLabels: CustomLabels;
  omittedProcessTimelineEvents: string[];
  diagramPreviewSize?: DiagramPreviewSize;
  isWorkflow(): boolean;
}

export interface UserChangeListener {
  onUserChange: (user: User) => void;
}

export interface UnSubscribeHandler {
  unSubscribe: () => void;
}

export class DevUIAppContextImpl implements DevUIAppContext {
  private readonly users?: User[];
  private currentUser: User;
  private readonly userListeners: UserChangeListener[] = [];
  private readonly devUIUrl: string;
  private readonly openApiPath: string;
  readonly isProcessEnabled: boolean;
  readonly isTracingEnabled: boolean;
  public readonly availablePages: string[];
  public readonly customLabels: CustomLabels;
  public readonly omittedProcessTimelineEvents: string[];
  public readonly diagramPreviewSize?: DiagramPreviewSize;

  constructor(
    users,
    url,
    path,
    isProcessEnabled,
    isTracingEnabled,
    availablePages,
    customLabels,
    omittedProcessTimelineEvents,
    diagramPreviewSize
  ) {
    this.users = users;
    this.devUIUrl = url;
    this.openApiPath = path;
    this.isProcessEnabled = isProcessEnabled;
    this.isTracingEnabled = isTracingEnabled;
    this.availablePages = availablePages;
    this.customLabels = customLabels;
    this.omittedProcessTimelineEvents = omittedProcessTimelineEvents;
    this.diagramPreviewSize = diagramPreviewSize;
    if (users?.length > 0) {
      this.currentUser = users[0];
    }
  }

  getDevUIUrl(): string {
    return this.devUIUrl;
  }

  getOpenApiPath(): string {
    return this.openApiPath;
  }

  getCurrentUser(): User {
    return this.currentUser;
  }

  getAllUsers(): User[] {
    return this.users;
  }

  switchUser(userId: string): void {
    const switchedUser = this.users.find(user => user.id === userId);
    if (switchedUser) {
      this.currentUser = switchedUser;
      this.userListeners.forEach(listener =>
        listener.onUserChange(switchedUser)
      );
    }
  }

  onUserChange(listener: UserChangeListener): UnSubscribeHandler {
    this.userListeners.push(listener);

    return {
      unSubscribe: () => {
        const index = this.userListeners.indexOf(listener);
        if (index > -1) {
          this.userListeners.splice(index, 1);
        }
      }
    };
  }

  isWorkflow(): boolean {
    return this.customLabels.singularProcessLabel == 'Workflow';
  }
}

const RuntimeToolsDevUIAppContext = React.createContext<DevUIAppContext>(null);

export default RuntimeToolsDevUIAppContext;

export const useDevUIAppContext = () =>
  useContext<DevUIAppContext>(RuntimeToolsDevUIAppContext);
