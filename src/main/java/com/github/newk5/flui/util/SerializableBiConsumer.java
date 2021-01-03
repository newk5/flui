package com.github.newk5.flui.util;

import java.io.Serializable;
import java.util.function.BiConsumer;



public interface SerializableBiConsumer<T, U> extends BiConsumer<T, U>, Serializable {
}
