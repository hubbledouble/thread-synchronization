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

package com.hubbledouble.thread.synchronization.service;

import com.hubbledouble.thread.synchronization.service.impl.LockServiceImpl;
import com.hubbledouble.thread.synchronization.collection.SimulatorProcessCollection;
import com.hubbledouble.thread.synchronization.service.impl.SimulatorProcessServiceImpl;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

public class LockServiceTest {

    private LockService lockService;
    private SimulatorProcessCollection simulatorProcessCollection;
    private static final String PROCESS_NAME = "testingProcessName";
    private static final String SOURCE_ID = "source";

    @Before
    public void setup() {
        simulatorProcessCollection = new SimulatorProcessCollection();
        ProcessService processService = new SimulatorProcessServiceImpl(simulatorProcessCollection.getProcessCollection());
        lockService = new LockServiceImpl(processService);

    }

    @Test
    public void test_acquireLock() {

        Assert.assertTrue(lockService.acquireLock(PROCESS_NAME, SOURCE_ID, null));
        Assert.assertFalse(lockService.acquireLock(PROCESS_NAME, SOURCE_ID, Duration.of(60, ChronoUnit.SECONDS)));
        Assert.assertTrue(lockService.acquireLock(PROCESS_NAME, SOURCE_ID, Duration.of(-60, ChronoUnit.SECONDS)));

    }

    @Test
    public void test_performHeartbeat() {

        lockService.acquireLock(PROCESS_NAME, SOURCE_ID, null);
        String version = simulatorProcessCollection.getProcessCollection().get(PROCESS_NAME).getVersion();
        lockService.performHeartbeat(PROCESS_NAME, SOURCE_ID);
        String nextVersion = simulatorProcessCollection.getProcessCollection().get(PROCESS_NAME).getVersion();
        Assert.assertNotEquals(version, nextVersion);

    }

    @Test
    public void test_releaseLock() {

        lockService.releaseLock(PROCESS_NAME, SOURCE_ID);
        lockService.acquireLock(PROCESS_NAME, SOURCE_ID, null);
        lockService.releaseLock(PROCESS_NAME, SOURCE_ID);
        Assert.assertFalse(simulatorProcessCollection.getProcessCollection().containsKey(PROCESS_NAME));

    }

}