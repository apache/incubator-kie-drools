/*
 * Copyright 2023 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

export const KOGITO_PROCESS_REFERENCE_ID = 'kogitoprocrefid';
export const KOGITO_BUSINESS_KEY = 'kogitobusinesskey';

export enum CloudEventMethod {
  POST = 'POST',
  PUT = 'PUT'
}

export interface CloudEventRequest {
  endpoint: string;
  method: CloudEventMethod;

  headers: CloudEventHeaders;
  data: string;
}

export interface CloudEventHeaders {
  type: string; // Type of the cloud event
  source: string; // Source of the cloud event

  extensions: Record<string, string>;
}

export interface CloudEventFormChannelApi {
  cloudEventForm__triggerCloudEvent(event: CloudEventRequest): Promise<void>;
}
