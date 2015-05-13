package fengyun.redis.twitter.infrastructure.persistence.redis;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

import redis.clients.jedis.Jedis;

public class RedisHelper {

	private static Jedis jedisSingleton = new Jedis("127.0.0.1", 6379);

	public static Jedis getJedisSingleton() {
		jedisSingleton.auth("123456");
		return jedisSingleton;
	}

	public static class RedisMap implements Map<String, String> {
		private static Jedis jedis = RedisHelper.getJedisSingleton();

		public int size() {
			throw new UnsupportedOperationException(
					"Can't calculate size of key-value in redis");
		}

		public boolean isEmpty() {
			throw new UnsupportedOperationException(
					"Can't know if redis' key-value store is empty");
		}

		public boolean containsKey(Object key) {
			return (jedis.get((String) key) != null);
		}

		public boolean containsValue(Object value) {
			throw new UnsupportedOperationException(
					"This operation in redis a cpu and time consuming operation.");
		}

		public String get(Object key) {
			return jedis.get((String) key);
		}

		public String put(String key, String value) {
			return jedis.set(key, value);
		}

		public String remove(Object key) {
			return jedis.del((String) key).toString();
		}

		public void putAll(Map<? extends String, ? extends String> m) {
			for (String key : m.keySet()) {
				jedis.set(key, m.get(key));
			}
		}

		public void clear() {
			keySet().forEach(t -> jedis.del(t));
		}

		public Set<String> keySet() {
			return jedis.keys(".*");
		}

		public Collection<String> values() {
			Collection<String> result = new LinkedList<String>();
			keySet().forEach(t -> result.add(jedis.get(t)));
			return result;
		}

		public Set<java.util.Map.Entry<String, String>> entrySet() {
			throw new UnsupportedOperationException("no need to support this");
		}
	}

	public static class FieldsHash {
		public void set(String key, String field, String value) {
			RedisHelper.getJedisSingleton().hset(key, field, value);
		}
	}

}
