package fengyun.redis.twitter.infrastructure.persistence.redis;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import com.google.gson.Gson;
import redis.clients.jedis.Jedis;

/**
 * This redisList only support for sequential operation, but not the random
 * access operation, all the random access operation will throw
 * {@link UnsupporedOperationException}
 * 
 * @author chentienan
 *
 * @param <E>
 */
public class RedisList<E> implements List<E> {
	private Jedis jedis = RedisHelper.getJedisSingleton();
	private final String boundedKey;
	private final Class<E> itemClass;
	private Gson serializer = new Gson();

	public RedisList(String boundedKey, Class<E> itemClass) {
		this.boundedKey = boundedKey;
		this.itemClass = itemClass;
	}

	public int size() {
		return this.jedis.llen(this.boundedKey).intValue();
	}

	public boolean isEmpty() {
		return this.jedis.llen(this.boundedKey) == 0L;
	}

	@SuppressWarnings("unchecked")
	/**
	 * This method will get all the items back from the redis, and compare with the serialized o.
	 * It's a memory-consuming operation, I suggest not to use it.
	 * 
	 * And maybe, in the next edition this method will be decorated
	 */
	public boolean contains(Object o) {
		if (!o.getClass().equals(this.itemClass)) {
			return false;
		}
		String targetContent = serialize((E) o);
		List<String> result = getAll();
		for (String item : result) {
			// check for every item
			if (item.equals(targetContent)) {
				return true;
			}
		}
		return false;
	}

	private List<String> getAll() {
		int length = this.jedis.llen(this.boundedKey).intValue();
		List<String> result = this.jedis.lrange(boundedKey, 0, length - 1);
		return result;
	}

	private String serialize(E objectValue) {
		return serializer.toJson(objectValue);
	}

	private E deserialize(String content) {
		return serializer.fromJson(content, this.itemClass);
	}

	private List<E> deserializeAll(List<String> contentList) {
		List<E> result = new LinkedList<E>();
		contentList.parallelStream().forEach(t -> result.add(deserialize(t)));
		return result;
	}

	@Override
	public Iterator<E> iterator() {
		return null;
	}

	public Object[] toArray() {
		return deserializeAll(this.getAll()).toArray();
	}

	@SuppressWarnings("unchecked")
	public <T> T[] toArray(T[] a) {
		List<T> result = new LinkedList<T>();
		deserializeAll(this.getAll()).parallelStream().forEach(
				t -> result.add((T) t));
		return (T[]) result.toArray();
	}

	public boolean add(E e) {
		return this.jedis.rpush(boundedKey, serialize(e)) != null;
	}

	/**
	 * This is a time consuming operation, as well as a memory consuming
	 * operation, so strongly recommend not to use this function.
	 * 
	 * In the next edition, this operation will be decorated.
	 */

	public boolean remove(Object o) {
		return false;
	}

	/**
	 * This one is time and memory consuming too.
	 */
	public boolean containsAll(Collection<?> c) {
		for (Object e : c) {
			if (!e.getClass().isAssignableFrom(itemClass)) {
				return false;
			}
			@SuppressWarnings("unchecked")
			E value = (E) e;
			if (!contains(value)) {
				return false;
			}
		}
		return true;
	}

	public boolean addAll(Collection<? extends E> c) {
		c.parallelStream().forEach(t -> this.add(t));
		return true;
	}

	public boolean addAll(int index, Collection<? extends E> c) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		for (Object item : c) {
			if (!item.getClass().isAssignableFrom(this.itemClass)) {
				return false;
			}
			@SuppressWarnings("unchecked")
			E value = (E) item;
			if (this.contains(value)) {
				this.jedis.lrem(this.boundedKey, 1L, serialize(value));
			}
		}
		return false;
	}

	public boolean retainAll(Collection<?> c) {
		throw new UnsupportedOperationException();
	}

	public void clear() {
		for (int i = 0; i < this.size(); i++) {
			this.jedis.lpop(this.boundedKey);
		}
	}

	/**
	 * Don't support this method
	 */
	public E get(int index) {
		throw new UnsupportedOperationException();
	}

	/**
	 * Don't support this method
	 */
	public E set(int index, E element) {
		throw new UnsupportedOperationException();
	}

	/**
	 * Don't support this method
	 */
	public void add(int index, E element) {
		throw new UnsupportedOperationException();
	}

	/**
	 * Don't support this method
	 */
	public E remove(int index) {
		throw new UnsupportedOperationException();
	}

	@Override
	public int indexOf(Object o) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int lastIndexOf(Object o) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public ListIterator<E> listIterator() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ListIterator<E> listIterator(int index) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<E> subList(int fromIndex, int toIndex) {
		// TODO Auto-generated method stub
		return null;
	}

}
