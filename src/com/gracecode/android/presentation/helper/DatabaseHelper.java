package com.gracecode.android.presentation.helper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import com.gracecode.android.presentation.Huaban;
import com.gracecode.android.presentation.dao.Pin;
import com.gracecode.android.presentation.util.Logger;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;
import java.util.ArrayList;

public class DatabaseHelper extends OrmLiteSqliteOpenHelper {

    public static final String FIELD_ID = "id";
    public static final String FIELD_BOARD_ID = "board_id";
    public static final String FIELD_TEXT = "text";
    public static final String FIELD_KEY = "key";
    public static final String FIELD_CREATE_AT = "create_at";
    public static final String FIELD_LINK = "link";
    public static final String FIELD_HEIGHT = "height";
    public static final String FIELD_WIDTH = "width";


    public DatabaseHelper(Context context) {
        super(context, Huaban.DATABASE_NAME, null, Huaban.APP_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase, ConnectionSource connectionSource) {
        try {
            TableUtils.createTable(connectionSource, Pin.class);
        } catch (SQLException e) {
            Logger.e(e.getMessage());
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, ConnectionSource connectionSource, int i, int i2) {

    }


    public Dao<Pin, Integer> getPinsDAO() throws SQLException {
        return getDao(Pin.class);
    }


    public ArrayList<Pin> getPinsBeforeMaxId(long maxId) throws SQLException {
        ArrayList<Pin> result = new ArrayList<Pin>();

        QueryBuilder<Pin, Integer> queryBuilder = getPinsDAO().queryBuilder()
                .orderBy(DatabaseHelper.FIELD_ID, false)
                .limit(Huaban.PAGE_SIZE);

        PreparedQuery<Pin> query;
        if (maxId > 0) {
            query = queryBuilder
                    .where()
                    .lt(DatabaseHelper.FIELD_ID, maxId)
                    .prepare();
        } else {
            query = queryBuilder.prepare();
        }

        result.addAll(getPinsDAO().query(query));
        return result;
    }

    public ArrayList<Pin> getPinsAfterSinceId(long sinceId) throws SQLException {
        ArrayList<Pin> result = new ArrayList<Pin>();

        QueryBuilder<Pin, Integer> queryBuilder = getPinsDAO().queryBuilder()
                .orderBy(DatabaseHelper.FIELD_ID, false)
                .limit(Huaban.PAGE_SIZE);

        PreparedQuery<Pin> query;
        if (sinceId > 0) {
            query = queryBuilder
                    .where()
                    .gt(DatabaseHelper.FIELD_ID, sinceId)
                    .prepare();
        } else {
            query = queryBuilder.prepare();
        }

        result.addAll(getPinsDAO().query(query));
        return result;
    }

    public Pin getPin(int id) {
        try {
            return getPinsDAO().queryForId(id);
        } catch (SQLException e) {
            return new Pin();
        }
    }
}
