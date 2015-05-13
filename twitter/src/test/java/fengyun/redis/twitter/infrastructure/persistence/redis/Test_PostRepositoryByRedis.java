package fengyun.redis.twitter.infrastructure.persistence.redis;

import static org.junit.Assert.*;

import org.junit.Test;

import fengyun.redis.twitter.domain.Post;
import fengyun.redis.twitter.domain.PostRepository;
import fengyun.redis.twitter.domain.UserRepository;

public class Test_PostRepositoryByRedis {
	PostRepository postRepo = new PostRepositoryByRedis();
	UserRepository userRepos = new UserRepositoryByRedis();

	@Test
	public void testAddPost() {
		Long userId = this.userRepos.findBy("testAdd").get().getId();
		Post underAdd = new Post(postRepo.generateId(), "testAddPost", userId,
				null, null);
		int beforeAddSize = this.postRepo.findAllByUserId(userId).size();
		this.postRepo.store(underAdd);
		int afterAddSize = this.postRepo.findAllByUserId(userId).size();
		assertEquals(beforeAddSize + 1, afterAddSize);
	}
}
