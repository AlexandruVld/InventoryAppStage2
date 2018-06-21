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
import com.example.vlada.inventoryappstage2.data.ProductContract.ProductEntry;

public class ProductCursorAdapter extends CursorAdapter {

    public ProductCursorAdapter (Context context, Cursor c) {
        super (context, c, 0);
    }

    public View newView (Context context, Cursor c, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.item_list_view, parent, false);

    }

    public void bindView (final View view, final Context context, Cursor c) {
        TextView productNameTv = view.findViewById(R.id.product_name);
        TextView productPriceTv = view.findViewById(R.id.product_price);
        TextView productQuantityTv = view.findViewById(R.id.product_quantity);
        TextView productAvailabilityTv = view.findViewById(R.id.product_stock);
        Button productSale = view.findViewById(R.id.sell_btn);

        int columnNameIndex = getCursor().getColumnIndex(ProductEntry.COLUMN_PRODUCT_NAME);
        int columnPriceIndex = getCursor().getColumnIndex(ProductEntry.COLUMN_PRODUCT_PRICE);
        int columnQuantityIndex = getCursor().getColumnIndex(ProductEntry.COLUMN_PRODUCT_QUANTITY);
        int columnAvailabilityIndex = getCursor().getColumnIndex(ProductEntry.COLUMN_PRODUCT_AVAILABILITY);

        final String productName = getCursor().getString(columnNameIndex);
        final String productPrice = getCursor().getString(columnPriceIndex);
        final String productQuantity = getCursor().getString(columnQuantityIndex);
        final String productAvailability = getCursor().getString(columnAvailabilityIndex);

        productNameTv.setText(productName);
        productPriceTv.setText(productPrice);
        productQuantityTv.setText(productQuantity);
        productAvailabilityTv.setText(productAvailability);

        final int idColumnIndex = getCursor().getInt(getCursor().getColumnIndex(ProductEntry._ID));
        final int actualQuantityIndex = getCursor().getInt(getCursor().getColumnIndex(ProductEntry.COLUMN_PRODUCT_QUANTITY));
        final int actualQuantity = Integer.valueOf(getCursor().getString(actualQuantityIndex));
        final int actualAvailabilityIndex = getCursor().getInt(getCursor().getColumnIndex(ProductEntry.COLUMN_PRODUCT_AVAILABILITY));
        final int actualAvailability = Integer.valueOf(getCursor().getString(actualAvailabilityIndex));

        productSale.setOnClickListener(new View.OnClickListener() {
            public void onClick (View view){
                if (actualQuantity > 0 && actualAvailability > 0 && actualAvailability < 2){

                    int newActualQuantity = actualQuantity - 1;
                    Uri quantityUri = ContentUris.withAppendedId(ProductEntry.CONTENT_URI, idColumnIndex);

                    ContentValues values = new ContentValues();
                    values.put(ProductEntry.COLUMN_PRODUCT_QUANTITY, newActualQuantity);
                    context.getContentResolver().update(quantityUri, values, null, null);

                    Toast.makeText(context, context.getString(R.string.sale_successful)+
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
