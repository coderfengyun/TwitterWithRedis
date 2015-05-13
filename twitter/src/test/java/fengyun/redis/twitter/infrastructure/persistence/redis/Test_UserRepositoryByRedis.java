package fengyun.redis.twitter.infrastructure.persistence.redis;

import static org.junit.Assert.*;

import java.util.Optional;

import org.junit.Test;

import fengyun.redis.twitter.domain.User;

public class Test_UserRepositoryByRedis {
	UserRepositoryByRedis userRepo = new UserRepositoryByRedis();

	@Test
	public void testAdd() {
		long id = this.userRepo.generateId();
		String userName = "testAdd";
		User case1 = new User(id, userName, 12, "123456");
		this.userRepo.store(case1);
		Optional<User> findById = this.userRepo.findBy(id);
		assertTrue(findById.isPresent());
		assertEquals(userName, findById.get().getName());
		Optional<User> findByName = this.userRepo.findBy(userName);
		assertTrue(findByName.isPresent());
		assertEquals(userName, findByName.get().getName());
	}
}
