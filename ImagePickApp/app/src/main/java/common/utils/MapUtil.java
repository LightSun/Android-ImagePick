package common.utils;

import android.text.TextUtils;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.json.JSONException;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import common.network.NetworkConfig;

public class MapUtil {
    public static HashMap<String, Object> jo2HashMap(JsonObject jsonObject) throws JSONException {
        HashMap<String, Object> result = get();

        Set<Map.Entry<String, JsonElement>> entries = jsonObject.entrySet();
        if( entries.isEmpty()) return result;

        Iterator<Map.Entry<String, JsonElement>> iterator = entries.iterator();
        while (iterator.hasNext()){
            Map.Entry<String, JsonElement> next = iterator.next();
            result.put(next.getKey(),next.getValue());
        }
        return result;
    }

    public static CustomerHashMap get() {
        return new CustomerHashMap();
    }

    public static class CustomerHashMap extends HashMap<String, Object> {

        public CustomerHashMap() {
            putToken();
        }
        public CustomerHashMap(int initialCapacity) {
            super(TextUtils.isEmpty(NetworkConfig.getToken()) ? initialCapacity : initialCapacity + 1);
            putToken();
        }

        private void putToken() {
            String token = NetworkConfig.getToken();
            if (!TextUtils.isEmpty(token)) {
                put("token", token);
            }
        }

        public CustomerHashMap append(String key, Object value) {
            this.put(key, value);
            return this;
        }
    }

    public static HashMap<String, Object> empty() {
        return get();
    }
}