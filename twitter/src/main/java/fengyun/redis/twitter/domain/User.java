package fengyun.redis.twitter.domain;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class User {
	private static final String COLUMN_ID = "id";
	private static final String COLUMN_NAME = "name";
	private static final String COLUMN_AGE = "age";
	private static final String COLUMN_PASSWORD = "password";
	@Id
	@Column(name = COLUMN_ID)
	private Long id;
	@Column(name = COLUMN_NAME)
	private String name;
	@Column(name = COLUMN_AGE)
	private int age;
	@Column(name = COLUMN_PASSWORD)
	private String password;

	public User() {
	}

	public User(Long id, String name, int age, String password) {
		this.setId(id);
		this.setName(name);
		this.setAge(age);
		this.setPassword(password);
	}

	public Long getId() {
		return id;
	}

	private void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	private void setName(String name) {
		this.name = name;
	}

	public int getAge() {
		return age;
	}

	private void setAge(int age) {
		this.age = age;
	}

	public String getPassword() {
		return password;
	}

	private void setPassword(String password) {
		this.password = password;
	}

	public String toString() {
		Map<String, String> kv = new HashMap<String, String>();

		StringBuilder builder = new StringBuilder();
		builder.append('[');
		for (Map.Entry<String, String> entry : kv.entrySet()) {
			builder.append(entry.getKey());
			builder.append(':');
			builder.append(entry.getValue());
			builder.append(',');
		}
		if (builder.charAt(builder.length() - 1) == ',') {
			builder.deleteCharAt(builder.length() - 1);
		}
		builder.append(']');
		return builder.toString();
	}

	public String authorize() {
		return this.getName();
	}

	public boolean follow(Long targetUserId) {
		
		return true;
	}
}
