package fengyun.redis.twitter.infrastructure.persistence.redis;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import fengyun.redis.twitter.domain.Post;
import fengyun.redis.twitter.domain.PostRepository;

public class PostRepositoryByRedis extends AbstractRedisRepsitory<Post, Long>
		implements PostRepository {

	private static final String POST_REPLY_USER_ID_KEY_PREFIX = "post:replyuserid";
	private static final String POST_REPLY_POST_ID_KEY_PREFIX = "post:replypostid:";
	private static final String POST_USERID_INDEX_KEY_PREFIX = "post:userid:";
	private static final String POST_PREFIX = "post:";

	public PostRepositoryByRedis() {
		super(Post.class);
	}

	@Override
	public List<Post> findAllByUserId(Long userId) {
		return this.findAllByIndexKey(toPostUserIdRedisKey(userId));
	}

	public String toPostUserIdRedisKey(Long userId) {
		return POST_USERID_INDEX_KEY_PREFIX + userId;
	}

	@Override
	String toRedisIdKey(Long id) {
		return POST_PREFIX + id;
	}

	@Override
	Long getId(Post entity) {
		return entity.getId();
	}

	@Override
	List<String> getUniquePropertyAsKey(Post entity) {
		return Collections.emptyList();
	}

	@Override
	String getIdRedisKeyFrom(Post entity) {
		return toRedisIdKey(entity.getId());
	}

	@Override
	Long toId(String id) {
		if (id == null) {
			return 0L;
		}
		return Long.valueOf(id);
	}

	@Override
	List<String> getIndexKeysFrom(Post entity) {
		LinkedList<String> result = new LinkedList<String>();
		result.add(toPostUserIdRedisKey(entity.getUserId()));
		if (entity.getReplyUserId() != null) {
			result.add(POST_REPLY_POST_ID_KEY_PREFIX + entity.getReplyPostid());
		}
		if (entity.getReplyUserId() != null) {
			result.add(POST_REPLY_USER_ID_KEY_PREFIX + entity.getReplyUserId());
		}
		return result;
	}

	String getIdGeneratorKey() {
		return "idgenerator:post";
	}

	Long nextId(Long currentId) {
		return currentId + 1;
	}
}
