package fengyun.redis.twitter.infrastructure.persistence.redis;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import com.google.gson.Gson;

import redis.clients.jedis.Jedis;

public abstract class AbstractRedisRepsitory<Entity, IdType> {
	Jedis jedis = RedisHelper.getJedisSingleton();
	Gson gson = new Gson();
	private final Class<Entity> clazz;

	AbstractRedisRepsitory(Class<Entity> clazz) {
		this.clazz = clazz;
	}

	public IdType generateId() {
		System.out.println("enter generateId");
		synchronized (AbstractRedisRepsitory.class) {
			String currentIdKey = this.jedis.get(getIdGeneratorKey());
			IdType currentId = toId(currentIdKey);
			IdType result = nextId(currentId);
			this.jedis.set(getIdGeneratorKey(), result.toString());
			return result;
		}
	}

	abstract IdType nextId(IdType currentId);

	abstract String getIdGeneratorKey();

	public Optional<Entity> findBy(IdType id) {
		return Optional
				.ofNullable(deserialize(this.jedis.get(toRedisIdKey(id))));
	}

	abstract String toRedisIdKey(IdType id);

	// This method should be guaranteed as a transaction
	// The storing way is <indexKey:idKey>
	public void store(Entity entity) {
		this.jedis.set(getIdRedisKeyFrom(entity), serialize(entity));
		// Deal with propertyUniqueKey
		for (String propertyUniqueKey : this.getUniquePropertyAsKey(entity)) {
			this.jedis.set(propertyUniqueKey,
					String.valueOf(this.getId(entity)));
		}
		// Deal with indexes
		for (String indexKey : getIndexKeysFrom(entity)) {
			this.jedis.lpush(indexKey, String.valueOf(this.getId(entity)));
		}
	}

	abstract String getIdRedisKeyFrom(Entity entity);

	abstract List<String> getUniquePropertyAsKey(Entity entity);

	abstract IdType getId(Entity entity);

	abstract List<String> getIndexKeysFrom(Entity entity);

	Optional<Entity> findByUniquePropertyKey(String uniquePropertyKey) {
		String userId = this.jedis.get(uniquePropertyKey);
		if (userId == null) {
			return Optional.empty();
		}
		return this.findBy(this.toId(userId));
	}

	abstract IdType toId(String id);

	List<Entity> findAllByIndexKey(String key) {
		List<String> idKeys = this.jedis.lrange(key, 0, 10);
		List<Entity> result = new LinkedList<Entity>();
		for (String idKey : idKeys) {
			Optional<Entity> findBy = this.findBy(this.toId(idKey));
			if (!findBy.isPresent()) {
				continue;
			}
			result.add(findBy.get());
		}
		return result;
	}

	// the following methods is a pair of functions used to Serialization and
	// Deserialization
	private String serialize(Entity entity) {
		return this.gson.toJson(entity);
	}

	private Entity deserialize(String content) {
		return this.gson.fromJson(content, this.clazz);
	}
}
