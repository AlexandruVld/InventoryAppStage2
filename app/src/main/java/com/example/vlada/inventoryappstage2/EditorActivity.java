package com.example.vlada.inventoryappstage2;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import com.example.vlada.inventoryappstage2.data.ProductContract.ProductEntry;
import static com.example.vlada.inventoryappstage2.data.ProductContract.ProductEntry.COLUMN_PRODUCT_AVAILABILITY;
import static com.example.vlada.inventoryappstage2.data.ProductContract.ProductEntry.COLUMN_PRODUCT_NAME;
import static com.example.vlada.inventoryappstage2.data.ProductContract.ProductEntry.COLUMN_PRODUCT_PRICE;
import static com.example.vlada.inventoryappstage2.data.ProductContract.ProductEntry.COLUMN_PRODUCT_QUANTITY;
import static com.example.vlada.inventoryappstage2.data.ProductContract.ProductEntry.COLUMN_PRODUCT_SUPPLIER;
import static com.example.vlada.inventoryappstage2.data.ProductContract.ProductEntry.COLUMN_SUPPLIER_PHONE_NUMBER;

public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int EXISTING_IT_LOADER = 0;

    private Uri mActualProductUri;

    private EditText mNameEditText;

    // EditText field to enter the supplier name
    private EditText mSupplierEditText;

    // EditText field to enter the supplier phone number
    private EditText mSupplierPhoneEditText;

    // EditText field to enter the product availability
    private Spinner mAvailabilitySpinner;

    // EditText field to enter the product quantity
    private EditText mQuantityEditText;

    //EditText field to enter the product price
    private EditText mPriceEditText;

    // Availability of the product. The possible values are in ProductContract.java file
    private int mAvailability = ProductEntry.AVAILABILITY_UNKNOWN;

    private boolean mProductHasChanged = false;

    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mProductHasChanged = true;
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.editor_activity);

        Intent intent = getIntent();
        mActualProductUri = intent.getData();

        // If the intent DOES NOT contain the product content URI, then we know that we are
        // creating the new product.
        if (mActualProductUri == null) {
            // The new product, so change the app bar to say "Add a new product"
            setTitle(getString(R.string.add_new_product));

            // Invalidate the options menu, so the "Delete" menu option can be hidden.
            // While the product that hasn't been created yet.
            invalidateOptionsMenu();
        } else {
            // Otherwise this is an existing product, so change app bar to say "Edit product"
            setTitle(getString(R.string.edit_the_product));

            // Initialize a loader to read the data from the database
            // and display the current values in the editor
            getLoaderManager().initLoader(EXISTING_IT_LOADER, null, this);
        }

        // Find all relevant views that we will need to read user input from
        mNameEditText = findViewById(R.id.edit_product_name);
        mSupplierEditText = findViewById(R.id.edit_supplier_name);
        mSupplierPhoneEditText = findViewById(R.id.edit_supplier_phone_number);
        mAvailabilitySpinner = findViewById(R.id.spinner_availability);
        mQuantityEditText = findViewById(R.id.edit_product_quantity);
        mPriceEditText = findViewById(R.id.edit_product_price);
        Button mIncreaseButton = findViewById(R.id.increase_button);
        Button mDecreaseButton = findViewById(R.id.decrease_button);
        Button mOrderButton = findViewById(R.id.order_button);

        mNameEditText.setOnTouchListener(mTouchListener);
        mPriceEditText.setOnTouchListener(mTouchListener);
        mQuantityEditText.setOnTouchListener(mTouchListener);
        mSupplierEditText.setOnTouchListener(mTouchListener);
        mSupplierPhoneEditText.setOnTouchListener(mTouchListener);
        mIncreaseButton.setOnTouchListener(mTouchListener);
        mDecreaseButton.setOnTouchListener(mTouchListener);

        mDecreaseButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String quantity = mQuantityEditText.getText().toString();
                if (TextUtils.isEmpty(quantity)) {
                    mQuantityEditText.setText("0");
                } else {
                    int new_quantity = Integer.parseInt(mQuantityEditText.getText().toString().trim());
                    if (new_quantity > 0) {
                        new_quantity--;
                        mQuantityEditText.setText(String.valueOf(new_quantity));
                    } else {
                        Toast.makeText(EditorActivity.this, "The quantity cannot be negative!",
                                Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        mIncreaseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String quantity = mQuantityEditText.getText().toString();
                if (TextUtils.isEmpty(quantity)) {
                    mQuantityEditText.setText("1");
                } else {
                    int not_null_quantity = Integer.parseInt(mQuantityEditText.getText().toString().trim());
                    not_null_quantity++;
                    mQuantityEditText.setText(String.valueOf(not_null_quantity));
                }
            }
        });

        mOrderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phone = mSupplierPhoneEditText.getText().toString().trim();
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + phone));
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                }
            }
        });

        setupSpinner();
    }

    // Setup the dropdown spinner that allows the user to select the availability of the product
    private void setupSpinner() {
        // Create adapter for spinner. The list options are from the String array it will use
        // the spinner will use the default layout
        ArrayAdapter availabilitySpinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.array_availability_options, android.R.layout.simple_spinner_item);

        // Specify dropdown layout style - simple list view with 1 item per line
        availabilitySpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);

        // Apply the adapter to the spinner
        mAvailabilitySpinner.setAdapter(availabilitySpinnerAdapter);

        // Set the integer mSelected to the constant values
        mAvailabilitySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);
                if (!TextUtils.isEmpty(selection)) {
                    if (selection.equals(getString(R.string.availability_yes))) {
                        mAvailability = ProductEntry.AVAILABILITY_IN_STOCK;
                    } else if (selection.equals(getString(R.string.availability_no))) {
                        mAvailability = ProductEntry.AVAILABILITY_OUT_OF_STOCK;
                    } else {
                        mAvailability = ProductEntry.AVAILABILITY_UNKNOWN;
                    }
                }
            }

            // Because AdapterView is an abstract class, onNothingSelected must be defined
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mAvailability = ProductEntry.AVAILABILITY_UNKNOWN;
            }
        });
    }

    private void saveProduct() {
        // Read from input fields
        // Use trim to eliminate leading or trailing white space
        String nameString = mNameEditText.getText().toString().trim();

        String priceString = mPriceEditText.getText().toString().trim();

        String quantityString = mQuantityEditText.getText().toString().trim();

        String supplierNameString = mSupplierEditText.getText().toString().trim();

        String supplierPhoneString = mSupplierPhoneEditText.getText().toString().trim();

        // Check if this is supposed to be a new product
        // and check if all the fields in the editor are blank
        if (mActualProductUri == null &&
                TextUtils.isEmpty(nameString) && TextUtils.isEmpty(priceString) &&
                TextUtils.isEmpty(quantityString) && TextUtils.isEmpty(supplierNameString) &&
                TextUtils.isEmpty(supplierPhoneString) && mAvailability == ProductEntry.AVAILABILITY_UNKNOWN) {
            // Since no fields were modified, we can return early without creating a new product.
            // No need to create ContentValues and no need to do any ContentProvider operations.
            Toast.makeText(this, getResources().getString(R.string.no_field_modification), Toast.LENGTH_SHORT).show();
            // Exit activity
            finish();
            return;
        }

        if (TextUtils.isEmpty(nameString) || TextUtils.isEmpty(priceString) ||
                TextUtils.isEmpty(quantityString) || TextUtils.isEmpty(supplierNameString) ||
                TextUtils.isEmpty(supplierPhoneString) || mAvailability == ProductEntry.AVAILABILITY_UNKNOWN) {
            mNameEditText.requestFocus();
            mNameEditText.setError(getString(R.string.empty_field_error));
            Toast.makeText(this, getString(R.string.enter_value_into_field), Toast.LENGTH_LONG).show();
            return;
        }

        // Create a ContentValues object where column names are the keys,
        // and products attributes from the editor are the values.
        ContentValues values = new ContentValues();
        values.put(COLUMN_PRODUCT_NAME, nameString);
        float priceFloat = Float.parseFloat(priceString);
        values.put(COLUMN_PRODUCT_PRICE, priceFloat);
        values.put(COLUMN_PRODUCT_QUANTITY, quantityString);
        values.put(COLUMN_PRODUCT_SUPPLIER, supplierNameString);
        values.put(COLUMN_SUPPLIER_PHONE_NUMBER, supplierPhoneString);
        values.put(COLUMN_PRODUCT_AVAILABILITY, mAvailability);

        // Determine if this is a new or existing product by checking if mActualProductUri is null or not
        if (mActualProductUri == null) {

            // New product, insert a new product into the provider,
            // returning the content URI for the new product.
            Uri newUri = getContentResolver().insert(ProductEntry.CONTENT_URI, values);

            // Show a toast message depending on whether or not the insertion was successful.
            if (newUri == null) {
                // If the new content URI is null, then there was an error with insertion.
                Toast.makeText(this, R.string.save_error,
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the insertion was successful and we can display a toast.
                Toast.makeText(this, getResources().getString(R.string.added_product_message),
                        Toast.LENGTH_SHORT).show();
                // Exit activity
                finish();
            }
        } else {
            // Otherwise this is an existing product, so update the product with content URI: mActualProductUri
            // and pass in the new ContentValues. Pass in null for the selection and selection args
            // because mActualProductUri will already identify the correct row in the database that
            // we want to modify.
            int rowsAffected = getContentResolver().update(mActualProductUri, values, null, null);

            // Show a toast message depending on whether or not the update was successful.
            if (rowsAffected == 0) {
                // If no rows were affected, then there was an error with the update.
                Toast.makeText(this, R.string.update_error,
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the update was successful and we can display a toast.
                Toast.makeText(this, R.string.updated_product_message,
                        Toast.LENGTH_SHORT).show();
            }
            // Exit activity
            finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_editor.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        // If this is a new product, hide the "Delete" menu item.
        if (mActualProductUri == null) {
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Save" menu option
            case R.id.action_save:
                saveProduct();
                finish();
                return true;
            // Respond to a click on the "Delete" menu option
            case R.id.action_delete:
                showDeleteConfirmationDialog();
                return true;
            // Respond to a click on the "Up" arrow button in the app bar
            case android.R.id.home:
                if (!mProductHasChanged) {
                    NavUtils.navigateUpFromSameTask(EditorActivity.this);
                    return true;
                }

                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // User clicked "Discard" button, navigate to parent activity.
                                NavUtils.navigateUpFromSameTask(EditorActivity.this);
                            }
                        };

                // Show a dialog that notifies the user they have unsaved changes
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        // If the product hasn't changed, continue with handling back button press
        if (!mProductHasChanged) {
            super.onBackPressed();
            return;
        }

        // Otherwise if there are unsaved changes, setup a dialog to warn the user.
        // Create a click listener to handle the user confirming that changes should be discarded.
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

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        String[] projection = {
                ProductEntry._ID,
                COLUMN_PRODUCT_NAME,
                ProductEntry.COLUMN_PRODUCT_SUPPLIER,
                ProductEntry.COLUMN_SUPPLIER_PHONE_NUMBER,
                ProductEntry.COLUMN_PRODUCT_AVAILABILITY,
                ProductEntry.COLUMN_PRODUCT_QUANTITY,
                ProductEntry.COLUMN_PRODUCT_PRICE};

        return new CursorLoader(this,   // Parent activity context
                mActualProductUri,             // Query the content URI for the current product
                projection,                    // Columns to include in the resulting Cursor
                null,                  // No selection clause
                null,               // No selection arguments
                null);                // Default sort order
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

        if (cursor == null || cursor.getCount() < 1) {
            return;
        }

        if (cursor.moveToFirst()) {
            // Find the columns of product attributes that we're interested in
            int productNameColumnIndex = cursor.getColumnIndex(COLUMN_PRODUCT_NAME);
            int productSupplierColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_SUPPLIER);
            int supplierPhoneColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_SUPPLIER_PHONE_NUMBER);
            int productAvailabilityColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_AVAILABILITY);
            int productQuantityColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_QUANTITY);
            int productPriceColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_PRICE);

            // Extract out the value from the Cursor for the given column index
            String productName = cursor.getString(productNameColumnIndex);
            String productSupplier = cursor.getString(productSupplierColumnIndex);
            String supplierPhone = cursor.getString(supplierPhoneColumnIndex);
            int productAvailability = cursor.getInt(productAvailabilityColumnIndex);
            int productQuantity = cursor.getInt(productQuantityColumnIndex);
            int productPrice = cursor.getInt(productPriceColumnIndex);

            mNameEditText.setText(productName);
            mSupplierEditText.setText(productSupplier);
            mSupplierPhoneEditText.setText(supplierPhone);
            mQuantityEditText.setText(Integer.toString(productQuantity));
            mPriceEditText.setText(Integer.toString(productPrice));

            switch (productAvailability){
                case ProductEntry.AVAILABILITY_IN_STOCK:
                    mAvailabilitySpinner.setSelection(1);
                    break;
                case ProductEntry.AVAILABILITY_OUT_OF_STOCK:
                    mAvailabilitySpinner.setSelection(2);
                    break;
                case ProductEntry.AVAILABILITY_UNKNOWN:
                    mAvailabilitySpinner.setSelection(0);
            }
        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mNameEditText.setText("");
        mSupplierEditText.setText("");
        mSupplierPhoneEditText.setText("");
        mAvailabilitySpinner.setSelection(0);
        mQuantityEditText.setText("");
        mPriceEditText.setText("");

    }

    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Keep editing" button, so dismiss the dialog
                // and continue editing the product.
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
     * Prompt the user to confirm that they want to delete this product.
     */
    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the product.
                deleteProduct();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Cancel" button, so dismiss the dialog
                // and continue editing the product.
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
        // Only perform the delete if this is an existing product.
        if (mActualProductUri != null) {
            // Call the ContentResolver to delete the product at the given content URI.
            // Pass in null for the selection and selection args because the mActualProductUri
            // content URI already identifies the product that we want.
            int rowsDeleted = getContentResolver().delete(mActualProductUri, null, null);

            // Show a toast message depending on whether or not the delete was successful.
            if (rowsDeleted == 0) {
                // If no rows were deleted, then there was an error with the delete.
                Toast.makeText(this, getString(R.string.editor_delete_product_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the delete was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.editor_delete_product_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }

        // Close the activity
        finish();
    }
}

