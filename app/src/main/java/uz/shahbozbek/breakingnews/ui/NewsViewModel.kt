package uz.shahbozbek.breakingnews.ui

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import retrofit2.Response
import uz.shahbozbek.breakingnews.api.NewsApi
import uz.shahbozbek.breakingnews.models.Article
import uz.shahbozbek.breakingnews.models.NewsResponse
import uz.shahbozbek.breakingnews.repository.NewsRepository
import uz.shahbozbek.breakingnews.util.Resource
import java.io.IOException

class NewsViewModel(
    app: Application,
    val newsRepository: NewsRepository
) : AndroidViewModel(app) {

    val breakingNews: MutableLiveData<Resource<NewsResponse>> = MutableLiveData()
    var breakingNewsPage = 1
    var breakingResponse: NewsResponse? = null

    val searchingNews: MutableLiveData<Resource<NewsResponse>> = MutableLiveData()
    var searchingNewsPage = 1
    var searchingResponse: NewsResponse? = null
    var newSearchQuery: String? = null
    var oldSearchQuery: String? = null

    init {
        getBreakingNews("us")
    }

    fun getBreakingNews(countryCode: String) = viewModelScope.launch {
        breakingInternet(countryCode)
    }

    fun searchingNews(searchQuery: String) = viewModelScope.launch {
        searchingInternet(searchQuery)
    }

    private fun handleBreakingNewsResponse(response: Response<NewsResponse>): Resource<NewsResponse> {
        if (response.isSuccessful) {
            response.body()?.let { result ->
                breakingNewsPage++
                if (breakingResponse == null) {
                    breakingResponse = result
                } else {
                    val oldArticles = breakingResponse?.articles
                    val newArticles = result.articles
                    oldArticles?.addAll(newArticles)
                }
                return Resource.Success(breakingResponse ?: result)
            }
        }
        return Resource.Error(response.message())
    }

    private fun handleSearchingNewsResponse(response: Response<NewsResponse>): Resource<NewsResponse> {
        if (response.isSuccessful) {
            response.body()?.let { result ->
                if (searchingResponse == null || newSearchQuery != oldSearchQuery) {
                    searchingNewsPage = 1
                    oldSearchQuery = newSearchQuery
                    searchingResponse = result
                } else {
                    searchingNewsPage++
                    val oldArticles = searchingResponse?.articles
                    val newArticles = result.articles
                    oldArticles?.addAll(newArticles)
                }
                return Resource.Success(searchingResponse ?: result)
            }
        }
        return Resource.Error(response.message())
    }

    fun addToFavourites(article: Article) = viewModelScope.launch {
        newsRepository.upsert(article)
    }

    fun getFavouriteNews() = newsRepository.getFavourites()

    fun deleteArticle(article: Article) =   viewModelScope.launch {
        newsRepository.deleteArt(article)
    }

    fun internetConnection(context: Context): Boolean {
        (context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager).apply {
            return getNetworkCapabilities(activeNetwork)?.run {
                when {
                    hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                    hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                    hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                    else -> false
                }
            } ?: false
        }
    }

    private suspend fun breakingInternet(countryCode:String) {
        breakingNews.postValue(Resource.Loading())
        try {
            if (internetConnection(this.getApplication())) {
                val response = newsRepository.getBreakingNews(countryCode,breakingNewsPage)
                breakingNews.postValue(handleBreakingNewsResponse(response))
            } else {
                breakingNews.postValue(Resource.Error("No internet connection"))
            }
        } catch (t: Throwable) {
            when(t) {
                is IOException -> breakingNews.postValue(Resource.Error("Unable to connect"))
                else -> breakingNews.postValue(Resource.Error("No signal"))
            }
        }
    }

    private suspend fun searchingInternet(searchQuery: String) {
        newSearchQuery = searchQuery
        searchingNews.postValue(Resource.Loading())
        try {
            if (internetConnection(this.getApplication())) {
                val response = newsRepository.searchNews(searchQuery, searchingNewsPage)
                searchingNews.postValue(handleSearchingNewsResponse(response))
            } else {
                breakingNews.postValue(Resource.Error("No internet connection"))
            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> breakingNews.postValue(Resource.Error("Unable to connect"))
                else -> breakingNews.postValue(Resource.Error("No signal"))
            }
        }
    }
}