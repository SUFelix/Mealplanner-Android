package com.felix.mealplanner20.Meals.Data

sealed class PublishRecipeResult {
    data class Success(val remoteId: Long) : PublishRecipeResult()
    object LoginRequired : PublishRecipeResult()
    object UserUnverified : PublishRecipeResult()
    object MissingImageMetadata : PublishRecipeResult()
    data class ValidationFailed(val details: List<String>, val traceId: String? = null) : PublishRecipeResult()
    data class RecipeImageUploadFailed(val traceId: String? = null) : PublishRecipeResult()
    data class DescriptionImagesUploadFailed(val traceId: String? = null) : PublishRecipeResult()
    data class PostToServerFailed(val traceId: String? = null) : PublishRecipeResult()
    data class ImageTooLarge(val traceId: String? = null) : PublishRecipeResult()
    data class ImageFormatNotSupported(val traceId: String? = null) : PublishRecipeResult()
    object Conflict : PublishRecipeResult()
    data class UnknownError(val traceId: String? = null) : PublishRecipeResult()
}

