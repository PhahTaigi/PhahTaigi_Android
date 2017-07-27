package com.taccotap.phahtaigi.utils;

public abstract class StoppableRunnable implements Runnable {

    private volatile boolean mIsStopped = false;

    public abstract void stoppableRun();

    public void run() {
        setStopped(false);
        while (!mIsStopped) {
            stoppableRun();
            stop();
        }
    }

    public boolean isStopped() {
        return mIsStopped;
    }

    private void setStopped(boolean isStop) {
        if (mIsStopped != isStop)
            mIsStopped = isStop;
    }

    public void stop() {
        setStopped(true);
    }
}
