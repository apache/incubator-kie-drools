import {
  Breadcrumb,
  BreadcrumbItem,
  Card,
  Divider,
  Grid,
  GridItem,
  PageSection
} from '@patternfly/react-core';
import {
  GraphQL,
  KogitoEmptyState,
  KogitoEmptyStateType,
  ServerErrors,
  LoadMore,
  constructObject
} from '@kogito-apps/common';
import {
  ouiaPageTypeAndObjectId,
  componentOuiaProps,
  OUIAProps
} from '@kogito-apps/ouia-tools';
import React, { useEffect, useState } from 'react';
import { Link, RouteComponentProps } from 'react-router-dom';
import PageTitle from '../../Molecules/PageTitle/PageTitle';
import ProcessListToolbar from '../../Molecules/ProcessListToolbar/ProcessListToolbar';
import './ProcessListPage.css';
import ProcessListTable from '../../Organisms/ProcessListTable/ProcessListTable';
import { StaticContext } from 'react-router';
import _ from 'lodash';
import { alterOrderByObj } from '../../../utils/Utils';
import { ISortBy } from '@patternfly/react-table';

type filterType = {
  status: GraphQL.ProcessInstanceState[];
  businessKey: string[];
};
interface MatchProps {
  domainName: string;
}

interface LocationProps {
  filters?: filterType;
}

const ProcessListPage: React.FC<
  OUIAProps & RouteComponentProps<MatchProps, StaticContext, LocationProps>
> = ({ ouiaId, ouiaSafe, ...props }) => {
  const defaultOrderBy: GraphQL.ProcessInstanceOrderBy = {
    lastUpdate: GraphQL.OrderBy.Asc
  };
  const [defaultPageSize] = useState<number>(10);
  const [initData, setInitData] = useState<GraphQL.GetProcessInstancesQuery>(
    {}
  );
  const [isError, setIsError] = useState<boolean>(false);
  const [limit, setLimit] = useState<number>(defaultPageSize);
  const [offset, setOffset] = useState<number>(0);
  const [pageSize, setPageSize] = useState<number>(defaultPageSize);
  const [isLoadingMore, setIsLoadingMore] = useState<boolean>(false);
  const [filters, setFilters] = useState<filterType>(
    props.location.state
      ? { ...props.location.state.filters }
      : {
          status: [GraphQL.ProcessInstanceState.Active],
          businessKey: []
        }
  );
  const [businessKeysArray, setBusinessKeysArray] = useState<string[]>(
    filters.businessKey
  );
  const [statusArray, setStatusArray] = useState<
    GraphQL.ProcessInstanceState[]
  >(filters.status);
  const [selectedInstances, setSelectedInstances] = useState<
    GraphQL.ProcessInstance[]
  >([]);
  const [searchWord, setSearchWord] = useState<string>('');
  const [isAllChecked, setIsAllChecked] = useState<boolean>(false);
  const [expanded, setExpanded] = React.useState<{ [key: number]: boolean }>(
    {}
  );
  const [selectableInstances, setSelectableInstances] = useState<number>(0);
  const [isLoading, setIsLoading] = useState<boolean>(true);
  const [sortBy, setSortBy] = useState<ISortBy>({});
  const [orderBy, setOrderBy] =
    useState<GraphQL.ProcessInstanceOrderBy>(defaultOrderBy);
  const [getProcessInstances, { loading, data, error }] =
    GraphQL.useGetProcessInstancesLazyQuery({
      fetchPolicy: 'network-only',
      notifyOnNetworkStatusChange: true
    });
  useEffect(() => {
    window.history.pushState(null, '');
    getProcessInstances({
      variables: {
        where: queryVariableGenerator(businessKeysArray, statusArray),
        orderBy,
        offset: 0,
        limit: pageSize
      }
    });
  }, []);

  useEffect(() => {
    setIsLoading(true);
    if (props.location.state) {
      if (props.location.state.filters) {
        setFilters(props.location.state.filters);
        setStatusArray(props.location.state.filters.status);
        setBusinessKeysArray(props.location.state.filters.businessKey);
      }
      getProcessInstances({
        variables: {
          where: queryVariableGenerator(
            props.location.state.filters.businessKey,
            props.location.state.filters.status
          ),
          orderBy,
          offset: 0,
          limit: pageSize
        }
      });
    }
  }, [props.location.state]);

  const resetPagination = (): void => {
    setIsLoading(true);
    setOffset(0);
    setLimit(defaultPageSize);
    setPageSize(defaultPageSize);
    setSelectableInstances(0);
    setSelectedInstances([]);
  };

  useEffect(() => {
    return ouiaPageTypeAndObjectId('process-instances');
  });

  const queryVariableGenerator = (_searchWordsArray, _statusArray) => {
    if (_searchWordsArray.length === 0) {
      return {
        parentProcessInstanceId: { isNull: true },
        state: { in: _statusArray }
      };
    } else {
      return {
        parentProcessInstanceId: { isNull: true },
        state: { in: _statusArray },
        or: formatSearchWords(_searchWordsArray)
      };
    }
  };

  const formatSearchWords = (searchWords: string[]) => {
    const tempSearchWordsArray = [];
    searchWords.forEach((word) => {
      tempSearchWordsArray.push({ businessKey: { like: word } });
    });
    return tempSearchWordsArray;
  };

  const onFilterClick = (arr = filters.status): void => {
    resetPagination();
    const copyOfBusinessKeysArray = [...filters.businessKey];
    /* istanbul ignore if */
    if (searchWord.length !== 0) {
      if (!copyOfBusinessKeysArray.includes(searchWord)) {
        copyOfBusinessKeysArray.push(searchWord);
      }
    }
    setIsLoadingMore(false);
    setIsError(false);
    setSelectedInstances([]);
    setIsAllChecked(false);
    setInitData({});
    setBusinessKeysArray(copyOfBusinessKeysArray);
    getProcessInstances({
      variables: {
        where: queryVariableGenerator(copyOfBusinessKeysArray, arr),
        orderBy,
        offset: 0,
        limit: defaultPageSize
      }
    });
  };

  const onGetMoreInstances = (initVal: number, _pageSize: number): void => {
    setIsLoading(false);
    setSelectableInstances(0);
    setSelectedInstances([]);
    setIsLoadingMore(true);
    setPageSize(_pageSize);
    getProcessInstances({
      variables: {
        where: queryVariableGenerator(businessKeysArray, statusArray),
        orderBy,
        offset: initVal,
        limit: _pageSize
      }
    });
  };
  const countSelectableInstances = (process, index) => {
    expanded[index] = false;
    if (process.serviceUrl && process.addons.includes('process-management')) {
      setSelectableInstances((prev) => prev + 1);
    }
  };

  useEffect(() => {
    setSelectedInstances([]);
    setSelectableInstances(0);
    setIsAllChecked(false);
    setSearchWord('');
    if (!loading && data !== undefined) {
      setIsLoading(false);
      data.ProcessInstances.forEach(
        (
          instance: GraphQL.ProcessInstance & {
            isSelected: boolean;
            childProcessInstances: GraphQL.ProcessInstance[];
            isOpen: boolean;
          }
        ) => {
          instance.isSelected = false;
          instance.isOpen = false;
          instance.childProcessInstances = [];
        }
      );
      setLimit(data.ProcessInstances.length);
      if (offset > 0 && initData.ProcessInstances.length > 0) {
        setIsLoadingMore(false);
        const newData = initData.ProcessInstances.concat(data.ProcessInstances);
        newData.forEach((process, i) => countSelectableInstances(process, i));
        setInitData({ ProcessInstances: newData });
      } else {
        data.ProcessInstances.forEach((process, i) =>
          countSelectableInstances(process, i)
        );
        setInitData(data);
      }
    }
  }, [data]);

  useEffect(() => {
    if (
      selectedInstances.length === selectableInstances &&
      selectableInstances !== 0
    ) {
      setIsAllChecked(true);
    } else {
      setIsAllChecked(false);
    }
  }, [initData]);

  const resetClick = (): void => {
    setSearchWord('');
    setStatusArray([GraphQL.ProcessInstanceState.Active]);
    setFilters({
      ...filters,
      status: [GraphQL.ProcessInstanceState.Active],
      businessKey: []
    });
    onFilterClick([GraphQL.ProcessInstanceState.Active]);
  };

  const onSort = (event, index: number, direction: 'asc' | 'desc'): void => {
    resetPagination();
    setSortBy({ index, direction });
    let sortingColumn: string = event.target.innerText;
    sortingColumn = _.camelCase(sortingColumn);
    let obj = {};
    constructObject(obj, sortingColumn, direction.toUpperCase());
    obj = alterOrderByObj(obj);
    setOrderBy(obj);
    getProcessInstances({
      variables: {
        where: queryVariableGenerator(businessKeysArray, statusArray),
        orderBy: obj,
        offset: 0,
        limit: pageSize
      }
    });
  };

  if (error) {
    return <ServerErrors error={error} variant="large" />;
  }

  return (
    <React.Fragment>
      <div
        {...componentOuiaProps(
          ouiaId,
          'process-list-page',
          ouiaSafe ? ouiaSafe : !loading
        )}
      >
        <PageSection variant="light">
          <PageTitle title="Process Instances" />
          <Breadcrumb>
            <BreadcrumbItem>
              <Link to={'/'}>Home</Link>
            </BreadcrumbItem>
            <BreadcrumbItem isActive>Process instances</BreadcrumbItem>
          </Breadcrumb>
        </PageSection>
        <PageSection>
          <Grid hasGutter md={1}>
            <GridItem span={12}>
              <Card className="dataList">
                {!isError && (
                  <>
                    {' '}
                    <ProcessListToolbar
                      filterClick={onFilterClick}
                      filters={filters}
                      setFilters={setFilters}
                      initData={initData}
                      setInitData={setInitData}
                      selectedInstances={selectedInstances}
                      setSelectedInstances={setSelectedInstances}
                      setSearchWord={setSearchWord}
                      searchWord={searchWord}
                      isAllChecked={isAllChecked}
                      setIsAllChecked={setIsAllChecked}
                      statusArray={statusArray}
                      setStatusArray={setStatusArray}
                    />
                    <Divider />
                  </>
                )}
                {filters.status.length > 0 ? (
                  <ProcessListTable
                    initData={initData}
                    loading={isLoading}
                    filters={filters}
                    setInitData={setInitData}
                    expanded={expanded}
                    setExpanded={setExpanded}
                    setSelectedInstances={setSelectedInstances}
                    selectedInstances={selectedInstances}
                    setSelectableInstances={setSelectableInstances}
                    setIsAllChecked={setIsAllChecked}
                    selectableInstances={selectableInstances}
                    onSort={onSort}
                    sortBy={sortBy}
                  />
                ) : (
                  <KogitoEmptyState
                    type={KogitoEmptyStateType.Reset}
                    title="No filters applied."
                    body="Try applying at least one filter to see results"
                    onClick={resetClick}
                  />
                )}
                {(!isLoading || isLoadingMore) &&
                  initData !== undefined &&
                  (limit === pageSize || isLoadingMore) &&
                  filters.status.length > 0 && (
                    <LoadMore
                      offset={offset}
                      setOffset={setOffset}
                      getMoreItems={onGetMoreInstances}
                      pageSize={pageSize}
                      isLoadingMore={isLoadingMore}
                    />
                  )}
              </Card>
            </GridItem>
          </Grid>
        </PageSection>
      </div>
    </React.Fragment>
  );
};

export default ProcessListPage;
