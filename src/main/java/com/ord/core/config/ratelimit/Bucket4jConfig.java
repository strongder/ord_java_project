package com.ord.core.config.ratelimit;

import io.github.bucket4j.distributed.proxy.ProxyManager;
import io.github.bucket4j.redis.lettuce.cas.LettuceBasedProxyManager;
import io.lettuce.core.AbstractRedisClient;
import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.codec.ByteArrayCodec;
import io.lettuce.core.codec.RedisCodec;
import io.lettuce.core.codec.StringCodec;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;

@Configuration
public class Bucket4jConfig {

//
//    @Bean
//    public ProxyManager<String> proxyManager() {
//
//        RedisClient redisClient = RedisClient.create("redis://localhost:6379");
//        StatefulRedisConnection<String, byte[]> connection =
//                redisClient.connect(RedisCodec.of(StringCodec.UTF8, ByteArrayCodec.INSTANCE));
//
//        return LettuceBasedProxyManager.builderFor(connection).build();
//    }


    // neu da có connect tởi redis thì dùng cách này
    @Bean
    public ProxyManager<String> proxyManager(LettuceConnectionFactory factory) {

        RedisClient redisClient = (RedisClient) factory.getNativeClient();
        assert redisClient != null;
        StatefulRedisConnection<String, byte[]> connection =
                redisClient.connect(RedisCodec.of(StringCodec.UTF8, ByteArrayCodec.INSTANCE));

        return LettuceBasedProxyManager.builderFor(connection).build();
    }

}
