package com.felix.mealplanner20.use_cases

data class RecipeUseCases(
    val addRecipeUseCase: AddRecipeUseCase,
    val getRecipeByIdUseCase: GetRecipeByIdUseCase,
    val getAllIngredientsForOneRecipeUseCase: GetAllIngredientsForOneRecipeUseCase,
    val deleteRecipeByIdUseCase: DeleteRecipeByIdUseCase,
    val getRecipeCaloriesUseCase: GetRecipeCaloriesUseCase,
    val addIngredientToRecipeUseCase: AddIngredientToRecipeUseCase,
    val deleteIngredientFromRecipeUseCase: DeleteIngredientFromRecipeUseCase,
    val updateRecipeMainTable: UpdateRecipeMainTable,
    val createShoppingListUseCase: CreateShoppingListUseCase,
    val getShoppingListUseCase: GetShoppingListUseCase,
    val clearShoppingListUseCase: ClearShoppingListUseCase,
    val deleteOneItemFromShoppingListUseCase: DeleteOneItemFromShoppingListUseCase,
    val areAllIngredientsVeganUseCase: AreAllIngredientsVeganUseCase,
    val areAllIngredientsVegetarianUseCase: AreAllIngredientsVegetarianUseCase,
    val calcCPFratioUseCase: CalculateCPFratioUseCase,
    val calculateTotalCaloriesForIngredientWithRecipeListUseCase: CalculateTotalCaloriesForIngredientWithRecipeListUseCase
)

