package com.zhizhuotec.common.redis;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Component;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

@Component
public class RedisService {

	@Autowired
	private JedisPool jedisPool;

	private <T> T execute(RedisCallback<T> action) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return action.doInRedis(jedis);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	/**
	 * get value
	 */
	public String get(final String key) {
		return execute(new RedisCallback<String>() {
			@Override
			public String doInRedis(Jedis jedis) {
				return jedis.get(key);
			}
		});
	}

	/**
	 * set value
	 */
	public String set(final String key, final String value) {
		return execute(new RedisCallback<String>() {
			@Override
			public String doInRedis(Jedis jedis) {
				return jedis.set(key, value);
			}
		});
	}

	/**
	 * 获取hkey里的key对应值
	 */
	public String hget(final String hkey, final String key) {
		return execute(new RedisCallback<String>() {
			@Override
			public String doInRedis(Jedis jedis) {
				return jedis.hget(hkey, key);
			}
		});
	}

	/**
	 * 设置hkey里的key对应值
	 */
	public long hset(final String hkey, final String key, final String value) {
		return execute(new RedisCallback<Long>() {
			@Override
			public Long doInRedis(Jedis jedis) {
				return jedis.hset(hkey, key, value);
			}
		});
	}

	/**
	 * 删除key
	 */
	public long del(final String key) {
		return execute(new RedisCallback<Long>() {
			@Override
			public Long doInRedis(Jedis jedis) {
				return jedis.del(key);
			}
		});
	}

	/**
	 * value值+1
	 */
	public long incr(final String key) {
		return execute(new RedisCallback<Long>() {
			@Override
			public Long doInRedis(Jedis jedis) {
				return jedis.incr(key);
			}
		});
	}

	/**
	 * hkey里的key对应值+value
	 */
	public long hincr(final String hkey, final String key, final Long value) {
		return execute(new RedisCallback<Long>() {
			@Override
			public Long doInRedis(Jedis jedis) {
				return jedis.hincrBy(hkey, key, value);
			}
		});
	}

	/**
	 * set 过期时间(秒)
	 */
	public long expire(final String key, final int second) {
		return execute(new RedisCallback<Long>() {
			@Override
			public Long doInRedis(Jedis jedis) {
				return jedis.expire(key, second);
			}
		});
	}

	/**
	 * get 过期时间(秒)
	 */
	public long ttl(final String key) {
		return execute(new RedisCallback<Long>() {
			@Override
			public Long doInRedis(Jedis jedis) {
				return jedis.ttl(key);
			}
		});
	}

}
