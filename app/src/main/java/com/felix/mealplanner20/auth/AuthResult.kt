package com.mealplanner20.jwtauthktorandroid.auth

sealed class AuthResult<T>(val data: T? = null) {
    class Authorized<T>(data: T? = null): AuthResult<T>(data)
    class Unauthorized<T>: AuthResult<T>()
    class IOError<T>: AuthResult<T>()
    class UnknownError<T>: AuthResult<T>()
    class SuccessFullLogout<T>: AuthResult<T>()
    class LogoutFailed<T>: AuthResult<T>()
    class InvalidTokenOrExpired<T> : AuthResult<T>()

    class PasswordResetConfirmed<T> : AuthResult<T>()

    class SuccessfulPasswordResetRequest<T> : AuthResult<T>()

    class UserNotFound<T> : AuthResult<T>()

    class PasswordToShort<T> : AuthResult<T>()
    class FieldsEmpty<T> : AuthResult<T>()
    class UsernameTaken<T> : AuthResult<T>()
    class EmailTaken<T> : AuthResult<T>()
    class SuccessfullySignUp<T> : AuthResult<T>()
    class IncorrectPasswordOrUsername<T> : AuthResult<T>()
}
