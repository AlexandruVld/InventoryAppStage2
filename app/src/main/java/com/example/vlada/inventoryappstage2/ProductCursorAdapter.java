package com.example.vlada.inventoryappstage2;

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
import android.widget.TextView;
import android.widget.Toast;

import com.example.vlada.inventoryappstage2.data.ProductContract;
import com.example.vlada.inventoryappstage2.data.ProductContract.ProductEntry;

public class ProductCursorAdapter extends CursorAdapter {

    public ProductCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }

    public View newView(Context context, Cursor c, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.item_list_view, parent, false);

    }

    public void bindView(final View view, final Context context, Cursor cursor) {
        TextView productNameTv = view.findViewById(R.id.product_name);
        TextView productQuantityTv = view.findViewById(R.id.product_quantity);
        TextView productPriceTv = view.findViewById(R.id.product_price);
        Button productSale = view.findViewById(R.id.sell_btn);

        int columnNameIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_NAME);
        int columnQuantityIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_QUANTITY);
        int columnPriceIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_PRICE);

        final String productName = cursor.getString(columnNameIndex);
        final String productQuantity = cursor.getString(columnQuantityIndex);
        final String productPrice = cursor.getString(columnPriceIndex);

        productNameTv.setText("Product name: " + productName);
        productQuantityTv.setText("Quantity: " + productQuantity);
        productPriceTv.setText("Price: " + productPrice);

        final int idColumnIndex = cursor.getInt(getCursor().getColumnIndex(ProductContract.ProductEntry._ID));
        final int actualQuantityIndex = cursor.getColumnIndex(ProductContract.ProductEntry.COLUMN_PRODUCT_QUANTITY);
        final int actualQuantity = Integer.valueOf(cursor.getString(actualQuantityIndex));

        productSale.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (actualQuantity > 0) {

                    int newActualQuantity = actualQuantity - 1;
                    Uri quantityUri = ContentUris.withAppendedId(ProductContract.ProductEntry.CONTENT_URI, idColumnIndex);

                    ContentValues values = new ContentValues();
                    values.put(ProductContract.ProductEntry.COLUMN_PRODUCT_QUANTITY, newActualQuantity);
                    context.getContentResolver().update(quantityUri, values, null, null);

                    Toast.makeText(context, context.getString(R.string.sale_successful) +
                            productName + context.getString(R.string.remaining_quantity) +
                            newActualQuantity, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, context.getString(R.string.sale_error) +
                            productName + context.getString(R.string.no_stock), Toast.LENGTH_SHORT).show();
                }

            }
        });

    }
}
