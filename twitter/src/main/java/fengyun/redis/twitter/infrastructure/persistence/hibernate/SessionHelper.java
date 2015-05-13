package fengyun.redis.twitter.infrastructure.persistence.hibernate;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;

import fengyun.redis.twitter.domain.User;

public final class SessionHelper {
	private static Class<?>[] ANNOTATED_CLASSES = new Class<?>[] { User.class };
	private SessionFactory sessionFactory;

	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	public SessionHelper() {
		try {
			Configuration cfg = new Configuration()
					.configure("com/microcampus/master/config/hibernate.cfg.xml");
			for (Class<?> annotatedClass : ANNOTATED_CLASSES) {
				cfg.addAnnotatedClass(annotatedClass);
			}
			ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder()
					.applySettings(cfg.getProperties()).build();
			this.setSessionFactory(cfg.buildSessionFactory(serviceRegistry));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Session openSession() {
		try {
			return this.getSessionFactory().openSession();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

}
