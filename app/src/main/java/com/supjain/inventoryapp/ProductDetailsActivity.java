package com.supjain.inventoryapp;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.NavUtils;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.supjain.inventoryapp.data.InventoryContract.ProductsInformation;

/**
 * This activity allows user to edit/add a product in the database.
 */
public class ProductDetailsActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int EXISTING_PRODUCT_LOADER = 0;

    private EditText mProductNameEditText;
    private EditText mProductPriceEditText;
    private EditText mProductQuantityEditText;
    private EditText mSupplierNameEditText;
    private EditText mSupplierPhoneEditText;
    private Button mQuantityIncreaseBtn;
    private Button mQuantityDecreaseBtn;
    private Button mContactSupplierBtn;

    private Uri mCurrentProductUri;

    // Boolean flag that keeps track of whether the product has been edited (true) or not (false)
    private boolean mProductDetailHasChanged = false;

    // OnTouchListener that listens for any user touches on a View, and then updates mProductDetailHasChanged boolean to true.
    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mProductDetailHasChanged = true;
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.product_details);

        Intent intent = getIntent();
        mCurrentProductUri = intent.getData();

        if (mCurrentProductUri == null) {
            setTitle(getString(R.string.add_product_activity_title));
        } else {
            setTitle(getString(R.string.edit_product_activity_title));
            getSupportLoaderManager().initLoader(EXISTING_PRODUCT_LOADER, null, this);
        }

        // Find all relevant views that we will need to read user input from
        mProductNameEditText = findViewById(R.id.product_name_edit_text);
        mProductPriceEditText = findViewById(R.id.product_price_edit_text);
        mProductQuantityEditText = findViewById(R.id.product_quantity_edit_text);
        mSupplierNameEditText = findViewById(R.id.supplier_name_edit_text);
        mSupplierPhoneEditText = findViewById(R.id.supplier_phone_edit_text);
        mQuantityIncreaseBtn = findViewById(R.id.increase_quantity_btn);
        mQuantityDecreaseBtn = findViewById(R.id.decrease_quantity_btn);
        mContactSupplierBtn = findViewById(R.id.contact_supplier_btn);

        // Setup OnTouchListeners on all the input fields and quantity change buttons
        mProductNameEditText.setOnTouchListener(mTouchListener);
        mProductPriceEditText.setOnTouchListener(mTouchListener);
        mProductQuantityEditText.setOnTouchListener(mTouchListener);
        mSupplierNameEditText.setOnTouchListener(mTouchListener);
        mSupplierPhoneEditText.setOnTouchListener(mTouchListener);
        mQuantityIncreaseBtn.setOnTouchListener(mTouchListener);
        mQuantityDecreaseBtn.setOnTouchListener(mTouchListener);

        mContactSupplierBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phone = mSupplierPhoneEditText.getText().toString().trim();
                if (!TextUtils.isEmpty(phone)) {
                    Intent phoneIntent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phone, null));
                    startActivity(phoneIntent);
                }
            }
        });

        mQuantityIncreaseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String productQuantity = mProductQuantityEditText.getText().toString().trim();
                int quantity = 0;
                if (!TextUtils.isEmpty(productQuantity)) {
                    quantity = Integer.parseInt(productQuantity);
                    quantity += 1;
                    mProductQuantityEditText.setText(String.valueOf(quantity));
                }
            }
        });

        mQuantityDecreaseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String productQuantity = mProductQuantityEditText.getText().toString().trim();
                int quantity = 0;
                if (!TextUtils.isEmpty(productQuantity)) {
                    quantity = Integer.parseInt(productQuantity);
                    if (quantity > 0) {
                        quantity -= 1;
                        mProductQuantityEditText.setText(String.valueOf(quantity));
                    }
                }
            }
        });
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        // If this is a new product, hide the "Delete" menu item.
        if (mCurrentProductUri == null) {
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_details.xml file.
        getMenuInflater().inflate(R.menu.menu_details, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar
        switch (item.getItemId()) {
            // Respond to a click on the "Save" menu option
            case R.id.action_save:
                saveProduct();
                return true;
            // Respond to a click on the "Delete" menu option
            case R.id.action_delete:
                showDeleteConfirmationDialog();
                return true;
            // Respond to a click on the "Up" arrow button in the app bar
            case android.R.id.home:
                // If the product detail hasn't changed, continue with navigating up to parent activity
                if (!mProductDetailHasChanged) {
                    NavUtils.navigateUpFromSameTask(ProductDetailsActivity.this);
                    return true;
                }

                // Otherwise if there are unsaved changes, setup a dialog to warn the user.
                // Create a click listener to handle the user confirming that
                // changes should be discarded.
                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // User clicked "Discard" button, navigate to parent activity.
                                NavUtils.navigateUpFromSameTask(ProductDetailsActivity.this);
                            }
                        };

                // Show a dialog that notifies the user they have unsaved changes
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void saveProduct() {
        String productName = mProductNameEditText.getText().toString().trim();
        String productPrice = mProductPriceEditText.getText().toString().trim();
        String productQuantity = mProductQuantityEditText.getText().toString().trim();
        String supplierName = mSupplierNameEditText.getText().toString().trim();
        String supplierPhone = mSupplierPhoneEditText.getText().toString().trim();

        float price = 0.0f;
        if (!TextUtils.isEmpty(productPrice)) {
            price = Float.parseFloat(productPrice);
        }

        int quantity = 0;
        if (!TextUtils.isEmpty(productQuantity)) {
            quantity = Integer.parseInt(productQuantity);
        }

        if (TextUtils.isEmpty(productName) || TextUtils.isEmpty(supplierName) || TextUtils.isEmpty(supplierPhone)) {
            Toast.makeText(this, R.string.valid_value_err_msg, Toast.LENGTH_LONG).show();
            return;
        }

        ContentValues values = new ContentValues();
        values.put(ProductsInformation.COLUMN_PRODUCT_NAME, productName);
        values.put(ProductsInformation.COLUMN_PRODUCT_PRICE, price);
        values.put(ProductsInformation.COLUMN_PRODUCT_QUANTITY, quantity);
        values.put(ProductsInformation.COLUMN_SUPPLIER_NAME, supplierName);
        values.put(ProductsInformation.COLUMN_SUPPLIER_PHONE_NUMBER, supplierPhone);

        if (mCurrentProductUri == null) {

            Uri resultUri = getContentResolver().insert(ProductsInformation.CONTENT_URI, values);

            if (resultUri != null) {
                Toast.makeText(this, R.string.product_saved_msg, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, R.string.product_save_err_msg, Toast.LENGTH_SHORT).show();
            }
        } else {
            int updatedRowCount = getContentResolver().update(mCurrentProductUri, values, null, null);

            if (updatedRowCount > 0) {
                Toast.makeText(this, R.string.product_update_msg, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, R.string.product_update_err_msg, Toast.LENGTH_SHORT).show();
            }
        }
        finish();
    }

    /**
     * This method is called when the back button is pressed.
     */
    @Override
    public void onBackPressed() {
        // If the product detail hasn't changed, continue with handling back button press
        if (!mProductDetailHasChanged) {
            super.onBackPressed();
            return;
        }

        // Otherwise if there are unsaved changes, setup a dialog to warn the user.
        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // User clicked "Discard" button, close the current activity.
                        finish();
                    }
                };

        // Show dialog that there are unsaved changes
        showUnsavedChangesDialog(discardButtonClickListener);
    }

    /**
     * Show a dialog that warns the user there are unsaved changes that will be lost
     * if they continue leaving the editor.
     */
    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.discard_changes_alert_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Keep editing" button, so dismiss the dialog
                // and continue editing the product details.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void showDeleteConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_product_alert_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the product.
                deleteProduct();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Cancel" button, so dismiss the dialog
                // and continue editing the product details.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /**
     * Perform the deletion of the product in the database.
     */
    private void deleteProduct() {
        if (mCurrentProductUri != null) {
            int deletedRowCount = getContentResolver().delete(mCurrentProductUri, null, null);
            if (deletedRowCount > 0) {
                Toast.makeText(this, R.string.product_deletion_confirm_msg, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, R.string.product_deletion_err_msg, Toast.LENGTH_SHORT).show();
            }
            finish();
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (mCurrentProductUri == null) {
            return null;
        }

        String[] projection = {
                ProductsInformation._ID,
                ProductsInformation.COLUMN_PRODUCT_NAME,
                ProductsInformation.COLUMN_PRODUCT_PRICE,
                ProductsInformation.COLUMN_PRODUCT_QUANTITY,
                ProductsInformation.COLUMN_SUPPLIER_NAME,
                ProductsInformation.COLUMN_SUPPLIER_PHONE_NUMBER
        };

        return new CursorLoader(this, mCurrentProductUri,
                projection, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor.moveToFirst()) {
            String productNameValue = cursor.getString(cursor.getColumnIndex(ProductsInformation.COLUMN_PRODUCT_NAME));
            float productPriceValue = cursor.getFloat(cursor.getColumnIndex(ProductsInformation.COLUMN_PRODUCT_PRICE));
            int productQuantityValue = cursor.getInt(cursor.getColumnIndex(ProductsInformation.COLUMN_PRODUCT_QUANTITY));
            String supplierNameValue = cursor.getString(cursor.getColumnIndex(ProductsInformation.COLUMN_SUPPLIER_NAME));
            String supplierPhoneValue = cursor.getString(cursor.getColumnIndex(ProductsInformation.COLUMN_SUPPLIER_PHONE_NUMBER));

            mProductNameEditText.setText(productNameValue);
            mProductPriceEditText.setText(String.valueOf(productPriceValue));
            mProductQuantityEditText.setText(String.valueOf(productQuantityValue));
            mSupplierNameEditText.setText(supplierNameValue);
            mSupplierPhoneEditText.setText(supplierPhoneValue);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mProductNameEditText.setText("");
        mProductPriceEditText.setText("");
        mProductQuantityEditText.setText(R.string.default_quantity_hint);
        mSupplierNameEditText.setText("");
        mSupplierPhoneEditText.setText("");
    }
}
