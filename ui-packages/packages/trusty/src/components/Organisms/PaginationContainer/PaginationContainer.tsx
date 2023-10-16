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
import React from 'react';
import { Pagination, PaginationVariant } from '@patternfly/react-core';

type PaginationContainerProps = {
  total: number;
  page: number;
  pageSize: number;
  paginationId: string;
  onSetPage: (page: number) => void;
  onSetPageSize: (size: number) => void;
  position: PaginationVariant;
};

const PaginationContainer = (props: PaginationContainerProps) => {
  const {
    total,
    page,
    pageSize,
    paginationId,
    onSetPage,
    onSetPageSize,
    position
  } = props;

  const updatePage = (event: never, pageNumber: number) => {
    onSetPage(pageNumber);
  };
  const updatePageSize = (event: never, pageNumber: number) => {
    onSetPageSize(pageNumber);
  };

  return (
    <Pagination
      itemCount={total}
      perPage={pageSize}
      page={page}
      onSetPage={updatePage}
      widgetId={paginationId}
      onPerPageSelect={updatePageSize}
      variant={position}
    />
  );
};

export default PaginationContainer;
