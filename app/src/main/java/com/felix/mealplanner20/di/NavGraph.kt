package com.felix.mealplanner20.di

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import coil.ImageLoader
import coil.disk.DiskCache
import coil.memory.MemoryCache
import com.felix.mealplanner20.ImageUpDownLoad
import com.felix.mealplanner20.Meals.Data.AppDatabase
import com.felix.mealplanner20.Meals.Data.IngredientAllowedUnitDao
import com.felix.mealplanner20.Meals.Data.IngredientDao
import com.felix.mealplanner20.Meals.Data.IngredientRepository
import com.felix.mealplanner20.Meals.Data.MealPlanRepository
import com.felix.mealplanner20.Meals.Data.ProfileRepository
import com.felix.mealplanner20.Meals.Data.RecipeDao
import com.felix.mealplanner20.Meals.Data.RecipeMealTypeWeightDao
import com.felix.mealplanner20.Meals.Data.RecipeRepository
import com.felix.mealplanner20.Meals.Data.SettingsRepository
import com.felix.mealplanner20.Shopping.Data.ShoppingListRepository
import com.felix.mealplanner20.apiService.FeedbackApiService
import com.felix.mealplanner20.apiService.ImageApiService
import com.felix.mealplanner20.apiService.IngredientAllowedUnitApiService
import com.felix.mealplanner20.apiService.IngredientApiService
import com.felix.mealplanner20.apiService.ProfileApiService
import com.felix.mealplanner20.apiService.RecipeApiService
import com.felix.mealplanner20.use_cases.AddIngredientToRecipeUseCase
import com.felix.mealplanner20.use_cases.AddRecipeUseCase
import com.felix.mealplanner20.use_cases.AreAllIngredientsVeganUseCase
import com.felix.mealplanner20.use_cases.AreAllIngredientsVegetarianUseCase
import com.felix.mealplanner20.use_cases.CalculateCPFratioUseCase
import com.felix.mealplanner20.use_cases.CalculateCaloriesUseCase
import com.felix.mealplanner20.use_cases.CalculateNutritionQualityUseCase
import com.felix.mealplanner20.use_cases.CalculateTotalCaloriesForIngredientWithRecipeListUseCase
import com.felix.mealplanner20.use_cases.CleanUnusedRecipeImagesUseCase
import com.felix.mealplanner20.use_cases.ClearShoppingListUseCase
import com.felix.mealplanner20.use_cases.CreateShoppingListUseCase
import com.felix.mealplanner20.use_cases.DeleteIngredientFromRecipeUseCase
import com.felix.mealplanner20.use_cases.DeleteOneItemFromShoppingListUseCase
import com.felix.mealplanner20.use_cases.DeleteRecipeByIdUseCase
import com.felix.mealplanner20.use_cases.DownloadRecipeUseCase
import com.felix.mealplanner20.use_cases.GetAllIngredientsForOneRecipeUseCase
import com.felix.mealplanner20.use_cases.GetDayDetailsUseCase
import com.felix.mealplanner20.use_cases.GetDgeRecommendationDataUseCase
import com.felix.mealplanner20.use_cases.GetMacroNutrientRecomendationsUseCase
import com.felix.mealplanner20.use_cases.GetMealPlanDayByIdUseCase
import com.felix.mealplanner20.use_cases.GetOwnEmailUseCase
import com.felix.mealplanner20.use_cases.GetOwnProfileDescriptionUseCase
import com.felix.mealplanner20.use_cases.GetOwnProfilePictureUseCase
import com.felix.mealplanner20.use_cases.GetRecipeByIdUseCase
import com.felix.mealplanner20.use_cases.GetRecipeCaloriesUseCase
import com.felix.mealplanner20.use_cases.GetShoppingListUseCase
import com.felix.mealplanner20.use_cases.MealPlanGenerator
import com.felix.mealplanner20.use_cases.MealPlanUseCases
import com.felix.mealplanner20.use_cases.NutritionBasicUseCases
import com.felix.mealplanner20.use_cases.RecipeUseCases
import com.felix.mealplanner20.use_cases.SynchronizeIngredientAllowedUnitUseCase
import com.felix.mealplanner20.use_cases.SynchronizeIngredientsUseCase
import com.felix.mealplanner20.use_cases.UpdateMealPlanDayUseCase
import com.felix.mealplanner20.use_cases.UpdateRecipeMainTable
import com.felix.mealplanner20.use_cases.UpdateRecipeUseCase
import com.felix.mealplanner20.use_cases.UploadIngredientUseCase
import com.felix.mealplanner20.use_cases.UploadNewProfileDescriptionUseCase
import com.felix.mealplanner20.use_cases.UploadNewProfilePictureUseCase
import com.felix.mealplanner20.use_cases.UploadRecipeUseCase
import com.felix.mealplanner20.use_cases.UploadUpdateIngredientUseCase
import com.mealplanner20.jwtauthktorandroid.auth.AuthApi
import com.mealplanner20.jwtauthktorandroid.auth.AuthRepository
import com.mealplanner20.jwtauthktorandroid.auth.AuthRepositoryImpl
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.Cache
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.create
import java.io.File
import java.util.concurrent.TimeUnit
import javax.inject.Singleton


const val BASE_URL = "https://www.mealplannerpro.net/"

@Module
@InstallIn(SingletonComponent::class)
object NavGraph {

    @Provides
    @Singleton
    fun provideDatabase(
        context: Context,
    ): AppDatabase {
        return Room.databaseBuilder(context, AppDatabase::class.java, "app_database.db")
            .addMigrations(MIGRATION_20_21)
            .addMigrations(MIGRATION_21_22)
            .addMigrations(MIGRATION_22_23)
            .addMigrations(MIGRATION_23_24)
            .addMigrations(MIGRATION_24_25)
            .addMigrations(MIGRATION_25_26)
            .addMigrations(MIGRATION_26_27)
            .addMigrations(MIGRATION_27_28)
            .addMigrations(MIGRATION_28_29)
            .addMigrations(MIGRATION_29_30)
            .addMigrations(MIGRATION_30_31)
            .addMigrations(MIGRATION_31_32)
            .addMigrations(MIGRATION_32_33)
            .addMigrations(MIGRATION_33_34)
            .addMigrations(MIGRATION_34_35)
            .addMigrations(MIGRATION_35_36)
            .addMigrations(MIGRATION_36_37)
            .addMigrations(MIGRATION_37_38)
            .build()
    }
    @Provides
    @Singleton
    fun provideContext(@ApplicationContext context: Context): Context {
        return context
    }

    @Provides @Singleton
    fun provideMoshi(): Moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()



    @Provides @Singleton
    fun provideRetrofit(moshi: Moshi, client: OkHttpClient): Retrofit =
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()

    @Provides @Singleton
    fun provideAuthApi(retrofit: Retrofit): AuthApi =
        retrofit.create()

    @Provides @Singleton
    fun provideIngredientApi(retrofit: Retrofit): IngredientApiService =
        retrofit.create()

    @Provides @Singleton
    fun provideIngredientAllowedUnitApiService(retrofit: Retrofit): IngredientAllowedUnitApiService =
        retrofit.create()

    @Provides @Singleton
    fun provideRecipeApi(retrofit: Retrofit): RecipeApiService =
        retrofit.create()

    @Provides
    @Singleton
    fun provideImageApi(retrofit: Retrofit): ImageApiService =
        retrofit.create()

    @Provides @Singleton
    fun provideFeedbackApi(retrofit: Retrofit): FeedbackApiService =
        retrofit.create()

    @Provides
    @Singleton
    fun provideOkHttp(
        @ApplicationContext ctx: Context
    ): OkHttpClient {
        val cacheDir = File(ctx.cacheDir, "http_cache")
        val cache = Cache(cacheDir, 100L * 1024 * 1024) // 100 MB
        return OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .callTimeout(60, TimeUnit.SECONDS)
            .retryOnConnectionFailure(true)
            .cache(cache)
            .addNetworkInterceptor { chain ->
                val req = chain.request()
                val resp = chain.proceed(req)

                // Nur GETs, nur erfolgreiche 200er, nur Content-Type image/*
                val isGet = req.method.equals("GET", ignoreCase = true)
                val is200 = resp.code == 200
                val ct = resp.header("Content-Type") ?: ""

                val shouldAddDefaultCache =
                    isGet &&
                            is200 &&
                            ct.startsWith("image/", ignoreCase = true) &&
                            resp.header("Cache-Control").isNullOrBlank()

                if (shouldAddDefaultCache) {
                    resp.newBuilder()
                        // lange lokal cachen; immutables, da Bildinhalte sich selten unter gleichem Key ändern
                        .header("Cache-Control", "public, max-age=86400, immutable")
                        .build()
                } else {
                    resp
                }
            }
            // followRedirects/followSslRedirects sind true per Default (gut für S3-Redirects)
            .build()
    }

    @Provides
    @Singleton
    fun provideImageLoader(@ApplicationContext ctx: Context, okHttp: OkHttpClient): ImageLoader {
        return ImageLoader.Builder(ctx)
            .okHttpClient(okHttp)
            .crossfade(true)
            .diskCache(
                DiskCache.Builder()
                    .directory(File(ctx.cacheDir, "image_cache"))
                    .maxSizeBytes(150L * 1024 * 1024) // 150 MB
                    .build()
            )
            .memoryCache(
                MemoryCache.Builder(ctx)
                    .maxSizePercent(0.25) // 25% vom verfügbaren RAM
                    .build()
            )
            .build()
    }

    @Provides
    @Singleton
    fun provideProfileApi(retrofit: Retrofit): ProfileApiService =
        retrofit.create()

    @Provides
    @Singleton
    fun provideSharedPref(app: Application): SharedPreferences {
        return app.getSharedPreferences("prefs", Context.MODE_PRIVATE)
    }

    @Provides
    @Singleton
    fun provideAuthRepository(api: AuthApi, prefs: SharedPreferences): AuthRepository {
        return AuthRepositoryImpl(api, prefs)
    }
    @Provides
    @Singleton
    fun provideImageUpDownLoad(
        imageApiService: ImageApiService
    ): ImageUpDownLoad {
        return ImageUpDownLoad(imageApiService = imageApiService)
    }
    @Provides
    @Singleton
    fun provideUploadIngredientUseCase(
        ingredientRepository: IngredientRepository,
        authRepository: AuthRepository
    ): UploadIngredientUseCase {
        return UploadIngredientUseCase(ingredientRepository,authRepository)
    }
    @Provides
    @Singleton
    fun provideCleanUnusedRecipeImagesUseCase(
        recipeRepository: RecipeRepository
    ): CleanUnusedRecipeImagesUseCase {
        return CleanUnusedRecipeImagesUseCase(recipeRepository)
    }
    @Provides
    @Singleton
    fun provideDownloadRecipeUseCase(
        recipeRepository: RecipeRepository,
        imageApiService: ImageApiService,
        settingsRepository: SettingsRepository
    ): DownloadRecipeUseCase {
        return DownloadRecipeUseCase(recipeRepository,imageApiService, settingsRepository)
    }
    @Provides
    @Singleton
    fun provideUploadNewProfileDescriptionUseCase(
        profileRepository: ProfileRepository,
        authRepository: AuthRepository
    ): UploadNewProfileDescriptionUseCase {
        return UploadNewProfileDescriptionUseCase(profileRepository,authRepository)
    }
    @Provides
    @Singleton
    fun provideUploadNewProfilePictureUseCase(
        profileRepository: ProfileRepository,
        authRepository: AuthRepository
    ): UploadNewProfilePictureUseCase {
        return UploadNewProfilePictureUseCase(profileRepository,authRepository)
    }
    @Provides
    @Singleton
    fun provideGetOwnProfileDescriptionUseCase(
        profileRepository: ProfileRepository,
        authRepository: AuthRepository
    ): GetOwnProfileDescriptionUseCase {
        return GetOwnProfileDescriptionUseCase(profileRepository,authRepository)
    }
    @Provides
    @Singleton
    fun provideGetOwnEmailUseCase(
        profileRepository: ProfileRepository,
        authRepository: AuthRepository
    ): GetOwnEmailUseCase {
        return GetOwnEmailUseCase(profileRepository,authRepository)
    }
    @Provides
    @Singleton
    fun provideGetOwnProfilePictureUseCase(
        profileRepository: ProfileRepository,
        authRepository: AuthRepository
    ): GetOwnProfilePictureUseCase {
        return GetOwnProfilePictureUseCase(profileRepository,authRepository)
    }
    @Provides
    @Singleton
    fun provideUploadUpdateIngredientUseCase(
        ingredientRepository: IngredientRepository,
        authRepository: AuthRepository
    ): UploadUpdateIngredientUseCase {
        return UploadUpdateIngredientUseCase(ingredientRepository,authRepository)
    }

    @Provides
    @Singleton
    fun provideSyncronzieIngredientsUseCase(
        ingredientRepository: IngredientRepository
    ): SynchronizeIngredientsUseCase {
        return SynchronizeIngredientsUseCase(ingredientRepository)
    }
    @Provides
    @Singleton
    fun provideUploadRecipeUseCase(
        recipeRepository: RecipeRepository,
        authRepository: AuthRepository
    ): UploadRecipeUseCase {
        return UploadRecipeUseCase(recipeRepository,authRepository)
    }

    @Provides
    @Singleton
    fun provideUpdateRecipeUseCase(
        recipeRepository: RecipeRepository,
        authRepository: AuthRepository
    ): UpdateRecipeUseCase {
        return UpdateRecipeUseCase(recipeRepository,authRepository)
    }
    @Provides
    @Singleton
    fun provideIngredientDao(database: AppDatabase): IngredientDao {
        return database.ingredientDao()
    }

    @Provides
    @Singleton
    fun provideIngredientAllowedUnitDao(database: AppDatabase): IngredientAllowedUnitDao {
        return database.ingredientAllowedUnitDao()
    }

    @Provides
    @Singleton
    fun provideRecipeMealTypeWeightDao(database: AppDatabase): RecipeMealTypeWeightDao {
        return database.recipeMealTypeWeightDao()
    }

    @Provides
    @Singleton
    fun provideRecipeDao(database: AppDatabase): RecipeDao {
        return database.recipeDao()
    }

    @Provides
    @Singleton
    fun provideIngredientRepository(database: AppDatabase, ingredientApiService : IngredientApiService): IngredientRepository {
        return IngredientRepository(database.ingredientDao(), ingredientApiService = ingredientApiService)
    }
    @Provides
    @Singleton
    fun provideRecipeRepository(database: AppDatabase,recipeApiService:RecipeApiService,imageApiService: ImageApiService,upDownLoad: ImageUpDownLoad,ingredientAllowedUnitDao: IngredientAllowedUnitDao,recipeMealTypeWeightDao:RecipeMealTypeWeightDao): RecipeRepository {
        return RecipeRepository(
            recipeDao = database.recipeDao(),
            ingredientRecipeJoinDao = database.ingredientRecipeJoinDao(),
            ingredientDao = database.ingredientDao(),
            recipeDescriptionDao = database.recipeDescriptionDao(),
            recipeApiService = recipeApiService,
            imageApiService = imageApiService,
            updownload = upDownLoad,
            ingredientAllowedUnitDao = ingredientAllowedUnitDao,
            weightDao = recipeMealTypeWeightDao
        )
    }
    @Provides
    @Singleton
    fun provideSettingsRepository(database: AppDatabase): SettingsRepository {
        return SettingsRepository(database.settingsDao())
    }
    @Provides
    @Singleton
    fun provideMealPlanDayRepository(database: AppDatabase): MealPlanRepository {
        return MealPlanRepository(
            database.mealPlanDayDao(),
            database.recipeDao(),
            database.mealPlanDayRecipeDao()
        )
    }
    @Provides
    @Singleton
    fun provideShoppingListRepository(database: AppDatabase): ShoppingListRepository {
        return ShoppingListRepository(
            database.shoppingListDao(),
            database.ingredientDao()
        )
    }
    @Provides
    @Singleton
    fun provideRecipeUseCases(
        recipeRepository: RecipeRepository,
        mealPlanRepository: MealPlanRepository,
        shoppingListRepository: ShoppingListRepository,
        ingredientRepository: IngredientRepository
    ): RecipeUseCases {
        return RecipeUseCases(
            addRecipeUseCase = AddRecipeUseCase(recipeRepository),
            getRecipeByIdUseCase = GetRecipeByIdUseCase(recipeRepository),
            getAllIngredientsForOneRecipeUseCase = GetAllIngredientsForOneRecipeUseCase(recipeRepository),
            deleteRecipeByIdUseCase = DeleteRecipeByIdUseCase(recipeRepository),
            getRecipeCaloriesUseCase = GetRecipeCaloriesUseCase(recipeRepository),
            addIngredientToRecipeUseCase = AddIngredientToRecipeUseCase(recipeRepository, ingredientRepository),
            deleteIngredientFromRecipeUseCase = DeleteIngredientFromRecipeUseCase(recipeRepository),
            updateRecipeMainTable = UpdateRecipeMainTable(recipeRepository),
            createShoppingListUseCase = CreateShoppingListUseCase(shoppingListRepository,mealPlanRepository,recipeRepository),
            getShoppingListUseCase = GetShoppingListUseCase(shoppingListRepository,ingredientRepository),
            clearShoppingListUseCase = ClearShoppingListUseCase(shoppingListRepository),
            deleteOneItemFromShoppingListUseCase = DeleteOneItemFromShoppingListUseCase(shoppingListRepository),
            areAllIngredientsVeganUseCase = AreAllIngredientsVeganUseCase(ingredientRepository),
            areAllIngredientsVegetarianUseCase = AreAllIngredientsVegetarianUseCase(ingredientRepository),
            calcCPFratioUseCase= CalculateCPFratioUseCase(ingredientRepository),
            calculateTotalCaloriesForIngredientWithRecipeListUseCase = CalculateTotalCaloriesForIngredientWithRecipeListUseCase(ingredientRepository)
        )
    }
    @Provides
    @Singleton
    fun provideNutritionUseCases(recipeRepository: RecipeRepository,settingsRepository: SettingsRepository):NutritionBasicUseCases{
        return NutritionBasicUseCases(
            getDgeRecommendationDataUseCase = GetDgeRecommendationDataUseCase(),
            getMacroNutrientRecommendationsUseCase = GetMacroNutrientRecomendationsUseCase(settingsRepository),
            getRecipeCaloriesUseCase = GetRecipeCaloriesUseCase(recipeRepository),
            calculateCaloriesUseCase = CalculateCaloriesUseCase()
            )
    }
    @Provides
    @Singleton
    fun providecalculateNutritionQualityUseCase(nutritionUseCases: NutritionBasicUseCases):CalculateNutritionQualityUseCase{
        return CalculateNutritionQualityUseCase(nutritionUseCases)
    }
    @Provides
    @Singleton
    fun provideMealPlanUseCases(mealPlanRepository: MealPlanRepository):MealPlanUseCases{
        return MealPlanUseCases(
            getMealPlanDayByIdUseCase = GetMealPlanDayByIdUseCase(mealPlanRepository),
            updateMealPlanDayUseCase = UpdateMealPlanDayUseCase(mealPlanRepository)
        )
    }
    @Provides
    @Singleton
    fun provideSynchronizeIngredientAllowedUnitUseCase(ingredientAllowedUnitDao: IngredientAllowedUnitDao,ingredientAllowedUnitApiService: IngredientAllowedUnitApiService): SynchronizeIngredientAllowedUnitUseCase {
        return SynchronizeIngredientAllowedUnitUseCase(
            ingredientAllowedUnitDao = ingredientAllowedUnitDao,
            ingredientAllowedUnitApiService = ingredientAllowedUnitApiService
        )
    }
    @Provides
    @Singleton
    fun provideMealPlanGenerator(mealPlanRepository: MealPlanRepository,recipeRepository: RecipeRepository, settingsRepository: SettingsRepository,nutritionUseCases: NutritionBasicUseCases):MealPlanGenerator{
        return MealPlanGenerator(mealPlanRepository,recipeRepository,settingsRepository,nutritionUseCases)
    }
    @Provides
    @Singleton
    fun provideGetDayDetailsUseCase(
        nutritionUseCases: NutritionBasicUseCases,
        mealPlanRepository: MealPlanRepository,
        recipeRepository: RecipeRepository,
        ingredientRepository: IngredientRepository
    ): GetDayDetailsUseCase {
        return GetDayDetailsUseCase(
            nutritionUseCases = nutritionUseCases,
            mealPlanRepository = mealPlanRepository,
            recipeRepository = recipeRepository,
            ingredientRepository = ingredientRepository
        )
    }

    val MIGRATION_20_21 = object : Migration(20, 21) {
        override fun migrate(database: SupportSQLiteDatabase) {
            // Füge die neuen Spalten isVegan und isVegetarian zur recipe_table hinzu
            database.execSQL("ALTER TABLE recipe_table ADD COLUMN isVegan INTEGER NOT NULL DEFAULT 0")
            database.execSQL("ALTER TABLE recipe_table ADD COLUMN isVegetarian INTEGER NOT NULL DEFAULT 0")
        }
    }
    val MIGRATION_21_22 = object : Migration(21, 22) {
        override fun migrate(database: SupportSQLiteDatabase) {
        }
    }
    val MIGRATION_22_23 = object : Migration(22, 23) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL("ALTER TABLE recipe_table ADD COLUMN servings REAL NOT NULL DEFAULT 1.0")
        }
    }
    val MIGRATION_23_24 = object : Migration(23, 24) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL("ALTER TABLE recipe_table ADD COLUMN createdBy TEXT")
        }
    }

    val MIGRATION_24_25 = object : Migration(24, 25) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL("ALTER TABLE recipe_table ADD COLUMN cpfRatio TEXT DEFAULT NULL")
        }
    }
    val MIGRATION_25_26 = object : Migration(25, 26) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL("ALTER TABLE recipe_table ADD COLUMN isDessert INTEGER NOT NULL DEFAULT 0")
            database.execSQL("ALTER TABLE recipe_table ADD COLUMN caloriesPerServing REAL NOT NULL DEFAULT 444.0")
        }
    }
    val MIGRATION_26_27 = object : Migration(26, 27) {
        override fun migrate(database: SupportSQLiteDatabase) {
            // Neue Spalte zur settings_table hinzufügen
            database.execSQL("ALTER TABLE settings_table ADD COLUMN profilePictureLocalUri TEXT")
        }
    }
    val MIGRATION_27_28 = object : Migration(27, 28) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL("ALTER TABLE ingredient_table ADD COLUMN englishName TEXT")
        }
    }
    val MIGRATION_28_29 = object : Migration(28, 29) {
        override fun migrate(database: SupportSQLiteDatabase) {
            // 1. Neue Tabelle "recipe_description" erstellen
            database.execSQL(
                """
            CREATE TABLE IF NOT EXISTS recipe_description (
                recipeId INTEGER NOT NULL,
                stepNr INTEGER NOT NULL,
                text TEXT NOT NULL,
                imgUri TEXT,
                PRIMARY KEY (recipeId, stepNr),
                FOREIGN KEY (recipeId) REFERENCES recipe_table(id) ON DELETE CASCADE
            )
            """.trimIndent()
            )


            database.execSQL("ALTER TABLE recipe_table RENAME TO recipe_table_old")

            // Neue Tabelle mit allen benötigten Spalten erstellen
            database.execSQL(
                """
            CREATE TABLE recipe_table (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                `recipe-name` TEXT NOT NULL DEFAULT 'untitled recipe',
                isFavorit INTEGER NOT NULL DEFAULT 0,
                isMeal INTEGER NOT NULL DEFAULT 1,
                isSnack INTEGER NOT NULL DEFAULT 0,
                isBreakfast INTEGER NOT NULL DEFAULT 0,
                isBeverage INTEGER NOT NULL DEFAULT 0,
                imgUri TEXT,
                probability REAL NOT NULL DEFAULT 0,
                isVegan INTEGER NOT NULL DEFAULT 0,
                isVegetarian INTEGER NOT NULL DEFAULT 0,
                createdBy TEXT DEFAULT 'system',
                servings REAL NOT NULL DEFAULT 1,
                cpfRatio TEXT, 
                caloriesPerServing REAL NOT NULL DEFAULT 1,
                isDessert INTEGER NOT NULL DEFAULT 0
            )
            """.trimIndent()
            )

            // Alte Tabelle löschen
            database.execSQL("DROP TABLE recipe_table_old")

        }
    }

    val MIGRATION_29_30 = object : Migration(29, 30) {
        override fun migrate(database: SupportSQLiteDatabase) {
            // Neue Tabelle mit `id` als Primary Key, `recipeId` als Foreign Key + Index
            database.execSQL(
                """ 
            CREATE TABLE recipe_description_new (
                id TEXT NOT NULL PRIMARY KEY,
                recipeId INTEGER NOT NULL,
                stepNr INTEGER NOT NULL,
                text TEXT NOT NULL,
                imgUri TEXT,
                FOREIGN KEY(recipeId) REFERENCES recipe_table(id) ON DELETE CASCADE
            )
            """.trimIndent()
            )

            // Daten übernehmen, `id` wird neu generiert
            database.execSQL(
                """
            INSERT INTO recipe_description_new (id, recipeId, stepNr, text, imgUri)
            SELECT LOWER(HEX(RANDOMBLOB(16))), recipeId, stepNr, text, imgUri FROM recipe_description
            """.trimIndent()
            )

            // Index auf recipeId hinzufügen
            database.execSQL("CREATE INDEX index_recipe_description_recipeId ON recipe_description_new(recipeId)")

            // Alte Tabelle löschen & neue umbenennen
            database.execSQL("DROP TABLE recipe_description")
            database.execSQL("ALTER TABLE recipe_description_new RENAME TO recipe_description")
        }
    }
    val MIGRATION_30_31 = object : Migration(30, 31) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL(
                """
            ALTER TABLE recipe_table 
            ADD COLUMN `english-recipe-name` TEXT NOT NULL DEFAULT 'recipe placeholder title'
            """.trimIndent()
            )
        }
    }
    val MIGRATION_31_32 = object : Migration(31, 32) {
        override fun migrate(database: SupportSQLiteDatabase) {
            // Neue Spalte hinzufügen
            database.execSQL("ALTER TABLE recipe_description ADD COLUMN englishText TEXT NOT NULL DEFAULT 'placeholder description'")
        }
    }
    val MIGRATION_32_33 = object : Migration(32, 33) {
        override fun migrate(database: SupportSQLiteDatabase) {
            // Neue Spalte hinzufügen
            database.execSQL("ALTER TABLE recipe_description ADD COLUMN germanText TEXT NOT NULL DEFAULT 'Platzhalter Beschreibung'")
            database.execSQL(
                """
            ALTER TABLE recipe_table 
            ADD COLUMN `german-recipe-name` TEXT NOT NULL DEFAULT 'Rezept Platzhaltertitel'
            """.trimIndent()
            )
        }
    }
    val MIGRATION_33_34 = object : Migration(33, 34) {
        override fun migrate(database: SupportSQLiteDatabase) {
            // 1️⃣ Neue Tabelle erstellen
            database.execSQL("""
            CREATE TABLE IF NOT EXISTS ingredient_allowed_unit (
                ingredientId INTEGER NOT NULL,
                unitOfMeasure TEXT NOT NULL,
                gramsPerUnit REAL NOT NULL,
                PRIMARY KEY(ingredientId, unitOfMeasure),
                FOREIGN KEY(ingredientId) REFERENCES ingredient_table(id) ON DELETE CASCADE
            )
        """)

            // 2️⃣ Neue Spalte in IngredientWithRecipe hinzufügen
            database.execSQL("""
            ALTER TABLE ingredient_recipe_join_table
            ADD COLUMN originalQuantity REAL NOT NULL DEFAULT 0
        """)
        }
    }

    val MIGRATION_34_35 = object : Migration(34, 35) {
        override fun migrate(database: SupportSQLiteDatabase) {
            // Neue Spalte für remoteId
            database.execSQL("ALTER TABLE recipe_table ADD COLUMN remote_id INTEGER")

            // Optional: Standardwert setzen (z. B. NULL = kein Remote-Rezept)
            // database.execSQL("UPDATE recipe_table SET remote_id = NULL")
        }
    }

    val MIGRATION_35_36 = object : Migration(35,36) {
        override fun migrate(database: SupportSQLiteDatabase) {
            // Neue Tabelle für MealPlanDay
            database.execSQL("""
            CREATE TABLE IF NOT EXISTS mealplanday_table_new (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                date INTEGER
            )
        """.trimIndent())

            // Neue Join-Tabelle mit Indizes
            database.execSQL("""
            CREATE TABLE IF NOT EXISTS mealplanday_recipe_table (
                mealPlanDayId INTEGER NOT NULL,
                recipeId INTEGER NOT NULL,
                quantity REAL NOT NULL,
                PRIMARY KEY(mealPlanDayId, recipeId),
                FOREIGN KEY(mealPlanDayId) REFERENCES mealplanday_table(id) ON DELETE CASCADE,
                FOREIGN KEY(recipeId) REFERENCES recipe_table(id) ON DELETE CASCADE
            )
        """.trimIndent())

            database.execSQL("""
            CREATE INDEX IF NOT EXISTS index_mealplanday_recipe_table_mealPlanDayId 
            ON mealplanday_recipe_table(mealPlanDayId)
        """.trimIndent())

            database.execSQL("""
            CREATE INDEX IF NOT EXISTS index_mealplanday_recipe_table_recipeId 
            ON mealplanday_recipe_table(recipeId)
        """.trimIndent())

            // Alte MealPlanDays nicht migrieren (date = NULL)
            database.execSQL("""
            INSERT INTO mealplanday_table_new (id, date)
            SELECT id, NULL FROM mealplanday_table
        """.trimIndent())

            // Alte Tabelle löschen und neue umbenennen
            database.execSQL("DROP TABLE mealplanday_table")
            database.execSQL("ALTER TABLE mealplanday_table_new RENAME TO mealplanday_table")
        }
    }

    val MIGRATION_36_37 = object : Migration(36,37) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL(
                """
CREATE TABLE IF NOT EXISTS recipe_mealtype_weight (
recipe_id INTEGER NOT NULL,
meal_type TEXT NOT NULL,
weight REAL NOT NULL,
PRIMARY KEY (recipe_id, meal_type),
FOREIGN KEY (recipe_id) REFERENCES recipe_table(id) ON DELETE CASCADE
)
""".trimIndent()
            )

            // 2) Indizes anlegen, damit sie zu @Entity(indices = [...]) passen
            db.execSQL(
                "CREATE INDEX IF NOT EXISTS index_recipe_mealtype_weight_recipe_id ON recipe_mealtype_weight(recipe_id)"
            )
            db.execSQL(
                "CREATE INDEX IF NOT EXISTS index_recipe_mealtype_weight_meal_type ON recipe_mealtype_weight(meal_type)"
            )

            // 3) Seed aus recipe_table (Fallback 0.5 wenn probability == 0)
            db.execSQL(
                """
        INSERT OR IGNORE INTO recipe_mealtype_weight (recipe_id, meal_type, weight)
        SELECT id, 'MEAL', CASE WHEN probability > 0.0 THEN probability ELSE 0.5 END
        FROM recipe_table WHERE isMeal = 1
        """.trimIndent()
            )
            db.execSQL(
                """
        INSERT OR IGNORE INTO recipe_mealtype_weight (recipe_id, meal_type, weight)
        SELECT id, 'BREAKFAST', CASE WHEN probability > 0.0 THEN probability ELSE 0.5 END
        FROM recipe_table WHERE isBreakfast = 1
        """.trimIndent()
            )
            db.execSQL(
                """
        INSERT OR IGNORE INTO recipe_mealtype_weight (recipe_id, meal_type, weight)
        SELECT id, 'SNACK', CASE WHEN probability > 0.0 THEN probability ELSE 0.5 END
        FROM recipe_table WHERE isSnack = 1
        """.trimIndent()
            )
            db.execSQL(
                """
        INSERT OR IGNORE INTO recipe_mealtype_weight (recipe_id, meal_type, weight)
        SELECT id, 'BEVERAGE', CASE WHEN probability > 0.0 THEN probability ELSE 0.5 END
        FROM recipe_table WHERE isBeverage = 1
        """.trimIndent()
            )
            db.execSQL(
                """
        INSERT OR IGNORE INTO recipe_mealtype_weight (recipe_id, meal_type, weight)
        SELECT id, 'DESSERT', CASE WHEN probability > 0.0 THEN probability ELSE 0.5 END
        FROM recipe_table WHERE isDessert = 1
        """.trimIndent()
            )
        }
        }

    val MIGRATION_37_38 = object : Migration(37, 38) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL( "ALTER TABLE settings_table " + "ADD COLUMN showOriginalTitle INTEGER NOT NULL DEFAULT 0" ) } }

}