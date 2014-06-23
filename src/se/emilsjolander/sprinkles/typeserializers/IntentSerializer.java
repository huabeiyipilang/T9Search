package se.emilsjolander.sprinkles.typeserializers;

import java.net.URISyntaxException;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;

public class IntentSerializer implements TypeSerializer<Intent> {

    @Override
    public Intent unpack(Cursor c, String name) {
        String value = c.getString(c.getColumnIndexOrThrow(name));
        try {
            return Intent.parseUri(value, 0);
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void pack(Intent intent, ContentValues cv, String name) {
        cv.put(name, intent.toUri(0));
    }

    @Override
    public String toSql(Intent intent) {
        return intent.toUri(0);
    }

    @Override
    public SqlType getSqlType() {
        return SqlType.TEXT;
    }

}
