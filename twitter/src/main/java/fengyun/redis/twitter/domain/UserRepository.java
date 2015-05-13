package fengyun.redis.twitter.domain;

import java.util.Optional;

public interface UserRepository {
	public Optional<User> findBy(Long id);

	public Optional<User> findBy(String userName);

	public void store(User user);

	Long generateId();

}
