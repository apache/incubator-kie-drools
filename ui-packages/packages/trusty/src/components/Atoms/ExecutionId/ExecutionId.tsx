import React, { useMemo } from 'react';
import './ExecutionId.scss';

type ExecutionIdProps = {
  id: string;
};

const ExecutionId = ({ id }: ExecutionIdProps) => {
  const shortenedId = useMemo(() => {
    return id.substring(0, 8);
  }, [id]);

  return <span className="execution-id">#{shortenedId}</span>;
};

export default ExecutionId;
