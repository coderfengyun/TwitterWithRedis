package fengyun.redis.twitter.domain;

import java.util.List;
import java.util.Optional;

public interface PostRepository {
	Optional<Post> findBy(Long id);

	List<Post> findAllByUserId(Long userId);

	void store(Post post);

	Long generateId();
}
