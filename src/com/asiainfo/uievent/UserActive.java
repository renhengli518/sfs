package com.asiainfo.uievent;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import com.asiainfo.model.SfsErrorCode;
import com.asiainfo.model.SfsResult;
import com.asiainfo.model.User;
import com.asiainfo.proto.Active;
import com.asiainfo.proto.SfsServerGet;

/**
 * Created with IntelliJ IDEA.
 * User: outofforce
 * Date: 13-8-11
 * Time: 下午3:32
 * To change this template use File | Settings | File Templates.
 */
public class UserActive implements ISfsUiEvent {


    @Override
    public Intent doUiEvent(Intent intent, Context cx, SfsResult result) {
        Intent t = new Intent();
        User user = intent.getParcelableExtra("User");
        String code = intent.getStringExtra("ActiveCode");

        if (code == null) {
            result.err_code = SfsErrorCode.E_UI_ARG;
            result.err_msg = "arg ActiveCode is NULL";
            return t;
        }

        if (user != null) {
            Active req = new Active(user,code);
            SfsServerGet.ServerResult res =  req.handle();
            result.err_msg = res.err_msg;
            result.result = res.result;
            result.err_code = res.err_code;
            if (res.err_code == SfsErrorCode.Success) {
                SharedPreferences mPerferences = PreferenceManager
                        .getDefaultSharedPreferences(cx);
                SharedPreferences.Editor mEditor = mPerferences.edit();
                mEditor.putInt("Status", User.NORMAL);
                mEditor.commit();
            }

        } else {
            result.err_code = SfsErrorCode.E_UI_ARG;
            result.err_msg = "arg User is NULL";

        }

        return t ;
    }
}
