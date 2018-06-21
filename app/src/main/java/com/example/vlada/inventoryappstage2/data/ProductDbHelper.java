package com.example.vlada.inventoryappstage2.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.vlada.inventoryappstage2.data.ProductContract.ProductEntry;

public class ProductDbHelper extends SQLiteOpenHelper{

    // Name of the database file
    private static final String DATABASE_NAME = "shop.db";

    // Database version. If you change the database schema, you must increment the database version.
    private static final int DATABASE_VERSION = 1;

    // Constructs a new instance of ProductDbHelper
    public ProductDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * This is called when the database is created for the first time.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create a String that contains the SQL statement to create the products table
        String SQL_CREATE_PRODUCTS_TABLE =  "CREATE TABLE " + ProductEntry.TABLE_NAME + " ("
                + ProductEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + ProductEntry.COLUMN_PRODUCT_NAME + " TEXT NOT NULL, "
                + ProductEntry.COLUMN_PRODUCT_SUPPLIER + " TEXT NOT NULL, "
                + ProductEntry.COLUMN_SUPPLIER_PHONE_NUMBER + " INTEGER NOT NULL, "
                + ProductEntry.COLUMN_PRODUCT_AVAILABILITY + " INTEGER NOT NULL, "
                + ProductEntry.COLUMN_PRODUCT_QUANTITY + " INTEGER NOT NULL DEFAULT 0, "
                + ProductEntry.COLUMN_PRODUCT_PRICE + " REAL NOT NULL); ";

        // Execute the SQL statement
        db.execSQL(SQL_CREATE_PRODUCTS_TABLE);
    }

    // This is called when the database needs to be upgraded.
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // The database is still at version 1, so there's nothing to do be done here.
    }
}