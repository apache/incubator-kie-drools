import React from 'react';
import { Button } from '@patternfly/react-core';

interface IOwnProps {
  offset: number;
  setOffset: (offset: number) => void;
  getProcessInstances: (initval: number, pageSize: number) => void;
  pageSize: number;
}

const LoadMoreComponent: React.FC<IOwnProps> = ({
  offset,
  setOffset,
  getProcessInstances,
  pageSize
}) => {
  const loadMore = newPageSize => {
    const newOffset = offset + pageSize;
    setOffset(newOffset);
    getProcessInstances(newOffset, newPageSize);
  };

  const load10More = () => {
    loadMore(10);
  };
  const load20More = () => {
    loadMore(20);
  };
  const load50More = () => {
    loadMore(50);
  };
  const load100More = () => {
    loadMore(100);
  };

  return (
    <React.Fragment>
      <Button onClick={load10More} variant="secondary">
        Load 10 more
      </Button>{' '}
      <Button onClick={load20More} variant="secondary">
        Load 20 more
      </Button>{' '}
      <Button onClick={load50More} variant="secondary">
        Load 50 more
      </Button>{' '}
      <Button onClick={load100More} variant="secondary">
        Load 100 more
      </Button>
    </React.Fragment>
  );
};

export default LoadMoreComponent;
