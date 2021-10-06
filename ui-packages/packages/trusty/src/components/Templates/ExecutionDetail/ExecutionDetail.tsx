import React, { useCallback } from 'react';
import { useHistory } from 'react-router-dom';
import { PageSection, Stack, StackItem, Title } from '@patternfly/react-core';
import Outcomes from '../../Organisms/Outcomes/Outcomes';
import SkeletonCards from '../../Molecules/SkeletonCards/SkeletonCards';
import { Outcome, RemoteData, RemoteDataStatus } from '../../../types';
import './ExecutionDetail.scss';

type ExecutionDetailProps = {
  outcomes: RemoteData<Error, Outcome[]>;
};

const ExecutionDetail = (props: ExecutionDetailProps) => {
  const { outcomes } = props;
  const history = useHistory();
  const goToExplanation = useCallback(
    (outcomeId: string) => {
      history.push({
        pathname: 'outcomes-details',
        search: `?outcomeId=${outcomeId}`
      });
    },
    [history]
  );

  return (
    <section className="execution-detail">
      <PageSection variant="default">
        <Stack hasGutter>
          <StackItem>
            <Title headingLevel="h3" size="2xl">
              Outcomes
            </Title>
          </StackItem>
          <StackItem>
            {outcomes.status === RemoteDataStatus.LOADING && (
              <SkeletonCards quantity={2} />
            )}
            {outcomes.status === RemoteDataStatus.SUCCESS && (
              <Outcomes
                outcomes={outcomes.data}
                onExplanationClick={goToExplanation}
                listView
                ouiaId="outcomes-gallery"
              />
            )}
          </StackItem>
        </Stack>
      </PageSection>
    </section>
  );
};

export default ExecutionDetail;
