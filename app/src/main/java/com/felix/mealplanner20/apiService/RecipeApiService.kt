package com.felix.mealplanner20.apiService

import com.felix.mealplanner20.Meals.Data.DTO.IngredientWithRecipeDTO
import com.felix.mealplanner20.Meals.Data.DTO.RecipeDTO
import com.felix.mealplanner20.Meals.Data.DTO.RecipeFullDTO
import com.felix.mealplanner20.Meals.Data.DTO.RecipeWithIngredientsWithoutRecipeIdDTO
import com.felix.mealplanner20.Meals.Data.RecipeDescription
import com.felix.mealplanner20.ViewModels.RecipeResponse
import kotlinx.serialization.Serializable
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.HeaderMap
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface RecipeApiService {

    @GET("recipes")
    suspend fun fetchRecipesWithIngredientsFromOnlineSource(
        @Query("page") currentPage: Int,
        @Query("pageSize") pageSize: Int
    ):RecipeResponse

    @GET("recipes")
    suspend fun fetchRecipesForType(
        @Query("page") currentPage: Int,
        @Query("pageSize") pageSize: Int,
        @Query("mealType") mealType: String? = "dessert"
    ): RecipeResponse

    @GET("recipes/{id}")
    suspend fun fetchIngredientsByRecipeId(
        @Path("id") recipeId: Long
    ): RecipeComponentsDTO?

    @GET("recipes/full/{id}")
    suspend fun fetchFullRecipe(
        @Path("id") recipeId: Long
    ): FullRecipeDTO?

    @POST("recipes")
    suspend fun postRecipe(
        @Body recipe: RecipeFullDTO,
        @HeaderMap headers: Map<String, String>
    ): Response<ResponseBody>

    @GET("recipes/search")
    suspend fun searchRecipes(
        @Query("q") q: String,
        @Query("lang") lang: String = "de",
        @Query("page") page: Int = 1,
        @Query("pageSize") pageSize: Int = 20
    ): RecipeResponse

    @PUT("recipes/{id}")
    suspend fun putRecipe(
        @Path("id") recipeId: Long,
        @Body recipe: RecipeFullDTO,
        @HeaderMap headers: Map<String, String>
    ): Response<ResponseBody>

}

@Serializable
data class RecipeComponentsDTO(
    val ingredients: List<IngredientWithRecipeDTO>,
    val steps: List<RecipeDescription>
)
@Serializable
data class FullRecipeDTO(
    val recipe: RecipeDTO,
    val ingredients: List<IngredientWithRecipeDTO>,
    val steps: List<RecipeDescription>
)
