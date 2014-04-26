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

package com.mattunderscore.tcProxy.gui;

import com.mattunderscore.tcproxy.proxy.ConnectionManager;
import com.mattunderscore.tcproxy.proxy.ProxyServer;
import com.mattunderscore.tcproxy.proxy.settings.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.io.IOException;

/**
 * @author matt on 15/03/14.
 */
public final class MainFrame extends JFrame {
    private static final Logger LOG = LoggerFactory.getLogger("gui");
    private final SettingsPanel settingsPanel;

    public MainFrame() {
        setTitle("tcProxy");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        settingsPanel = new SettingsPanel(new Runnable() {
            @Override
            public void run() {
                remove(settingsPanel);
                try {
                    final AcceptorSettings acceptorSettings = settingsPanel.getAcceptorSettings();
                    final ConnectionSettings connectionSettings = settingsPanel.getConnectionSettings();
                    final InboundSocketSettings inboundSocketSettings = settingsPanel.getInboundSocketSettings();
                    final OutboundSocketSettings outboundSocketSettings = settingsPanel.getOutboundSocketSettings();
                    final ReadSelectorSettings readSelectorSettings = settingsPanel.getReadSelectorSettings();
                    final ConnectionManager manager = new ConnectionManager();
                    try {
                        add(new ConnectionsPanel(manager));
                        validate();
                        final ProxyServer proxy = new ProxyServer(acceptorSettings, connectionSettings, inboundSocketSettings, outboundSocketSettings, readSelectorSettings, manager);
                        proxy.start();
                    }
                    catch (final IOException e) {
                        LOG.warn("Error starting proxy", e);
                    }
                }
                catch (final Exception e) {
                    LOG.warn("Unable to create settings", e);
                }
            }
        });
        add(settingsPanel);
        pack();
    }
}
