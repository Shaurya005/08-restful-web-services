package com.in28minutes.rest.webservices.restfulwebservices.filtering;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.sql.Timestamp;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class JavaSQL {

    public static final int DEFAULT_LIMIT = 100;
    public static final long MAX_SYNC_CLOCK_SKEW = 300000l;
    public static final String TABLE_NAME = "{tableName}";
    public static final String LIMIT = "{limit}";
    public static final String OFFSET = "{offset}";
    public static final String BEFORE_CONDITION = "{beforeCondition}";
    public static final String DELETED_TIME_CONDITION = "{deletedTimeCondition}";
    public static final String IS_NOT_NULL = "IS NOT NULL";
    public static final String IS_NULL = "IS NULL";
    public static final String LESS_EQUALS = "<=";
    public static final String LESS = "<";

    private static EntityManager entityManager;

    private static final String SELECT_TOKEN = "SELECT token, COUNT(*) AS count FROM ( " +
            "SELECT alias.modified_time AS token " +
            "FROM {tableName} AS alias ";

    private static final String JOIN_MENU = "INNER JOIN menu m ON alias.menu_id = m.id WHERE m.menu_type in (?, ?) ";
    private static final String WITHOUT_JOIN_MENU = "WHERE alias.menu_type in (?, ?) ";

    private static final String WHERE_CONDITIONS = "AND alias.merchant_id = ? " +
            "AND alias.modified_time >= ? AND alias.modified_time {beforeCondition} ? " + "AND alias.deleted_time {deletedTimeCondition} " +
            "ORDER BY alias.modified_time " +
            "LIMIT {limit} OFFSET {offset} ) base " +
            "GROUP BY token " +
            "ORDER BY token ASC";

    @Autowired
    public JavaSQL(EntityManager entityManager) {
        JavaSQL.entityManager = entityManager;
    }

    public static void main(String args[]) {
        String sql = buildBucketSql(true);

        String sql2 = buildBucketSql(false);

        RequestFilter filter = new RequestFilter();
        filter.setLimit(3);
        filter.setTableName("MENU");
        filter.setMerchantId("TGSSTRHGBBVS");
        filter.setDeletedOnly(false);
        filter.setOffset(0);
        filter.setBeforeAndMostRecentEqual(true);


        String finalSql = sql
                .replace(TABLE_NAME, filter.getTableName())
                .replace(LIMIT, String.valueOf(filter.getLimit()))
                .replace(OFFSET, String.valueOf(filter.getOffset()))
                .replace(DELETED_TIME_CONDITION, filter.isDeletedOnly() ? IS_NOT_NULL : IS_NULL)
                .replace(BEFORE_CONDITION, filter.isBeforeAndMostRecentEqual() ? LESS_EQUALS : LESS);

        System.out.println(sql);
        System.out.println(sql2);

        executeNativeQuery(sql, "CRSVETA", "DEFAULT_POS_MENU", "POS_MENU", filter.getModifiedSince(),
                filter.getModifiedBefore());
    }

    private static String buildBucketSql(boolean joinMenu) {
        StringBuilder queryBuilder = new StringBuilder();
        queryBuilder.append(SELECT_TOKEN);
        if (joinMenu) {
            queryBuilder.append(JOIN_MENU);
        } else {
            queryBuilder.append(WITHOUT_JOIN_MENU);
        }
        return queryBuilder.append(WHERE_CONDITIONS).toString();
    }

    private static List<TokenBucket> executeNativeQuery(String sql, Object... parameters) {
        Query query = entityManager.createNativeQuery(sql);
        System.out.println(query.getParameters().stream().toList());
        for (int i = 0; i < parameters.length; i++) {
            query.setParameter(i + 1, parameters[i]);
        }

        System.out.println(query.getParameters().stream().toList());

        List<Object[]> resultList = query.getResultList();
        return resultList.stream()
                .map(objects -> {
                    TokenBucket tokenBucket = new TokenBucket();
                    tokenBucket.setToken((Timestamp) objects[0]);
                    tokenBucket.setCount(Integer.valueOf(objects[1].toString()));
                    return tokenBucket;
                }).collect(Collectors.toList());
    }
}

