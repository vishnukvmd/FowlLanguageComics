package tech.vishnu.fowllaguagecomics.utils;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;

import java.util.concurrent.Executor;
import java.util.concurrent.ScheduledThreadPoolExecutor;

public class Executors {
    public static final ScheduledThreadPoolExecutor http = getExecutor(2, "http");

    public static final Executor ui = new Executor() {
        @Override
        public void execute(@NonNull Runnable command) {
            if (Looper.myLooper() == Looper.getMainLooper()) {
                command.run();
            } else {
                new Handler(Looper.getMainLooper()).post(command);
            }
        }
    };

    public static ScheduledThreadPoolExecutor getExecutor(int poolSize, String label) {
        return new ScheduledThreadPoolExecutor(poolSize, ThreadFactoryMaker.get(label));
    }
}
