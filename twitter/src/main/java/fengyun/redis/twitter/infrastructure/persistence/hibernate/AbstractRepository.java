package fengyun.redis.twitter.infrastructure.persistence.hibernate;

import java.io.Serializable;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Criterion;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class AbstractRepository<Entity, IdType extends Serializable> {
	private SessionHelper sessionHelper;
	private Logger logger = Logger.getLogger(this.getClass());
	private final Class<Entity> clazz;

	protected AbstractRepository(Class<Entity> clazz) {
		this.clazz = clazz;
	}

	SessionHelper getSessionHelper() {
		return sessionHelper;
	}

	@Autowired
	void setSessionHelper(SessionHelper sessionHelper) {
		this.sessionHelper = sessionHelper;
	}

	void releaseSession(Session session) {
		if (session != null) {
			session.close();
		}
	}

	Logger getLogger() {
		return this.logger;
	}

	@SuppressWarnings("unchecked")
	public final IdType attach(Entity dumEntity) {
		Session session = this.sessionHelper.openSession();
		Transaction transaction = session.beginTransaction();
		IdType result = null;
		try {
			Criterion restrictions = getAllUniqueCondition(dumEntity);
			if (restrictions != null
					&& !session.createCriteria(this.clazz).add(restrictions)
							.list().isEmpty()) {
				return null;
			}
			result = (IdType) session.save(dumEntity);
			transaction.commit();
			return result;
		} catch (Exception e) {
			transaction.rollback();
			this.getLogger().error(e, e);
			return null;
		} finally {
			releaseSession(session);
		}
	}

	public final boolean detach(IdType id) {
		Session session = this.sessionHelper.openSession();
		Transaction transaction = session.beginTransaction();
		try {
			@SuppressWarnings("unchecked")
			Entity result = (Entity) session.get(this.clazz, id);
			if (result == null) {
				return false;
			}
			session.delete(result);
			transaction.commit();
			return true;
		} catch (Exception e) {
			transaction.rollback();
			this.logger.error(e, e);
			return false;
		} finally {
			releaseSession(session);
		}
	}

	public final boolean detach(Entity dumEntity) {
		Session session = this.sessionHelper.openSession();
		Transaction transaction = session.beginTransaction();
		try {
			session.delete(dumEntity);
			transaction.commit();
			return true;
		} catch (Exception e) {
			transaction.rollback();
			this.getLogger().error(e, e);
			return false;
		} finally {
			releaseSession(session);
		}
	}

	public boolean update(Entity entity) {
		Session session = this.getSessionHelper().openSession();
		Transaction transaction = session.beginTransaction();
		try {
			session.update(entity);
			transaction.commit();
			return true;
		} catch (Exception e) {
			this.getLogger().error(e, e);
			transaction.rollback();
			return false;
		} finally {
			releaseSession(session);
		}
	}

	public final Entity findBy(IdType id) {
		Session session = this.getSessionHelper().openSession();
		@SuppressWarnings("unchecked")
		Entity result = (Entity) session.get(this.clazz, id);
		return result;
	}

	/**
	 * Find a unique item with the specification of the unique fileds.
	 * 
	 * @param specificationForUniqueConstraints
	 *            ，is the specification of the unique fields of the table
	 * @return the found item
	 */
	public final Entity findBy(Criterion specificationForUniqueConstraints) {
		Session session = this.getSessionHelper().openSession();
		@SuppressWarnings("unchecked")
		Entity result = (Entity) session.createCriteria(this.clazz)
				.add(specificationForUniqueConstraints).uniqueResult();
		return result;
	}

	public List<Entity> findAllBy() {
		return findAllBy(null);
	}

	@SuppressWarnings("unchecked")
	public List<Entity> findAllBy(Criterion specification) {
		List<Entity> result = null;
		Session session = this.getSessionHelper().openSession();
		Criteria criteria = session.createCriteria(this.clazz);
		if (specification != null) {
			criteria.add(specification);
		}
		result = criteria.list();
		return result;
	}

	/**
	 * Here you must make sure that the unique condition will be returned. Such
	 * as User's name, or building's campusId&buildingName
	 * 
	 * @param toAttach
	 * @return
	 */
	protected abstract Criterion getAllUniqueCondition(Entity toAttach);
}
