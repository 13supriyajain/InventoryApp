package com.supjain.inventoryapp;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.supjain.inventoryapp.data.InventoryContract.ProductsInformation;

/**
 * Displays list of products that were entered and stored in the app.
 */
public class ProductListActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int PRODUCT_LOADER = 0;
    InventoryCursorAdapter inventoryCursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProductListActivity.this, ProductDetailsActivity.class);
                startActivity(intent);
            }
        });

        ListView productListView = (ListView) findViewById(R.id.inventory_list);
        View emptyView = findViewById(R.id.empty_view_text);
        productListView.setEmptyView(emptyView);

        inventoryCursorAdapter = new InventoryCursorAdapter(this, null);
        productListView.setAdapter(inventoryCursorAdapter);

        productListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(ProductListActivity.this, ProductDetailsActivity.class);
                intent.setData(ContentUris.withAppendedId(ProductsInformation.CONTENT_URI, id));
                startActivity(intent);
            }
        });

        getSupportLoaderManager().initLoader(PRODUCT_LOADER, null, this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_product_list.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_product_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Insert dummy data" menu option
            case R.id.action_insert_dummy_data:
                insertDummyData();
                return true;
            // Respond to a click on the "Delete all entries" menu option
            case R.id.action_delete_all_entries:
                showDeleteConfirmationDialog();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // Method to insert dummy product information data into inventory database.
    private void insertDummyData() {
        ContentValues values = new ContentValues();
        values.put(ProductsInformation.COLUMN_PRODUCT_NAME, "iPhone");
        values.put(ProductsInformation.COLUMN_PRODUCT_PRICE, 599.9);
        values.put(ProductsInformation.COLUMN_PRODUCT_QUANTITY, 10);
        values.put(ProductsInformation.COLUMN_SUPPLIER_NAME, "Apple Inc");
        values.put(ProductsInformation.COLUMN_SUPPLIER_PHONE_NUMBER, 987654321);

        Uri resultUri = getContentResolver().insert(ProductsInformation.CONTENT_URI, values);
        if (resultUri != null) {
            Toast.makeText(this, R.string.product_saved_msg, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, R.string.product_save_err_msg, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {
                ProductsInformation._ID,
                ProductsInformation.COLUMN_PRODUCT_NAME,
                ProductsInformation.COLUMN_PRODUCT_PRICE,
                ProductsInformation.COLUMN_PRODUCT_QUANTITY,
                ProductsInformation.COLUMN_SUPPLIER_NAME,
                ProductsInformation.COLUMN_SUPPLIER_PHONE_NUMBER
        };

        return new CursorLoader(this, ProductsInformation.CONTENT_URI,
                projection, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        inventoryCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        inventoryCursorAdapter.swapCursor(null);
    }

    // Show confirmation dialog to the user before deleting all the products from the database
    private void showDeleteConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_all_products_confirm_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the products.
                deleteAllProducts();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    // Perform the deletion of all the products in the database.
    private void deleteAllProducts() {
        int deletedRowCount = getContentResolver().delete(ProductsInformation.CONTENT_URI, null, null);
        if (deletedRowCount > 0) {
            Toast.makeText(this, R.string.product_deleted_confirm_msg, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, R.string.product_delete_err_msg, Toast.LENGTH_SHORT).show();
        }
    }
}
