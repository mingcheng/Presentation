package com.gracecode.android.presentation.dao;

import com.google.gson.annotations.SerializedName;
import com.gracecode.android.presentation.helper.DatabaseHelper;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;

@DatabaseTable
public class Pin implements Serializable {
    @SerializedName("pin_id")
    @DatabaseField(columnName = DatabaseHelper.FIELD_ID, id = true, unique = true, uniqueIndex = true)
    private int mId;

    @SerializedName("board_id")
    @DatabaseField(columnName = DatabaseHelper.FIELD_BOARD_ID, index = true)
    private int mBoardId;

    @SerializedName("raw_text")
    @DatabaseField(columnName = DatabaseHelper.FIELD_TEXT)
    private String mText;

    @DatabaseField(columnName = DatabaseHelper.FIELD_KEY)
    private String mKey;

    @SerializedName("created_at")
    @DatabaseField(columnName = DatabaseHelper.FIELD_CREATE_AT, index = true)
    private long mCreateAt;

    @SerializedName("link")
    @DatabaseField(columnName = DatabaseHelper.FIELD_LINK)
    private String mLink;

    @DatabaseField(columnName = DatabaseHelper.FIELD_HEIGHT)
    private long mHeight;

    @DatabaseField(columnName = DatabaseHelper.FIELD_WIDTH)
    private long mWidth;

    public class File {
        public String bucket = "hbimg";
        public String farm = "farm1";
        public int frames = 1;
        public int width;
        public int height;
        public String type = "image/jpeg";
        public String key;
    }

    public Pin() {

    }

    public int getId() {
        return mId;
    }

    public void setId(int i) {
        this.mId = i;
    }

    public int getBoardId() {
        return mBoardId;
    }

    public void setBoardId(int i) {
        this.mBoardId = i;
    }

    public String getText() {
        return mText;
    }

    public void setText(String s) {
        this.mText = s;
    }

    public String getKey() {
        return mKey;
    }

    public void setKey(String s) {
        this.mKey = s;
    }

    public String getOriginUrl() {
        return "http://img.hb.aicdn.com/" + mKey;
    }

    public String getBigPstUrl() {
        return getOriginUrl() + "_fw580";
    }

    public String getRetinaSquareThumbUrl() {
        return getOriginUrl() + "_sq235";
    }

    public String getSquareThumbUrl() {
        return getOriginUrl() + "_sq120";
    }

    public String getLink() {
        return mLink;
    }

    public long getHeight() {
        return mHeight;
    }

    public void setHeight(long height) {
        this.mHeight = height;
    }

    public long getWidth() {
        return mWidth;
    }

    public void setWidth(long width) {
        this.mWidth = width;
    }
}
