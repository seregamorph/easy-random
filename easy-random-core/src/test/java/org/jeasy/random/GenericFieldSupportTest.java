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
package org.jeasy.random;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.Serializable;
import org.junit.jupiter.api.Test;

public class GenericFieldSupportTest {

    @Test
    void genericFirstTypeShouldBeCorrectlyPopulated() {
        // given
        EasyRandom easyRandom = new EasyRandom();

        // when
        LongResourceFirstType longResource = easyRandom.nextObject(LongResourceFirstType.class);

        // then
        assertThat(longResource.getId())
                .isInstanceOf(Long.class);
        assertThat(longResource.stringResource.getId())
                .isInstanceOf(String.class);
    }

    @Test
    void genericSecondTypeShouldBeCorrectlyPopulated() {
        // given
        EasyRandom easyRandom = new EasyRandom();

        // when
        StringResourceSecondType stringResource = easyRandom.nextObject(StringResourceSecondType.class);

        // then
        assertThat(stringResource.getId())
                .isInstanceOf(String.class);
        assertThat(stringResource.longResource.getId())
                .isInstanceOf(Long.class);
    }

    @Test
    void testMultipleGenericLevels() {
        abstract class BaseClass<T> {
            private final T x;

            BaseClass(T x) {
                this.x = x;
            }

            public T getX() {
                return x;
            }
        }

        abstract class GenericBaseClass<T, P> extends BaseClass<T> {
            private final P y;

            GenericBaseClass(T x, P y) {
                super(x);
                this.y = y;
            }

            public P getY() {
                return y;
            }
        }

        class Concrete extends GenericBaseClass<String, Long> {
            Concrete(String x, Long y) {
                super(x, y);
            }
        }

        EasyRandom easyRandom = new EasyRandom();

        Concrete concrete = easyRandom.nextObject(Concrete.class);
        assertThat(concrete.getX()).isInstanceOf(String.class);
        assertThat(concrete.getY()).isInstanceOf(Long.class);
    }

    private static abstract class IdResourceFirstType<K, T extends IdResourceFirstType<K, ?>> {

        private K id;

        @SuppressWarnings("unchecked")
        public T setId(K id) {
            this.id = id;
            return (T) this;
        }

        public K getId() {
            return id;
        }
    }

    private static abstract class IdResourceSecondType<T extends IdResourceSecondType<?, K>, K extends Serializable> {

        private K id;

        @SuppressWarnings("unchecked")
        public T setId(K id) {
            this.id = id;
            return (T) this;
        }

        public K getId() {
            return id;
        }
    }

    private static abstract class IntermediateParent<K extends Serializable, T extends IntermediateParent<K, ?>>
            extends IdResourceFirstType<K, T> {

    }

    private static class LongResourceFirstType extends IntermediateParent<Long, LongResourceFirstType> {

        private StringResourceSecondType stringResource;
    }

    private static class StringResourceSecondType extends IdResourceSecondType<StringResourceSecondType, String> {

        private LongResourceFirstType longResource;
    }

}
