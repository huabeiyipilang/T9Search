package se.emilsjolander.sprinkles.typeserializers;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import android.content.ContentValues;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class BitmapSerializer implements TypeSerializer<Bitmap> {

    @Override
    public Bitmap unpack(Cursor c, String name) {
        byte[] data = c.getBlob(c.getColumnIndexOrThrow(name));
        try {
            return BitmapFactory.decodeByteArray(data, 0, data.length);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public void pack(Bitmap object, ContentValues cv, String name) {
        cv.put(name, flattenBitmap(object));
    }

    @Override
    public String toSql(Bitmap object) {
        return flattenBitmap(object).toString();
    }

    @Override
    public SqlType getSqlType() {
        return SqlType.BLOB;
    }
    
    private static byte[] flattenBitmap(Bitmap bitmap){
        int size = bitmap.getWidth() * bitmap.getHeight() * 4;
        ByteArrayOutputStream out = new ByteArrayOutputStream(size);
        try {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.flush();
            out.close();
            return out.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
