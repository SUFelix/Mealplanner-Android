package com.felix.mealplanner20.ViewModels

import android.content.Context
import android.content.SharedPreferences
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
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val prefs: SharedPreferences,
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

    init {

        Log.i("ProfileVM","init for ${_username.value}")

        viewModelScope.launch(Dispatchers.IO) {
            getOwnProfileDescriptionUseCase.execute()?.let {
                _description.value = it
                Log.i("DESCRIPTION","exec() : $it")
            }
            getOwnEmailUseCase.execute()?.let {
                email = it
            }
            getOwnProfilePictureUseCase.execute()?.let {
                image = it
            }
        }

        val storedUsername = prefs.getString("username", null)
        val storedUserRole = prefs.getString("role", null)

        if (!storedUsername.isNullOrEmpty()) {
            _username.value = storedUsername!!
        }
        if (!storedUserRole.isNullOrEmpty()) {
            _userrole.value = storedUserRole!!
        }
    }

    fun loadProfile(){
        viewModelScope.launch(Dispatchers.IO) {
            getOwnProfileDescriptionUseCase.execute()?.let {
                _description.value = it
                Log.i("DESCRIPTION","exec() : $it")
            }
            getOwnEmailUseCase.execute()?.let {
                email = it
            }
            getOwnProfilePictureUseCase.execute()?.let {
                image = it
            }
        }
        val storedUsername = prefs.getString("username", null)
        val storedUserRole = prefs.getString("role", null)

        if (!storedUsername.isNullOrEmpty()) {
            _username.value = storedUsername!!
        }
        if (!storedUserRole.isNullOrEmpty()) {
            _userrole.value = storedUserRole!!
        }
    }

    fun updateDescription(){
        viewModelScope.launch(Dispatchers.IO) {
            uploadNewProfileDescriptionUseCase.execute(_description.value)
        }
    }
    fun updatePicture(context: Context, uri: Uri, code:String){
        viewModelScope.launch(Dispatchers.IO) {
            uploadNewProfilePictureUseCase.execute(context,uri,code)
            getOwnProfilePictureUseCase.execute()?.let {
                image = it
            }
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
    fun updateLocalProfilePictureAndUpload(context:Context, uri: Uri?) {
        updateLocalProfilePicture(uri)
        val code = generateRandomCode()
        uri?.let {
            updatePicture(context,it,code)
        }

    }
    private fun generateRandomCode(): String {
        return UUID.randomUUID().toString()+ "###" + _username.value.toString()
    }
}