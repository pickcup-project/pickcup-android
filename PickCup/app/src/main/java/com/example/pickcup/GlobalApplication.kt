package com.example.pickcup

import android.app.Application
import com.kakao.sdk.common.KakaoSdk

class GlobalApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // 카카오 SDK 초기화
        // 네이티브 앱 키를 여기에 입력하세요. (AndroidManifest.xml과 동일)
        KakaoSdk.init(this, "c2d50bfa5f57371b6929bf68d2be3275")
    }
}