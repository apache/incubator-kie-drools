import Moment from 'react-moment';
import {
  Card,
  CardBody,
  CardHeader,
  Title,
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
  UserIcon,
  CheckCircleIcon,
  ErrorCircleOIcon,
  OnRunningIcon
} from '@patternfly/react-icons';
import React, { useState } from 'react';
import './ProcessDetailsTimeline.css';
import { GraphQL } from '@kogito-apps/common';
import {
  handleRetry,
  handleSkip,
  handleNodeInstanceRetrigger,
  setTitle,
  handleNodeInstanceCancel
} from '../../../utils/Utils';
import ProcessInstance = GraphQL.ProcessInstance;
import ProcessListModal from '../../Atoms/ProcessListModal/ProcessListModal';

export interface IOwnProps {
  data: Pick<
    ProcessInstance,
    'id' | 'nodes' | 'addons' | 'error' | 'serviceUrl' | 'processId' | 'state'
  >;
}
enum TitleType {
  SUCCESS = 'success',
  FAILURE = 'failure'
}
const ProcessDetailsTimeline: React.FC<IOwnProps> = ({ data }) => {
  const [kebabOpenArray, setKebabOpenArray] = useState([]);
  const [modalTitle, setModalTitle] = useState<string>('');
  const [titleType, setTitleType] = useState<string>('');
  const [modalContent, setModalContent] = useState<string>('');
  const [isModalOpen, setIsModalOpen] = useState<boolean>(false);
  const ignoredNodeTypes = ['Join', 'Split', 'EndNode'];

  const onKebabToggle = (isOpen: boolean, id) => {
    if (isOpen) {
      setKebabOpenArray([...kebabOpenArray, id]);
    } else {
      onDropdownSelect(id);
    }
  };

  const onDropdownSelect = id => {
    const tempKebabArray = [...kebabOpenArray];
    const index = tempKebabArray.indexOf(id);
    tempKebabArray.splice(index, 1);
    setKebabOpenArray(tempKebabArray);
  };

  const handleModalToggle = () => {
    setIsModalOpen(!isModalOpen);
  };

  const onShowMessage = (
    title: string,
    content: string,
    type: TitleType
  ): void => {
    setTitleType(type);
    setModalTitle(title);
    setModalContent(content);
    handleModalToggle();
  };
  const dropdownItems = (processInstanceData, node) => {
    if (
      processInstanceData.error &&
      node.definitionId === processInstanceData.error.nodeDefinitionId
    ) {
      return [
        <DropdownItem
          key="retry"
          component="button"
          onClick={() =>
            handleRetry(
              processInstanceData,
              () =>
                onShowMessage(
                  'Retry operation',
                  `The node ${node.name} was successfully re-executed.`,
                  TitleType.SUCCESS
                ),
              (errorMessage: string) =>
                onShowMessage(
                  'Retry operation',
                  `The node ${node.name} failed to re-execute. Message: ${errorMessage}`,
                  TitleType.FAILURE
                )
            )
          }
        >
          Retry
        </DropdownItem>,
        <DropdownItem
          key="skip"
          component="button"
          onClick={() =>
            handleSkip(
              processInstanceData,
              () =>
                onShowMessage(
                  'Skip operation',
                  `The node ${node.name} was successfully skipped.`,
                  TitleType.SUCCESS
                ),
              (errorMessage: string) =>
                onShowMessage(
                  'Skip operation',
                  `The node ${node.name} failed to skip. Message: ${errorMessage}`,
                  TitleType.FAILURE
                )
            )
          }
        >
          Skip
        </DropdownItem>
      ];
    } else if (node.exit === null && !ignoredNodeTypes.includes(node.type)) {
      return [
        <DropdownItem
          key="retrigger"
          component="button"
          onClick={() =>
            handleNodeInstanceRetrigger(
              processInstanceData,
              node,
              () =>
                onShowMessage(
                  'Node retrigger operation',
                  `The node ${node.name} was successfully retriggered.`,
                  TitleType.SUCCESS
                ),
              (errorMessage: string) =>
                onShowMessage(
                  'Node retrigger operation',
                  `The node ${node.name} failed to retrigger. Message: ${errorMessage}`,
                  TitleType.FAILURE
                )
            )
          }
        >
          Retrigger node
        </DropdownItem>,
        <DropdownItem
          key="cancel"
          component="button"
          onClick={() =>
            handleNodeInstanceCancel(
              processInstanceData,
              node,
              () =>
                onShowMessage(
                  'Node cancel operation',
                  `The node ${node.name} was successfully canceled.`,
                  TitleType.SUCCESS
                ),
              (errorMessage: string) =>
                onShowMessage(
                  'Node cancel operation',
                  `The node ${node.name} failed to cancel. Message: ${errorMessage}`,
                  TitleType.FAILURE
                )
            )
          }
        >
          Cancel node
        </DropdownItem>
      ];
    } else {
      return [];
    }
  };
  const processManagementKebabButtons = (node, index) => {
    const dropdownItemsValue = dropdownItems(data, node);
    if (
      data.addons.includes('process-management') &&
      data.serviceUrl !== null &&
      dropdownItemsValue.length !== 0
    ) {
      return (
        <Dropdown
          onSelect={() => onDropdownSelect('timeline-kebab-toggle-' + index)}
          toggle={
            <KebabToggle
              onToggle={isOpen =>
                onKebabToggle(isOpen, 'timeline-kebab-toggle-' + index)
              }
              id={'timeline-kebab-toggle-' + index}
            />
          }
          position="right"
          isOpen={kebabOpenArray.includes('timeline-kebab-toggle-' + index)}
          isPlain
          dropdownItems={dropdownItemsValue}
        />
      );
    }
  };

  return (
    <Card>
      <ProcessListModal
        isModalOpen={isModalOpen}
        handleModalToggle={handleModalToggle}
        checkedArray={data && [data.state]}
        modalTitle={setTitle(titleType, modalTitle)}
        modalContent={modalContent}
      />
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
                    {processManagementKebabButtons(content, idx)}
                  </SplitItem>
                </Split>
              );
            })}
        </Stack>
      </CardBody>
    </Card>
  );
};

export default ProcessDetailsTimeline;
