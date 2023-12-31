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

package org.apache.flink.table.planner.runtime.stream.jsonplan;

import org.apache.flink.table.planner.factories.TestValuesTableFactory;
import org.apache.flink.table.planner.runtime.utils.TestData;
import org.apache.flink.table.planner.utils.JavaScalaConversionUtil;
import org.apache.flink.table.planner.utils.JsonPlanTestBase;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;

/** Test for Rank JsonPlan ser/de. */
class RankJsonPlanITCase extends JsonPlanTestBase {
    @Test
    void testRank() throws ExecutionException, InterruptedException, IOException {
        createTestValuesSourceTable(
                "MyTable",
                JavaScalaConversionUtil.toJava(TestData.data1()),
                "a int",
                "b varchar",
                "c int");
        createTestNonInsertOnlyValuesSinkTable("`result`", "a int", "b varchar", "c bigint");
        String sql =
                "insert into `result` select * from "
                        + "(select a, b, row_number() over(partition by b order by c) as c from MyTable)"
                        + " where c = 1";
        compileSqlAndExecutePlan(sql).await();

        List<String> expected = Arrays.asList("+I[1, a, 1]", "+I[3, b, 1]", "+I[5, c, 1]");
        assertResult(expected, TestValuesTableFactory.getResultsAsStrings("result"));
    }

    @Test
    void testFirstN() throws ExecutionException, InterruptedException, IOException {
        createTestValuesSourceTable(
                "MyTable1",
                JavaScalaConversionUtil.toJava(TestData.data4()),
                "a varchar",
                "b int",
                "c int",
                "t as proctime()");
        createTestNonInsertOnlyValuesSinkTable("`result1`", "a varchar", "b int", "c bigint");
        String sql =
                "insert into `result1` select * from "
                        + "(select a, b, row_number() over(partition by a order by t asc) as c from MyTable1)"
                        + " where c <= 2";
        compileSqlAndExecutePlan(sql).await();

        List<String> expected =
                Arrays.asList(
                        "+I[book, 1, 1]", "+I[book, 2, 2]", "+I[fruit, 4, 1]", "+I[fruit, 3, 2]");
        assertResult(expected, TestValuesTableFactory.getResultsAsStrings("result1"));
    }
}
