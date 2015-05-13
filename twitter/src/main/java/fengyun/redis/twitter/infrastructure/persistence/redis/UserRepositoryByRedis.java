package fengyun.redis.twitter.infrastructure.persistence.redis;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import fengyun.redis.twitter.domain.User;
import fengyun.redis.twitter.domain.UserRepository;

public class UserRepositoryByRedis extends AbstractRedisRepsitory<User, Long>
		implements UserRepository {

	private static final String IDGENERATOR_USER = "idgenerator:user";
	private static final String USER_PREFIX = "user:";
	private static final String USER_NAME_INDEX_KEY_PREFIX = "user:name:";

	public UserRepositoryByRedis() {
		super(User.class);
	}

	Long toId(String id) {
		if (id == null) {
			return 0L;
		}
		return Long.valueOf(id);
	}

	String toRedisIdKey(Long userId) {
		return USER_PREFIX + userId;
	}

	String getIdRedisKeyFrom(User entity) {
		return toRedisIdKey(entity.getId());
	}

	Long getId(User entity) {
		return entity.getId();
	}

	@Override
	List<String> getUniquePropertyAsKey(User entity) {
		return Arrays.asList(toRedisKey(entity.getName()));
	}

	String toRedisKey(String userName) {
		return USER_NAME_INDEX_KEY_PREFIX + userName;
	}

	@Override
	public Optional<User> findBy(String userName) {
		return this.findByUniquePropertyKey(toRedisKey(userName));
	}

	@Override
	List<String> getIndexKeysFrom(User entity) {
		return Collections.emptyList();
	}

	Long nextId(Long currentId) {
		return currentId + 1;
	}

	String getIdGeneratorKey() {
		return IDGENERATOR_USER;
	}

}
