import Moment from 'react-moment';
import React, { useEffect, useState } from 'react';
import {
  Bullseye,
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
  KebabToggle
} from '@patternfly/react-core';
import {
  GraphQL,
  KogitoEmptyState,
  KogitoEmptyStateType,
  KogitoSpinner,
  ProcessDescriptor,
  ServerErrors,
  EndpointLink
} from '@kogito-apps/common';
import { Link } from 'react-router-dom';
import { HistoryIcon } from '@patternfly/react-icons';
import ErrorPopover from '../../Atoms/ErrorPopover/ErrorPopover';
import ProcessListModal from '../../Atoms/ProcessListModal/ProcessListModal';
import DisablePopup from '../DisablePopup/DisablePopup';
import {
  handleAbort,
  handleRetry,
  handleSkip,
  setTitle,
  stateIconCreator
} from '../../../utils/Utils';
import ProcessInstance = GraphQL.ProcessInstance;

type filterType = {
  status: GraphQL.ProcessInstanceState[];
  businessKey: string[];
};

interface IOwnProps {
  id: number;
  processInstanceData: ProcessInstance;
  initData: any;
  setInitData: any;
  loadingInitData: boolean;
  abortedObj: any;
  setAbortedObj: any;
  setIsAllChecked: (isAllChecked: boolean) => void;
  setSelectedNumber: (selectedNumber: number) => void;
  selectedNumber: number;
  filters: filterType;
}

enum TitleType {
  SUCCESS = 'success',
  FAILURE = 'failure'
}

const ProcessListTableItems: React.FC<IOwnProps> = ({
  processInstanceData,
  initData,
  setInitData,
  loadingInitData,
  abortedObj,
  setAbortedObj,
  setIsAllChecked,
  selectedNumber,
  setSelectedNumber,
  filters
}) => {
  const [expanded, setexpanded] = useState([]);
  const [isOpen, setisOpen] = useState(false);
  const [isLoaded, setisLoaded] = useState(false);
  const [modalTitle, setModalTitle] = useState('');
  const [modalContent, setModalContent] = useState('');
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [titleType, setTitleType] = useState('');
  const isChecked = 'isChecked';
  const [
    getChildInstances,
    { loading, data, error }
  ] = GraphQL.useGetChildInstancesLazyQuery({
    fetchPolicy: 'network-only'
  });
  const currentPage = { prev: location.pathname };
  window.localStorage.setItem('state', JSON.stringify(currentPage));

  const handleModalToggle = () => {
    setIsModalOpen(!isModalOpen);
  };

  const onSelect = event => {
    setisOpen(isOpen ? false : true);
  };
  const onToggle = _isOpen => {
    setisOpen(_isOpen);
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
  const onSkipClick = () => {
    handleSkip(
      processInstanceData,
      () =>
        onShowMessage(
          'Skip operation',
          `The process ${processInstanceData.processName} was successfully skipped.`,
          TitleType.SUCCESS
        ),
      (errorMessage: string) =>
        onShowMessage(
          'Skip operation',
          `The process ${processInstanceData.processName} failed to skip. Message: ${errorMessage}`,
          TitleType.FAILURE
        )
    );
  };
  const onRetryClick = () => {
    handleRetry(
      processInstanceData,
      () =>
        onShowMessage(
          'Retry operation',
          `The process ${processInstanceData.processName} was successfully re-executed.`,
          TitleType.SUCCESS
        ),
      (errorMessage: string) =>
        onShowMessage(
          'Retry operation',
          `The process ${processInstanceData.processName} failed to re-execute. Message: ${errorMessage}`,
          TitleType.FAILURE
        )
    );
  };

  const onAbortClick = () => {
    handleAbort(
      processInstanceData,
      () =>
        onShowMessage(
          'Abort operation',
          `The process ${processInstanceData.processName} was successfully aborted.`,
          TitleType.SUCCESS
        ),
      (errorMessage: string) =>
        onShowMessage(
          'Abort operation',
          `Failed to abort process ${processInstanceData.processName}. Message: ${errorMessage}`,
          TitleType.FAILURE
        )
    );
  };

  const toggle = async _id => {
    const copyOfInitData = { ...initData };
    copyOfInitData.ProcessInstances.map(instance => {
      if (instance.id === processInstanceData.id) {
        if (instance.isOpen) {
          instance.isOpen = false;
        } else {
          instance.isOpen = true;
        }
      }
    });
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

  const onCheckBoxClick = () => {
    const copyOfInitData = { ...initData };
    let copyOfAbortedObject = { ...abortedObj };
    copyOfInitData.ProcessInstances.map(instanceData => {
      if (instanceData.id === processInstanceData.id) {
        if (instanceData.isChecked) {
          if (abortedObj[instanceData.id] !== undefined) {
            delete copyOfAbortedObject[instanceData.id];
            setSelectedNumber(selectedNumber > 0 && selectedNumber - 1);
          }
          instanceData.isChecked = false;
        } else {
          const tempObj = {};
          tempObj[instanceData.id] = instanceData;
          copyOfAbortedObject = { ...copyOfAbortedObject, ...tempObj };
          instanceData.isChecked = true;
          setSelectedNumber(selectedNumber + 1);
        }
      }
      if (instanceData.childDataList !== undefined) {
        instanceData.childDataList.map(child => {
          if (child.id === processInstanceData.id) {
            if (child.isChecked) {
              if (copyOfAbortedObject[child.id] !== undefined) {
                delete copyOfAbortedObject[child.id];
                setSelectedNumber(selectedNumber > 0 && selectedNumber - 1);
              }
              child.isChecked = false;
            } else {
              const tempObj = {};
              tempObj[child.id] = child;
              copyOfAbortedObject = { ...copyOfAbortedObject, ...tempObj };
              setSelectedNumber(selectedNumber + 1);
              child.isChecked = true;
            }
          }
        });
      }
    });
    lengthChecker(copyOfInitData);
    setInitData(copyOfInitData);
    setAbortedObj(copyOfAbortedObject);
  };
  const lengthChecker = copyOfData => {
    let totalLength = 0;
    let isCheckedLength = 0;
    copyOfData.ProcessInstances.map(instance => {
      if (
        instance.addons.includes('process-management') &&
        instance.serviceUrl !== null
      ) {
        totalLength += 1;
        if (instance.isChecked) {
          isCheckedLength += 1;
        }
      }

      if (instance.childDataList !== undefined) {
        instance.childDataList.map(child => {
          if (
            child.addons.includes('process-management') &&
            instance.serviceUrl !== null
          ) {
            totalLength += 1;
            if (child.isChecked) {
              isCheckedLength += 1;
            }
          }
        });
      }
    });
    if (isCheckedLength === totalLength) {
      setIsAllChecked(true);
    } else {
      setIsAllChecked(false);
    }
  };
  useEffect(() => {
    if (!loading && !loadingInitData && data !== undefined) {
      data.ProcessInstances.forEach((instance: any) => {
        instance.isChecked = false;
      });
      const copyOfInitData = { ...initData };
      copyOfInitData.ProcessInstances.map(instanceData => {
        if (instanceData.id === processInstanceData.id) {
          instanceData.childDataList = data.ProcessInstances;
        }
      });
      setInitData(copyOfInitData);
      setisLoaded(true);
    }
  }, [data]);

  const dropDownList = () => {
    if (
      processInstanceData.addons.includes('process-management') &&
      processInstanceData.serviceUrl !== null
    ) {
      if (processInstanceData.state === 'ERROR') {
        return [
          <DropdownItem key={1} onClick={onRetryClick}>
            Retry
          </DropdownItem>,
          <DropdownItem key={2} onClick={onSkipClick}>
            Skip
          </DropdownItem>,
          <DropdownItem key={4} onClick={onAbortClick}>
            Abort
          </DropdownItem>
        ];
      } else {
        return [
          <DropdownItem key={4} onClick={onAbortClick}>
            Abort
          </DropdownItem>
        ];
      }
    } else {
      return [];
    }
  };
  return (
    <React.Fragment>
      <ProcessListModal
        isModalOpen={isModalOpen}
        handleModalToggle={handleModalToggle}
        checkedArray={processInstanceData && [processInstanceData.state]}
        modalTitle={setTitle(titleType, modalTitle)}
        modalContent={modalContent}
      />
      <DataListItem
        aria-labelledby={'kie-datalist-item-' + processInstanceData.id}
        isExpanded={expanded.includes('kie-datalist-toggle')}
      >
        <DataListItemRow>
          {processInstanceData.parentProcessInstanceId === null && (
            <DataListToggle
              onClick={() => toggle('kie-datalist-toggle')}
              isExpanded={expanded.includes('kie-datalist-toggle')}
              id={'kie-datalist-toggle-' + processInstanceData.id}
              aria-controls="kie-datalist-expand"
            />
          )}
          {processInstanceData.addons.includes('process-management') &&
          processInstanceData.serviceUrl !== null ? (
            <DataListCheck
              aria-labelledby={'kie-datalist-item-' + processInstanceData.id}
              name="width-kie-datalist-item"
              checked={processInstanceData[isChecked]}
              onChange={() => {
                onCheckBoxClick();
              }}
            />
          ) : (
            <DisablePopup
              processInstanceData={processInstanceData}
              component={
                <DataListCheck
                  aria-labelledby={
                    'kie-datalist-item-' + processInstanceData.id
                  }
                  isDisabled={true}
                />
              }
            />
          )}
          <DataListItemCells
            dataListCells={[
              <DataListCell
                key={1}
                id={'kie-datalist-item-' + processInstanceData.id}
              >
                <Link
                  to={{
                    pathname: '/Process/' + processInstanceData.id,
                    state: { filters }
                  }}
                >
                  <div>
                    <strong>
                      <ProcessDescriptor
                        processInstanceData={processInstanceData}
                      />
                    </strong>
                  </div>
                </Link>
                <EndpointLink
                  serviceUrl={processInstanceData.serviceUrl}
                  isLinkShown={false}
                />
              </DataListCell>,
              <DataListCell key={4}>
                {processInstanceData.state === 'ERROR' ? (
                  <ErrorPopover
                    processInstanceData={processInstanceData}
                    onSkipClick={onSkipClick}
                    onRetryClick={onRetryClick}
                  />
                ) : (
                  stateIconCreator(processInstanceData.state)
                )}
              </DataListCell>,
              <DataListCell key={2}>
                {processInstanceData.start ? (
                  <Moment fromNow>
                    {new Date(`${processInstanceData.start}`)}
                  </Moment>
                ) : (
                  ''
                )}
              </DataListCell>,
              <DataListCell key={3}>
                {processInstanceData.lastUpdate ? (
                  <span>
                    {' '}
                    <HistoryIcon className="pf-u-mr-sm" /> Updated{' '}
                    <Moment fromNow>
                      {new Date(`${processInstanceData.lastUpdate}`)}
                    </Moment>
                  </span>
                ) : (
                  ''
                )}
              </DataListCell>
            ]}
          />
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
                toggle={
                  <KebabToggle
                    isDisabled={dropDownList().length === 0}
                    onToggle={onToggle}
                  />
                }
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
          </DataListAction>
        </DataListItemRow>
        <DataListContent
          aria-label="Primary Content Details"
          id={'kie-datalist-expand-' + processInstanceData.id}
          isHidden={!expanded.includes('kie-datalist-toggle')}
          className="kogito-management-console__embedded-list pf-m-compact"
        >
          {isLoaded &&
            !loading &&
            !loadingInitData &&
            !error &&
            initData.ProcessInstances.map((instance, idx) => {
              if (instance.id === processInstanceData.id) {
                if (instance.childDataList.length === 0) {
                  return (
                    <KogitoEmptyState
                      key={idx}
                      type={KogitoEmptyStateType.Info}
                      title="No child process instances"
                      body="This process has no related sub processes"
                    />
                  );
                } else {
                  return instance.childDataList.map((child, index) => {
                    return (
                      <ProcessListTableItems
                        id={index}
                        key={child.id}
                        processInstanceData={child}
                        filters={filters}
                        initData={initData}
                        setInitData={setInitData}
                        loadingInitData={loading}
                        abortedObj={abortedObj}
                        setAbortedObj={setAbortedObj}
                        setIsAllChecked={setIsAllChecked}
                        selectedNumber={selectedNumber}
                        setSelectedNumber={setSelectedNumber}
                      />
                    );
                  });
                }
              }
            })}
          {!isLoaded && !error && (
            <Bullseye>
              <KogitoSpinner spinnerText="Loading process instances..." />
            </Bullseye>
          )}
          {error && <ServerErrors error={error} />}
        </DataListContent>
      </DataListItem>
    </React.Fragment>
  );
};

export default ProcessListTableItems;
