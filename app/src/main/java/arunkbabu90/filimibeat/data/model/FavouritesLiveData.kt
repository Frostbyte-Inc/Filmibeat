package arunkbabu90.filimibeat.data.model

import androidx.lifecycle.LiveData
import arunkbabu90.filimibeat.R
import arunkbabu90.filimibeat.data.api.PAGE_SIZE
import com.google.firebase.firestore.*

class FavouritesLiveData(private var query: Query,
                         private var onLastVisibleMovieCallback: OnLastVisibleMovieCallback,
                         private var onLastMovieReachedCallback: OnLastMovieReachedCallback)
    : LiveData<Operation>(), EventListener<QuerySnapshot> {

    private lateinit var listenerRegistration: ListenerRegistration

    override fun onActive() {
        listenerRegistration = query.addSnapshotListener(this)
    }

    override fun onInactive() {
        listenerRegistration.remove()
    }

    override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {
        if (error != null || value == null) return

        for (docChange: DocumentChange in value.documentChanges) {
            when(docChange.type) {
                DocumentChange.Type.ADDED -> {
                    val movie: Favourite = docChange.document.toObject(Favourite::class.java)
                    val addOperation = Operation(movie, R.string.add_operation)
                    setValue(addOperation)
                }
                DocumentChange.Type.MODIFIED -> {
                    val movie: Favourite = docChange.document.toObject(Favourite::class.java)
                    val modifyOperation = Operation(movie, R.string.modify_operation)
                    setValue(modifyOperation)
                }
                DocumentChange.Type.REMOVED -> {
                    val movie: Favourite = docChange.document.toObject(Favourite::class.java)
                    val removeOperation = Operation(movie, R.string.remove_operation)
                    setValue(removeOperation)
                }
            }
        }

        val querySnapshotSize = value.size()
        if (querySnapshotSize < PAGE_SIZE) {
            onLastMovieReachedCallback.setLastMovieReached(true)
        } else {
            val lastVisibleMovie = value.documents[querySnapshotSize - 1]
            onLastVisibleMovieCallback.setLastVisibleProduct(lastVisibleMovie)
        }
    }

    interface OnLastVisibleMovieCallback {
        fun setLastVisibleProduct(lastVisibleMovie: DocumentSnapshot)
    }

    interface OnLastMovieReachedCallback {
        fun setLastMovieReached(isLastMovieReached: Boolean)
    }
}