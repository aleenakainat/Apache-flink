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

package org.apache.flink.runtime.rest.messages.checkpoints;

import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.annotation.JsonCreator;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.annotation.JsonSubTypes;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.annotation.JsonTypeInfo;

import io.swagger.v3.oas.annotations.media.DiscriminatorMapping;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Objects;

/** Checkpoint statistics for a subtask. */
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "className")
@JsonSubTypes({
    @JsonSubTypes.Type(
            value = SubtaskCheckpointStatistics.CompletedSubtaskCheckpointStatistics.class,
            name = "completed"),
    @JsonSubTypes.Type(
            value = SubtaskCheckpointStatistics.PendingSubtaskCheckpointStatistics.class,
            name = "pending")
})
@Schema(
        discriminatorProperty = "className",
        discriminatorMapping = {
            @DiscriminatorMapping(
                    value = "completed",
                    schema =
                            SubtaskCheckpointStatistics.CompletedSubtaskCheckpointStatistics.class),
            @DiscriminatorMapping(
                    value = "pending",
                    schema = SubtaskCheckpointStatistics.PendingSubtaskCheckpointStatistics.class),
        })
public class SubtaskCheckpointStatistics {

    public static final String FIELD_NAME_INDEX = "index";

    public static final String FIELD_NAME_CHECKPOINT_STATUS = "status";

    @JsonProperty(FIELD_NAME_INDEX)
    private final int index;

    @JsonProperty(FIELD_NAME_CHECKPOINT_STATUS)
    private final String checkpointStatus;

    @JsonCreator
    private SubtaskCheckpointStatistics(
            @JsonProperty(FIELD_NAME_INDEX) int index,
            @JsonProperty(FIELD_NAME_CHECKPOINT_STATUS) String checkpointStatus) {
        this.index = index;
        this.checkpointStatus = checkpointStatus;
    }

    public int getIndex() {
        return index;
    }

    public String getCheckpointStatus() {
        return checkpointStatus;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        SubtaskCheckpointStatistics that = (SubtaskCheckpointStatistics) o;
        return index == that.index && Objects.equals(checkpointStatus, that.checkpointStatus);
    }

    @Override
    public int hashCode() {
        return Objects.hash(index, checkpointStatus);
    }

    // ---------------------------------------------------------------------------------
    // Static inner classes
    // ---------------------------------------------------------------------------------

    /** Checkpoint statistics for a completed subtask checkpoint. */
    public static final class CompletedSubtaskCheckpointStatistics
            extends SubtaskCheckpointStatistics {

        public static final String FIELD_NAME_ACK_TIMESTAMP = "ack_timestamp";

        public static final String FIELD_NAME_DURATION = "end_to_end_duration";

        public static final String FIELD_NAME_CHECKPOINTED_SIZE = "checkpointed_size";

        /**
         * The accurate name of this field should be 'checkpointed_data_size', keep it as before to
         * not break backwards compatibility for old web UI.
         *
         * @see <a href="https://issues.apache.org/jira/browse/FLINK-13390">FLINK-13390</a>
         */
        public static final String FIELD_NAME_STATE_SIZE = "state_size";

        public static final String FIELD_NAME_CHECKPOINT_DURATION = "checkpoint";

        public static final String FIELD_NAME_ALIGNMENT = "alignment";

        public static final String FIELD_NAME_START_DELAY = "start_delay";

        public static final String FIELD_NAME_UNALIGNED_CHECKPOINT = "unaligned_checkpoint";

        public static final String FIELD_NAME_ABORTED = "aborted";

        @JsonProperty(FIELD_NAME_ACK_TIMESTAMP)
        private final long ackTimestamp;

        @JsonProperty(FIELD_NAME_DURATION)
        private final long duration;

        @JsonProperty(FIELD_NAME_CHECKPOINTED_SIZE)
        private final long checkpointedSize;

        @JsonProperty(FIELD_NAME_STATE_SIZE)
        private final long stateSize;

        @JsonProperty(FIELD_NAME_CHECKPOINT_DURATION)
        private final CheckpointDuration checkpointDuration;

        @JsonProperty(FIELD_NAME_ALIGNMENT)
        private final CheckpointAlignment alignment;

        @JsonProperty(FIELD_NAME_START_DELAY)
        private final long startDelay;

        @JsonProperty(FIELD_NAME_UNALIGNED_CHECKPOINT)
        private boolean unalignedCheckpoint;

        @JsonProperty(FIELD_NAME_ABORTED)
        private final boolean aborted;

        @JsonCreator
        public CompletedSubtaskCheckpointStatistics(
                @JsonProperty(FIELD_NAME_INDEX) int index,
                @JsonProperty(FIELD_NAME_ACK_TIMESTAMP) long ackTimestamp,
                @JsonProperty(FIELD_NAME_DURATION) long duration,
                @JsonProperty(FIELD_NAME_CHECKPOINTED_SIZE) long checkpointedSize,
                @JsonProperty(FIELD_NAME_STATE_SIZE) long stateSize,
                @JsonProperty(FIELD_NAME_CHECKPOINT_DURATION) CheckpointDuration checkpointDuration,
                @JsonProperty(FIELD_NAME_ALIGNMENT) CheckpointAlignment alignment,
                @JsonProperty(FIELD_NAME_START_DELAY) long startDelay,
                @JsonProperty(FIELD_NAME_UNALIGNED_CHECKPOINT) boolean unalignedCheckpoint,
                @JsonProperty(FIELD_NAME_ABORTED) boolean aborted) {
            super(index, "completed");
            this.ackTimestamp = ackTimestamp;
            this.duration = duration;
            this.checkpointedSize = checkpointedSize;
            this.stateSize = stateSize;
            this.checkpointDuration = checkpointDuration;
            this.alignment = alignment;
            this.startDelay = startDelay;
            this.unalignedCheckpoint = unalignedCheckpoint;
            this.aborted = aborted;
        }

        public long getAckTimestamp() {
            return ackTimestamp;
        }

        public long getDuration() {
            return duration;
        }

        public long getStateSize() {
            return stateSize;
        }

        public long getCheckpointedSize() {
            return checkpointedSize;
        }

        public CheckpointDuration getCheckpointDuration() {
            return checkpointDuration;
        }

        public CheckpointAlignment getAlignment() {
            return alignment;
        }

        public long getStartDelay() {
            return startDelay;
        }

        public boolean getUnalignedCheckpoint() {
            return unalignedCheckpoint;
        }

        public boolean isAborted() {
            return aborted;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            CompletedSubtaskCheckpointStatistics that = (CompletedSubtaskCheckpointStatistics) o;
            return ackTimestamp == that.ackTimestamp
                    && duration == that.duration
                    && checkpointedSize == that.checkpointedSize
                    && stateSize == that.stateSize
                    && Objects.equals(checkpointDuration, that.checkpointDuration)
                    && Objects.equals(alignment, that.alignment)
                    && startDelay == that.startDelay
                    && unalignedCheckpoint == that.unalignedCheckpoint
                    && aborted == that.aborted;
        }

        @Override
        public int hashCode() {
            return Objects.hash(
                    ackTimestamp,
                    duration,
                    checkpointedSize,
                    stateSize,
                    checkpointDuration,
                    alignment,
                    startDelay,
                    unalignedCheckpoint,
                    aborted);
        }

        /** Duration of the checkpoint. */
        public static final class CheckpointDuration {

            public static final String FIELD_NAME_SYNC_DURATION = "sync";

            public static final String FIELD_NAME_ASYNC_DURATION = "async";

            @JsonProperty(FIELD_NAME_SYNC_DURATION)
            private final long syncDuration;

            @JsonProperty(FIELD_NAME_ASYNC_DURATION)
            private final long asyncDuration;

            @JsonCreator
            public CheckpointDuration(
                    @JsonProperty(FIELD_NAME_SYNC_DURATION) long syncDuration,
                    @JsonProperty(FIELD_NAME_ASYNC_DURATION) long asyncDuration) {
                this.syncDuration = syncDuration;
                this.asyncDuration = asyncDuration;
            }

            public long getSyncDuration() {
                return syncDuration;
            }

            public long getAsyncDuration() {
                return asyncDuration;
            }

            @Override
            public boolean equals(Object o) {
                if (this == o) {
                    return true;
                }
                if (o == null || getClass() != o.getClass()) {
                    return false;
                }
                CheckpointDuration that = (CheckpointDuration) o;
                return syncDuration == that.syncDuration && asyncDuration == that.asyncDuration;
            }

            @Override
            public int hashCode() {
                return Objects.hash(syncDuration, asyncDuration);
            }
        }

        /** Alignment statistics of the checkpoint. */
        public static final class CheckpointAlignment {

            public static final String FIELD_NAME_ALIGNMENT_BUFFERED = "buffered";

            public static final String FIELD_NAME_ALIGNMENT_PROCESSED = "processed";

            public static final String FIELD_NAME_ALIGNMENT_PERSISTED = "persisted";

            public static final String FIELD_NAME_ALIGNMENT_DURATION = "duration";

            @JsonProperty(FIELD_NAME_ALIGNMENT_BUFFERED)
            private final long alignmentBuffered;

            @JsonProperty(FIELD_NAME_ALIGNMENT_PROCESSED)
            private final long processed;

            @JsonProperty(FIELD_NAME_ALIGNMENT_PERSISTED)
            private final long persisted;

            @JsonProperty(FIELD_NAME_ALIGNMENT_DURATION)
            private final long alignmentDuration;

            @JsonCreator
            public CheckpointAlignment(
                    @JsonProperty(FIELD_NAME_ALIGNMENT_BUFFERED) long alignmentBuffered,
                    @JsonProperty(FIELD_NAME_ALIGNMENT_PROCESSED) long processed,
                    @JsonProperty(FIELD_NAME_ALIGNMENT_PERSISTED) long persisted,
                    @JsonProperty(FIELD_NAME_ALIGNMENT_DURATION) long alignmentDuration) {
                this.alignmentBuffered = alignmentBuffered;
                this.processed = processed;
                this.persisted = persisted;
                this.alignmentDuration = alignmentDuration;
            }

            public long getAlignmentDuration() {
                return alignmentDuration;
            }

            @Override
            public boolean equals(Object o) {
                if (this == o) {
                    return true;
                }
                if (o == null || getClass() != o.getClass()) {
                    return false;
                }
                CheckpointAlignment that = (CheckpointAlignment) o;
                return alignmentBuffered == that.alignmentBuffered
                        && processed == that.processed
                        && persisted == that.persisted
                        && alignmentDuration == that.alignmentDuration;
            }

            @Override
            public int hashCode() {
                return Objects.hash(alignmentBuffered, processed, persisted, alignmentDuration);
            }
        }
    }

    /** Checkpoint statistics for a pending subtask checkpoint. */
    public static final class PendingSubtaskCheckpointStatistics
            extends SubtaskCheckpointStatistics {

        @JsonCreator
        public PendingSubtaskCheckpointStatistics(@JsonProperty(FIELD_NAME_INDEX) int index) {
            super(index, "pending_or_failed");
        }
    }
}
