package stefanowicz.kacper.repository.generic;

import java.util.List;
import java.util.Optional;

public interface CrudRepository<T, ID> {
    Optional<T> add(T t);
    Optional<T> update(T t);
    List<T> findAll();
    Optional<T> findOne(ID id);
    Optional<T> deleteOne(ID id);
    boolean deleteAll();
    Optional<T> findLast();
}
