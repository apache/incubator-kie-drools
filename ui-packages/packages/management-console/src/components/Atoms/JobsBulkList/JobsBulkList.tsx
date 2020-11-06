import React from 'react';
import {
  TextContent,
  TextVariants,
  Text,
  Divider,
  TextList,
  TextListItem
} from '@patternfly/react-core';
import {
  ItemDescriptor,
  OUIAProps,
  componentOuiaProps,
  GraphQL
} from '@kogito-apps/common';
import { getJobsDescription } from '../../../utils/Utils';
import { IJobOperation } from '../../Templates/JobsManagementPage/JobsManagementPage';

interface IOwnProps {
  operationResult: IJobOperation;
}
const JobsBulkList: React.FC<IOwnProps & OUIAProps> = ({
  operationResult,
  ouiaId,
  ouiaSafe
}) => {
  return (
    <div {...componentOuiaProps(ouiaId, 'jobs-list-bulk-instances', ouiaSafe)}>
      {Object.keys(operationResult.results.successJobs).length > 0 ? (
        <>
          <TextContent>
            <Text component={TextVariants.h2}>
              {operationResult.messages.successMessage}
            </Text>
            <TextList>
              {Object.entries(operationResult.results.successJobs).map(
                (job: [string, GraphQL.Job]) => {
                  return (
                    <TextListItem key={job[0]}>
                      <strong>
                        <ItemDescriptor
                          itemDescription={getJobsDescription(job[1])}
                        />
                      </strong>
                    </TextListItem>
                  );
                }
              )}
            </TextList>
          </TextContent>
          {Object.keys(operationResult.results.successJobs).length !== 0 &&
            operationResult.messages.warningMessage && (
              <TextContent className="pf-u-mt-sm">
                <Text component={TextVariants.small}>
                  {operationResult.messages.warningMessage}
                </Text>
              </TextContent>
            )}
        </>
      ) : (
        <TextContent>
          <Text component={TextVariants.h2}>
            {operationResult.messages.noJobsMessage}
          </Text>
        </TextContent>
      )}
      {Object.keys(operationResult.results.ignoredJobs).length !== 0 && (
        <>
          <Divider component="div" className="pf-u-my-xl" />
          <TextContent>
            <Text component={TextVariants.h2}>
              <span>Ignored Jobs:</span>
            </Text>
            <Text component={TextVariants.small} className="pf-u-mt-sm">
              <span>{operationResult.messages.ignoredMessage}</span>
            </Text>
            <TextList>
              {Object.entries(operationResult.results.ignoredJobs).map(
                (job: [string, GraphQL.Job]) => {
                  return (
                    <TextListItem key={job[0]}>
                      <strong>
                        <ItemDescriptor
                          itemDescription={getJobsDescription(job[1])}
                        />
                      </strong>
                    </TextListItem>
                  );
                }
              )}
            </TextList>
          </TextContent>
        </>
      )}
      {Object.keys(operationResult.results.failedJobs).length !== 0 && (
        <>
          <Divider component="div" className="pf-u-my-xl" />
          <TextContent>
            <Text component={TextVariants.h2}>Errors:</Text>
            <TextList>
              {Object.entries(operationResult.results.failedJobs).map(
                (job: [string, GraphQL.Job & { errorMessage?: string }]) => {
                  return (
                    <TextListItem key={job[0]}>
                      <strong>
                        <ItemDescriptor
                          itemDescription={getJobsDescription(job[1])}
                        />
                      </strong>{' '}
                      -{job[1].errorMessage}
                    </TextListItem>
                  );
                }
              )}
            </TextList>
          </TextContent>
        </>
      )}
    </div>
  );
};

export default JobsBulkList;
