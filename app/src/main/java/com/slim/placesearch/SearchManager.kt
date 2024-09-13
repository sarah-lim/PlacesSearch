package com.slim.placesearch

import android.content.Context
import androidx.appsearch.app.AppSearchSession
import androidx.appsearch.app.PutDocumentsRequest
import androidx.appsearch.app.SearchSpec
import androidx.appsearch.app.SetSchemaRequest
import androidx.appsearch.localstorage.LocalStorage
import com.slim.placesearch.data.local.PlaceUI
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


class SearchManager(private val appContext : Context) {

    private var session : AppSearchSession? = null

    suspend fun init() {
        withContext(Dispatchers.IO) {
            val sessionFuture = LocalStorage.createSearchSessionAsync(
                LocalStorage.SearchContext.Builder(appContext, PLACES_DB).build()
            )

            val setSchemaRequest = SetSchemaRequest.Builder()
                .addDocumentClasses(PlaceUI::class.java)
                .build()

            session = sessionFuture.get()

            session?.setSchemaAsync(setSchemaRequest)
        }
    }

    suspend fun putPlaces(places : List<PlaceUI>) : Boolean {

        return withContext(Dispatchers.IO) {
            session?.putAsync(PutDocumentsRequest.Builder()
                .addDocuments(places)
                .build())?.get()?.isSuccess == true
        }
    }

    suspend fun searchPlaces(query: String ) : List<PlaceUI> {
        return withContext(Dispatchers.IO) {
            val searchSpec = SearchSpec.Builder()
                .setSnippetCount(10)
                .build()
            val result = session?.search(
                query,searchSpec) ?: return@withContext emptyList()

            result.nextPageAsync.get().mapNotNull {
                if(it.genericDocument.schemaType == PlaceUI::class.java.simpleName) {
                    it.getDocument(PlaceUI::class.java)
                } else null
            }

        }
    }

    suspend fun fetchAllPlaces() : List<PlaceUI>? {
        return withContext(Dispatchers.IO) {
            val searchSpec = SearchSpec.Builder()
                .setSnippetCount(10)
                .build()
            val result = session?.search(
                "",searchSpec)


            result?.nextPageAsync?.get()?.mapNotNull {
                if(it.genericDocument.schemaType == PlaceUI::class.java.simpleName) {
                    it.getDocument(PlaceUI::class.java)
                } else null
            }

        }
    }

    fun closeSession() {
        session?.close()
        session = null
    }

    companion object {
        private const val PLACES_DB = "placesDB"
    }
}