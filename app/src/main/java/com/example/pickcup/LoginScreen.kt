//package com.example.pickcup
//
//import android.util.Log
//import androidx.compose.material3.Button
//import androidx.compose.material3.Text
//import androidx.compose.runtime.Composable
//import androidx.compose.ui.platform.LocalContext
//import com.kakao.sdk.auth.model.OAuthToken
//import com.kakao.sdk.user.UserApiClient
//
//// 1. 카카오 로그인 결과를 처리하는 콜백 함수 정의
//private val kakaoLoginCallback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
//    if (error != null) {
//        // 로그인 실패 처리
//        Log.e("KakaoLogin", "로그인 실패", error)
//    } else if (token != null) {
//        // 로그인 성공 처리
//        Log.i("KakaoLogin", "로그인 성공: ${token.accessToken}")
//        // 사용자 정보 가져오기 함수 호출
//        getUserInfo()
//    }
//}
//
//// 2. 로그인 버튼 Composable
//@Composable
//fun KakaoLoginButton() {
//    val context = LocalContext.current
//
//    Button(onClick = {
//        // 카카오톡 설치 유무 확인
//        if (UserApiClient.instance.isKakaoTalkLoginAvailable(context)) {
//            // 카카오톡 앱으로 로그인
//            UserApiClient.instance.loginWithKakaoTalk(context, callback = kakaoLoginCallback)
//        } else {
//            // 카카오톡 미설치 시 웹 브라우저로 로그인
//            UserApiClient.instance.loginWithKakaoAccount(context, callback = kakaoLoginCallback)
//        }
//    }) {
//        Text("카카오 로그인 시작")
//    }
//}
//
//
/////사용자 정보 가져오기 함수 정의
//private fun getUserInfo() {
//    UserApiClient.instance.me { user, error ->
//        if (error != null) {
//            Log.e("KakaoLogin", "사용자 정보 요청 실패", error)
//        } else if (user != null) {
//            Log.i("KakaoLogin", "사용자 정보 요청 성공")
//            Log.i("KakaoLogin", "회원번호: ${user.id}")
//            Log.i("KakaoLogin", "닉네임: ${user.kakaoAccount?.profile?.nickname}")
//            // 이제 이 정보를 앱의 서버로 보내거나 내부적으로 처리할 수 있습니다.
//        }
//    }
//}