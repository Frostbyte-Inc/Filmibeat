package arunkbabu90.filimibeat.data.repository

enum class Status {
    RUNNING,
    SUCCESS,
    FAILED,
}

class NetworkState private constructor(val status: Status, val msg: String) {
    companion object {
        val LOADED: NetworkState
        val LOADING: NetworkState
        val ERROR: NetworkState
        val EOL: NetworkState
        val CLEAR: NetworkState

        init {
            LOADED = NetworkState(Status.SUCCESS, "Success")
            LOADING = NetworkState(Status.RUNNING, "Running")
            ERROR = NetworkState(Status.FAILED, "Network Error")
            EOL = NetworkState(Status.FAILED, "End of list")
            CLEAR = NetworkState(Status.RUNNING, "")
        }
    }
}