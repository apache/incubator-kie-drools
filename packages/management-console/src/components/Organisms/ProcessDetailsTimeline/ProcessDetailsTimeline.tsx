import Moment from 'react-moment';
import {
  Card,
  CardBody,
  CardHeader,
  Title,
  Bullseye,
  Text,
  TextContent,
  TextVariants,
  Split,
  SplitItem,
  Stack,
  Dropdown,
  KebabToggle,
  DropdownItem,
  Tooltip
} from '@patternfly/react-core';
import {
  ServicesIcon,
  UserIcon,
  CheckCircleIcon,
  ErrorCircleOIcon,
  OnRunningIcon
} from '@patternfly/react-icons';
import React, { useState } from 'react';
import './ProcessDetailsTimeline.css';
import { ProcessInstance } from '../../../graphql/types';
import { handleRetry, handleSkip } from '../../../utils/Utils';

export interface IOwnProps {
  data: Pick<
    ProcessInstance,
    'id' | 'nodes' | 'addons' | 'error' | 'serviceUrl' | 'processId' | 'state'
  >;
  setModalTitle: (modalTitle: string) => void,
  setTitleType: (titleType: string) => void,
  setModalContent: (modalContent: string) => void,
  handleSkipModalToggle: () => void
  handleRetryModalToggle: () => void
}

const ProcessDetailsTimeline: React.FC<IOwnProps> = ({
  data,
  setModalTitle,
  setModalContent,
  setTitleType,
  handleRetryModalToggle,
  handleSkipModalToggle
}) => {
  const [isKebabOpen, setIsKebabOpen] = useState(false);
  const dropdownItems = [
    <DropdownItem key="retry" component="button" onClick={() => handleRetry(data, setModalTitle, setTitleType, setModalContent, handleRetryModalToggle)}>
      Retry
    </DropdownItem>,
    <DropdownItem key="skip" component="button" onClick={() => handleSkip(data, setModalTitle, setTitleType, setModalContent, handleSkipModalToggle)}>
      Skip
    </DropdownItem>
  ];

  const onKebabToggle = isOpen => {
    setIsKebabOpen(isOpen);
  };

  const onDropdownSelect = event => {
    setIsKebabOpen(!isKebabOpen);
  };
  return (
    <Card>
      <CardHeader>
        <Title headingLevel="h3" size="xl">
          Timeline
        </Title>
      </CardHeader>
      <CardBody>
        <Stack gutter="md" className="kogito-management-console--timeline">
          {data.nodes &&
            data.nodes.map((content, idx) => {
              return (
                <Split
                  gutter={'sm'}
                  className={'kogito-management-console--timeline-item'}
                  key={content.id}
                >
                  <SplitItem>
                    {
                      <>
                        {data.error &&
                          content.definitionId === data.error.nodeDefinitionId ? (
                            <Tooltip content={data.error.message}>
                              <ErrorCircleOIcon
                                color="var(--pf-global--danger-color--100)"
                                className="kogito-management-console--timeline-status"
                              />
                            </Tooltip>
                          ) : content.exit === null ? (
                            <Tooltip content={'Active'}>
                              <OnRunningIcon className="kogito-management-console--timeline-status" />
                            </Tooltip>
                          ) : (
                              <Tooltip content={'Completed'}>
                                <CheckCircleIcon
                                  color="var(--pf-global--success-color--100)"
                                  className="kogito-management-console--timeline-status"
                                />
                              </Tooltip>
                            )}
                      </>
                    }
                  </SplitItem>
                  <SplitItem isFilled>
                    <TextContent>
                      <Text component={TextVariants.p}>
                        {content.name}
                        <span>
                          {content.type === 'HumanTaskNode' && (
                            <Tooltip content={'Human task'}>
                              <UserIcon
                                className="pf-u-ml-sm"
                                color="var(--pf-global--icon--Color--light)"
                              />
                            </Tooltip>
                          )}
                        </span>
                        <Text component={TextVariants.small}>
                          {content.exit === null ? (
                            'Active'
                          ) : (
                              <Moment fromNow>
                                {new Date(`${content.exit}`)}
                              </Moment>
                            )}
                        </Text>
                      </Text>
                    </TextContent>
                  </SplitItem>
                  <SplitItem>
                    {
                      <>
                        {data.addons.includes('process-management') &&
                          data.serviceUrl !== null &&
                          data.error &&
                          content.definitionId === data.error.nodeDefinitionId ? (
                            <Dropdown
                              onSelect={onDropdownSelect}
                              toggle={
                                <KebabToggle
                                  onToggle={onKebabToggle}
                                  id={'timeline-kebab-toggle-' + idx}
                                />
                              }
                              isOpen={isKebabOpen}
                              isPlain
                              dropdownItems={dropdownItems}
                            />
                          ) : (
                            <Dropdown
                              toggle={
                                <KebabToggle
                                  isDisabled
                                  id={'timeline-kebab-toggle-disabled-' + idx}
                                />
                              }
                              isPlain
                            />
                          )}
                      </>
                    }
                  </SplitItem>{' '}
                </Split>
              );
            })}
        </Stack>
      </CardBody>
    </Card>
  );
};

export default ProcessDetailsTimeline;
