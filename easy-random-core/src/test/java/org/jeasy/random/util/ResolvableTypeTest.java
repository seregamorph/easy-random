package org.jeasy.random.util;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.Test;

class ResolvableTypeTest {

    @Test
    void collectionTypeShouldBeSupported() throws Exception{
        abstract class Parent<T> {
            private T id;
        }
        class Intermediate<T> extends Parent<T> {
        }
        class Sub extends Intermediate<List<String>> {
        }

        ResolvableType resolvableType = ResolvableType.forField(Parent.class.getDeclaredField("id"), Sub.class);

        assertThat(resolvableType)
                .hasToString("java.util.List<java.lang.String>");
    }

}
