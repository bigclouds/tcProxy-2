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

package com.mattunderscore.tcproxy.io.impl;

import java.io.IOException;
import java.net.SocketAddress;

import com.mattunderscore.tcproxy.io.factory.IOFactory;
import com.mattunderscore.tcproxy.io.socket.IOOutboundSocketChannel;
import com.mattunderscore.tcproxy.io.socket.IOSocketChannel;
import com.mattunderscore.tcproxy.io.factory.IOOutboundSocketChannelFactory;
import com.mattunderscore.tcproxy.io.factory.IOOutboundSocketFactory;
import com.mattunderscore.tcproxy.io.configuration.IOOutboundSocketChannelConfiguration;

/**
 * A {@link IOOutboundSocketFactory} implementation for {@link IOSocketChannel}s.
 * @author Matt Champion on 17/10/2015
 */
final class IOOutboundSocketChannelFactoryImpl extends AbstractOutboundSocketFactoryImpl<IOOutboundSocketChannel, IOOutboundSocketChannelConfiguration> implements IOOutboundSocketChannelFactory {

    /*package*/ IOOutboundSocketChannelFactoryImpl(
        IOFactory ioFactory,
        IOOutboundSocketChannelConfiguration configuration) {

        super(ioFactory, configuration);
    }

    @Override
    protected IOOutboundSocketChannelFactoryImpl newBuilder(IOOutboundSocketChannelConfiguration configuration) {
        return new IOOutboundSocketChannelFactoryImpl(ioFactory, configuration);
    }

    @Override
    protected IOOutboundSocketChannel newSocket() throws IOException {
        return ioFactory.openSocket();
    }

    @Override
    public IOOutboundSocketFactory<IOOutboundSocketChannel> noDelay(boolean noDelay) {
        return new IOOutboundSocketChannelFactoryImpl(ioFactory, configuration.noDelay(noDelay));
    }

    @Override
    public IOOutboundSocketFactory<IOOutboundSocketChannel> keepAlive(boolean keepAlive) {
        return new IOOutboundSocketChannelFactoryImpl(ioFactory, configuration.keepAlive(keepAlive));
    }

    @Override
    public IOOutboundSocketFactory<IOOutboundSocketChannel> bind(SocketAddress localAddress) {
        return new IOOutboundSocketChannelFactoryImpl(ioFactory, configuration.bind(localAddress));
    }
}
