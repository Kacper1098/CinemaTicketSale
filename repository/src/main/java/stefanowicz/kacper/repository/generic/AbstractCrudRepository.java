package stefanowicz.kacper.repository.generic;

import com.google.common.base.CaseFormat;
import org.jdbi.v3.core.Jdbi;
import stefanowicz.kacper.exception.AppException;
import stefanowicz.kacper.repository.connection.DbConnection;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public abstract class AbstractCrudRepository<T, ID> implements CrudRepository<T, ID> {

    private final Class<T> entityType = (Class<T>)((ParameterizedType)super.getClass().getGenericSuperclass()).getActualTypeArguments()[0];
    private final Class<ID> idType = (Class<ID>)((ParameterizedType)super.getClass().getGenericSuperclass()).getActualTypeArguments()[1];

    protected final Jdbi jdbi = DbConnection.getConnection().getJdbi();

    @Override
    public Optional<T> add(T t) {
        final String SQL =
                "insert into " + CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, entityType.getSimpleName())
                + createColumnNamesForAdd() + " values " + createColumnValuesForAdd(t) + ";";
        if(jdbi.withHandle(handle -> handle.execute(SQL) >= 1)){
            return findLast();
        }
        return Optional.empty();
    }

    @Override
    public Optional<T> update(T t) {
        ID id = getIdForUpdate(t);
        final String SQL =
                "update " + CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, entityType.getSimpleName())
                + " set " + createColumnNamesAndValuesForUpdate(t)
                + " where id = " + id + ";";
        if(jdbi.withHandle(handle -> handle.execute(SQL)) >= 1){
            return findOne(id);
        }
        return Optional.empty();
    }

    @Override
    public List<T> findAll() {
        final String SQL = "select * from " + CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, entityType.getSimpleName()) + ";";
        return jdbi.withHandle(handle -> handle.createQuery(SQL).mapToBean(entityType).list());
    }

    @Override
    public Optional<T> findOne(ID id) {
        final String SQL = "select * from "
                + CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, entityType.getSimpleName())
                + " where id = :id;";
        return jdbi.withHandle(handle -> handle
                .createQuery(SQL)
                .bind("id", id)
                .mapToBean(entityType)
                .findFirst());
    }

    @Override
    public Optional<T> deleteOne(ID id) {
        final String SQL = "delete from " + CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, entityType.getSimpleName()) + " where id = :id;";
        Optional<T> deletedObject = findOne(id);
        jdbi.useHandle(handle -> handle
                .createUpdate(SQL)
                .bind("id", id)
                .execute());
        return deletedObject;
    }

    @Override
    public boolean deleteAll() {
        final String SQL = "delete from " + CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, entityType.getSimpleName()) + " where id >= 1;";
        return jdbi.withHandle(handle -> handle.createUpdate(SQL).execute() > 0);
    }


    @Override
    public Optional<T> findLast() {
        final String SQL = "select * from " + CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, entityType.getSimpleName()) + " order by id desc limit 1";
        return jdbi.withHandle(handle -> handle.createQuery(SQL).mapToBean(entityType).findFirst());
    }

    private String createColumnNamesForAdd(){
        return " ( " + Arrays
                .stream(entityType.getDeclaredFields())
                .filter(field ->  !field.getName().toLowerCase().equals("id"))
                .map(field -> CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, field.getName()))
                .collect(Collectors.joining(",")) + " ) ";
    }

    private String createColumnValuesForAdd(T t){
        return " ( " + Arrays
                .stream(entityType.getDeclaredFields())
                .filter(field -> !field.getName().toLowerCase().equals("id") )
                .map(field -> {
                    try{
                        field.setAccessible(true);
                        if(field.get(t) == null){
                            return null;
                        }
                        if(field.getType().equals(String.class) || field.getType().equals(LocalDate.class) || field.getType().equals(LocalDateTime.class)){
                            return "'" + field.get(t) + "'";
                        }
                        return field.get(t).toString();
                    }
                    catch (Exception e){
                        throw new AppException("Create colum values for add exception: " + e.getMessage());
                    }
                })
                .collect(Collectors.joining(",")) + " ) ";
    }

    private ID getIdForUpdate(T t){
        try{
            Field field = entityType.getDeclaredField("id");
            field.setAccessible(true);
            return (ID)field.get(t);
        }
        catch (Exception e){
            throw new AppException("GetIdForUpdate method exception - " + e.getMessage());
        }
    }

    private String createColumnNamesAndValuesForUpdate(T t){
        return Arrays
                .stream(entityType.getDeclaredFields())
                .filter(field ->  {
                    try{
                        field.setAccessible(true);
                        return !field.getName().toLowerCase().equals("id") && field.get(t) != null;
                    }
                    catch (Exception e){
                        throw new AppException("CreateColumnNamesAndValuesForUpdate exception - " + e.getMessage());
                    }
                })
                .map(field ->  {
                    try{
                        field.setAccessible(true);
                        if(field.getType().equals(String.class) || field.getType().equals(LocalDate.class)){
                            return CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, field.getName()) + "='" +field.get(t) + "'";
                        }
                        return CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, field.getName()) + "=" + field.get(t).toString();
                    }
                    catch (Exception e){
                        throw new AppException("CreateColumnNamesAndValuesForUpdate exception - " + e.getMessage());
                    }
                })
                .collect(Collectors.joining(","));
    }
}
