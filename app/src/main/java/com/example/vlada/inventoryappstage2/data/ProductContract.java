package com.example.vlada.inventoryappstage2.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public final class ProductContract {

    public static final String PATH_PRODUCT = "inventoryappstage2";

    public static final String CONTENT_AUTHORITY = "com.example.vlada.inventoryappstage2";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    private ProductContract() {}

    // Inner class that defines constant values for the products database table.
    // Each entry in the table represents a single product.
    public static final class ProductEntry implements BaseColumns {

        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_PRODUCT);

        /**
         * The MIME type of the {@link #CONTENT_URI} for a list of products.
         */
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PRODUCT;

        /**
         * The MIME type of the {@link #CONTENT_URI} for a single product.
         */
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PRODUCT;

        // Name of database table for products
        public final static String TABLE_NAME = "products";

        // Unique ID for the product
        // Integer type
        public final static String _ID = BaseColumns._ID;

        // Name of the product
        // Text type
        public final static String COLUMN_PRODUCT_NAME = "name";

        // Name of the supplier
        // Text type
        public final static String COLUMN_PRODUCT_SUPPLIER_NAME = "supplier";

        // Phone number of the supplier
        // Integer type
        public final static String COLUMN_SUPPLIER_PHONE_NUMBER = "phone";

        // Quantity of the product
        // Integer type
        public final static String COLUMN_PRODUCT_QUANTITY = "quantity";

        // Product price
        // Real type
        public final static String COLUMN_PRODUCT_PRICE = "price";

    }
}

