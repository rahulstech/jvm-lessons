// i am not storing the expiry. because on an unauthorized request client refreshes the token
// or removes the token on refresh fails. therefore no need to check token expiry client side.
data class AuthToken(
    val accessToken: String,
    val refreshToken: String
)

// AuthTokenProvider manages the tokens securely. here tokens are stored in memory;
// but in real project these are some encrypted storage
class AuthTokenProvider() {
    private var _token: AuthToken? = null;

    fun updateToken(accessToken: String,refreshToken: String) {
        _token = AuthToken(accessToken, refreshToken)
    }

    fun getAccessToken(): String? = _token?.accessToken

    fun getRefreshToken(): String? = _token?.refreshToken

    fun removeToken() {
        _token = null
    }
}