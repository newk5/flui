package com.github.newk5.flui.util;

import java.io.Serializable;
import java.util.function.Consumer;

/**
 *
 * @author newk
 * @param <T>
 */
public interface SerializableConsumer<T> extends Consumer<T>, Serializable {
}
