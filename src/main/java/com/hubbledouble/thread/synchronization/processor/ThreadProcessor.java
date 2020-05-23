/*
 *    Copyright (c) 2020, HubbleDouble
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.hubbledouble.thread.synchronization.processor;

import com.hubbledouble.thread.synchronization.RunnableCode;
import com.hubbledouble.thread.synchronization.exception.RunnableCodeException;
import com.hubbledouble.thread.synchronization.exception.ThreadSynchronizationException;
import com.hubbledouble.thread.synchronization.service.LockService;

import java.lang.management.ManagementFactory;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * Thread process. Main login to handle the processing including the locking mechanism on a high level as well
 * as incorporating the {@link RunnableCode} provided by the consumer of this code.
 *
 * @author Jorge Saldivar
 */
public class ThreadProcessor {

    private final LockService lockService;

    private final Map<String, ScheduledFuture<?>> processHeartbeatFutures;
    private final ScheduledExecutorService scheduledExecutorService;
    private static final Long HEARTBEAT_IN_SECONDS = 10l;
    private static final Long LOCK_DURATION_IN_SECONDS = 30l;

    public ThreadProcessor(LockService lockService) {
        this.lockService = lockService;
        this.processHeartbeatFutures = new HashMap<>();
        this.scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
    }

    public boolean execute(String processName, RunnableCode runnableCode) {

        final String processSourceId = getSourceId();

        try {

            if (lockService.acquireLock(processName, processSourceId, Duration.of(LOCK_DURATION_IN_SECONDS, ChronoUnit.SECONDS))) {

                processHeartbeatFutures.put(
                        processName,
                        scheduledExecutorService.scheduleWithFixedDelay(
                                () -> lockService.performHeartbeat(processName, processSourceId),
                                HEARTBEAT_IN_SECONDS,
                                HEARTBEAT_IN_SECONDS,
                                TimeUnit.SECONDS
                        )
                );

                runnableCodeExecution(runnableCode);

                return true;

            }

        } catch (RunnableCodeException e) {

            throw new RunnableCodeException(e);

        } catch (Exception e) {

            throw new ThreadSynchronizationException(e);

        } finally {

            clearProcessHeartbeat(processName);
            lockService.releaseLock(processName, processSourceId);

        }

        return false;

    }

    private void runnableCodeExecution(RunnableCode runnableCode) {

        try {
            runnableCode.execute();
        } catch (Exception e) {
            throw new RunnableCodeException(e);
        }

    }

    private String getSourceId() {
        return ManagementFactory.getRuntimeMXBean().getName();
    }

    private void clearProcessHeartbeat(String processName) {

        if (!processHeartbeatFutures.isEmpty() &&
                processHeartbeatFutures.containsKey(processName) &&
                !processHeartbeatFutures.get(processName).isCancelled()) {

            processHeartbeatFutures.get(processName).cancel(true);

        }

    }

}