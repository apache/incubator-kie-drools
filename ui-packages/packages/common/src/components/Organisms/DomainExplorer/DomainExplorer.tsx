import React, { useState, useEffect } from 'react';
import {
  DataToolbar,
  DataToolbarContent,
  DataToolbarToggleGroup,
  DataToolbarGroup,
  Card,
  Bullseye,
  DataToolbarItem,
  DataToolbarFilter
} from '@patternfly/react-core';
import { FilterIcon } from '@patternfly/react-icons';
import DomainExplorerFilterOptions from '../../Molecules/DomainExplorerFilterOptions/DomainExplorerFilterOptions';
import DomainExplorerManageColumns from '../../Molecules/DomainExplorerManageColumns/DomainExplorerManageColumns';
import DomainExplorerTable from '../../Molecules/DomainExplorerTable/DomainExplorerTable';
import KogitoSpinner from '../../Atoms/KogitoSpinner/KogitoSpinner';
import LoadMore from '../../Atoms/LoadMore/LoadMore';
import ServerErrors from '../../Molecules/ServerErrors/ServerErrors';
import '../../styles.css';

import { deleteKey, clearEmpties } from '../../../utils/Utils';
import { query } from 'gql-query-builder';
import { GraphQL } from '../../../graphql/types';
import useGetQueryTypesQuery = GraphQL.useGetQueryTypesQuery;
import useGetQueryFieldsQuery = GraphQL.useGetQueryFieldsQuery;
import useGetColumnPickerAttributesQuery = GraphQL.useGetColumnPickerAttributesQuery;
import useGetInputFieldsFromQueryQuery = GraphQL.useGetInputFieldsFromQueryQuery;
interface IOwnProps {
  domainName: string;
  rememberedParams: object[];
  rememberedSelections: string[];
  metaData: object;
  rememberedFilters: object;
  rememberedChips: string[];
  defaultChip: string[];
  defaultFilter: object;
}

const DomainExplorer: React.FC<IOwnProps> = ({
  domainName,
  rememberedParams,
  rememberedSelections,
  rememberedFilters,
  rememberedChips,
  metaData,
  defaultChip,
  defaultFilter
}) => {
  const [columnPickerType, setColumnPickerType] = useState('');
  const [columnFilters, setColumnFilters] = useState({});
  const [tableLoading, setTableLoading] = useState(true);
  const [displayTable, setDisplayTable] = useState(false);
  const [displayEmptyState, setDisplayEmptyState] = useState(false);
  const [selected, setSelected] = useState([]);
  const [limit, setLimit] = useState(10);
  const [offset, setOffset] = useState(0);
  const [pageSize, setPageSize] = useState(10);
  const [isLoadingMore, setIsLoadingMore] = useState(false);
  const [rows, setRows] = useState([]);
  const [enableCache, setEnableCache] = useState(false);
  const [parameters, setParameters] = useState([metaData]);
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [runQuery, setRunQuery] = useState(false);
  const [filterChips, setFilterChips] = useState([...defaultChip]);
  const [finalFilters, setFinalFilters] = useState<any>(defaultFilter);
  const [filterError, setFilterError] = useState('');
  const [reset, setReset] = useState(false);
  const [enableRefresh, setEnableRefresh] = useState(true);
  const [loadMoreClicked, setLoadMoreClicked] = useState(false);
  useEffect(() => {
    /* istanbul ignore else */
    if (domainName) {
      setColumnPickerType(domainName);
    }
  }, []);

  useEffect(() => {
    /* istanbul ignore else */
    if (isLoadingMore) {
      setRunQuery(true);
    }
  }, [isLoadingMore]);

  useEffect(() => {
    /* istanbul ignore else */
    if (
      (rememberedParams.length === 0 && parameters.length !== 1) ||
      rememberedParams.length > 0
    ) {
      setRunQuery(true);
    }
  }, [parameters.length > 1]);

  const getQuery = useGetQueryFieldsQuery();

  const getQueryTypes = useGetQueryTypesQuery();
  const getPicker = useGetColumnPickerAttributesQuery({
    variables: { columnPickerType: domainName }
  });
  const onAddColumnFilters = _columnFilter => {
    setColumnFilters(_columnFilter);
    setLimit(_columnFilter.length);
  };
  const domainArg =
    !getQuery.loading &&
    getQuery.data &&
    getQuery.data.__type.fields.find(item => {
      if (item.name === domainName) {
        return item;
      }
    });

  const argument = domainArg && domainArg.args[0].type.name;
  const getSchema = useGetInputFieldsFromQueryQuery({
    variables: {
      currentQuery: argument
    }
  });

  let data = [];
  const tempArray = [];
  let selections = [];
  let defaultParams = [];
  !getPicker.loading &&
    getPicker.data &&
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
      /* istanbul ignore else */
      if (item.type.kind !== 'OBJECT') {
        const tempObj = {};
        selections.push(key + '/' + item.name);
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
      setFinalFilters(rememberedFilters);
      setFilterChips(rememberedChips);
    } else {
      setParameters(prev => [...defaultParams, ...prev]);
      setSelected(selections);
    }
  }, [columnPickerType, selections.length > 0]);

  const onDeleteChip = (type = '', id = '') => {
    if (type) {
      setFilterChips(prev => prev.filter(item => item !== id));
      const chipText = id.split(':');
      let removeString = chipText[0].split('/');
      removeString = removeString.map(stringEle => stringEle.trim());
      let tempObj = finalFilters;
      tempObj = deleteKey(tempObj, removeString);
      const FinalObj = clearEmpties(tempObj);
      setFinalFilters(FinalObj);
      setRunQuery(true);
    } else {
      setOffset(0);
      setFinalFilters(defaultFilter);
      setFilterChips(defaultChip);
      setReset(true);
    }
  };

  const domainQuery = query({
    operation: domainName,
    variables: {
      pagination: {
        value: { offset, limit: pageSize },
        type: 'Pagination'
      },
      where: { value: finalFilters, type: argument }
    },
    fields: parameters
  });

  const renderToolbar = () => {
    return (
      <DataToolbar
        id="data-toolbar-with-chip-groups"
        className="pf-m-toggle-group-container"
        collapseListedFiltersBreakpoint="md"
        clearAllFilters={onDeleteChip}
        clearFiltersButtonText="Reset to default"
      >
        <DataToolbarContent>
          {!getPicker.loading && (
            <>
              <DataToolbarToggleGroup
                toggleIcon={<FilterIcon />}
                breakpoint="xl"
              >
                {!getQuery.loading && !getQueryTypes.loading && (
                  <DataToolbarFilter
                    categoryName="Filters"
                    chips={filterChips}
                    deleteChip={onDeleteChip}
                  >
                    <DataToolbarItem>
                      <DomainExplorerFilterOptions
                        enableCache={enableCache}
                        filterChips={filterChips}
                        finalFilters={finalFilters}
                        getQueryTypes={getQueryTypes}
                        getSchema={getSchema}
                        loadMoreClicked={loadMoreClicked}
                        parameters={parameters}
                        Query={domainQuery}
                        reset={reset}
                        runQuery={runQuery}
                        setColumnFilters={onAddColumnFilters}
                        setDisplayTable={setDisplayTable}
                        setDisplayEmptyState={setDisplayEmptyState}
                        setFilterError={setFilterError}
                        setFilterChips={setFilterChips}
                        setFinalFilters={setFinalFilters}
                        setLoadMoreClicked={setLoadMoreClicked}
                        setOffset={setOffset}
                        setRunQuery={setRunQuery}
                        setReset={setReset}
                        setTableLoading={setTableLoading}
                        setEnableRefresh={setEnableRefresh}
                        setIsLoadingMore={setIsLoadingMore}
                      />
                    </DataToolbarItem>
                  </DataToolbarFilter>
                )}
              </DataToolbarToggleGroup>
              <DataToolbarGroup>
                <DataToolbarItem>
                  <DomainExplorerManageColumns
                    columnPickerType={columnPickerType}
                    getQueryTypes={getQueryTypes}
                    setParameters={setParameters}
                    selected={selected}
                    setSelected={setSelected}
                    data={data}
                    getPicker={getPicker}
                    setOffsetVal={setOffset}
                    setPageSize={setPageSize}
                    metaData={metaData}
                    setIsModalOpen={setIsModalOpen}
                    isModalOpen={isModalOpen}
                    setRunQuery={setRunQuery}
                    enableRefresh={enableRefresh}
                    setEnableRefresh={setEnableRefresh}
                  />
                </DataToolbarItem>
              </DataToolbarGroup>
            </>
          )}
        </DataToolbarContent>
      </DataToolbar>
    );
  };

  if (!getQuery.loading && getQuery.error) {
    return <ServerErrors error={getQuery.error} variant="large" />;
  }

  if (!getQueryTypes.loading && getQueryTypes.error) {
    return <ServerErrors error={getQueryTypes.error} variant="large" />;
  }

  if (!getPicker.loading && getPicker.error) {
    return <ServerErrors error={getPicker.error} variant="large" />;
  }

  const onGetMoreInstances = (initVal, _pageSize) => {
    setOffset(initVal);
    setPageSize(_pageSize);
    setIsLoadingMore(true);
  };
  const handleRetry = () => {
    setIsModalOpen(true);
  };

  return (
    <>
      {renderToolbar()}

      {!tableLoading || isLoadingMore ? (
        <div className="kogito-common--domain-explorer__table-OverFlow">
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
            handleRetry={handleRetry}
            filterError={filterError}
            finalFilters={finalFilters}
            filterChips={filterChips}
            onDeleteChip={onDeleteChip}
          />
          {displayTable &&
            !displayEmptyState &&
            !filterError &&
            filterChips.length > 0 &&
            (limit === pageSize || isLoadingMore) && (
              <LoadMore
                offset={offset}
                setOffset={setOffset}
                getMoreItems={onGetMoreInstances}
                pageSize={pageSize}
                isLoadingMore={isLoadingMore}
                setLoadMoreClicked={setLoadMoreClicked}
              />
            )}
        </div>
      ) : (
        <Card>
          <Bullseye>
            <KogitoSpinner spinnerText="Loading domain data..." />
          </Bullseye>
        </Card>
      )}
    </>
  );
};

export default DomainExplorer;
