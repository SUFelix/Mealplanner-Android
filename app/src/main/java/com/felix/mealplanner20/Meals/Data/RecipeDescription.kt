package com.felix.mealplanner20.Meals.Data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.Companion.CASCADE
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable
import java.util.UUID

@Entity(
    tableName = "recipe_description",
    foreignKeys = [
        ForeignKey(
            entity = Recipe::class,
            parentColumns = ["id"],
            childColumns = ["recipeId"],
            onDelete = CASCADE
        )
    ],
    indices = [Index(value = ["recipeId"])] // Beschleunigt JOINs auf recipeId
)
@Serializable
data class RecipeDescription(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val recipeId: Int,
    val stepNr: Int,
    val text: String,
    val englishText: String,
    val germanText: String,
    val imgUri: String? = null
)
