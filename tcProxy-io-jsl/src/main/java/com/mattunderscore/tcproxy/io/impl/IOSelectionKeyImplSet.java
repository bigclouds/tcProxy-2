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

import java.nio.channels.SelectionKey;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

/**
 * Implementation of Set for IOSelectionKey that delegates to a Set for SelectionKey.
 * @author Matt Champion on 28/03/14.
 */
final class IOSelectionKeyImplSet implements Set<IOSelectionKey> {
    private final Set<SelectionKey> setDelegate;

    IOSelectionKeyImplSet(Set<SelectionKey> setDelegate) {
        this.setDelegate = setDelegate;
    }

    @Override
    public int size() {
        return setDelegate.size();
    }

    @Override
    public boolean isEmpty() {
        return setDelegate.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        if (o != null && o instanceof IOSelectionKeyImpl) {
            final IOSelectionKeyImpl key = (IOSelectionKeyImpl)o;
            return setDelegate.contains(key.keyDelegate);
        }
        else {
            return false;
        }
    }

    @Override
    public Iterator<IOSelectionKey> iterator() {
        return new IOSelectionKeyImplIterator(setDelegate.iterator());
    }

    @Override
    public Object[] toArray() {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean add(IOSelectionKey ioSelectionKey) {
        throw new UnsupportedOperationException("This set is immutable");
    }

    @Override
    public boolean remove(Object o) {
        throw new UnsupportedOperationException("This set is immutable");
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return false;
    }

    @Override
    public boolean addAll(Collection<? extends IOSelectionKey> c) {
        throw new UnsupportedOperationException("This set is immutable");
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        throw new UnsupportedOperationException("This set is immutable");
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        throw new UnsupportedOperationException("This set is immutable");
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException("This set is immutable");
    }

    /**
     * Iterator for the set.
     */
    private static final class IOSelectionKeyImplIterator implements Iterator<IOSelectionKey> {
        private final Iterator<SelectionKey> iteratorDelegate;

        private IOSelectionKeyImplIterator(Iterator<SelectionKey> iteratorDelegate) {
            this.iteratorDelegate = iteratorDelegate;
        }

        @Override
        public boolean hasNext() {
            return iteratorDelegate.hasNext();
        }

        @Override
        public IOSelectionKey next() {
            return new IOSelectionKeyImpl(iteratorDelegate.next());
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("This set is immutable");
        }
    }
}