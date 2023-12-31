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

import org.apache.flink.table.planner.runtime.utils.TestData;
import org.apache.flink.table.planner.utils.JavaScalaConversionUtil;
import org.apache.flink.table.planner.utils.JsonPlanTestBase;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static org.apache.flink.table.utils.DateTimeUtils.toLocalDateTime;

/** Test for table source json plan. */
class TableSourceJsonPlanITCase extends JsonPlanTestBase {

    @Test
    void testProjectPushDown() throws Exception {
        List<String> data = Arrays.asList("1,1,hi", "2,1,hello", "3,2,hello world");
        createTestCsvSourceTable("MyTable", data, "a bigint", "b int not null", "c varchar");
        File sinkPath = createTestCsvSinkTable("MySink", "a bigint", "b int");

        compileSqlAndExecutePlan("insert into MySink select a, b from MyTable").await();

        assertResult(Arrays.asList("1,1", "2,1", "3,2"), sinkPath);
    }

    @Test
    void testReadingMetadata() throws Exception {
        createTestValuesSourceTable(
                "MyTable",
                JavaScalaConversionUtil.toJava(TestData.smallData3()),
                new String[] {"a int", "b bigint", "m varchar metadata"},
                new HashMap<String, String>() {
                    {
                        put("readable-metadata", "m:STRING");
                    }
                });

        File sinkPath = createTestCsvSinkTable("MySink", "a bigint", "m varchar");

        compileSqlAndExecutePlan("insert into MySink select a, m from MyTable").await();

        assertResult(Arrays.asList("1,Hi", "2,Hello", "3,Hello world"), sinkPath);
    }

    @Test
    void testReadingMetadataWithProjectionPushDownDisabled() throws Exception {
        createTestValuesSourceTable(
                "MyTable",
                JavaScalaConversionUtil.toJava(TestData.smallData3()),
                new String[] {"a int", "b bigint", "m varchar metadata"},
                new HashMap<String, String>() {
                    {
                        put("readable-metadata", "m:STRING");
                        put("enable-projection-push-down", "false");
                    }
                });

        File sinkPath = createTestCsvSinkTable("MySink", "a int", "m varchar");

        compileSqlAndExecutePlan("insert into MySink select a, m from MyTable").await();

        assertResult(Arrays.asList("1,Hi", "2,Hello", "3,Hello world"), sinkPath);
    }

    @Test
    void testFilterPushDown() throws Exception {
        List<String> data = Arrays.asList("1,1,hi", "2,1,hello", "3,2,hello world");
        createTestCsvSourceTable("MyTable", data, "a bigint", "b int not null", "c varchar");
        File sinkPath = createTestCsvSinkTable("MySink", "a bigint", "b int", "c varchar");

        compileSqlAndExecutePlan("insert into MySink select * from MyTable where a > 1").await();

        assertResult(Arrays.asList("2,1,hello", "3,2,hello world"), sinkPath);
    }

    @Test
    void testPartitionPushDown() throws Exception {
        createTestValuesSourceTable(
                "MyTable",
                JavaScalaConversionUtil.toJava(TestData.smallData3()),
                new String[] {"a int", "p bigint", "c varchar"},
                "p",
                new HashMap<String, String>() {
                    {
                        put("partition-list", "p:1;p:2");
                    }
                });
        File sinkPath = createTestCsvSinkTable("MySink", "a int", "p bigint", "c varchar");

        compileSqlAndExecutePlan("insert into MySink select * from MyTable where p = 2").await();

        assertResult(Arrays.asList("2,2,Hello", "3,2,Hello world"), sinkPath);
    }

    @Test
    void testWatermarkPushDown() throws Exception {
        createTestValuesSourceTable(
                "MyTable",
                JavaScalaConversionUtil.toJava(TestData.data3WithTimestamp()),
                new String[] {
                    "a int",
                    "b bigint",
                    "c varchar",
                    "ts timestamp(3)",
                    "watermark for ts as ts - interval '5' second"
                },
                new HashMap<String, String>() {
                    {
                        put("enable-watermark-push-down", "true");
                    }
                });

        File sinkPath = createTestCsvSinkTable("MySink", "a int", "b bigint", "ts timestamp(3)");

        compileSqlAndExecutePlan("insert into MySink select a, b, ts from MyTable where b = 3")
                .await();

        assertResult(
                Arrays.asList(
                        "4,3," + toLocalDateTime(4000L),
                        "5,3," + toLocalDateTime(5000L),
                        "6,3," + toLocalDateTime(6000L)),
                sinkPath);
    }

    @Test
    void testPushDowns() throws Exception {
        createTestValuesSourceTable(
                "MyTable",
                JavaScalaConversionUtil.toJava(TestData.data3WithTimestamp()),
                new String[] {
                    "a int",
                    "b bigint",
                    "c varchar",
                    "ts timestamp(3)",
                    "watermark for ts as ts - interval '5' second"
                },
                "b",
                new HashMap<String, String>() {
                    {
                        put("readable-metadata", "a:INT");
                        put("filterable-fields", "a");
                        put("enable-watermark-push-down", "true");
                        put("partition-list", "b:1;b:2;b:3;b:4;b:5;b:6");
                    }
                });

        File sinkPath = createTestCsvSinkTable("MySink", "a int", "ts timestamp(3)");

        compileSqlAndExecutePlan(
                        "insert into MySink select a, ts from MyTable where b = 3 and a > 4")
                .await();

        assertResult(
                Arrays.asList("5," + toLocalDateTime(5000L), "6," + toLocalDateTime(6000L)),
                sinkPath);
    }
}
