package com.mattunderscore.tcproxy.examples.workers;

import java.io.IOException;
import java.util.Queue;

import com.mattunderscore.tcproxy.io.IOServerSocketChannel;
import com.mattunderscore.tcproxy.io.IOSocketChannel;

/**
 * Acceptor worker. Performs a block accept on a server socket channel and passes the opened sockets to a blocking
 * queue.
 * @author Matt Champion on 10/10/2015
 */
public final class Acceptor extends AbstractWorker {
    private final IOServerSocketChannel channel;
    private final Queue<IOSocketChannel> channels;

    /**
     * Constructor.
     * @param channel The channel to accept from
     * @param channels The queue to put accepted channels onto
     */
    public Acceptor(IOServerSocketChannel channel, Queue<IOSocketChannel> channels) {
        super("example-acceptor");
        this.channel = channel;
        this.channels = channels;
    }

    @Override
    public void doWork() throws IOException {
        channels.add(channel.accept());
    }
}