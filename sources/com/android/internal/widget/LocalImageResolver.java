package com.android.internal.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import java.io.IOException;
import java.io.InputStream;

public class LocalImageResolver {
    private static final int MAX_SAFE_ICON_SIZE_PX = 480;

    public static Drawable resolveImage(Uri uri, Context context) throws IOException {
        Options onlyBoundsOptions = getBoundsOptionsForImage(uri, context);
        if (onlyBoundsOptions.outWidth == -1 || onlyBoundsOptions.outHeight == -1) {
            return null;
        }
        int originalSize;
        double ratio;
        if (onlyBoundsOptions.outHeight > onlyBoundsOptions.outWidth) {
            originalSize = onlyBoundsOptions.outHeight;
        } else {
            originalSize = onlyBoundsOptions.outWidth;
        }
        if (originalSize > 480) {
            ratio = (double) (originalSize / 480);
        } else {
            ratio = 1.0d;
        }
        Options bitmapOptions = new Options();
        bitmapOptions.inSampleSize = getPowerOfTwoForSampleRatio(ratio);
        InputStream input = context.getContentResolver().openInputStream(uri);
        Bitmap bitmap = BitmapFactory.decodeStream(input, null, bitmapOptions);
        input.close();
        return new BitmapDrawable(context.getResources(), bitmap);
    }

    private static Options getBoundsOptionsForImage(Uri uri, Context context) throws IOException {
        InputStream input = context.getContentResolver().openInputStream(uri);
        Options onlyBoundsOptions = new Options();
        onlyBoundsOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(input, null, onlyBoundsOptions);
        input.close();
        return onlyBoundsOptions;
    }

    private static int getPowerOfTwoForSampleRatio(double ratio) {
        return Math.max(1, Integer.highestOneBit((int) Math.floor(ratio)));
    }
}
