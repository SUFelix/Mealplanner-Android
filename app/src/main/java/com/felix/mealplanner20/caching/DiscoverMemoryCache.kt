package com.felix.mealplanner20.caching
import com.felix.mealplanner20.Meals.Data.Recipe
import com.felix.mealplanner20.Meals.Data.helpers.Mealtype
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeUnit
object DiscoverMemoryCache {
    private data class Entry(
        val recipes: MutableList<Recipe> = mutableListOf(),
        var currentPage: Int = 0,
        var allRecipesFetched: Boolean = false,
        var timestamp: Long = System.currentTimeMillis()
    )
    data class Snapshot(
        val recipes: List<Recipe>,
        val currentPage: Int,
        val allRecipesFetched: Boolean
    )

    private val ALL_KEY = Any() // Sentinel für type == null
    private fun keyOf(type: Mealtype?): Any = type ?: ALL_KEY

    private val cache = ConcurrentHashMap<Any, Entry>()
    private val TTL_MS = TimeUnit.MINUTES.toMillis(10)

    fun get(type: Mealtype?): Snapshot? {
        val k = keyOf(type)
        val e = cache[k] ?: return null
        val expired = System.currentTimeMillis() - e.timestamp > TTL_MS
        return if (expired) {
            cache.remove(k)
            null
        } else Snapshot(
            recipes = e.recipes.toList(),
            currentPage = e.currentPage,
            allRecipesFetched = e.allRecipesFetched
        )
    }

    fun clear(type: Mealtype?) {
        cache.remove(keyOf(type))
    }

    fun clearAll() {
        cache.clear()
    }

    fun putRecipes(
        type: Mealtype?,
        newRecipes: List<Recipe>,
        currentPage: Int,
        allFetched: Boolean
    ) {
        val k = keyOf(type)
        val entry = cache.computeIfAbsent(k) { Entry() }

        val existingIds = entry.recipes.asSequence().mapNotNull { it.id }.toMutableSet()
        newRecipes.forEach { r ->
            val id = r.id ?: return@forEach
            if (existingIds.add(id)) entry.recipes.add(r)
        }

        entry.currentPage = currentPage
        entry.allRecipesFetched = allFetched
        entry.timestamp = System.currentTimeMillis()
    }

    fun replaceRecipes(
        type: Mealtype?,
        recipes: List<Recipe>,
        currentPage: Int,
        allFetched: Boolean
    ) {
        val k = keyOf(type)
        val entry = cache.computeIfAbsent(k) { Entry() }

        entry.recipes.clear()
        val seen = HashSet<Long>()
        recipes.forEach { r ->
            val id = r.id ?: return@forEach
            if (seen.add(id)) entry.recipes.add(r)
        }

        entry.currentPage = currentPage
        entry.allRecipesFetched = allFetched
        entry.timestamp = System.currentTimeMillis()
    }

}