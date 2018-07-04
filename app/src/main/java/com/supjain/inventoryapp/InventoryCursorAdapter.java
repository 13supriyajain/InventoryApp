package com.supjain.inventoryapp;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.supjain.inventoryapp.data.InventoryContract.ProductsInformation;

/**
 * {@link InventoryCursorAdapter} is an adapter for a list
 * that uses a {@link Cursor} of product data as its data source. This adapter knows
 * how to create list items for each row of product data in the {@link Cursor}.
 */
public class InventoryCursorAdapter extends CursorAdapter {

    // Constructor
    public InventoryCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }

    // This method makes a new blank list item view. No data is set (or bound) to the views yet.
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    // This method binds the product data (in the current row pointed to by cursor) to the given list item layout.
    @Override
    public void bindView(View view, final Context context, final Cursor cursor) {
        // Find fields to populate in inflated template
        TextView productNameTv = view.findViewById(R.id.product_name);
        TextView productPriceTv = view.findViewById(R.id.product_price);
        TextView productQuantityTv = view.findViewById(R.id.product_quantity);
        Button saleBtn = view.findViewById(R.id.sale_btn);

        // Extract properties from cursor
        String productNameValue = cursor.getString(cursor.getColumnIndex(ProductsInformation.COLUMN_PRODUCT_NAME));
        productNameTv.setText(productNameValue);
        float productPriceValue = cursor.getFloat(cursor.getColumnIndex(ProductsInformation.COLUMN_PRODUCT_PRICE));
        productPriceTv.setText(" " + String.valueOf(productPriceValue));
        int productQuantityValue = cursor.getInt(cursor.getColumnIndex(ProductsInformation.COLUMN_PRODUCT_QUANTITY));
        productQuantityTv.setText(String.valueOf(productQuantityValue));

        saleBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                View parentRow = (View) v.getParent();
                ListView listView = (ListView) parentRow.getParent();
                int position = listView.getPositionForView(parentRow);
                long id = listView.getItemIdAtPosition(position);

                TextView currentProductQuantityTv = parentRow.findViewById(R.id.product_quantity);
                int currentQuantity = Integer.valueOf(currentProductQuantityTv.getText().toString());
                if (currentQuantity > 0) {
                    currentQuantity = currentQuantity - 1;
                    int updatedRowCount = updateProductQuantity(currentQuantity, context, id);
                    if (updatedRowCount > 0) {
                        currentProductQuantityTv.setText(String.valueOf(currentQuantity));
                        Toast.makeText(context, R.string.product_quantity_updated_msg, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context, R.string.product_quantity_not_updated_msg, Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    // This method updates the quantity of the product in the database, when user clicks on 'Sale' button
    private int updateProductQuantity(int updatedQuantity, Context context, long productId) {
        Uri contentUri = ContentUris.withAppendedId(ProductsInformation.CONTENT_URI, productId);
        ContentValues values = new ContentValues();
        values.put(ProductsInformation.COLUMN_PRODUCT_QUANTITY, updatedQuantity);
        return context.getContentResolver().update(contentUri, values, null, null);
    }
}
