import React, { useState, useEffect } from 'react';
import {
  Card,
  Bullseye,
  ToolbarGroup,
  ToolbarContent,
  ToolbarToggleGroup,
  Toolbar,
  ToolbarItem,
  ToolbarFilter,
  Divider
} from '@patternfly/react-core';
import { FilterIcon } from '@patternfly/react-icons';
import { useApolloClient } from 'react-apollo';
import DomainExplorerFilterOptions from '../../Molecules/DomainExplorerFilterOptions/DomainExplorerFilterOptions';
import DomainExplorerManageColumns from '../../Molecules/DomainExplorerManageColumns/DomainExplorerManageColumns';
import DomainExplorerTable from '../../Molecules/DomainExplorerTable/DomainExplorerTable';
import KogitoSpinner from '../../Atoms/KogitoSpinner/KogitoSpinner';
import LoadMore from '../../Atoms/LoadMore/LoadMore';
import ServerErrors from '../../Molecules/ServerErrors/ServerErrors';
import '../../styles.css';
import gql from 'graphql-tag';
import {
  validateResponse,
  deleteKey,
  clearEmpties
} from '../../../utils/Utils';
import { query } from 'gql-query-builder';
import { GraphQL } from '../../../graphql/types';
import useGetQueryTypesQuery = GraphQL.useGetQueryTypesQuery;
import useGetQueryFieldsQuery = GraphQL.useGetQueryFieldsQuery;
import useGetColumnPickerAttributesQuery = GraphQL.useGetColumnPickerAttributesQuery;
import useGetInputFieldsFromQueryQuery = GraphQL.useGetInputFieldsFromQueryQuery;
import { OUIAProps, componentOuiaProps } from '@kogito-apps/ouia-tools';
interface IOwnProps {
  domainName: string;
  rememberedParams: Record<string, unknown>[];
  rememberedSelections: string[];
  metaData: Record<string, unknown>;
  rememberedFilters: Record<string, unknown>;
  rememberedChips: string[];
  defaultChip: string[];
  defaultFilter: Record<string, unknown>;
}

const DomainExplorer: React.FC<IOwnProps & OUIAProps> = ({
  domainName,
  rememberedParams,
  rememberedSelections,
  rememberedFilters,
  rememberedChips,
  metaData,
  defaultChip,
  defaultFilter,
  ouiaId,
  ouiaSafe
}) => {
  const client = useApolloClient();
  const [columnPickerType, setColumnPickerType] = useState('');
  const [columnFilters, setColumnFilters] = useState([]);
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
  const [orderByObj, setOrderByObj] = useState({});
  const [sortBy, setSortBy] = useState({});
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
  const onAddColumnFilters = (_columnFilter) => {
    setColumnFilters(_columnFilter);
    setLimit(_columnFilter.length);
  };
  const domainArg =
    !getQuery.loading &&
    getQuery.data &&
    getQuery.data.__type.fields.find((item) => {
      if (item.name === domainName) {
        return item;
      }
    });
  let filterArgument;
  let orderByArgument;
  let paginationArgument;
  if (domainArg) {
    filterArgument = domainArg.args[0].type.name;
    orderByArgument = domainArg.args[1].type.name;
    paginationArgument = domainArg.args[2].type.name;
  }

  const getSchema = useGetInputFieldsFromQueryQuery({
    variables: {
      currentQuery: filterArgument
    }
  });

  let data = [];
  const tempArray = [];
  let selections = [];
  let defaultParams = [];
  !getPicker.loading &&
    getPicker.data &&
    getPicker.data.__type &&
    getPicker.data.__type.fields.filter((i) => {
      if (i.type.kind === 'SCALAR') {
        tempArray.push(i);
      } else {
        data.push(i);
      }
    });
  data = tempArray.concat(data);
  const fields: any = [];
  data.filter((field) => {
    if (field.type.fields !== null) {
      const obj = {};
      obj[`${field.name}`] = field.type.fields;
      fields.push(obj);
    }
  });
  fields.map((obj) => {
    let value: any = Object.values(obj);
    const key = Object.keys(obj);
    value = value.flat();
    value.filter((item) => {
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
      setParameters((prev) => [...defaultParams, ...prev]);
      setSelected(selections);
    }
  }, [columnPickerType, selections.length > 0]);

  useEffect(() => {
    if (filterChips.length === 0) {
      setDisplayTable(false);
    }
  }, [filterChips]);

  const onDeleteChip = (type = '', id = '') => {
    if (type) {
      setFilterChips((prev) => prev.filter((item) => item !== id));
      const chipText = id.split(':');
      let removeString = chipText[0].split('/');
      removeString = removeString.map((stringEle) => stringEle.trim());
      let tempObj = finalFilters;
      tempObj = deleteKey(tempObj, removeString);
      const FinalObj = clearEmpties(tempObj);
      setFinalFilters(FinalObj);
      setRunQuery(true);
    } else {
      setOffset(0);
      setFinalFilters({ ...defaultFilter });
      setFilterChips(defaultChip);
      setReset(true);
    }
  };

  const domainQuery = query({
    operation: domainName,
    variables: {
      pagination: {
        value: { offset, limit: pageSize },
        type: paginationArgument
      },
      where: { value: finalFilters, type: filterArgument },
      orderBy: { value: orderByObj, type: orderByArgument }
    },
    fields: parameters
  });

  async function generateFilterQuery() {
    setTableLoading(true);
    setEnableRefresh(true);
    // parameters(selected columns) length must always above 1 and filters length must be above zero else empty state is displayed
    if (
      parameters.length > 1 &&
      finalFilters &&
      Object.keys(finalFilters).length > 0
    ) {
      try {
        const response = await client.query({
          query: gql`
            ${domainQuery.query}
          `,
          variables: domainQuery.variables,
          fetchPolicy: enableCache ? 'cache-first' : 'network-only'
        });
        const firstKey = Object.keys(response.data)[0];
        if (
          Object.keys(response.data).length === 1 &&
          response.data[firstKey].length > 0
        ) {
          setFilterError('');
          const resp = response.data;
          const respKeys = Object.keys(resp)[0];
          const tableContent = resp[respKeys];
          const finalResp = [];
          tableContent.map((content) => {
            const finalObject = validateResponse(content, parameters);
            finalResp.push(finalObject);
          });
          onAddColumnFilters(finalResp);
          setDisplayTable(true);
          setTableLoading(false);
          setDisplayEmptyState(false);
        } else {
          if (loadMoreClicked) {
            setDisplayTable(true);
            setTableLoading(false);
            setLoadMoreClicked(false);
          } else {
            setDisplayEmptyState(true);
            setDisplayTable(false);
            setTableLoading(false);
          }
        }
      } catch (error) {
        setFilterError(error);
        setTableLoading(false);
        setDisplayTable(false);
        setDisplayEmptyState(false);
      }
    } else {
      setTableLoading(false);
      setDisplayEmptyState(false);
      setDisplayTable(false);
    }
    setRunQuery(false);
    setReset(false);
    setIsLoadingMore(false);
  }

  const renderToolbar = () => {
    return (
      <Toolbar
        id="data-toolbar-with-chip-groups"
        className="pf-m-toggle-group-container"
        collapseListedFiltersBreakpoint="md"
        clearAllFilters={onDeleteChip}
        clearFiltersButtonText="Reset to default"
      >
        <ToolbarContent>
          {!getPicker.loading && (
            <>
              <ToolbarToggleGroup toggleIcon={<FilterIcon />} breakpoint="xl">
                {!getQuery.loading && !getQueryTypes.loading && (
                  <ToolbarFilter
                    categoryName="Filters"
                    chips={filterChips}
                    deleteChip={onDeleteChip}
                  >
                    <DomainExplorerFilterOptions
                      filterArgument={filterArgument}
                      filterChips={filterChips}
                      finalFilters={finalFilters}
                      getQueryTypes={getQueryTypes}
                      getSchema={getSchema}
                      reset={reset}
                      runQuery={runQuery}
                      setFilterChips={setFilterChips}
                      setFinalFilters={setFinalFilters}
                      setOffset={setOffset}
                      setReset={setReset}
                      setRunQuery={setRunQuery}
                      generateFilterQuery={generateFilterQuery}
                    />
                  </ToolbarFilter>
                )}
              </ToolbarToggleGroup>
              <ToolbarGroup>
                <ToolbarItem>
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
                </ToolbarItem>
              </ToolbarGroup>
            </>
          )}
        </ToolbarContent>
      </Toolbar>
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
      <Divider />
      <Card
        className="kogito-common--domain-explorer__table-OverFlow"
        {...componentOuiaProps(
          ouiaId,
          'domain-explorer',
          ouiaSafe ? ouiaSafe : !tableLoading && !isLoadingMore
        )}
      >
        {columnFilters.length > 0 ? (
          <>
            <DomainExplorerTable
              columnFilters={columnFilters}
              displayTable={displayTable}
              displayEmptyState={displayEmptyState}
              filterError={filterError}
              filterChips={filterChips}
              finalFilters={finalFilters}
              handleRetry={handleRetry}
              isLoadingMore={isLoadingMore}
              offset={offset}
              onDeleteChip={onDeleteChip}
              parameters={parameters}
              rows={rows}
              selected={selected}
              setOrderByObj={setOrderByObj}
              setRows={setRows}
              setRunQuery={setRunQuery}
              setSortBy={setSortBy}
              sortBy={sortBy}
              tableLoading={tableLoading}
            />
            {displayTable &&
              !displayEmptyState &&
              !filterError &&
              filterChips.length > 0 &&
              (limit === pageSize || isLoadingMore) &&
              !tableLoading && (
                <LoadMore
                  offset={offset}
                  setOffset={setOffset}
                  getMoreItems={onGetMoreInstances}
                  pageSize={pageSize}
                  isLoadingMore={isLoadingMore}
                  setLoadMoreClicked={setLoadMoreClicked}
                />
              )}
          </>
        ) : (
          <Bullseye>
            <KogitoSpinner spinnerText="Loading domain explorer..." />
          </Bullseye>
        )}
      </Card>
    </>
  );
};

export default DomainExplorer;
