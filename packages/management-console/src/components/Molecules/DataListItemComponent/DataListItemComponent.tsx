import Moment from 'react-moment';
import React, { useState, useEffect } from 'react';
import {
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
  KebabToggle
} from '@patternfly/react-core';
import { ServerErrors } from '@kogito-apps/common/src/components';
import { Link } from 'react-router-dom';
import SpinnerComponent from '../../Atoms/SpinnerComponent/SpinnerComponent';
import {
  useGetChildInstancesLazyQuery,
  ProcessInstance
} from '../../../graphql/types';
import EmptyStateComponent from '../../Atoms/EmptyStateComponent/EmptyStateComponent';
import { HistoryIcon } from '@patternfly/react-icons';
import ErrorPopover from '../../Atoms/ErrorPopoverComponent/ErrorPopoverComponent';
import ProcessBulkModalComponent from '../../Atoms/ProcessBulkModalComponent/ProcessBulkModalComponent';
import ProcessDescriptor from '../ProcessDescriptor/ProcessDescriptor';
import DisablePopup from '../DisablePopup/DisablePopup';
import {
  stateIconCreator,
  setTitle,
  handleSkip,
  handleRetry,
  handleAbort,
  isModalOpen,
  modalToggle
} from '../../../utils/Utils';
import EndpointLink from '../EndpointLink/EndpointLink';
interface IOwnProps {
  id: number;
  processInstanceData: ProcessInstance;
  checkedArray: string[];
  initData: any;
  setInitData: any;
  loadingInitData: boolean;
  abortedObj: any;
  setAbortedObj: any;
  setIsAllChecked: any;
  setSelectedNumber: (selectedNumber: number) => void;
  selectedNumber: number;
}

const DataListItemComponent: React.FC<IOwnProps> = ({
  processInstanceData,
  checkedArray,
  initData,
  setInitData,
  loadingInitData,
  abortedObj,
  setAbortedObj,
  setIsAllChecked,
  selectedNumber,
  setSelectedNumber
}) => {
  const [expanded, setexpanded] = useState([]);
  const [isOpen, setisOpen] = useState(false);
  const [isLoaded, setisLoaded] = useState(false);
  const [modalTitle, setModalTitle] = useState('');
  const [modalContent, setModalContent] = useState('');
  const [isSkipModalOpen, setIsSkipModalOpen] = useState(false);
  const [isRetryModalOpen, setIsRetryModalOpen] = useState(false);
  const [isAbortModalOpen, setIsAbortModalOpen] = useState(false);
  const [titleType, setTitleType] = useState('');
  const isChecked = 'isChecked';
  const [
    getChildInstances,
    { loading, data, error }
  ] = useGetChildInstancesLazyQuery({
    fetchPolicy: 'network-only'
  });
  const currentPage = { prev: location.pathname };
  window.localStorage.setItem('state', JSON.stringify(currentPage));
  const handleSkipModalToggle = () => {
    setIsSkipModalOpen(!isSkipModalOpen);
  };

  const handleRetryModalToggle = () => {
    setIsRetryModalOpen(!isRetryModalOpen);
  };

  const handleAbortModalToggle = () => {
    setIsAbortModalOpen(!isAbortModalOpen);
  };

  const onSelect = event => {
    setisOpen(isOpen ? false : true);
  };
  const onToggle = _isOpen => {
    setisOpen(_isOpen);
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
    if (data !== undefined && !loading && !loadingInitData) {
      data.ProcessInstances.map((instance: any) => {
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
          <DropdownItem
            key={1}
            onClick={() =>
              handleRetry(
                processInstanceData,
                setModalTitle,
                setTitleType,
                setModalContent,
                handleRetryModalToggle
              )
            }
          >
            Retry
          </DropdownItem>,
          <DropdownItem
            key={2}
            onClick={() =>
              handleSkip(
                processInstanceData,
                setModalTitle,
                setTitleType,
                setModalContent,
                handleSkipModalToggle
              )
            }
          >
            Skip
          </DropdownItem>,
          <DropdownItem
            key={4}
            onClick={() =>
              handleAbort(
                processInstanceData,
                setModalTitle,
                setTitleType,
                setModalContent,
                handleAbortModalToggle
              )
            }
          >
            Abort
          </DropdownItem>
        ];
      } else {
        return [
          <DropdownItem
            key={4}
            onClick={() =>
              handleAbort(
                processInstanceData,
                setModalTitle,
                setTitleType,
                setModalContent,
                handleAbortModalToggle
              )
            }
          >
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
      <ProcessBulkModalComponent
        isModalOpen={isAbortModalOpen}
        handleModalToggle={handleAbortModalToggle}
        checkedArray={checkedArray}
        modalTitle={setTitle(titleType, modalTitle)}
        isSingleAbort={true}
        abortedMessageObj={{
          [processInstanceData.id]: processInstanceData
        }}
        completedMessageObj={{}}
        isAbortModalOpen={isAbortModalOpen}
      />
      <ProcessBulkModalComponent
        isModalOpen={isModalOpen(modalTitle, isSkipModalOpen, isRetryModalOpen)}
        handleModalToggle={modalToggle(
          modalTitle,
          handleSkipModalToggle,
          handleRetryModalToggle
        )}
        checkedArray={checkedArray}
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
                <Link to={'/Process/' + processInstanceData.id}>
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
                    setModalTitle={setModalTitle}
                    setTitleType={setTitleType}
                    setModalContent={setModalContent}
                    handleRetryModalToggle={handleRetryModalToggle}
                    handleSkipModalToggle={handleSkipModalToggle}
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
                    <EmptyStateComponent
                      key={idx}
                      iconType="infoCircleIcon"
                      title="No child process instances"
                      body="This process has no related sub processes"
                    />
                  );
                } else {
                  return instance.childDataList.map((child, index) => {
                    return (
                      <DataListItemComponent
                        id={index}
                        key={child.id}
                        processInstanceData={child}
                        checkedArray={checkedArray}
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
              <SpinnerComponent spinnerText="Loading process instances..." />
            </Bullseye>
          )}
          {error && <ServerErrors error={error} />}
        </DataListContent>
      </DataListItem>
    </React.Fragment>
  );
};

export default DataListItemComponent;
