/*
 * The MIT License
 *
 *   Copyright (c) 2020, Mahmoud Ben Hassine (mahmoud.benhassine@icloud.com)
 *
 *   Permission is hereby granted, free of charge, to any person obtaining a copy
 *   of this software and associated documentation files (the "Software"), to deal
 *   in the Software without restriction, including without limitation the rights
 *   to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *   copies of the Software, and to permit persons to whom the Software is
 *   furnished to do so, subject to the following conditions:
 *
 *   The above copyright notice and this permission notice shall be included in
 *   all copies or substantial portions of the Software.
 *
 *   THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *   IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *   FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *   AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *   LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *   OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 *   THE SOFTWARE.
 */
package org.jeasy.random.randomizers.misc;

import org.jeasy.random.api.Randomizer;

/**
 * A {@link Randomizer} that generates constant values. Yeah.. That's not random :-)
 *
 * @author Mahmoud Ben Hassine (mahmoud.benhassine@icloud.com)
 */
public class ConstantRandomizer<T> implements Randomizer<T> {

    private final T value;

    /**
     * Create a new {@link ConstantRandomizer}.
     *
     * @param value the constant value
     */
    public ConstantRandomizer(T value) {
        this.value = value;
    }

    /**
     * Create a new {@link ConstantRandomizer}.
     *
     * @param value the constant value
     * @param <T>   the type of generated elements
     * @return a new {@link ConstantRandomizer}.
     * @deprecated in favor of the equivalent constructor
     */
    @Deprecated
    public static <T> ConstantRandomizer<T> aNewConstantRandomizer(final T value) {
        return new ConstantRandomizer<>(value);
    }

    @Override
    public T getRandomValue() {
        return value;
    }
}
