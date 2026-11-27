package com.udea.parcialFinal.cache;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.BatchStrategies;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.JacksonJsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext.SerializationPair;

@Configuration(proxyBeanMethods = false)
@EnableCaching
class RedisCacheConfig {

	@Bean
	RedisCacheManager cacheManager(RedisConnectionFactory connectionFactory,
			@Value("${app.cache.notas-ttl-segundos}") long ttlSegundos) {
		var serializador = new JacksonJsonRedisSerializer<>(ConsultaNotasCache.Pagina.class);
		var configuracion = RedisCacheConfiguration.defaultCacheConfig()
				.entryTtl(Duration.ofSeconds(ttlSegundos))
				.disableCachingNullValues()
				.serializeValuesWith(SerializationPair.fromSerializer(serializador));
		var writer = RedisCacheWriter.nonLockingRedisCacheWriter(connectionFactory, BatchStrategies.scan(100));

		return RedisCacheManager.builder(writer)
				.withCacheConfiguration(ConsultaNotasCache.CACHE, configuracion)
				.transactionAware()
				.enableStatistics()
				.build();
	}
}
