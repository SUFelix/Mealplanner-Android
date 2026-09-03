package com.felix.mealplanner20.ViewModels

import android.content.Context
import android.content.SharedPreferences
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.felix.mealplanner20.Meals.Data.EMPTY_STRING
import com.felix.mealplanner20.Meals.Data.ProfileRepository
import com.felix.mealplanner20.R
import com.felix.mealplanner20.apiService.ImageApiService
import com.felix.mealplanner20.use_cases.GetOwnEmailUseCase
import com.felix.mealplanner20.use_cases.GetOwnProfileDescriptionUseCase
import com.felix.mealplanner20.use_cases.GetOwnProfilePictureUseCase
import com.felix.mealplanner20.use_cases.UploadNewProfileDescriptionUseCase
import com.felix.mealplanner20.use_cases.UploadNewProfilePictureUseCase
import com.mealplanner20.jwtauthktorandroid.auth.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    @ApplicationContext private val appContext: Context,
    private val prefs: SharedPreferences,
    private val authRepository: AuthRepository,
    private val uploadNewProfileDescriptionUseCase: UploadNewProfileDescriptionUseCase,
    private val getOwnProfileDescriptionUseCase: GetOwnProfileDescriptionUseCase,
    private val uploadNewProfilePictureUseCase: UploadNewProfilePictureUseCase,
    private val getOwnProfilePictureUseCase: GetOwnProfilePictureUseCase,
    private val getOwnEmailUseCase: GetOwnEmailUseCase

) : ViewModel() {

    private val _username = MutableLiveData<String>()
    private val _userrole = MutableLiveData<String>()

    private val _isEditing = MutableStateFlow<Boolean>(false)
    val isEditing = _isEditing

    val defaultImage:Int = R.drawable.baseline_account_circle_24


    val username: LiveData<String> get() = _username
    val userrole: LiveData<String> get() = _userrole

    private val _description = MutableStateFlow(EMPTY_STRING)
    val description: StateFlow<String> = _description.asStateFlow()

    var email by mutableStateOf(EMPTY_STRING)
        private set
    var profilePictureUri by mutableStateOf<Uri?>(null)
        private set
    var image:ByteArray? by mutableStateOf(null)
        private set

    // Der SharedPreferences-Key "username" wird beim Login NICHT geschrieben – zuverlaessige
    // Quelle ist der JWT-Claim. Ohne echten Namen laufen Bild-Upload (Code "###null") und
    // die lokale Cache-Datei ins Leere, das eigene und das oeffentliche Profilbild bleiben leer.
    @Volatile
    private var resolvedUsername: String? =
        prefs.getString("username", null)?.takeIf { it.isNotBlank() }

    init {

        Log.i("ProfileVM","init for ${_username.value}")

        loadProfile()
    }

    fun loadProfile(){
        viewModelScope.launch {
            currentUsername()
            loadLocalProfilePicture()

            val description = withContext(Dispatchers.IO) { getOwnProfileDescriptionUseCase.execute() }
            if (description != null) _description.value = description

            val loadedEmail = withContext(Dispatchers.IO) { getOwnEmailUseCase.execute() }
            if (loadedEmail != null) email = loadedEmail

            refreshProfilePictureFromServer()
        }
        val storedUserRole = prefs.getString("role", null)
        if (!storedUserRole.isNullOrEmpty()) {
            _userrole.value = storedUserRole!!
        }
    }

    /**
     * Ermittelt den Benutzernamen aus dem JWT-Claim (Fallback: SharedPreferences) und
     * spiegelt ihn nach [_username] sowie in die Prefs, damit Legacy-Code weiter funktioniert.
     */
    private suspend fun currentUsername(): String? {
        resolvedUsername?.let { return it }
        val user = runCatching { authRepository.getUsernameClaim() }.getOrNull()?.takeIf { it.isNotBlank() }
            ?: prefs.getString("username", null)?.takeIf { it.isNotBlank() }
        if (user != null) {
            resolvedUsername = user
            if (prefs.getString("username", null) != user) {
                prefs.edit().putString("username", user).apply()
            }
            _username.postValue(user)
        }
        return user
    }

    fun updateDescription(){
        viewModelScope.launch(Dispatchers.IO) {
            uploadNewProfileDescriptionUseCase.execute(_description.value)
        }
    }
    fun changeDescription(newDesciption: String){
        _description.value = newDesciption
        Log.i("DESCRIPTION","newDescription: $newDesciption")
    }

    fun toggleIsEditing(){
        _isEditing.value = !_isEditing.value
    }
    fun toggleIsEditingWithDescriptionPost(){
        if(_isEditing.value){
            updateDescription()
        }
        toggleIsEditing()
    }
    fun updateLocalProfilePicture(value: Uri?) {
        profilePictureUri = value
    }

    fun updateLocalProfilePictureAndUpload(context: Context, uri: Uri?) {
        updateLocalProfilePicture(uri)
        val pickedUri = uri ?: return

        viewModelScope.launch {
            val user = currentUsername()

            val bytes = withContext(Dispatchers.IO) {
                runCatching {
                    context.contentResolver.openInputStream(pickedUri)?.use { it.readBytes() }
                }.getOrNull()
            }

            if (bytes != null && bytes.isDecodableImage()) {
                // Lokale Kopie ist ab jetzt die Quelle der Wahrheit fuer das eigene
                // Profilbild und ueberlebt App-Neustarts. Sofort anzeigen.
                withContext(Dispatchers.IO) {
                    localProfilePictureFile?.let { f -> runCatching { f.writeBytes(bytes) } }
                }
                image = bytes
            } else {
                Log.e("ProfileVM", "Neues Profilbild konnte nicht gelesen/dekodiert werden: $pickedUri")
            }

            if (user == null) {
                Log.e("ProfileVM", "Kein Benutzername ermittelbar – Upload wuerde serverseitig nicht zugeordnet")
                return@launch
            }

            // Server-Upload als "fire and forget". Bewusst KEIN erneuter Download danach:
            // der Endpoint /images/profile liefert frisch hochgeladene Bilder derzeit
            // nicht zuverlaessig aus und wuerde sonst das korrekte lokale Bild ueberschreiben.
            val code = generateRandomCode(user)
            withContext(Dispatchers.IO) {
                runCatching { uploadNewProfilePictureUseCase.execute(context, pickedUri, code) }
                    .onFailure { Log.e("ProfileVM", "Profilbild-Upload fehlgeschlagen", it) }
            }
        }
    }

    /**
     * Zeigt sofort die lokal gespeicherte Kopie des aktuellen Benutzers (falls vorhanden
     * und gueltig). Gibt es keine, wird [image] geleert, damit nach einem Account-Wechsel
     * nicht das Bild des vorherigen Benutzers haengen bleibt.
     */
    private suspend fun loadLocalProfilePicture() {
        val bytes = withContext(Dispatchers.IO) {
            localProfilePictureFile
                ?.takeIf { it.exists() }
                ?.let { runCatching { it.readBytes() }.getOrNull() }
                ?.takeIf { it.isDecodableImage() }
        }
        image = bytes
    }

    /**
     * Holt das Profilbild vom Server, aber nur als Fallback: Ein lokal gespeichertes
     * Bild bleibt bestehen. Server-Antworten werden nur uebernommen, wenn sie sich als
     * echtes Bild dekodieren lassen (der Server liefert bei Fehlern teils leere/HTML-Bodies).
     */
    private suspend fun refreshProfilePictureFromServer() {
        val file = localProfilePictureFile
        if (file != null && withContext(Dispatchers.IO) { file.exists() }) return
        val bytes = runCatching { getOwnProfilePictureUseCase.execute() }.getOrNull() ?: return
        if (bytes.isDecodableImage()) {
            withContext(Dispatchers.IO) { file?.let { f -> runCatching { f.writeBytes(bytes) } } }
            image = bytes
        } else {
            Log.w("ProfileVM", "Server lieferte kein gueltiges Profilbild (${bytes.size} bytes) – ignoriert")
        }
    }

    /** Pro Benutzer eigene Datei, damit ein Account-Wechsel nicht das falsche Bild zeigt. */
    private val localProfilePictureFile: File?
        get() {
            val user = (resolvedUsername ?: prefs.getString("username", null))
                ?.takeIf { it.isNotBlank() } ?: return null
            val safeUser = user.replace(Regex("[^A-Za-z0-9_-]"), "_")
            return File(
                File(appContext.filesDir, "profile_images").apply { mkdirs() },
                "own_profile_$safeUser.jpg"
            )
        }

    private fun ByteArray.isDecodableImage(): Boolean {
        if (isEmpty()) return false
        val opts = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        BitmapFactory.decodeByteArray(this, 0, size, opts)
        return opts.outWidth > 0 && opts.outHeight > 0
    }

    private fun generateRandomCode(username: String): String {
        return UUID.randomUUID().toString() + "###" + username
    }
}