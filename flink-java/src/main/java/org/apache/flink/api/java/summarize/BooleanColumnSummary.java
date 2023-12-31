/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.flink.api.java.summarize;

import org.apache.flink.annotation.PublicEvolving;

/**
 * Summary for a column of booleans.
 *
 * @deprecated All Flink DataSet APIs are deprecated since Flink 1.18 and will be removed in a
 *     future Flink major version. You can still build your application in DataSet, but you should
 *     move to either the DataStream and/or Table API.
 * @see <a href="https://cwiki.apache.org/confluence/pages/viewpage.action?pageId=158866741">
 *     FLIP-131: Consolidate the user-facing Dataflow SDKs/APIs (and deprecate the DataSet API</a>
 */
@Deprecated
@PublicEvolving
public class BooleanColumnSummary extends ColumnSummary {

    private long trueCount;
    private long falseCount;
    private long nullCount;

    public BooleanColumnSummary(long trueCount, long falseCount, long nullCount) {
        this.trueCount = trueCount;
        this.falseCount = falseCount;
        this.nullCount = nullCount;
    }

    public long getTrueCount() {
        return trueCount;
    }

    public long getFalseCount() {
        return falseCount;
    }

    /** The number of non-null values in this column. */
    @Override
    public long getNonNullCount() {
        return trueCount + falseCount;
    }

    public long getNullCount() {
        return nullCount;
    }

    @Override
    public String toString() {
        return "BooleanColumnSummary{"
                + "totalCount="
                + getTotalCount()
                + ", trueCount="
                + trueCount
                + ", falseCount="
                + falseCount
                + ", nullCount="
                + nullCount
                + '}';
    }
}
