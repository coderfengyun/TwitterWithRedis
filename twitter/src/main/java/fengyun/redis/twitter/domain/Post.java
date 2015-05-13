package fengyun.redis.twitter.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Post {
	@Id
	@Column
	private Long id;
	@Column
	private String content;
	@Column
	private Long userId;
	@Column
	private Long replyUserId;
	@Column
	private Long replyPostid;
	@Column
	private long time;

	public Long getId() {
		return id;
	}

	private void setId(Long id) {
		this.id = id;
	}

	public String getContent() {
		return content;
	}

	private void setContent(String content) {
		this.content = content;
	}

	public Long getUserId() {
		return userId;
	}

	private void setUserId(Long userId) {
		this.userId = userId;
	}

	public Long getReplyUserId() {
		return replyUserId;
	}

	private void setReplyUserId(Long replyUserId) {
		this.replyUserId = replyUserId;
	}

	public Long getReplyPostid() {
		return replyPostid;
	}

	private void setReplyPostid(Long replyPostid) {
		this.replyPostid = replyPostid;
	}

	public long getTime() {
		return time;
	}

	private void setTime(long time) {
		this.time = time;
	}

	public Post() {
	}

	public Post(Long id, String content, Long userId, Long replyUserId,
			Long replyPostId) {
		this.setContent(content);
		this.setId(id);
		this.setUserId(userId);
		this.setReplyPostid(replyPostId);
		this.setReplyUserId(replyUserId);
		this.setTime(new Date().getTime());
	}
}
