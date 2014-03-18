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

package com.mattunderscore.tcproxy.proxy;

import com.mattunderscore.tcproxy.io.IOSocketChannel;
import com.mattunderscore.tcproxy.proxy.action.queue.ActionQueue;
import com.mattunderscore.tcproxy.proxy.action.queue.ActionQueueImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * Implementation of {@link com.mattunderscore.tcproxy.proxy.Direction}.
 * @author Matt Champion on 19/02/14.
 */
public class DirectionImpl implements Direction {
    private static final Logger LOG = LoggerFactory.getLogger("direction");
    private final IOSocketChannel from;
    private final IOSocketChannel to;
    private final ConnectionImpl connection;
    private final ActionQueue queue;
    private final String stringValue;
    private final Set<Listener> listeners = new CopyOnWriteArraySet<>();
    private volatile int read;
    private volatile int written;
    private volatile boolean open;

    public DirectionImpl(final IOSocketChannel from, final IOSocketChannel to, final ConnectionImpl connection, final int queueSize) {
        this.from = from;
        this.to = to;
        this.connection = connection;
        this.queue = new ActionQueueImpl(this, connection, queueSize);
        this.read = 0;
        this.written = 0;
        this.open = true;
        this.stringValue = asString();
    }

    @Override
    public IOSocketChannel getFrom() {
        return from;
    }

    @Override
    public IOSocketChannel getTo() {
        return to;
    }

    @Override
    public Connection getConnection() {
        return connection;
    }

    @Override
    public ActionQueue getQueue() {
        return queue;
    }

    @Override
    public int read() {
        return read;
    }

    @Override
    public int written() {
        return written;
    }

    @Override
    public int write(final ByteBuffer destination) throws IOException {
        final int newlyWritten = to.write(destination);
        if (newlyWritten > 0) {
            written += newlyWritten;
            for (final Listener listener : listeners) {
                listener.dataWritten(this, newlyWritten);
            }
        }
        return newlyWritten;
    }

    @Override
    public int read(final ByteBuffer source) throws IOException {
        final int newlyRead = from.read(source);
        if (newlyRead > 0) {
            read += newlyRead;
            for (final Listener listener : listeners) {
                listener.dataRead(this, newlyRead);
            }
        }
        return newlyRead;
    }

    @Override
    public void close() throws IOException {
        if (open) {
            LOG.info("{} : Closed", this);
            to.close();
            open = false;
            connection.partClosed();
            for (final Listener listener : listeners) {
                listener.closed(this);
            }
        }
    }

    @Override
    public void addListener(final Listener listener) {
        listeners.add(listener);
    }

    private String asString() {
        final String unknown = "unknown";
        final String separator = " -> ";
        final StringBuilder builder = new StringBuilder(12 + (4 * 32));
        try {
            builder.append(from.getRemoteAddress().toString());
        }
        catch (final IOException e) {
            builder.append(unknown);
        }
        builder.append(separator);
        try {
            builder.append(from.getLocalAddress().toString());
        }
        catch (final IOException e) {
            builder.append(unknown);
        }
        builder.append(separator);
        try {
            builder.append(to.getLocalAddress().toString());
        }
        catch (final IOException e) {
            builder.append(unknown);
        }
        builder.append(separator);
        try {
            builder.append(to.getRemoteAddress().toString());
        }
        catch (final IOException e) {
            builder.append(unknown);
        }
        return builder.toString();
    }

    @Override
    public String toString() {
        return stringValue;
    }
}
