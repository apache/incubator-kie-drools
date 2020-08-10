import {
  Breadcrumb,
  BreadcrumbItem,
  Card,
  Grid,
  GridItem,
  InjectedOuiaProps,
  PageSection,
  withOuiaContext
} from '@patternfly/react-core';
import {
  GraphQL,
  KogitoEmptyState,
  KogitoEmptyStateType,
  ouiaPageTypeAndObjectId,
  ServerErrors,
  LoadMore
} from '@kogito-apps/common';
import React, { useEffect, useState } from 'react';
import { Link, RouteComponentProps } from 'react-router-dom';
import PageTitle from '../../Molecules/PageTitle/PageTitle';
import ProcessListToolbar from '../../Molecules/ProcessListToolbar/ProcessListToolbar';
import './ProcessListPage.css';
import ProcessListTable from '../../Organisms/ProcessListTable/ProcessListTable';

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

const ProcessListPage: React.FC<InjectedOuiaProps &
  RouteComponentProps<MatchProps, {}, LocationProps>> = ({
  ouiaContext,
  ...props
}) => {
  const [defaultPageSize] = useState<number>(10);
  const [initData, setInitData] = useState<any>({});
  const [isLoading, setIsLoading] = useState<boolean>(false);
  const [isError, setIsError] = useState<boolean>(false);
  const [limit, setLimit] = useState<number>(defaultPageSize);
  const [offset, setOffset] = useState<number>(0);
  const [pageSize, setPageSize] = useState<number>(defaultPageSize);
  const [isLoadingMore, setIsLoadingMore] = useState<boolean>(false);
  const [businessKeysArray, setBusinessKeysArray] = useState([]);
  const [filters, setFilters] = useState<filterType>(
    props.location.state
      ? { ...props.location.state.filters }
      : {
          status: [GraphQL.ProcessInstanceState.Active],
          businessKey: []
        }
  );
  const [statusArray, setStatusArray] = useState<
    GraphQL.ProcessInstanceState[]
  >(filters.status);
  const [selectedInstances, setSelectedInstances] = useState({});
  const [searchWord, setSearchWord] = useState<string>('');
  const [selectedNumber, setSelectedNumber] = useState<number>(0);
  const [isAllChecked, setIsAllChecked] = useState<boolean>(false);

  const [
    getProcessInstances,
    { loading, data, error }
  ] = GraphQL.useGetProcessInstancesLazyQuery({
    fetchPolicy: 'network-only',
    notifyOnNetworkStatusChange: true
  });

  useEffect(() => {
    window.history.pushState(null, '');
  }, []);

  useEffect(() => {
    if (props.location.state) {
      if (props.location.state.filters) {
        setFilters(props.location.state.filters);
        setStatusArray(props.location.state.filters.status);
      }
    }
  }, [props.location.state]);

  const resetPagination = () => {
    setOffset(0);
    setLimit(defaultPageSize);
    setPageSize(defaultPageSize);
  };

  useEffect(() => {
    return ouiaPageTypeAndObjectId(ouiaContext, 'process-instances');
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
        or: _searchWordsArray
      };
    }
  };

  const onFilterClick = (arr = filters.status) => {
    resetPagination();
    const searchWordsArray = [];
    const copyOfBusinessKeysArray = [...filters.businessKey];
    /* istanbul ignore if */

    if (searchWord.length !== 0) {
      if (!copyOfBusinessKeysArray.includes(searchWord)) {
        copyOfBusinessKeysArray.push(searchWord);
      }
    }
    copyOfBusinessKeysArray.forEach(word => {
      const tempBusinessKeys = { businessKey: { like: word } };
      searchWordsArray.push(tempBusinessKeys);
    });
    setIsLoading(true);
    setIsLoadingMore(false);
    setIsError(false);
    setSelectedInstances({});
    setIsAllChecked(false);
    setSelectedNumber(0);
    setInitData({});
    setBusinessKeysArray(searchWordsArray);
    getProcessInstances({
      variables: {
        where: queryVariableGenerator(searchWordsArray, arr),
        offset: 0,
        limit: defaultPageSize
      }
    });
  };

  const onGetMoreInstances = (initVal, _pageSize) => {
    setIsLoadingMore(true);
    setPageSize(_pageSize);
    getProcessInstances({
      variables: {
        where: queryVariableGenerator(businessKeysArray, statusArray),
        offset: initVal,
        limit: _pageSize
      }
    });
  };

  useEffect(() => {
    setSelectedInstances({});
    if (isLoadingMore === undefined || !isLoadingMore) {
      setIsLoading(loading);
    }
    setSearchWord('');
    if (!loading && data !== undefined) {
      data.ProcessInstances.forEach((instance: any) => {
        instance.isChecked = false;
        instance.isOpen = false;
      });
      setLimit(data.ProcessInstances.length);
      if (offset > 0 && initData.ProcessInstances.length > 0) {
        setIsLoadingMore(false);
        initData.ProcessInstances = initData.ProcessInstances.concat(
          data.ProcessInstances
        );
      } else {
        setInitData(data);
      }
    }
  }, [data]);

  const resetClick = () => {
    setSearchWord('');
    setStatusArray([GraphQL.ProcessInstanceState.Active]);
    setFilters({
      ...filters,
      status: [GraphQL.ProcessInstanceState.Active],
      businessKey: []
    });
    onFilterClick([GraphQL.ProcessInstanceState.Active]);
  };

  if (error) {
    return <ServerErrors error={error} variant="large" />;
  }
  return (
    <React.Fragment>
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
        <Grid gutter="md">
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
                    selectedNumber={selectedNumber}
                    setSelectedNumber={setSelectedNumber}
                    statusArray={statusArray}
                    setStatusArray={setStatusArray}
                  />
                </>
              )}
              {filters.status.length > 0 ? (
                <ProcessListTable
                  initData={initData}
                  setInitData={setInitData}
                  setLimit={setLimit}
                  isLoading={isLoading}
                  setIsError={setIsError}
                  pageSize={defaultPageSize}
                  selectedInstances={selectedInstances}
                  setSelectedInstances={setSelectedInstances}
                  filters={filters}
                  setIsAllChecked={setIsAllChecked}
                  setSelectedNumber={setSelectedNumber}
                  selectedNumber={selectedNumber}
                />
              ) : (
                <KogitoEmptyState
                  type={KogitoEmptyStateType.Reset}
                  title="No status is selected"
                  body="Try selecting at least one status to see results"
                  onClick={resetClick}
                />
              )}
              {(!loading || isLoadingMore) &&
                !isLoading &&
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
    </React.Fragment>
  );
};

export default withOuiaContext(ProcessListPage);
