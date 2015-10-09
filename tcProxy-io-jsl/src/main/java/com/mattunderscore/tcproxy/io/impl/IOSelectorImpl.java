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

import com.mattunderscore.tcproxy.io.IOSelectionKey;
import com.mattunderscore.tcproxy.io.IOSelector;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Collections;
import java.util.Set;

/**
 * Implements {@link com.mattunderscore.tcproxy.io.IOSelector}. Delegates to {@link Selector}.
 * @author Matt Champion on 12/03/14.
 */
final class IOSelectorImpl implements IOSelector {
    final Selector selectorDelegate;

    IOSelectorImpl(final Selector selectorDelegate) {
        this.selectorDelegate = selectorDelegate;
    }

    @Override
    public void selectNow() throws IOException {
        selectorDelegate.selectNow();
    }

    @Override
    public Set<IOSelectionKey> selectedKeys() {
        final Set<SelectionKey> keys = selectorDelegate.selectedKeys();
        if (keys.size() == 0) {
            return Collections.emptySet();
        }
        else {
            return new IOSelectionKeyImplSet(keys);
        }
    }

    @Override
    public Set<IOSelectionKey> keys() {
        final Set<SelectionKey> keys = selectorDelegate.keys();
        if (keys.size() == 0) {
            return Collections.emptySet();
        }
        else {
            return new IOSelectionKeyImplSet(keys);
        }
    }

    @Override
    public void close() throws IOException {
        selectorDelegate.close();
    }
}