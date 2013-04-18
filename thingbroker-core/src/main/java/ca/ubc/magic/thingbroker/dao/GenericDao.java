package ca.ubc.magic.thingbroker.dao;

import java.util.List;

public interface GenericDao<T> {
	T create(T object);
	T update(T object);
	void delete(String id);
	T find(String id);
	List<T> findAll(int offset, int limit);
}
