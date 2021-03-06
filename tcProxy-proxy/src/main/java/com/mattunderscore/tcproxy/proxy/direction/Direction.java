/* Copyright © 2014 Matthew Champion
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

package com.mattunderscore.tcproxy.proxy.direction;

import java.io.IOException;
import java.nio.ByteBuffer;

import com.mattunderscore.tcproxy.io.socket.IOSocketChannel;
import com.mattunderscore.tcproxy.proxy.action.processor.ActionProcessor;
import com.mattunderscore.tcproxy.proxy.action.processor.ActionProcessorFactory;
import com.mattunderscore.tcproxy.proxy.action.queue.ActionQueue;

/**
 * A direction.
 * @author Matt Champion on 18/02/14.
 */
public interface Direction {

    IOSocketChannel getFrom();

    IOSocketChannel getTo();

    ActionQueue getQueue();

    /**
     * @return The first action processor in the chain to call.
     */
    ActionProcessor getProcessor();

    /**
     * Chain a new action processor.
     * @param processorFactory Factory for the action processor,
     */
    void chainProcessor(ActionProcessorFactory processorFactory);

    /**
     * Removes the last chained action processor.
     */
    void unchainProcessor();

    int read();

    int written();

    int write(ByteBuffer data) throws IOException;

    int read(ByteBuffer data) throws IOException;

    void close() throws IOException;

    void addListener(Listener listener);

    /**
     * Listener for direction events.
     */
    interface Listener {
        void dataRead(Direction direction, int bytesRead);

        void dataWritten(Direction direction, int bytesWritten);

        void closed(Direction direction);
    }
}
