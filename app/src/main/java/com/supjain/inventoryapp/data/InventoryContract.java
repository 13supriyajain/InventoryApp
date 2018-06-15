package com.supjain.inventoryapp.data;

import android.provider.BaseColumns;

/**
 * InventoryContract class is for storing constants that define names for product tables and columns.
 */
public final class InventoryContract {

    // Default no-argument constructor
    private InventoryContract() {
    }

    /**
     * Inner class that defines constant values for the inventory database table.
     */
    public static final class ProductsInformation implements BaseColumns {

        /**
         * Name of database table for products
         */
        public final static String TABLE_NAME = "products";

        /**
         * Unique ID number for the product
         * Type: INTEGER
         */
        public final static String _ID = BaseColumns._ID;

        /**
         * Name of the product.
         * Type: TEXT
         */
        public final static String COLUMN_PRODUCT_NAME = "product_name";

        /**
         * Price of the product.
         * Type: REAL
         */
        public final static String COLUMN_PRODUCT_PRICE = "product_price";

        /**
         * Quantity of the product.
         * Type: INTEGER
         */
        public final static String COLUMN_PRODUCT_QUANTITY = "product_quantity";

        /**
         * Name of the Supplier.
         * Type: STRING
         */
        public final static String COLUMN_SUPPLIER_NAME = "supplier_name";

        /**
         * Phone number of the Supplier.
         * Type: INTEGER
         */
        public final static String COLUMN_SUPPLIER_PHONE_NUMBER = "supplier_phone_number";
    }
}
