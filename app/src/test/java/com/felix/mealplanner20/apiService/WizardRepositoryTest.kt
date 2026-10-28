package com.felix.mealplanner20.apiService

import com.google.common.truth.Truth.assertThat
import com.mealplanner20.jwtauthktorandroid.auth.AuthRepository
import com.mealplanner20.jwtauthktorandroid.auth.AuthResult
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.runBlocking
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Test
import retrofit2.Response
import java.net.SocketTimeoutException

class WizardRepositoryTest {

    private val recipe = WizardIngredientList(
        recipeTitle = "Veganes Kichererbsen-Curry",
        ingredients = listOf(
            WizardIngredient(
                name = "Kichererbse",
                amount = 400.0,
                unit = "g",
                originalText = "400 g Kichererbsen",
                matchedIngredientId = 123
            )
        ),
        steps = listOf(WizardStep(stepNumber = 1, text = "Zwiebel würfeln"))
    )

    private class FakeWizardApi(
        private val respond: suspend () -> Response<WizardIngredientList>
    ) : WizardApiService {
        var lastRequest: WizardGenerateRequest? = null
        var lastHeaders: Map<String, String>? = null

        override suspend fun analyzeRecipeImage(
            image: MultipartBody.Part,
            headers: Map<String, String>
        ): Response<WizardIngredientList> {
            lastHeaders = headers
            return respond()
        }

        override suspend fun generateRecipe(
            request: WizardGenerateRequest,
            headers: Map<String, String>
        ): Response<WizardIngredientList> {
            lastRequest = request
            lastHeaders = headers
            return respond()
        }
    }

    private class FakeAuth(private val token: String?) : AuthRepository {
        override suspend fun getToken(): String? = token
        override suspend fun signUp(username: String, email: String, password: String): AuthResult<Unit> = TODO()
        override suspend fun signIn(usernameOrEmail: String, password: String): AuthResult<Unit> = TODO()
        override suspend fun authenticate(): AuthResult<Unit> = TODO()
        override suspend fun logout(): AuthResult<Unit> = TODO()
        override suspend fun requestPasswordReset(email: String): AuthResult<Unit> = TODO()
        override suspend fun confirmPasswordReset(token: String, newPassword: String): AuthResult<Unit> = TODO()
        override suspend fun getUsernameClaim(): String? = null
    }

    private fun errorResponse(code: Int, body: String, type: String = "application/json") =
        Response.error<WizardIngredientList>(code, body.toResponseBody(type.toMediaType()))

    private fun repository(api: WizardApiService, token: String? = "jwt") =
        WizardRepository(api, FakeAuth(token), Moshi.Builder().add(KotlinJsonAdapterFactory()).build())

    @Test
    fun `generate returns Success and sends bearer token with trimmed prompt`() = runBlocking<Unit> {
        val api = FakeWizardApi { Response.success(recipe) }

        val result = repository(api).generate("  Erstell mir ein veganes Curry  ", previous = null)

        assertThat(result).isEqualTo(WizardResponse.Success(recipe))
        assertThat(api.lastRequest).isEqualTo(WizardGenerateRequest("Erstell mir ein veganes Curry", null))
        assertThat(api.lastHeaders).containsExactly("Authorization", "Bearer jwt")
    }

    @Test
    fun `generate passes previous unchanged`() = runBlocking<Unit> {
        val api = FakeWizardApi { Response.success(recipe) }

        repository(api).generate("schärfer", previous = recipe)

        assertThat(api.lastRequest?.previous).isSameInstanceAs(recipe)
    }

    @Test
    fun `422 maps to NotARecipe with server message`() = runBlocking<Unit> {
        val api = FakeWizardApi {
            errorResponse(422, """{"status":"NOT_A_RECIPE","message":"Kein Rezeptwunsch"}""")
        }

        val result = repository(api).generate("Wie wird das Wetter?", previous = null)

        assertThat(result).isInstanceOf(WizardResponse.NotARecipe::class.java)
        assertThat((result as WizardResponse.NotARecipe).message).isEqualTo("Kein Rezeptwunsch")
    }

    @Test
    fun `401 with plain text body maps to Unauthorized`() = runBlocking<Unit> {
        val api = FakeWizardApi { errorResponse(401, "Token fehlt", "text/plain") }

        val result = repository(api).generate("Curry", previous = null)

        assertThat(result).isInstanceOf(WizardResponse.Unauthorized::class.java)
        assertThat((result as WizardResponse.Unauthorized).code).isEqualTo(401)
    }

    @Test
    fun `500 maps to Error with parsed status and message`() = runBlocking<Unit> {
        val api = FakeWizardApi {
            errorResponse(500, """{"status":"ERROR","message":"only for Premium Users"}""")
        }

        val result = repository(api).generate("Curry", previous = null)

        assertThat(result).isEqualTo(
            WizardResponse.Error(500, "Response.error()", "ERROR", "only for Premium Users")
        )
    }

    @Test
    fun `non-JSON error body does not crash`() = runBlocking<Unit> {
        val api = FakeWizardApi { errorResponse(502, "<html>Bad Gateway</html>", "text/html") }

        val result = repository(api).generate("Curry", previous = null)

        assertThat(result).isEqualTo(WizardResponse.Error(502, "Response.error()", null, null))
    }

    @Test
    fun `timeout maps to network Failure`() = runBlocking<Unit> {
        val api = FakeWizardApi { throw SocketTimeoutException("timeout") }

        val result = repository(api).generate("Curry", previous = null)

        assertThat(result).isInstanceOf(WizardResponse.Failure::class.java)
        assertThat((result as WizardResponse.Failure).isNetwork).isTrue()
    }

    @Test
    fun `missing token returns NotLoggedIn without calling the api`() = runBlocking<Unit> {
        val api = FakeWizardApi { error("must not be called") }

        val result = repository(api, token = null).generate("Curry", previous = null)

        assertThat(result).isEqualTo(WizardResponse.NotLoggedIn)
        assertThat(api.lastRequest).isNull()
    }

    @Test
    fun `analyzeImage keeps the legacy error text`() = runBlocking<Unit> {
        val api = FakeWizardApi { errorResponse(500, """{"status":"ERROR","message":"x"}""") }

        val result = repository(api).analyzeImage(byteArrayOf(1, 2, 3))

        assertThat(result.errorText()).isEqualTo("Wizard failed: 500 Response.error()")
    }
}
