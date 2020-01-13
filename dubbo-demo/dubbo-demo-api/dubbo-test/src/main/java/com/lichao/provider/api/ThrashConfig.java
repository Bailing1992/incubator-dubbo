package com.lichao.provider.api;

import java.util.concurrent.Semaphore;

/**
 * @author guohaoice@gmail.com
 */
public class ThrashConfig {
    public static final ThrashConfig INIT_CONFIG = new ThrashConfig(0, 1600, 50);
    public final long durationInSec;
    public final int averageRTTInMs;
    public final Semaphore permit;

    public ThrashConfig(long durationInSec, int maxConcurrency, int averageRTTInMs) {
        this.durationInSec = durationInSec;
        this.averageRTTInMs = averageRTTInMs;
        this.permit = new Semaphore(maxConcurrency);
    }
    @Override
    public String toString(){
        return "Duration :"+  durationInSec+" averageRTT:"+averageRTTInMs+" maxConcurrency:"+permit.availablePermits();
    }
}
