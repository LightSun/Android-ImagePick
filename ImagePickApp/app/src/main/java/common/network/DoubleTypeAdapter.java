package common.network;

import android.text.TextUtils;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

/**
 * Created by Administrator on 2017/5/5.
 */
public class DoubleTypeAdapter extends TypeAdapter<Number> {

    @Override
    public void write(JsonWriter out, Number value)
            throws IOException {
        if (null == value || -1 == value.intValue()){
            out.nullValue();
            return;
        }
        out.value(value);
    }

    @Override
    public Number read(JsonReader in) throws IOException {
        if (in.peek() == JsonToken.NULL) {
            in.nextNull();
            return null;
        }
        try {
            String result = in.nextString();
            if (TextUtils.isEmpty(result)) {
                return 0;
            }
            return Double.parseDouble(result);
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}
