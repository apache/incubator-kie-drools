import React, { useState, useEffect } from 'react';
import {
  DataToolbar,
  DataToolbarContent,
  DataToolbarToggleGroup,
  DataToolbarGroup,
  PageSection,
  Breadcrumb,
  BreadcrumbItem,
  Card,
  Bullseye
} from '@patternfly/react-core';
import { ServerErrors } from '@kogito-apps/common/src/components';
import { FilterIcon } from '@patternfly/react-icons';
import { Link } from 'react-router-dom';
import { Redirect } from 'react-router';
import './DomainExplorerDashboard.css';
import DomainExplorerColumnPicker from '../../Organisms/DomainExplorerColumnPicker/DomainExplorerColumnPicker';
import DomainExplorerTable from '../../Organisms/DomainExplorerTable/DomainExplorerTable';
import PageTitleComponent from '../../Molecules/PageTitleComponent/PageTitleComponent';
import SpinnerComponent from '../../Atoms/SpinnerComponent/SpinnerComponent';
import LoadMoreComponent from '../../Atoms/LoadMoreComponent/LoadMoreComponent';

import {
  useGetQueryTypesQuery,
  useGetQueryFieldsQuery,
  useGetColumnPickerAttributesQuery
} from '../../../graphql/types';

export interface IOwnProps {
  domains: any;
}

const DomainExplorerDashboard = props => {
  const rememberedParams =
    (props.location.state && props.location.state.parameters) || [];
  const rememberedSelections =
    (props.location.state && props.location.state.selected) || [];
  const [defaultPageSize] = useState(10);
  const domainName = props.match.params.domainName;
  let BreadCrumb = props.location.pathname.split('/');
  BreadCrumb = BreadCrumb.filter(item => {
    if (item !== '') {
      return item;
    }
  });
  const [pathName] = BreadCrumb.slice(-1);
  const [columnPickerType, setColumnPickerType] = useState('');
  const [columnFilters, setColumnFilters] = useState({});
  const [tableLoading, setTableLoading] = useState(true);
  const [displayTable, setDisplayTable] = useState(false);
  const [displayEmptyState, setDisplayEmptyState] = useState(false);
  const [selected, setSelected] = useState([]);
  const [limit, setLimit] = useState(defaultPageSize);
  const [offset, setOffset] = useState(0);
  const [pageSize, setPageSize] = useState(defaultPageSize);
  const [isLoadingMore, setIsLoadingMore] = useState(false);
  const [rows, setRows] = useState([]);
  const [error, setError] = useState();
  const [enableCache, setEnableCache] = useState(false);
  const [parameters, setParameters] = useState([
    {
      metadata: [
        {
          processInstances: [
            'id',
            'processName',
            'state',
            'start',
            'lastUpdate',
            'businessKey'
          ]
        }
      ]
    }
  ]);

  useEffect(() => {
    if (domainName) {
      setColumnPickerType(domainName);
    }
  }, []);

  const getQuery = useGetQueryFieldsQuery();
  const getQueryTypes = useGetQueryTypesQuery();
  const getPicker = useGetColumnPickerAttributesQuery({
    variables: { columnPickerType: domainName }
  });

  const onAddColumnFilters = _columnFilter => {
    setColumnFilters(_columnFilter);
    setLimit(_columnFilter.length);
  };

  let data = [];
  const tempArray = [];
  let selections = [];
  let defaultParams = [];
  !getPicker.loading &&
    getPicker.data.__type &&
    getPicker.data.__type.fields.filter(i => {
      if (i.type.kind === 'SCALAR') {
        tempArray.push(i);
      } else {
        data.push(i);
      }
    });
  data = tempArray.concat(data);
  const fields: any = [];
  data.filter(field => {
    if (field.type.fields !== null) {
      const obj = {};
      obj[`${field.name}`] = field.type.fields;
      fields.push(obj);
    }
  });

  fields.map(obj => {
    let value: any = Object.values(obj);
    const key = Object.keys(obj);
    value = value.flat();
    value.filter(item => {
      if (item.type.kind !== 'OBJECT') {
        const tempObj = {};
        selections.push(item.name + key);
        tempObj[`${key}`] = [item.name];
        defaultParams.push(tempObj);
      }
    });
  });

  selections = selections.slice(0, 5);
  defaultParams = defaultParams.slice(0, 5);

  useEffect(() => {
    if (rememberedParams.length > 0) {
      setEnableCache(true);
      setParameters(rememberedParams);
      setSelected(rememberedSelections);
    } else {
      setParameters(prev => [...defaultParams, ...prev]);
      setSelected(selections);
    }
  }, [columnPickerType, selections.length > 0]);

  const renderToolbar = () => {
    return (
      <DataToolbar
        id="data-toolbar-with-chip-groups"
        className="pf-m-toggle-group-container"
        collapseListedFiltersBreakpoint="md"
      >
        <DataToolbarContent>
          <DataToolbarToggleGroup toggleIcon={<FilterIcon />} breakpoint="md">
            <DataToolbarGroup>
              {!getPicker.loading && (
                <DomainExplorerColumnPicker
                  columnPickerType={columnPickerType}
                  setColumnFilters={onAddColumnFilters}
                  setTableLoading={setTableLoading}
                  getQueryTypes={getQueryTypes}
                  setDisplayTable={setDisplayTable}
                  parameters={parameters}
                  setParameters={setParameters}
                  selected={selected}
                  setSelected={setSelected}
                  data={data}
                  getPicker={getPicker}
                  setError={setError}
                  setDisplayEmptyState={setDisplayEmptyState}
                  rememberedParams={rememberedParams}
                  enableCache={enableCache}
                  setEnableCache={setEnableCache}
                  pageSize={pageSize}
                  offsetVal={offset}
                  setOffsetVal={setOffset}
                  setPageSize={setPageSize}
                  setIsLoadingMore={setIsLoadingMore}
                  isLoadingMore={isLoadingMore}
                />
              )}
            </DataToolbarGroup>
          </DataToolbarToggleGroup>
        </DataToolbarContent>
      </DataToolbar>
    );
  };

  if (!getQuery.loading && getQuery.error) {
    return <ServerErrors error={getQuery.error} />;
  }

  if (!getQueryTypes.loading && getQueryTypes.error) {
    return <ServerErrors error={getQueryTypes.error} />;
  }

  if (!getPicker.loading && getPicker.error) {
    return <ServerErrors error={getPicker.error} />;
  }

  const onGetMoreInstances = (initVal, _pageSize) => {
    setOffset(initVal);
    setPageSize(_pageSize);
    setIsLoadingMore(true);
  };

  return (
    <>
      {!getQuery.loading &&
        !props.domains.includes(domainName) &&
        !props.domains.includes(pathName) && (
          <Redirect
            to={{
              pathname: '/NoData',
              state: {
                prev: location.pathname,
                title: 'Domain not found',
                description: `Domain with the name ${domainName} not found`,
                buttonText: 'Go to domain explorer'
              }
            }}
          />
        )}
      <PageSection variant="light">
        <PageTitleComponent title="Domain Explorer" />
        <Breadcrumb>
          <BreadcrumbItem>
            <Link to={'/'}>Home</Link>
          </BreadcrumbItem>
          {BreadCrumb.map((item, index) => {
            if (index === BreadCrumb.length - 1) {
              return (
                <BreadcrumbItem isActive key={index}>
                  {item}
                </BreadcrumbItem>
              );
            } else {
              return (
                <BreadcrumbItem key={index}>
                  <Link to={'/DomainExplorer'}>
                    {item.replace(/([A-Z])/g, ' $1').trim()}
                  </Link>
                </BreadcrumbItem>
              );
            }
          })}
        </Breadcrumb>
      </PageSection>
      {!error ? (
        <PageSection>
          {renderToolbar()}

          {!tableLoading || isLoadingMore ? (
            <div className="kogito-management-console--domain-explorer__table-OverFlow">
              <DomainExplorerTable
                columnFilters={columnFilters}
                tableLoading={tableLoading}
                displayTable={displayTable}
                displayEmptyState={displayEmptyState}
                parameters={parameters}
                selected={selected}
                offset={offset}
                setRows={setRows}
                rows={rows}
                isLoadingMore={isLoadingMore}
              />
              {!displayEmptyState && (limit === pageSize || isLoadingMore) && (
                <LoadMoreComponent
                  offset={offset}
                  setOffset={setOffset}
                  getMoreItems={onGetMoreInstances}
                  pageSize={pageSize}
                  isLoadingMore={isLoadingMore}
                />
              )}
            </div>
          ) : (
            <Card>
              <Bullseye>
                <SpinnerComponent spinnerText="Loading domain data..." />
              </Bullseye>
            </Card>
          )}
        </PageSection>
      ) : (
        <ServerErrors error={error} />
      )}
    </>
  );
};

export default React.memo(DomainExplorerDashboard);
