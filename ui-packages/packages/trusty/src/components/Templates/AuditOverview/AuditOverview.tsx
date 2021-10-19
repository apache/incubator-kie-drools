import React, { useEffect, useState } from 'react';
import {
  PageSection,
  PageSectionVariants,
  Text,
  TextContent,
  Title
} from '@patternfly/react-core';
import {
  AuditToolbarTop,
  AuditToolbarBottom
} from '../../Organisms/AuditToolbar/AuditToolbar';
import ExecutionTable from '../../Organisms/ExecutionTable/ExecutionTable';
import useExecutions from './useExecutions';
import { formatISO, sub } from 'date-fns';
import { RemoteDataStatus } from '../../../types';

type AuditOverviewProps = {
  dateRangePreset?: {
    fromDate: string;
    toDate: string;
  };
};

const AuditOverview = (props: AuditOverviewProps) => {
  const { dateRangePreset } = props;
  const toPreset = dateRangePreset
    ? dateRangePreset.toDate
    : formatISO(Date.now(), { representation: 'date' });
  const fromPreset = dateRangePreset
    ? dateRangePreset.fromDate
    : formatISO(sub(Date.now(), { months: 1 }), { representation: 'date' });
  const [searchString, setSearchString] = useState('');
  const [fromDate, setFromDate] = useState(fromPreset);
  const [toDate, setToDate] = useState(toPreset);
  const [page, setPage] = useState(1);
  const [pageSize, setPageSize] = useState(10);
  const [total, setTotal] = useState(0);
  const { loadExecutions, executions } = useExecutions({
    searchString,
    from: fromDate,
    to: toDate,
    limit: pageSize,
    offset: pageSize * (page - 1)
  });

  useEffect(() => {
    if (executions.status === RemoteDataStatus.SUCCESS) {
      setTotal(executions.data.total);
    }
  }, [executions]);

  return (
    <>
      <PageSection variant={PageSectionVariants.light}>
        <TextContent>
          <Title size="3xl" headingLevel="h2">
            Audit investigation
          </Title>
          <Text component="p">
            Here you can retrieve all the available information about past
            cases.
          </Text>
        </TextContent>
      </PageSection>

      <PageSection isFilled={true}>
        <AuditToolbarTop
          setSearchString={setSearchString}
          fromDate={fromDate}
          setFromDate={setFromDate}
          toDate={toDate}
          setToDate={setToDate}
          total={total}
          pageSize={pageSize}
          page={page}
          setPage={setPage}
          setPageSize={setPageSize}
          onRefresh={loadExecutions}
        />

        <ExecutionTable data={executions} />

        <AuditToolbarBottom
          total={total}
          pageSize={pageSize}
          page={page}
          setPage={setPage}
          setPageSize={setPageSize}
        />
      </PageSection>
    </>
  );
};

export default AuditOverview;
