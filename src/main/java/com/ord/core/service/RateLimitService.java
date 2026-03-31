package com.ord.core.service;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.BucketConfiguration;
import io.github.bucket4j.ConsumptionProbe;
import io.github.bucket4j.distributed.proxy.ProxyManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.Collections;
import java.util.Map;

@Service
public class RateLimitService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private ProxyManager<String> proxyManager;

    @Autowired
    private DefaultRedisScript<Long> tokenBucketScript;


    //region rate-limit sử dụng lua(atomic)
    public boolean allowRequest(String key)
    {
        Long result = redisTemplate.execute(
                tokenBucketScript,
                Collections.singletonList("rate_limit: " + key),
                100, //capacity
                1, // refill rate
                Instant.now().getEpochSecond()

        );
        return result == 1;
    }
    //endregion


    //region rate-limit sử dụng bucket4j
    private Bucket resolveBucket(String key) {

        // limit 100 request / phút
        Bandwidth limit = Bandwidth.builder()
                .capacity(60) //60 token
                .refillGreedy(1, Duration.ofSeconds(1)) //fill 1 token moi giay
                .build();

        // có thể add nhiều limit
        // Bandwidth apiLimit = Bandwidth.simple(20, Duration.ofSeconds(10));

        return proxyManager.builder()
                .build(key, () -> BucketConfiguration.builder()
                        .addLimit(limit)
                        // .addLimit(apiLimit)
                        .build());
    }

    public ConsumptionProbe consume(String key) {
        Bucket bucket = resolveBucket(key);
        return bucket.tryConsumeAndReturnRemaining(1);
    }
    //endregion

}
