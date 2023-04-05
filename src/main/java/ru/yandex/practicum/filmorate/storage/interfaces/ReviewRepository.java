package ru.yandex.practicum.filmorate.storage.interfaces;

import java.util.List;

public interface ReviewRepository<I, T> {

	T unload(T review);

	T update(T t);

	T remove(I id);

	T findById (I id);

	List<T> load(I count);

	List<T> load(I id, I count);
}
