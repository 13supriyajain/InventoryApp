package com.supjain.inventoryapp;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.supjain.inventoryapp.data.InventoryContract.ProductsInformation;
import com.supjain.inventoryapp.data.InventoryDbHelper;

public class MainActivity extends AppCompatActivity {

    private InventoryDbHelper mDbHelper;
    private TextView productInfoTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDbHelper = new InventoryDbHelper(this);
        productInfoTextView = findViewById(R.id.display_inventory_text);

        insertDummyData();
        queryData();
    }

    // Method to insert dummy product information data into inventory database.
    private void insertDummyData() {
        // Create and/or open a database to write to it
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(ProductsInformation.COLUMN_PRODUCT_NAME, "Iphone");
        values.put(ProductsInformation.COLUMN_PRODUCT_PRICE, 599.99);
        values.put(ProductsInformation.COLUMN_PRODUCT_QUANTITY, 10);
        values.put(ProductsInformation.COLUMN_SUPPLIER_NAME, "Apple Inc.");
        values.put(ProductsInformation.COLUMN_SUPPLIER_PHONE_NUMBER, 987654321);

        // Inserting the new row, returns the primary key value of the new row
        long newRowId = db.insert(ProductsInformation.TABLE_NAME, null, values);
        if (newRowId > -1) {
            Toast.makeText(this, "Product information stored successfully !", Toast.LENGTH_SHORT).show();
        } else {
            Log.e(getClass().getSimpleName(), "Error occurred while trying to store product information !");
        }
    }

    // Method to query / read data from inventory database.
    private void queryData() {
        // Create and/or open a database to read from it
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        // Columns to be selected from the table
        String[] projection = {
                ProductsInformation._ID,
                ProductsInformation.COLUMN_PRODUCT_NAME,
                ProductsInformation.COLUMN_PRODUCT_PRICE,
                ProductsInformation.COLUMN_PRODUCT_QUANTITY,
                ProductsInformation.COLUMN_SUPPLIER_NAME,
                ProductsInformation.COLUMN_SUPPLIER_PHONE_NUMBER
        };

        // Query the db to get product information.
        Cursor cursor = db.query(
                ProductsInformation.TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                null
        );

        // Display the resultant data.
        displayData(cursor);
    }

    // Method to display data in a textview, returned as a result of querying the inventory database.
    private void displayData(Cursor cursor) {
        try {
            productInfoTextView.setText("The products table contains " + cursor.getCount() + " product(s).\n\n");
            productInfoTextView.append(ProductsInformation._ID + " - " +
                    ProductsInformation.COLUMN_PRODUCT_NAME + " - " +
                    ProductsInformation.COLUMN_PRODUCT_PRICE + " - " +
                    ProductsInformation.COLUMN_PRODUCT_QUANTITY + " - " +
                    ProductsInformation.COLUMN_SUPPLIER_NAME + " - " +
                    ProductsInformation.COLUMN_SUPPLIER_PHONE_NUMBER + "\n");

            int idColumnIndex = cursor.getColumnIndex(ProductsInformation._ID);
            int productNameColumnIndex = cursor.getColumnIndex(ProductsInformation.COLUMN_PRODUCT_NAME);
            int productPriceColumnIndex = cursor.getColumnIndex(ProductsInformation.COLUMN_PRODUCT_PRICE);
            int productQuantityColumnIndex = cursor.getColumnIndex(ProductsInformation.COLUMN_PRODUCT_QUANTITY);
            int supplierNameColumnIndex = cursor.getColumnIndex(ProductsInformation.COLUMN_SUPPLIER_NAME);
            int supplierPhoneColumnIndex = cursor.getColumnIndex(ProductsInformation.COLUMN_SUPPLIER_PHONE_NUMBER);

            while (cursor.moveToNext()) {
                int currentID = cursor.getInt(idColumnIndex);
                String currentProductName = cursor.getString(productNameColumnIndex);
                float currentProductPrice = cursor.getFloat(productPriceColumnIndex);
                int currentProductQuantity = cursor.getInt(productQuantityColumnIndex);
                String currentSupplierName = cursor.getString(supplierNameColumnIndex);
                int currentSupplierPhone = cursor.getInt(supplierPhoneColumnIndex);

                productInfoTextView.append(("\n" + currentID + " - " + currentProductName + " - " + currentProductPrice +
                        " - " + currentProductQuantity + " - " + currentSupplierName + " - " + currentSupplierPhone));
            }
        } finally {
            cursor.close();
        }
    }
}
