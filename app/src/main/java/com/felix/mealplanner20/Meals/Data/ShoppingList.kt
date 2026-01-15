package com.felix.mealplanner20.Shopping.Data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.felix.mealplanner20.Meals.Data.helpers.UnitOfMeasure

@Entity(tableName = "shopping_list_table")
data class ShoppingListItem(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    @ColumnInfo("ingredientId")
    val ingredientId: Long = 0L,
    @ColumnInfo(name = "quantity")
    val quantity: Float = 1f,
    @ColumnInfo(name = "unitOfMeasure")
    val unitOfMeasure: UnitOfMeasure = UnitOfMeasure.GRAM
)
