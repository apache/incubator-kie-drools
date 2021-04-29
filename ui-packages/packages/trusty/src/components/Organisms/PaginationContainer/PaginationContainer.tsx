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
