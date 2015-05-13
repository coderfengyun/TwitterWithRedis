package fengyun.redis.twitter.application;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import fengyun.redis.twitter.domain.Post;
import fengyun.redis.twitter.domain.PostRepository;
import fengyun.redis.twitter.domain.User;
import fengyun.redis.twitter.domain.UserRepository;
import fengyun.redis.twitter.infrastructure.persistence.redis.PostRepositoryByRedis;
import fengyun.redis.twitter.infrastructure.persistence.redis.UserRepositoryByRedis;

@Controller
@RequestMapping(value = "/")
public class TwitterApplilcation {

	private UserRepository userRepository = new UserRepositoryByRedis();
	private PostRepository postRepository = new PostRepositoryByRedis();

	@RequestMapping(value = "")
	@ResponseBody
	public String home() {
		return "HH";
	}

	@RequestMapping(value = "user/authorize")
	@ResponseBody
	public String login(@RequestParam String userName,
			@RequestParam String password) {
		Optional<User> result = this.userRepository.findBy(userName);
		if (!result.isPresent()) {
			return "wrong user name or password!";
		}
		return result.get().authorize();
	}

	@RequestMapping(value = "user/register")
	@ResponseBody
	public boolean register(@RequestParam String userName,
			@RequestParam int age, @RequestParam String password) {
		this.userRepository.store(new User(1L, userName, age, password));
		return true;
	}

	@RequestMapping(value = "post/add")
	@ResponseBody
	public boolean post(@RequestParam String content,
			@RequestParam String userName) {
		this.postRepository.store(new Post(this.postRepository.generateId(),
				content, this.userRepository.findBy(userName).get().getId(),
				null, null));
		return true;
	}

	@RequestMapping(value = "post/all")
	@ResponseBody
	public List<Post> allPosts(@RequestParam String userName) {
		Long userId = this.userRepository.findBy(userName).get().getId();
		return this.postRepository.findAllBy(userId);
	}
}
