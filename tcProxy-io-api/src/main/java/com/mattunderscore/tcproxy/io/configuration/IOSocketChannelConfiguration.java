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

package com.mattunderscore.tcproxy.io.configuration;

import static com.mattunderscore.tcproxy.io.IOSocketOption.KEEP_ALIVE;
import static com.mattunderscore.tcproxy.io.IOSocketOption.TCP_NO_DELAY;

import java.io.IOException;
import java.net.SocketAddress;

import com.mattunderscore.tcproxy.io.IOSocketChannel;

/**
 * A configuration for a {@link IOSocketChannel}s.
 * @author Matt Champion on 02/12/2015
 */
public final class IOSocketChannelConfiguration extends AbstractIOSocketConfiguration<IOSocketChannel> {
    protected final Boolean keepAlive;
    protected final Boolean noDelay;

    /*package*/ IOSocketChannelConfiguration(
            Integer receiveBuffer,
            Integer sendBuffer,
            boolean blocking,
            Integer linger,
            boolean reuseAddress,
            SocketAddress boundSocket,
            Boolean keepAlive,
            Boolean noDelay) {
        super(receiveBuffer, sendBuffer, blocking, linger, reuseAddress, boundSocket);
        this.keepAlive = keepAlive;
        this.noDelay = noDelay;
    }

    /**
     * @return Instance of {@link IOSocketChannelConfigurationBuilder}
     */
    public static IOSocketChannelConfigurationBuilder builder() {
        return new IOSocketChannelConfigurationBuilder();
    }

    public void apply(IOSocketChannel ioSocketChannel) throws IOException {
        super.apply(ioSocketChannel);

        if (keepAlive != null) {
            ioSocketChannel.set(KEEP_ALIVE, keepAlive);
        }
        if (noDelay != null) {
            ioSocketChannel.set(TCP_NO_DELAY, noDelay);
        }
    }
}