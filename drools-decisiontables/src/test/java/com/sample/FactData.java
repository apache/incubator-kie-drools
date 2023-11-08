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
package com.sample;

/**
 * ファクト
 */
public class FactData {

    private int 値;
    private String エラーメッセージ;

    public int get値() {
        return this.値;
    }

    public void set値(int 値) {
        this.値 = 値;
    }

    public String getエラーメッセージ() {
        return this.エラーメッセージ;
    }

    public void setエラーメッセージ(String エラーメッセージ) {
        this.エラーメッセージ = エラーメッセージ;
    }
}
