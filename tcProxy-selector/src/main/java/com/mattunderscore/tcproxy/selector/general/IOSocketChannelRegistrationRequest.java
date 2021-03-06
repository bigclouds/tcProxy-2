/* Copyright © 2015, 2016 Matthew Champion
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

package com.mattunderscore.tcproxy.selector.general;

import com.mattunderscore.tcproxy.io.selection.IOSelectionKey;
import com.mattunderscore.tcproxy.io.selection.IOSelectionKey.Op;
import com.mattunderscore.tcproxy.io.selection.IOSelector;
import com.mattunderscore.tcproxy.io.socket.IOSocketChannel;
import com.mattunderscore.tcproxy.selector.SelectionRunnable;

import java.nio.channels.ClosedChannelException;
import java.util.Set;

/**
 * {@link RegistrationRequest} of a server runnable for an {@link IOSocketChannel} against multiple operations.
 * @author Matt Champion on 26/10/2015
 */
/*package*/ final class IOSocketChannelRegistrationRequest implements RegistrationRequest {
    private final IOSocketChannel channel;
    private final Set<Op> ops;
    private final SelectionRunnable<IOSocketChannel> runnable;

    IOSocketChannelRegistrationRequest(
            IOSocketChannel channel,
            Set<Op> ops,
            SelectionRunnable<IOSocketChannel> runnable) {
        this.channel = channel;
        this.ops = ops;
        this.runnable = runnable;
    }

    @Override
    public void register(IOSelector selector) throws ClosedChannelException {
        final IOSelectionKey key = channel.keyFor(selector);
        if (key != null) {
            final IOSocketChannelRegistration registration = (IOSocketChannelRegistration) key.attachment();
            for (Op op : ops) {
                registration.addRegistration(op, runnable);
                key.setInterestedOperation(op);
            }
        }
        else {
            final IOSocketChannelRegistration registration = new IOSocketChannelRegistration();
            for (Op op : ops) {
                registration.addRegistration(op, runnable);
            }
            channel.register(selector, ops, registration);
        }
    }
}
