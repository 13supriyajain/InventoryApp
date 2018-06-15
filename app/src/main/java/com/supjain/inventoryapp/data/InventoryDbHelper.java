package com.supjain.inventoryapp.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.supjain.inventoryapp.data.InventoryContract.ProductsInformation;

/**
 * InventoryDbHelper class is for creating the Inventory database.
 */
public class InventoryDbHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "inventory.db";

    public InventoryDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String SQL_CREATE_PRODUCTSINFORMATION_TABLE = "CREATE TABLE "
                + ProductsInformation.TABLE_NAME + " ("
                + ProductsInformation._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + ProductsInformation.COLUMN_PRODUCT_NAME + " TEXT NOT NULL, "
                + ProductsInformation.COLUMN_PRODUCT_PRICE + " REAL NOT NULL DEFAULT 0.0, "
                + ProductsInformation.COLUMN_PRODUCT_QUANTITY + " INTEGER NOT NULL DEFAULT 0, "
                + ProductsInformation.COLUMN_SUPPLIER_NAME + " TEXT NOT NULL, "
                + ProductsInformation.COLUMN_SUPPLIER_PHONE_NUMBER + " INTEGER NOT NULL DEFAULT 0);";
        db.execSQL(SQL_CREATE_PRODUCTSINFORMATION_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}
