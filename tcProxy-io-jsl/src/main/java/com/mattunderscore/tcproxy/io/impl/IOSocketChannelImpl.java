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

package com.mattunderscore.tcproxy.io.impl;

import com.mattunderscore.tcproxy.io.data.CircularBuffer;
import com.mattunderscore.tcproxy.io.selection.IOSelectionKey;
import com.mattunderscore.tcproxy.io.selection.IOSelector;
import com.mattunderscore.tcproxy.io.socket.IOOutboundSocketChannel;
import com.mattunderscore.tcproxy.io.socket.IOSocketOption;

import java.io.IOException;
import java.net.SocketAddress;
import java.net.StandardSocketOptions;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.Set;

/**
 * Delegates to {@link SocketChannel}.
 * @author Matt Champion on 12/03/14.
 */
final class IOSocketChannelImpl implements IOOutboundSocketChannel {
    private final SocketChannel channel;

    IOSocketChannelImpl(final SocketChannel channel) {
        this.channel = channel;
    }

    @Override
    public int read(final ByteBuffer dst) throws IOException {
        return channel.read(dst);
    }

    @Override
    public int write(final ByteBuffer src) throws IOException {
        return channel.write(src);
    }

    @Override
    public boolean isOpen() {
        return channel.isOpen();
    }

    @Override
    public void close() throws IOException {
        channel.close();
    }

    @Override
    public <T> void set(final IOSocketOption<T> option, final T value) throws IOException {
        IOUtils.convertSocketOption(option).apply(channel, value);
    }

    @Override
    public <T> T get(IOSocketOption<T> option) throws IOException {
        return IOUtils.convertSocketOption(option).lookup(channel);
    }

    @Override
    public void abort() throws IOException {
        // http://docs.oracle.com/javase/8/docs/technotes/guides/net/articles/connection_release.html
        channel.setOption(StandardSocketOptions.SO_LINGER, 0);
        channel.close();
    }

    @Override
    public void bind(final SocketAddress localAddress) throws IOException {
        channel.bind(localAddress);
    }

    @Override
    public boolean connect(final SocketAddress remoteAddress) throws IOException {
        return channel.connect(remoteAddress);
    }

    @Override
    public boolean finishConnect() throws IOException {
        return channel.finishConnect();
    }

    @Override
    public IOSelectionKey register(final IOSelector selector, final IOSelectionKey.Op op, final Object att) throws ClosedChannelException {
        final IOSelectorImpl selectorImpl = (IOSelectorImpl)selector;
        return new IOSelectionKeyImpl(channel.register(selectorImpl.selectorDelegate, IOUtils.mapToIntFromOp(op), att));
    }

    @Override
    public IOSelectionKey register(final IOSelector selector, final Set<IOSelectionKey.Op> ops, final Object att) throws ClosedChannelException {
        final IOSelectorImpl selectorImpl = (IOSelectorImpl)selector;
        return new IOSelectionKeyImpl(channel.register(selectorImpl.selectorDelegate, IOUtils.mapToIntFromOps(ops), att));
    }

    @Override
    public IOSelectionKey keyFor(IOSelector selector) {
        final IOSelectorImpl selectorImpl = (IOSelectorImpl)selector;
        final SelectionKey keyDelegate = channel.keyFor(selectorImpl.selectorDelegate);
        if (keyDelegate == null) {
            return null;
        }
        else {
            return new IOSelectionKeyImpl(keyDelegate);
        }
    }

    @Override
    public int read(CircularBuffer dst) throws IOException {
        final CircularBufferImpl dstImpl = (CircularBufferImpl)dst;
        return dstImpl.doSocketRead(channel);
    }

    @Override
    public int write(CircularBuffer src) throws IOException {
        final CircularBufferImpl srcImpl = (CircularBufferImpl)src;
        return srcImpl.doSocketWrite(channel);
    }

    @Override
    public SocketAddress getRemoteAddress() throws IOException {
        return channel.getRemoteAddress();
    }

    @Override
    public SocketAddress getLocalAddress() throws IOException {
        return channel.getLocalAddress();
    }

    @Override
    public String toString() {
        String localAddress;
        try {
            localAddress = getLocalAddress().toString();
        }
        catch (IOException e) {
            localAddress = "unknown local address";
        }
        String remoteAddress;
        try {
            remoteAddress = getRemoteAddress().toString();
        }
        catch (IOException e) {
            remoteAddress = "unknown removeAddress";
        }
        return String.format("IOSocketChannel %s|%s", localAddress, remoteAddress);
    }

    @Override
    public int hashCode() {
        return channel.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof IOSocketChannelImpl)) {
            return false;
        }
        else {
            return channel.equals(((IOSocketChannelImpl) obj).channel);
        }
    }
}
