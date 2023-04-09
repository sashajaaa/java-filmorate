package ru.yandex.practicum.filmorate.storage.interfaces;

import java.util.List;
import java.util.Optional;

public interface InteractionReviewRepository<I, T> {

	Optional<T> unload(T review);

	T update(T t);

	T remove(I id);

	T findById(I id);

	List<T> load(I count);

	List<T> load(I id, I count);

	T like(I to, I from);

	T dislike(I to, I from);

	T removeLike(I to, I from);

	T removeDislike(I to, I from);
}
