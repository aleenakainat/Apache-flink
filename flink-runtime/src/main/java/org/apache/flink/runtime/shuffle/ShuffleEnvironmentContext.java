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

package org.apache.flink.runtime.shuffle;

import org.apache.flink.configuration.Configuration;
import org.apache.flink.configuration.MemorySize;
import org.apache.flink.metrics.MetricGroup;
import org.apache.flink.runtime.clusterframework.types.ResourceID;
import org.apache.flink.runtime.io.network.TaskEventPublisher;
import org.apache.flink.util.concurrent.ScheduledExecutor;

import java.net.InetAddress;
import java.util.concurrent.Executor;

import static org.apache.flink.util.Preconditions.checkNotNull;

/** Local context used to create {@link ShuffleEnvironment}. */
public class ShuffleEnvironmentContext {
    private final Configuration configuration;
    private final ResourceID taskExecutorResourceId;
    private final MemorySize networkMemorySize;
    private final boolean localCommunicationOnly;
    private final InetAddress hostAddress;
    private final TaskEventPublisher eventPublisher;
    private final MetricGroup parentMetricGroup;
    private final int numberOfSlots;
    private final String[] tmpDirPaths;

    private final Executor ioExecutor;
    private final ScheduledExecutor scheduledExecutor;

    public ShuffleEnvironmentContext(
            Configuration configuration,
            ResourceID taskExecutorResourceId,
            MemorySize networkMemorySize,
            boolean localCommunicationOnly,
            InetAddress hostAddress,
            int numberOfSlots,
            String[] tmpDirPaths,
            TaskEventPublisher eventPublisher,
            MetricGroup parentMetricGroup,
            Executor ioExecutor,
            ScheduledExecutor scheduledExecutor) {
        this.configuration = checkNotNull(configuration);
        this.taskExecutorResourceId = checkNotNull(taskExecutorResourceId);
        this.networkMemorySize = networkMemorySize;
        this.localCommunicationOnly = localCommunicationOnly;
        this.hostAddress = checkNotNull(hostAddress);
        this.eventPublisher = checkNotNull(eventPublisher);
        this.parentMetricGroup = checkNotNull(parentMetricGroup);
        this.ioExecutor = ioExecutor;
        this.scheduledExecutor = scheduledExecutor;
        this.numberOfSlots = numberOfSlots;
        this.tmpDirPaths = checkNotNull(tmpDirPaths);
    }

    public Configuration getConfiguration() {
        return configuration;
    }

    public ResourceID getTaskExecutorResourceId() {
        return taskExecutorResourceId;
    }

    public MemorySize getNetworkMemorySize() {
        return networkMemorySize;
    }

    public boolean isLocalCommunicationOnly() {
        return localCommunicationOnly;
    }

    public InetAddress getHostAddress() {
        return hostAddress;
    }

    public TaskEventPublisher getEventPublisher() {
        return eventPublisher;
    }

    public MetricGroup getParentMetricGroup() {
        return parentMetricGroup;
    }

    public Executor getIoExecutor() {
        return ioExecutor;
    }

    public ScheduledExecutor getScheduledExecutor() {
        return scheduledExecutor;
    }

    public int getNumberOfSlots() {
        return numberOfSlots;
    }

    public String[] getTmpDirPaths() {
        return tmpDirPaths;
    }
}
