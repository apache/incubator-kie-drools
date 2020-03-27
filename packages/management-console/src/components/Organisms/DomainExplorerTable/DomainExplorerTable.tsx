import React, { useState, useEffect } from 'react';
import {
  Table,
  TableHeader,
  TableBody,
  TableVariant
} from '@patternfly/react-table';
import {
  Title,
  EmptyState,
  EmptyStateIcon,
  EmptyStateBody,
  Bullseye,
  Card,
  CardBody
} from '@patternfly/react-core';
import {
  EllipsisVIcon,
  ExternalLinkAltIcon,
  InProgressIcon,
  CheckCircleIcon,
  ExclamationCircleIcon,
  SyncIcon,
  BanIcon,
  OnRunningIcon,
  SearchIcon,
  ErrorCircleOIcon,
  PausedIcon,
  HistoryIcon,
  FilterIcon
} from "@patternfly/react-icons";
import Moment from 'react-moment';
import {Link} from 'react-router-dom';
import {ProcessInstanceState} from '../../../graphql/types';
import './DomainExplorerTable.css';
import SpinnerComponent from '../../Atoms/SpinnerComponent/SpinnerComponent';
import ProcessDescriptor from '../../Molecules/ProcessDescriptor/ProcessDescriptor';

const DomainExplorerTable = ({ columnFilters, tableLoading, displayTable }) => {
  const [columns, setColumns] = useState([]);
  const [rows, setRows] = useState([]);

  const stateIcon = (state) => {
    switch (state) {
      case ProcessInstanceState.Active:
        return (
          <>
            <OnRunningIcon className="pf-u-mr-sm" />
            Active
          </>
        );
      case ProcessInstanceState.Completed:
        return (
          <>
            <CheckCircleIcon
              className="pf-u-mr-sm"
              color="var(--pf-global--success-color--100)"
            />
            Completed
          </>
        );
      case ProcessInstanceState.Aborted:
        return (
          <>
            <BanIcon className="pf-u-mr-sm" />
            Aborted
          </>
        );
      case ProcessInstanceState.Suspended:
        return (
          <>
            <PausedIcon className="pf-u-mr-sm" />
            Suspended
          </>
        );
      case ProcessInstanceState.Pending:
        return (
          <>
            <PausedIcon className="pf-u-mr-sm" />
            Pending
            </>
        );
      case ProcessInstanceState.Error:
        return (
          <>
            <ErrorCircleOIcon
              className="pf-u-mr-sm"
              color="var(--pf-global--danger-color--100)"
            />
            Error
          </>
        );
    }
  }

  const getKeys = object => {
    const iter = (data, k = '') => {
      // tslint:disable-next-line: forin
      for (const i in data) {
        const rest = k.length ? ' / ' + i : i;
        if (data[i] === null) {
          !tempKeys.includes(k + rest) && tempKeys.push(k + rest);
          if (rest.hasOwnProperty) {
            tempValue.push(data[i]);
          }
        }
        if (typeof data[i] === 'object') {
          if (!Array.isArray(data[i])) {
            iter(data[i], k + rest);
          }
        } else {
          if (rest !== '__typename' && !rest.match('/ __typename')) {
            !tempKeys.includes(k + rest) && tempKeys.push(k + rest);
            if (rest.hasOwnProperty) {
              tempValue.push(data[i].toString());
            }
          }
        }
      }
    };
    const tempKeys = [];
    let tempValue = [];
    iter(object);
    tempValue = tempValue.filter(value => value !== null)
    return { tempKeys, tempValue };
  };



  const getChildKeys = object => {
    const iter = (data, k = '') => {
      // tslint:disable-next-line: forin
      for (const i in data) {
        const rest = k.length ? ' / ' + i : i;
        if (data[i] === null) {
          !tempKeys.includes(k + rest) && tempKeys.push(k + rest);
          if (rest.hasOwnProperty) {
            tempValue.push(data[i]);
          }
        }
        if (typeof data[i] === 'object') {
          if (!Array.isArray(data[i])) {
            iter(data[i], k + rest);
          }
        } else {
          if (rest !== '__typename' && !rest.match('/ __typename')) {
            !tempKeys.includes(k + rest) && tempKeys.push(k + rest);
            if (rest.hasOwnProperty) {
              if(rest === 'processName') {
                const tempObj = {
                  id: data.id,
                  processName: data.processName,
                  businessKey: data.businessKey
                }
                const ele = {
                  title: (
                    <>
                     <Link to={'/ProcessInstances/' + tempObj.id}>
                    <strong>
                      <ProcessDescriptor processInstanceData={tempObj}/>
                      </strong>
                      </Link>
                    </>
                  )
                }
                tempValue.push(ele)
              }
              else if (rest === 'start') {
                const ele = {
                  title: (
                      <Moment fromNow>{data[i].toString()}</Moment>
                  )
                }
                tempValue.push(ele)
              } else if (rest === 'state') {
                const ele = {
                  title:
                    stateIcon(data[i].toString())
                }
                tempValue.push(ele)
              } else if (rest === 'lastUpdate') {
                const ele = {
                  title: (
                    <>
                      <HistoryIcon className="pf-u-mr-sm" /> Updated{' '}
                      <Moment fromNow>{data[i].toString()}</Moment>
                    </>
                  )
                }
                tempValue.push(ele)
              } 
            }
          }
        }
      }
    };
    const tempKeys = [];
    let tempValue = [];
    iter(object);
    tempValue = tempValue.filter(value => value !== null)
    return { tempKeys, tempValue };
  };
  const firstKey = Object.keys(columnFilters)[0];
  const tableContent = columnFilters[firstKey];

  const parentkeys = [];
  let values = [];
  let parentIndex = 0;

  const initLoad = () => {
    if (tableContent) {
      tableContent.map(item => {
        let metaArray = [];
        const metaKeys = [];
        const metaValues = [];
        metaArray = item.metadata.processInstances;
        const tempParents = getKeys(item);
        parentkeys.push(tempParents.tempKeys);
        values.push({
          isOpen: false,
          cells: tempParents.tempValue,
          rowKey: Math.random().toString()
        });
        metaArray.map(data => {
          const tempMeta = getChildKeys(data);
          metaKeys.push(tempMeta.tempKeys);
          metaValues.push({
            cells: tempMeta.tempValue,
            rowKey: Math.random().toString()
          });
        });
        const finalMetaKeys = ['Process name', 'State', 'Start', 'Last update'];
        const innerTable = [
          {
            parent: parentIndex,
            rowKey: Math.random().toString(),
            cells: [
              {
                title: (
                  <Table
                    aria-label="Process Instances"
                    variant={TableVariant.compact}
                    cells={finalMetaKeys}
                    rows={metaValues}
                    className="kogito-management-console__embedded-table"
                  >
                    <TableHeader />
                    <TableBody />
                  </Table>
                )
              }
            ]
          }
        ];
        values = values.concat(innerTable);
        parentIndex = parentIndex + 2;
      });
      const rowObject: any = {};
      if (tableLoading) {
        rowObject.cells = [
          {
            props: { colSpan: 8 },
            title: (
              <Bullseye>
                <SpinnerComponent spinnerText="Loading domain explorer" />
              </Bullseye>
            )
          }
        ];
        values.push(rowObject);
      }
    }
    const finalKeys = parentkeys[0];
    finalKeys && setColumns([...finalKeys]);
    setRows([...values]); 

  };

  useEffect(() => {
    initLoad();
  }, [tableContent]);


  const onRowSelect = (event, isSelected, rowId) => {
    return null;
  };

  const onCollapse = (event, rowKey, isOpen) => {
    rows[rowKey].isOpen = isOpen;
    setRows([...rows]);
  };

  return (
    <React.Fragment>
      {displayTable && (
        <Table
          cells={columns}
          rows={rows}
          aria-label="Domain Explorer Table"
          className="kogito-management-console--domain-explorer__table"
          onCollapse={onCollapse}
        >
          <TableHeader />
          <TableBody rowKey="rowKey" />
        </Table>
      )}
      {!displayTable  && (
        <Card component={'div'}>
          <CardBody>
            <Bullseye>
              <EmptyState>
                <EmptyStateIcon icon={FilterIcon} />
                <Title headingLevel="h5" size="lg">
                  No columns selected
                </Title>
                <EmptyStateBody>
                  Select columns from the dropdown to see content
                </EmptyStateBody>
              </EmptyState>
            </Bullseye>
          </CardBody>
        </Card>
      )}
    </React.Fragment>
  );
};

export default DomainExplorerTable;
