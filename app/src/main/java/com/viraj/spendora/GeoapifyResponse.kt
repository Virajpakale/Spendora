package com.viraj.spendora

data class GeoapifyResponse(
    val features: List<Feature>
)

data class Feature(
    val properties: Properties
)

data class Properties(
    val name: String?,
    val formatted: String?
)