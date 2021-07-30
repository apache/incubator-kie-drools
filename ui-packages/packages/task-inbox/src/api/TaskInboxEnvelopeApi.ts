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

/**
 * Envelope Api
 */
export interface TaskInboxEnvelopeApi {
  /**
   * Initializes the envelope.
   * @param association
   * @param initArgs
   */
  taskInbox__init(
    association: Association,
    initArgs: TaskInboxInitArgs
  ): Promise<void>;
  taskInbox__notify(userName: string): Promise<void>;
}

export interface Association {
  origin: string;
  envelopeServerId: string;
}

export interface TaskInboxInitArgs {
  initialState?: TaskInboxState;
  allTaskStates?: string[];
  activeTaskStates?: string[];
}

/**
 * Representation of the TaskInbox state containing information about the applied filters, sorting and the current page.
 * This state will be shared between the channel and the TaskInbox.
 */
export interface TaskInboxState {
  filters: QueryFilter;
  sortBy: SortBy;
  currentPage: QueryPage;
}

/**
 * Filter applied in TaskInbox.
 */
export interface QueryFilter {
  taskStates: string[];
  taskNames: string[];
}

/**
 * Sorting applied in TaskInbox
 */
export interface SortBy {
  property: string;
  direction: 'asc' | 'desc';
}

/**
 * The last page of elements loaded in TaskInbox
 */
export interface QueryPage {
  offset: number;
  limit: number;
}
