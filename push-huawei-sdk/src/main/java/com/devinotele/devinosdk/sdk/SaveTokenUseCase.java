package com.devinotele.devinosdk.sdk;


import android.text.TextUtils;

import com.huawei.hms.aaid.HmsInstanceId;
import com.huawei.hms.common.ApiException;


class SaveTokenUseCase extends BaseUC {

    private DevinoLogsCallback logsCallback;

    SaveTokenUseCase(HelpersPackage hp, DevinoLogsCallback callback) {
        super(hp);
        logsCallback = callback;
    }

    void run(String agAppId, HmsInstanceId hmsInstanceId) {
        new Thread() {
            @Override
            public void run() {
                try {
                    String tokenScope = "HCM";
                    String token = hmsInstanceId.getToken(agAppId, tokenScope);

                    if (!TextUtils.isEmpty(token)) {
                        String persistedToken = sharedPrefsHelper.getString(SharedPrefsHelper.KEY_PUSH_TOKEN);

                        if (!token.equals(persistedToken)) {
                            sharedPrefsHelper.saveData(SharedPrefsHelper.KEY_PUSH_TOKEN, token);
                            networkRepository.updateToken(token);
                            logsCallback.onMessageLogged("Push token persisted\n" + token);
                            DevinoSdk.getInstance().appStarted();
                        }
                    }
                } catch (ApiException e) {
                    logsCallback.onMessageLogged("Push Kit Error: " + e.getMessage());
                }
            }
        }.start();
    }
}
