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
import {
  FormFilter,
  FormInfo
} from '@kogito-apps/components-common/dist/types';
import { getForms } from '../apis';

/* eslint-disable @typescript-eslint/no-empty-interface */
export interface FormsListGatewayApi {
  getFormFilter(): Promise<FormFilter>;
  applyFilter(formList: FormFilter): Promise<void>;
  getFormsQuery(): Promise<FormInfo[]>;
  openForm: (formData: FormInfo) => Promise<void>;
  onOpenFormListen: (listener: OnOpenFormListener) => UnSubscribeHandler;
}

export interface OnOpenFormListener {
  onOpen: (formData: FormInfo) => void;
}

export interface UnSubscribeHandler {
  unSubscribe: () => void;
}

export class FormsListGatewayApiImpl implements FormsListGatewayApi {
  private _FormFilter: FormFilter = {
    formNames: []
  };
  private readonly listeners: OnOpenFormListener[] = [];

  getFormFilter = (): Promise<FormFilter> => {
    return Promise.resolve(this._FormFilter);
  };

  applyFilter = (formFilter: FormFilter): Promise<void> => {
    this._FormFilter = formFilter;
    return Promise.resolve();
  };

  getFormsQuery(): Promise<FormInfo[]> {
    return getForms(this._FormFilter.formNames);
  }

  openForm = (formData: FormInfo): Promise<void> => {
    this.listeners.forEach((listener) => listener.onOpen(formData));
    return Promise.resolve();
  };

  onOpenFormListen(listener: OnOpenFormListener): UnSubscribeHandler {
    this.listeners.push(listener);

    const unSubscribe = () => {
      const index = this.listeners.indexOf(listener);
      if (index > -1) {
        this.listeners.splice(index, 1);
      }
    };

    return {
      unSubscribe
    };
  }
}
