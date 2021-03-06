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

package com.mattunderscore.tcproxy.examples;

import static com.mattunderscore.tcproxy.io.configuration.SocketConfiguration.socketChannel;
import static com.mattunderscore.tcproxy.io.impl.StaticIOFactory.socketFactory;

import java.io.IOException;
import java.net.InetSocketAddress;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mattunderscore.tcproxy.examples.data.RandomDataProducer;
import com.mattunderscore.tcproxy.examples.data.ThrottledDataProducer;
import com.mattunderscore.tcproxy.examples.selector.EchoServer;
import com.mattunderscore.tcproxy.examples.workers.Consumer;
import com.mattunderscore.tcproxy.examples.workers.Producer;
import com.mattunderscore.tcproxy.io.factory.IOOutboundSocketChannelFactory;
import com.mattunderscore.tcproxy.io.socket.IOOutboundSocketChannel;
import com.mattunderscore.tcproxy.proxy.ProxyServerBuilder;
import com.mattunderscore.tcproxy.proxy.settings.OutboundSocketSettings;
import com.mattunderscore.tcproxy.selector.server.AcceptSettings;
import com.mattunderscore.tcproxy.selector.server.Server;

/**
 * Example combining proxy and echo servers for bidirectional proxing.
 * @author Matt Champion on 28/11/2015
 */
public final class BiDirectionalExample {
    private static final Logger LOG = LoggerFactory.getLogger("example");

    public static void main(String[] args) throws IOException {
        // Start the proxy
        final Server proxyServer = ProxyServerBuilder
            .builder()
            .acceptSettings(
                AcceptSettings
                    .builder()
                    .listenOn(8085)
                    .build())
            .outboundSocketSettings(
                OutboundSocketSettings
                    .builder()
                    .port(8080)
                    .host("localhost")
                    .receiveBuffer(1024)
                    .sendBuffer(1024)
                    .build())
            .build();
        proxyServer.start();

        // Start the echo server
        final Server echoSever = EchoServer.builder()
            .acceptSettings(
                AcceptSettings
                    .builder()
                    .listenOn(8080)
                    .build())
            .selectorThreads(1)
            .socketSettings(
                socketChannel()
                    .receiveBuffer(1024)
                    .sendBuffer(1024))
            .build();
        echoSever.start();

        proxyServer.waitForRunning();
        echoSever.waitForRunning();

        try {
            // Start client
            final IOOutboundSocketChannel clientChannel = socketFactory(IOOutboundSocketChannelFactory.class).create();
            clientChannel.connect(new InetSocketAddress("localhost", 8085));
            // Start a producer
            final Producer producer = new Producer(
                clientChannel,
                new ThrottledDataProducer(
                    1000L,
                    new RandomDataProducer(8, 32)));
            producer.start();
            final Consumer consumer = new Consumer(clientChannel);
            consumer.start();
        }
        catch (IOException e) {
            LOG.warn("Failed to start client", e);
            echoSever.stop();
            proxyServer.stop();
            throw e;
        }

        // Keep going
        System.out.print("Press return to stop: ");
        System.in.read();
        proxyServer.stop();
    }
}
