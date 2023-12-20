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
import { Form } from '@kogito-apps/components-common/dist/types';

export interface FormDetailsContext {
  updateContent(formContent: Form): void;
  onUpdateContent(listener: UpdateContentListener): UnSubscribeHandler;
}

export interface UpdateContentListener {
  onUpdateContent: (formContent: Form) => void;
}

export interface UnSubscribeHandler {
  unSubscribe: () => void;
}

export class FormDetailsContextImpl implements FormDetailsContext {
  private readonly updateContentListeners: UpdateContentListener[] = [];

  updateContent(formContent: Form): void {
    if (formContent) {
      this.updateContentListeners.forEach((listener) =>
        listener.onUpdateContent(formContent)
      );
    }
  }

  onUpdateContent(listener: UpdateContentListener): UnSubscribeHandler {
    this.updateContentListeners.push(listener);

    return {
      unSubscribe: () => {
        const index = this.updateContentListeners.indexOf(listener);
        if (index > -1) {
          this.updateContentListeners.splice(index, 1);
        }
      }
    };
  }
}

const RuntimeToolsFormDetailsContext =
  React.createContext<FormDetailsContext>(null);

export default RuntimeToolsFormDetailsContext;

export const useFormDetailsContext = () =>
  useContext<FormDetailsContext>(RuntimeToolsFormDetailsContext);
