package tech.vishnu.fowllaguagecomics.utils;

import android.support.annotation.NonNull;

import java.util.concurrent.ThreadFactory;

public class ThreadFactoryMaker {
    public static ThreadFactory get(final String name) {
        return new ThreadFactory() {
            private int count = 0;

            @Override
            public Thread newThread(@NonNull Runnable runnable) {
                count++;
                return new Thread(runnable, name + "-" + count);
            }
        };
    }
}

