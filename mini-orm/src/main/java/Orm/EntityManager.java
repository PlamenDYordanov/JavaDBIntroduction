package Orm;

import Anotations.Column;
import Anotations.Entity;
import Anotations.Id;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class EntityManager<E> implements DbContext<E>{
    private Connection connection;

    public EntityManager(Connection connection){
        this.connection = connection;
    }

    @Override
    public boolean persist(E entity) throws IllegalAccessException, SQLException {
        Field primaryKey = getId(entity.getClass());
        primaryKey.setAccessible(true);
        Object value = primaryKey.get(entity);

        if(value == null || (long) value <= 0) {
            return doInsert(entity, primaryKey);
        }
        return doUpdate(entity, primaryKey);
     }

    private boolean doUpdate(E entity, Field primaryKey) {
        return true;
    }

    private boolean doInsert(E entity, Field primary) throws SQLException, IllegalAccessException {
        String tableName = this.getTableName(entity.getClass());
        String tableFields = getColumnsWithoutId(entity.getClass());
        String tableValues = getColumnsValuesWithoutId(entity);

        String insertQuery = String.format("INSERT  INTO %s (%s) VALUES(%s)", tableName, tableFields, tableValues);
        return  connection.prepareStatement(insertQuery).execute();

     }

    private String getColumnsValuesWithoutId(E entity) throws IllegalAccessException {
        Class<?> aClass = entity.getClass();
        List<Field> fields = Arrays.stream(aClass.getDeclaredFields())
                .filter(f -> !f.isAnnotationPresent(Id.class))
                .filter(f -> f.isAnnotationPresent(Column.class))
                .collect(Collectors.toList());

        List<String> values = new ArrayList<>();
        for (Field field : fields) {
            field.setAccessible(true);
            Object o = field.get(entity);
            if(o instanceof String || o instanceof LocalDate) {
                values.add("'" + o.toString() + "'");
            }else  {
                values.add(o.toString());
            }
        }
        return String.join(", ", values);
    }

    private String getColumnsWithoutId(Class<?> aClass) {

        return Arrays.stream(aClass.getDeclaredFields())
                .filter(a -> !a.isAnnotationPresent(Id.class))
                .filter(a -> a.isAnnotationPresent(Column.class))
                .map(m -> m.getAnnotationsByType(Column.class))
                .map(a -> a[0].name())
                .collect(Collectors.joining(", "));
    }

    private String getTableName(Class<?> aClass) {
        Entity[] annotationsByType = aClass.getAnnotationsByType(Entity.class);
        if (annotationsByType.length == 0) {
            throw  new UnsupportedOperationException("Does not exist this entity");
        }


            return annotationsByType[0].name();
    }

    @Override
    public Iterable<E> find(Class<E> table) throws SQLException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        return find(table,null);
    }

    @Override
    public Iterable<E> find(Class<E> table, String where) throws SQLException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        Statement statement = connection.createStatement();
        String tableName = getTableName(table);
        String query = String .format("SELECT * FROM %s %s LIMIT 1", tableName
                ,where != null ? " WHERE " + where : "");
        ResultSet resultSet = statement.executeQuery(query);

        List<E> entities = new ArrayList<>();
        while (resultSet.next()) {
            E entity = table.getDeclaredConstructor().newInstance();
            this.fillEntity(table, resultSet, entity);
            entities.add(entity);
        }

        return entities;
    }

    @Override
    public E findFirst(Class<E> table) throws SQLException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        Statement stmt = connection.createStatement();
        String tableName = getTableName(table);

        String query = String .format("SELECT * FROM %s  LIMIT 1", tableName);

        ResultSet resultSet = stmt.executeQuery(query);
        E entity = table.getDeclaredConstructor().newInstance();
        resultSet.next();
        fillEntity(table, resultSet, entity);
        return entity;
    }

    @Override
    public E findFirst(Class<E> table, String where) throws SQLException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        Statement statement = connection.createStatement();
        String tableName = getTableName(table);

        String query = String .format("SELECT * FROM %s %s LIMIT 1", tableName
                ,where != null ? " WHERE " + where : "");

        ResultSet resultSet = statement.executeQuery(query);
        E entity = table.getDeclaredConstructor().newInstance();
        resultSet.next();
        fillEntity(table, resultSet, entity);
        return entity;
    }
        private void fillEntity(Class<E> table, ResultSet resultSet, E entity) throws SQLException, IllegalAccessException {
        Field[] declaredFields = Arrays.stream(table.getDeclaredFields())
                .toArray(Field[]::new);

        for (Field field : declaredFields) {

            field.setAccessible(true);

            fillField(field, resultSet, entity);
        }

    }

    private void fillField(Field field, ResultSet resultSet, E entity) throws SQLException, IllegalAccessException {
        field.setAccessible(true);

        if(field.getType() == int.class || field.getType() == long.class){
            field.set(entity, resultSet.getInt(field.getName()));
        }else if(field.getType() == LocalDate.class) {
            field.set(entity, LocalDate.parse(resultSet.getString(field.getAnnotation(Column.class).name())));
        }else {
            field.set(entity, resultSet.getString(field.getAnnotation(Column.class).name()));
        }
    }

    private Field getId (Class<?> entity) {
        return Arrays.stream(entity.getDeclaredFields())
                .filter(x -> x.isAnnotationPresent(Id.class))
                .findFirst()
                .orElseThrow(() -> new UnsupportedOperationException("Entity does not have primary key"));
    }
}




