package arunkbabu90.filimibeat.ui

class Constants {
    companion object {
        @JvmStatic
        var userType: Int = -1

        const val COLLECTION_USERS = "Users"

        const val FIELD_USER_TYPE = "userType"
        const val FIELD_FULL_NAME = "name"
        const val FIELD_ACCOUNT_VERIFIED = "activated"

        const val USER_TYPE_GUEST = 0
        const val USER_TYPE_PERSON = 1
    }
}