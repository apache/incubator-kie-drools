/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
import { useCallback, useContext, useEffect, useRef, useState } from 'react';
import { EXECUTIONS_PATH, httpClient } from '../../../utils/api/httpClient';
import {
  CFAnalysisExecution,
  CFAnalysisResultsSets,
  CFGoal,
  CFGoalRole,
  CFSearchInput,
  RemoteData,
  RemoteDataStatus
} from '../../../types';
import { AxiosRequestConfig } from 'axios';
import { TrustyContext } from '../../Templates/TrustyApp/TrustyApp';

const useCounterfactualExecution = (executionId: string) => {
  const [cfAnalysis, setCFAnalysis] = useState<
    RemoteData<Error, CFAnalysisExecution>
  >({
    status: RemoteDataStatus.NOT_ASKED
  });
  const [cfResults, setCFResults] = useState<CFAnalysisResultsSets>();
  const [counterfactualId, setCounterfactualId] = useState();
  const [resultsPolling, setResultsPolling] = useState<number | null>(null);

  const baseUrl = useContext(TrustyContext).config.serverRoot;

  const runCFAnalysis = useCallback(
    (parameters: { goals: CFGoal[]; searchDomains: CFSearchInput[] }) => {
      let isMounted = true;
      const { goals, searchDomains } = parameters;
      setCFAnalysis({ status: RemoteDataStatus.LOADING });

      const partialGoals = goals
        .filter(
          (goal) =>
            goal.role === CFGoalRole.FIXED || goal.role === CFGoalRole.ORIGINAL
        )
        // eslint-disable-next-line @typescript-eslint/no-unused-vars
        .map(({ role, ...rest }) => rest);

      const config: AxiosRequestConfig = {
        baseURL: baseUrl,
        url: `${EXECUTIONS_PATH}/decisions/${executionId}/explanations/counterfactuals`,
        method: 'post',
        data: {
          goals: partialGoals,
          searchDomains
        }
      };

      httpClient(config)
        .then((response) => {
          if (isMounted) {
            setCFAnalysis({
              status: RemoteDataStatus.SUCCESS,
              data: response.data
            });
            setCounterfactualId(response.data.counterfactualId);
          }
        })
        .catch((error) => {
          setCFAnalysis({ status: RemoteDataStatus.FAILURE, error });
        });
      return () => {
        isMounted = false;
      };
    },
    [executionId]
  );

  const getCFResults = useCallback(() => {
    let isMounted = true;

    const config: AxiosRequestConfig = {
      baseURL: baseUrl,
      url: `${EXECUTIONS_PATH}/decisions/${executionId}/explanations/counterfactuals/${counterfactualId}`,
      method: 'get'
    };

    httpClient(config)
      .then((response) => {
        if (isMounted) {
          setCFResults(response.data);
        }
      })
      .catch((error) => {
        setCFAnalysis({ status: RemoteDataStatus.FAILURE, error });
      });
    return () => {
      isMounted = false;
    };
  }, [executionId, counterfactualId]);

  useEffect(() => {
    if (counterfactualId) {
      setResultsPolling(3000);
    }
  }, [counterfactualId]);

  useInterval(() => {
    getCFResults();
  }, resultsPolling);

  useEffect(() => {
    if (cfResults) {
      const finalResult = cfResults.solutions.find(
        (solution) => solution.stage === 'FINAL'
      );
      if (finalResult) {
        setResultsPolling(null);
      }
    }
  }, [cfResults]);

  return { runCFAnalysis, cfAnalysis, cfResults };
};

export default useCounterfactualExecution;

const useInterval = (callback: () => void, delay: number | null) => {
  const savedCallback = useRef(null);

  useEffect(() => {
    savedCallback.current = callback;
  }, [callback]);

  useEffect(() => {
    const tick = () => {
      savedCallback.current();
    };
    if (delay !== null) {
      const id = setInterval(tick, delay);
      return () => clearInterval(id);
    }
  }, [delay]);
};
