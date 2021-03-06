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

import static com.mattunderscore.tcproxy.io.impl.IOUtils.mapToIntFromOp;
import static com.mattunderscore.tcproxy.io.impl.IOUtils.mapToIntFromOps;
import static com.mattunderscore.tcproxy.io.impl.IOUtils.mapToOpsFromInt;

import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Set;

import com.mattunderscore.tcproxy.io.selection.IOSelectionKey;
import com.mattunderscore.tcproxy.io.socket.IOSocket;

/**
 * Implementation of {@link IOSelectionKey}. Delegates to {@link SelectionKey}.
 * @author Matt Champion on 12/03/14.
 */
final class IOSelectionKeyImpl implements IOSelectionKey {
    final SelectionKey keyDelegate;

    IOSelectionKeyImpl(final SelectionKey keyDelegate) {
        this.keyDelegate = keyDelegate;
    }

    @Override
    public boolean isValid() {
        return keyDelegate.isValid();
    }

    @Override
    public boolean isAcceptable() {
        return keyDelegate.isAcceptable();
    }

    @Override
    public boolean isConnectable() {
        return keyDelegate.isConnectable();
    }

    @Override
    public boolean isReadable() {
        return keyDelegate.isReadable();
    }

    @Override
    public boolean isWritable() {
        return keyDelegate.isWritable();
    }

    @Override
    public void cancel() {
        keyDelegate.cancel();
    }

    @Override
    public Object attachment() {
        return keyDelegate.attachment();
    }

    @Override
    public Set<Op> interestedOperations() {
        return mapToOpsFromInt(keyDelegate.interestOps());
    }

    @Override
    public Set<Op> readyOperations() {
        return mapToOpsFromInt(keyDelegate.interestOps());
    }

    @Override
    public void interestedOperations(Set<Op> ops) {
        keyDelegate.interestOps(mapToIntFromOps(ops));
    }

    @Override
    public void setInterestedOperation(Op op) {
        keyDelegate.interestOps(keyDelegate.interestOps() | mapToIntFromOp(op));
    }

    @Override
    public void clearInterestedOperation(Op op) {
        if (keyDelegate.isValid()) {
            keyDelegate.interestOps(keyDelegate.interestOps() & ~mapToIntFromOp(op));
        }
    }

    @Override
    public IOSocket socket() {
        final SelectableChannel channel = keyDelegate.channel();
        if (channel instanceof ServerSocketChannel) {
            return new IOServerSocketChannelImpl((ServerSocketChannel) channel);
        }
        else if (channel instanceof SocketChannel) {
            return new IOSocketChannelImpl((SocketChannel) channel);
        }
        else {
            throw new IllegalStateException("Unsupported channel type");
        }
    }

    @Override
    public int hashCode() {
        return keyDelegate.hashCode();
    }

    @Override
    public boolean equals(final Object o) {
        if (o != null && o instanceof IOSelectionKeyImpl) {
            final IOSelectionKeyImpl key = (IOSelectionKeyImpl)o;
            return keyDelegate.equals(key.keyDelegate);
        }
        else {
            return false;
        }
    }
}
