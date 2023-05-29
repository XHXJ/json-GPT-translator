package com.xhxj.jsongpttranslator.translation;

import com.google.common.util.concurrent.RateLimiter;
import com.xhxj.jsongpttranslator.service.openaiproperties.OpenaiPropertiesService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author:luoshuzhong
 * @create: 2023-05-29 13:22
 * @Description: api限制器
 */
@Component
@Slf4j
public class ApiLimiter {
    private final AtomicInteger apiCallCounter = new AtomicInteger(0);
    private final AtomicInteger apiCallRpmCounter = new AtomicInteger(0);
    private final Object lock = new Object();
    private long lastMinuteTimestamp = System.currentTimeMillis();
    private static int MAX_API_CALLS_PER_MINUTE = 3000; // 每分钟的API调用限制 (RPM)
    private static int MAX_TOKENS_PER_MINUTE = 300000; // 每分钟的令牌限制 (TPM)
    private static int MAX_REQUESTS_PER_SECOND = 2; // 每秒最大请求速率

    private final RateLimiter rateLimiter = RateLimiter.create(MAX_REQUESTS_PER_SECOND);

    @Autowired
    private OpenaiPropertiesService openaiPropertiesService;

    @Resource(name = "startFlag")
    private AtomicBoolean startFlag;


    /**
     * api限制设置
     *
     * @param size key的数量
     */
    public void apiRestrictionSettings(int size) {
        //设置翻译api的调用限制为key的数量倍数
        MAX_API_CALLS_PER_MINUTE = size * 3000;
        MAX_TOKENS_PER_MINUTE = size * 300000;
        MAX_REQUESTS_PER_SECOND = size * 5;
    }

    /**
     * 停止限制器
     */
    public void stop() {
        rateLimiter.setRate(Double.MAX_VALUE);
        synchronized (lock) {
            lock.notifyAll();
        }
    }

    /**
     * 开启限制器
     */
    public void start() {
        rateLimiter.setRate(MAX_REQUESTS_PER_SECOND);
        //按key数量设置限制器
        apiRestrictionSettings((int) openaiPropertiesService.count());
    }


    /**
     * 限制每分钟的API调用
     *
     * @param token 消耗的令牌
     */
    public void waitForApiCallAvailability(long token) {

        rateLimiter.acquire(); // 请求一个许可证，根据当前速率限制可能需要等待

        while (startFlag.get()) {
            synchronized (lock) {
                long currentTimestamp = System.currentTimeMillis();
                if (currentTimestamp - lastMinuteTimestamp >= 60000) {
                    // 如果已经过去一分钟，重置计数器和时间戳
                    apiCallCounter.set(0);
                    apiCallRpmCounter.set(0);
                    lastMinuteTimestamp = currentTimestamp;
                }
                if (apiCallCounter.get() + token <= MAX_TOKENS_PER_MINUTE && apiCallRpmCounter.get() < MAX_API_CALLS_PER_MINUTE) {
                    // 如果未超过限制，递增计数器
                    apiCallCounter.addAndGet(Math.toIntExact(token));
                    apiCallRpmCounter.incrementAndGet();
                    break;
                } else {
                    // 如果超过限制，使线程等待直到下一分钟
                    try {
                        long remainingMillis = 60000 - (currentTimestamp - lastMinuteTimestamp);
                        lock.wait(remainingMillis);
                    } catch (InterruptedException e) {
                        log.error("等待时线程异常退出", e);
                    }
                }
            }
        }
    }
}
