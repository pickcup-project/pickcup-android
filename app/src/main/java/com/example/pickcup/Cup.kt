package com.example.pickcup

data class Cup(
    val name: String,
    val imageUrl: String?,
    val videoUrl: String? // 영상이 없을 수도 있으므로 Nullable
)
