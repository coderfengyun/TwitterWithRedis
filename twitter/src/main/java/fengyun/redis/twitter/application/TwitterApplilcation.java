package fengyun.redis.twitter.application;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
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
			@RequestParam(required = false) Integer age,
			@RequestParam String password) {
		Long currentId = this.userRepository.generateId();
		System.out.println("Current user id is " + currentId);
		this.userRepository.store(new User(currentId, userName, age == null ? 0
				: age, password));
		return true;
	}

	@RequestMapping(value = "post/add")
	@ResponseBody
	public boolean post(@RequestParam String content,
			@RequestParam String userName) {
		Long currentId = this.postRepository.generateId();
		System.out.println(currentId);
		this.postRepository.store(new Post(currentId, content,
				this.userRepository.findBy(userName).get().getId()));
		return true;
	}

	@RequestMapping(value = "post/reply/{topicPostId}/{replyToUserId}")
	@ResponseBody
	public boolean reply(@RequestParam String content,
			@RequestParam String userName, @PathVariable Long replyToUserId,
			@PathVariable Long topicPostId) {
		Long postId = this.postRepository.generateId();
		System.out.println(postId);
		this.postRepository.store(new Post(postId, content, this.userRepository
				.findBy(userName).get().getId(), replyToUserId, topicPostId));
		return true;
	}

	@RequestMapping(value = "pos/relatives/{topicPostId}")
	@ResponseBody
	public List<Post> allRelativePosts(@PathVariable Long topicPostId) {
		return this.postRepository.findAllByUserId(topicPostId);
	}

	@RequestMapping(value = "post/all")
	@ResponseBody
	public List<Post> allPosts(@RequestParam String userName) {
		Long userId = this.userRepository.findBy(userName).get().getId();
		return this.postRepository.findAllByUserId(userId);
	}
}
