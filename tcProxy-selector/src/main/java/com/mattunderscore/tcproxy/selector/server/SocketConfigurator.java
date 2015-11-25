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

package com.mattunderscore.tcproxy.selector.server;

import java.io.IOException;

import com.mattunderscore.tcproxy.io.IOSocket;
import com.mattunderscore.tcproxy.io.IOSocketOption;

/**
 * Apply settings to a socket.
 * @author Matt Champion on 08/11/2015
 */
public final class SocketConfigurator {
    private final SocketSettings settings;

    /**
     * Constructor.
     * @param settings The settings to apply
     */
    public SocketConfigurator(SocketSettings settings) {
        this.settings = settings;
    }

    /**
     * Apply the settings.
     * @param socket The socket to apply the settings to
     * @throws IOException If there was a problem applying the settings
     */
    public <T extends IOSocket> T apply(T socket) throws IOException {
        socket.set(IOSocketOption.RECEIVE_BUFFER, settings.getReceiveBuffer());
        socket.set(IOSocketOption.SEND_BUFFER, settings.getSendBuffer());
        return socket;
    }
}
