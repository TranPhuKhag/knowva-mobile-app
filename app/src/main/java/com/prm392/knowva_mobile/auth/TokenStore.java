package com.prm392.knowva_mobile.auth;

import android.content.Context;
import android.content.SharedPreferences;
import java.util.logging.Logger;
public final class TokenStore {
    private final SharedPreferences sp;

    public TokenStore(Context ctx) {
        sp = ctx.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE);
    }

    public void save(String token) {

        sp.edit().putString("token", token).apply(); }
    public String get() { return sp.getString("token", null); }
    public void clear() { sp.edit().clear().apply(); }
}
