import { TimeAgo } from '@n1ru4l/react-time-ago';
import React, { useCallback, useState, useEffect } from 'react';
import axios from 'axios';
import {
  Button,
  DataListAction,
  DataListCell,
  DataListCheck,
  DataListContent,
  DataListItem,
  DataListItemCells,
  DataListItemRow,
  DataListToggle,
  Dropdown,
  DropdownItem,
  DropdownPosition,
  Bullseye,
  KebabToggle,
  Modal,
  Alert,
  TextContent,
  TextVariants,
  Text,
  Flex,
  FlexItem,
  Title,
  TitleLevel,
  BaseSizes
} from '@patternfly/react-core';
import { Link } from 'react-router-dom';
import SpinnerComponent from '../../Atoms/SpinnerComponent/SpinnerComponent';
import { useGetChildInstancesLazyQuery } from '../../../graphql/types';
import EmptyStateComponent from '../../Atoms/EmptyStateComponent/EmptyStateComponent';

import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faInfoCircle, faTimesCircle } from '@fortawesome/free-solid-svg-icons';
/* tslint:disable:no-string-literal */

export interface IProcessInstanceError {
  nodeDefinitionId: string;
  message: string;
}
interface IProcessInstance {
  lastUpdate: string;
  id: string;
  processId: string;
  parentProcessInstanceId: string | null;
  processName: string;
  start: string;
  state: string;
  addons: string[];
  endpoint: string;
  error: IProcessInstanceError;
}
export interface IOwnProps {
  id: number;
  processInstanceData: IProcessInstance;
  checkedArray: string[]
}

const DataListItemComponent: React.FC<IOwnProps> = ({
  processInstanceData,
  checkedArray
}) => {
  const [expanded, setexpanded] = useState([]);
  const [isOpen, setisOpen] = useState(false);
  const [isLoaded, setisLoaded] = useState(false);
  const [isChecked, setisChecked] = useState(false);
  const [childList, setChildList] = useState({});
  const [openModal, setOpenModal] = useState(false);
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [modalTitle, setModalTitle] = useState('');
  const [modalContent, setModalContent] = useState('');
  const [alertType, setAlertType] = useState(null);

  const [getChildInstances, { loading, data }] = useGetChildInstancesLazyQuery({
    fetchPolicy: 'network-only'
  });

  const handleSmallModalToggle = () => {
    setIsModalOpen(!isModalOpen);
  };
  const handleViewError = useCallback(
    async (_processID, _instanceID, _endpoint) => {
      setOpenModal(true);
    },
    []
  );
  const onSelect = event => {
    setisOpen(isOpen ? false : true);
  };
  const onCheckBoxClick = () => {
    setisChecked(isChecked ? false : true);
  };

  const onToggle = _isOpen => {
    setisOpen(_isOpen);
  };

  const handleModalToggle = () => {
    setOpenModal(!openModal);
  };

  const handleSkip = useCallback(async (_processID, _instanceID, _endpoint) => {
    const processInstanceId = processInstanceData.id;
    const processId = processInstanceData.processId;

    try {
      await axios.post(
        `${processInstanceData.endpoint}/management/processes/${processId}/instances/${processInstanceId}/skip`
      );
      setModalTitle('Skip operation');
      setModalContent(
        'Process execution has successfully skipped node which was in error state.'
      );
      setAlertType('success');
      handleSmallModalToggle();
    } catch (error) {
      setModalTitle('Skip operation');
      setModalContent(
        `Process execution failed to skip node which is in error state. Message: ${JSON.stringify(
          error.message
        )}`
      );
      setAlertType('danger');
      handleSmallModalToggle();
    }
  }, []);

  const handleRetry = useCallback(
    async (_processID, _instanceID, _endpoint) => {
      const processInstanceId = processInstanceData.id;
      const processId = processInstanceData.processId;
      try {
        await axios.post(
          `${processInstanceData.endpoint}/management/processes/${processId}/instances/${processInstanceId}/retrigger`
        );
        setModalTitle('Retry operation');
        setModalContent(
          `Process execution has successfully re-executed node which was in error state.`
        );
        setAlertType('success');
        handleSmallModalToggle();
      } catch (error) {
        setModalTitle('Retry operation');
        setModalContent(
          `Process execution failed to re-execute node which is in error state. Message: ${JSON.stringify(
            error.message
          )}`
        );
        setAlertType('danger');
        handleSmallModalToggle();
      }
    },
    []
  );

  const handleAbortActiveInstances = useCallback(
    (processInstanceId, processId) => {
      axios
        .post(
          `${processInstanceData.endpoint}/management/processes/${processId}/instances/${processInstanceId}`
        )
        .then(() => {
          setModalTitle('Process aborted');
          setModalContent(
            `${processId} - process execution has been aborted.`
          );
          setAlertType('success');
          processInstanceData.state = 'ABORTED';
          handleSmallModalToggle();
        })
        .catch(error => {
          setModalTitle('Process not aborted');
          setModalContent(
            `Aborting process instance failed with error - ${JSON.stringify(
              error.message
            )}`
          );
          setAlertType('danger');
          handleSmallModalToggle();
        });
    },
    []
  );

  const toggle = async _id => {
    const index = expanded.indexOf(_id);
    const newExpanded =
      index >= 0
        ? [
          ...expanded.slice(0, index),
          ...expanded.slice(index + 1, expanded.length)
        ]
        : [...expanded, _id];
    setexpanded(newExpanded);

    if (!isLoaded) {
      getChildInstances({
        variables: {
          rootProcessInstanceId: processInstanceData.id
        }
      });
    }
  };

  useEffect(() => {
    if (data !== undefined) {
      setChildList(data);
      setisLoaded(true);
    }
  }, [data]);

  const handleRetryButton = async () => {
    setOpenModal(!openModal);
    await handleRetry(
      processInstanceData.processId,
      processInstanceData.id,
      processInstanceData.endpoint
    );
  };

  const handleSkipButton = async () => {
    setOpenModal(!openModal);
    await handleSkip(
      processInstanceData.processId,
      processInstanceData.id,
      processInstanceData.endpoint
    );
  };

  const dropDownList = () => {
    if (processInstanceData.addons.includes('process-management')) {
      if (processInstanceData.state === 'ERROR') {
        return [
          <DropdownItem
            key={1}
            onClick={() =>
              handleRetry(
                processInstanceData.processId,
                processInstanceData.id,
                processInstanceData.endpoint
              )
            }
          >
            Retry
          </DropdownItem>,
          <DropdownItem
            key={2}
            onClick={() =>
              handleSkip(
                processInstanceData.processId,
                processInstanceData.id,
                processInstanceData.endpoint
              )
            }
          >
            Skip
          </DropdownItem>,
          <DropdownItem
            key={4}
            onClick={() =>
              handleAbortActiveInstances(
                processInstanceData.id,
                processInstanceData.processId
              )
            }
          >
            Abort
          </DropdownItem>,
          <DropdownItem
            key={3}
            onClick={() =>
              handleViewError(
                processInstanceData.processId,
                processInstanceData.id,
                processInstanceData.endpoint
              )
            }
          >
            View error
          </DropdownItem>
        ];
      } else {
        return [
          <DropdownItem
            key={4}
            onClick={() =>
              handleAbortActiveInstances(
                processInstanceData.id,
                processInstanceData.processId
              )
            }
          >
            Abort
          </DropdownItem>
        ];
      }
    } else {
      return [
        <DropdownItem
          key={1}
          onClick={() =>
            handleViewError(
              processInstanceData.processId,
              processInstanceData.id,
              processInstanceData.endpoint
            )
          }
        >
          View error
        </DropdownItem>
      ];
    }
  };

  return (
    <React.Fragment>
      <Modal
        isSmall
        title=""
        header={
          <Title headingLevel={TitleLevel.h1} size={BaseSizes['2xl']}>
            {alertType === 'success' ? (
              <FontAwesomeIcon
                icon={faInfoCircle}
                size="sm"
                color="var(--pf-global--info-color--100)"
                className="pf-u-mr-md"
              />
            ) : (
                <FontAwesomeIcon
                  icon={faTimesCircle}
                  size="sm"
                  color="var(--pf-global--danger-color--100)"
                  className="pf-u-mr-md"
                />
              )}
            {modalTitle}
          </Title>
        }
        isOpen={isModalOpen}
        onClose={handleSmallModalToggle}
        actions={[
          <Button
            key="confirm"
            variant="primary"
            onClick={handleSmallModalToggle}
          >
            OK
          </Button>
        ]}
        isFooterLeftAligned={false}
      >
        <TextContent>
          <Text>
            <strong>
              {modalContent}
            </strong>
          </Text>
          {!checkedArray.includes('ABORTED') &&
            <Text>
              Note: The process status has been updated. The list may appear inconsistent until you refresh any applied filters.
            </Text>
          }
        </TextContent>
      </Modal>
      <DataListItem
        aria-labelledby="kie-datalist-item"
        isExpanded={expanded.includes('kie-datalist-toggle')}
      >
        <DataListItemRow>
          {processInstanceData.parentProcessInstanceId === null && (
            <DataListToggle
              onClick={() => toggle('kie-datalist-toggle')}
              isExpanded={expanded.includes('kie-datalist-toggle')}
              id="kie-datalist-toggle"
              aria-controls="kie-datalist-expand"
            />
          )}
          <DataListCheck
            aria-labelledby="width-kie-datalist-item"
            name="width-kie-datalist-item"
            checked={isChecked}
            onChange={() => {
              onCheckBoxClick();
            }}
          />
          <DataListItemCells
            dataListCells={[
              <DataListCell key={1}>
                {processInstanceData.processName}
              </DataListCell>,
              <DataListCell key={2}>
                {processInstanceData.start ? (
                  <TimeAgo
                    date={new Date(`${processInstanceData.start}`)}
                    render={({ _error, value }) => <span>{value}</span>}
                  />
                ) : (
                    ''
                  )}
              </DataListCell>,
              <DataListCell key={3}>
                {processInstanceData.lastUpdate ? (
                  <TimeAgo
                    date={new Date(`${processInstanceData.lastUpdate}`)}
                    render={({ _error, value }) => <span>{value}</span>}
                  />
                ) : (
                    ''
                  )}
              </DataListCell>,
              <DataListCell key={4}>{processInstanceData.state}</DataListCell>
            ]}
          />

          <DataListAction
            aria-labelledby="kie-datalist-item kie-datalist-action"
            id="kie-datalist-action"
            aria-label="Actions"
          >
            <Link to={'/ProcessInstances/' + processInstanceData.id}>
              <Button variant="secondary">Details</Button>
            </Link>
          </DataListAction>
          <DataListAction
            aria-labelledby="kie-datalist-item kie-datalist-action"
            id="kie-datalist-action"
            aria-label="Actions"
          >
            {processInstanceData.state === 'ERROR' ||
              processInstanceData.state === 'ACTIVE' ||
              processInstanceData.state === 'SUSPENDED' ? (
                <Dropdown
                  isPlain
                  position={DropdownPosition.right}
                  isOpen={isOpen}
                  onSelect={onSelect}
                  toggle={<KebabToggle onToggle={onToggle} />}
                  dropdownItems={dropDownList()}
                />
              ) : (
                <Dropdown
                  isPlain
                  position={DropdownPosition.right}
                  isOpen={isOpen}
                  onSelect={onSelect}
                  toggle={<KebabToggle isDisabled onToggle={onToggle} />}
                  dropdownItems={[]}
                />
              )}
            <Modal
              isLarge
              title="Error"
              isOpen={openModal}
              onClose={handleModalToggle}
              actions={
                processInstanceData.addons.includes('process-management')
                  ? [
                    <Button
                      key="confirm1"
                      variant="secondary"
                      onClick={handleSkipButton}
                    >
                      Skip
                      </Button>,
                    <Button
                      key="confirm2"
                      variant="secondary"
                      onClick={handleRetryButton}
                    >
                      Retry
                      </Button>,
                    <Button
                      key="confirm3"
                      variant="primary"
                      onClick={handleModalToggle}
                    >
                      Close
                      </Button>
                  ]
                  : [
                    <Button
                      key="confirm3"
                      variant="primary"
                      onClick={handleModalToggle}
                    >
                      Close
                      </Button>
                  ]
              }
            >
              {processInstanceData.error
                ? processInstanceData.error.message
                : 'No error message found'}
            </Modal>
          </DataListAction>
        </DataListItemRow>
        <DataListContent
          aria-label="Primary Content Details"
          id="kie-datalist-expand1"
          isHidden={!expanded.includes('kie-datalist-toggle')}
        >
          {isLoaded &&
            childList['ProcessInstances'] !== undefined &&
            childList['ProcessInstances'].map((child, index) => {
              return (
                <DataListItemComponent
                  id={index}
                  key={child.id}
                  processInstanceData={child}
                  checkedArray={checkedArray}
                />
              );
            })}
          {isLoaded &&
            childList['ProcessInstances'] !== undefined &&
            childList['ProcessInstances'].length === 0 && (
              <EmptyStateComponent
                iconType="infoCircleIcon"
                title="No child process instances"
                body="This process has no related sub processes"
              />
            )}

          {!isLoaded && (
            <Bullseye>
              <SpinnerComponent spinnerText="Loading process instances..." />
            </Bullseye>
          )}
        </DataListContent>
      </DataListItem>
    </React.Fragment>
  );
};

export default DataListItemComponent;
