package com.github.newk5.flui.util;

import java.io.Serializable;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 *
 * @author newk
 * @param <T>
 */
public interface SerializableBiConsumer<T, U> extends BiConsumer<T, U>, Serializable {
}
