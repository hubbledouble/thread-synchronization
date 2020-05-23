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

import com.hubbledouble.thread.synchronization.exception.RunnableCodeException;
import com.hubbledouble.thread.synchronization.exception.ThreadSynchronizationException;
import com.hubbledouble.thread.synchronization.service.impl.LockServiceImpl;
import com.hubbledouble.thread.synchronization.collection.SimulatorProcessCollection;
import com.hubbledouble.thread.synchronization.service.impl.SimulatorProcessServiceImpl;
import org.junit.Before;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

public class ThreadProcessorTest {

    private ThreadProcessor threadProcessor;
    private SimulatorProcessCollection simulatorProcessCollection;

    @Before
    public void setup() {
        simulatorProcessCollection = new SimulatorProcessCollection();
        threadProcessor =
                new ThreadProcessor(
                        new LockServiceImpl(
                                new SimulatorProcessServiceImpl(
                                        simulatorProcessCollection.getProcessCollection())));
    }

    @Test(expected = RunnableCodeException.class)
    public void test_execute_CustomCodeThrowException_PropagateAsRunnableCodeException() {
        Set<String> items = new HashSet<>();
        final String addingItemsProcessName = "addItems";
        threadProcessor.execute(addingItemsProcessName, () -> items.add("first_item"));
        threadProcessor.execute(addingItemsProcessName, () -> {
            throw new RuntimeException();
        });
    }

    @Test(expected = ThreadSynchronizationException.class)
    public void test_execute_InternalExceptionSuchAsDBDown_PropagateAsThreadSynchronizationException() {
        Set<String> items = new HashSet<>();
        final String addingItemsProcessName = "addItems";
        threadProcessor =
                new ThreadProcessor(
                        new LockServiceImpl(
                                new SimulatorProcessServiceImpl(null)));
        threadProcessor.execute(addingItemsProcessName, () -> items.add("first_item"));
    }

}