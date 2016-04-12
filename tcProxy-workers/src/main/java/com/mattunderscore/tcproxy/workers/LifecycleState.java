/* Copyright © 2015 Matthew Champion
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright
      notice, this list of conditions and the following disclaimer.
 * Redistributions in binary form must reproduce the above copyright
      notice, this list of conditions and the following disclaimer in the
      documentation and/or other materials provided with the distribution.
 * Neither the name of mattunderscore.com nor the
      names of its contributors may be used to endorse or promote products
      derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL MATTHEW CHAMPION BE LIABLE FOR ANY
DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE. */

package com.mattunderscore.tcproxy.workers;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;

import net.jcip.annotations.ThreadSafe;

/**
 * Helper for managing {@link Worker}.
 * @author Matt Champion on 10/11/2015
 */
@ThreadSafe
public final class LifecycleState {
    /*package*/ final AtomicReference<State> state = new AtomicReference<>(State.STOPPED);
    private volatile CountDownLatch readyLatch = new CountDownLatch(1);
    private volatile CountDownLatch stoppedLatch;

    /**
     * @return If currently running
     */
    public boolean isRunning() {
        return state.get() == State.RUNNING;
    }

    /**
     * Call when beginning to start.
     * @throws IllegalStateException If currently running
     */
    public void beginStartup() {
        if (state.compareAndSet(State.STOPPED, State.RUNNING)) {
            stoppedLatch = new CountDownLatch(1);
            readyLatch.countDown();
        }
        else {
            throw new IllegalStateException("Already running");
        }
    }

    /**
     * Call when finished to stopping.
     */
    public void endShutdown() {
        stoppedLatch.countDown();
        readyLatch = new CountDownLatch(1);
        state.set(State.STOPPED);
    }

    /**
     * Call to start the shutdown.
     * @throws IllegalStateException If currently stopped
     */
    public void beginShutdown() {
        if (!state.compareAndSet(State.RUNNING, State.STOPPING)) {
            throw new IllegalStateException("Not currently running");
        }
    }

    /**
     * Wait until running.
     * @throws UncheckedInterruptedException
     */
    public void waitForRunning() {
        try {
            readyLatch.await();
        }
        catch (InterruptedException e) {
            throw new UncheckedInterruptedException(e);
        }
    }

    /**
     * Wait until stopped.
     * @throws UncheckedInterruptedException
     */
    public void waitForStopped() {
        final CountDownLatch latch = this.stoppedLatch;
        if (latch != null) {
            try {
                latch.await();
            }
            catch (InterruptedException e) {
                throw new UncheckedInterruptedException(e);
            }
        }
    }

    /**
     * Possible states in the lifecycle.
     */
    /*package*/ enum State {
        STOPPED,
        RUNNING,
        STOPPING
    }
}