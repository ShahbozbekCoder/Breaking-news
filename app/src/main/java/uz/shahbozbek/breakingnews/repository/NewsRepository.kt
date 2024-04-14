package uz.shahbozbek.breakingnews.repository

import uz.shahbozbek.breakingnews.api.RetrofitInstance
import uz.shahbozbek.breakingnews.db.ArticleDatabase
import uz.shahbozbek.breakingnews.models.Article

class NewsRepository(private val db: ArticleDatabase) {

    suspend fun getBreakingNews(countryCode: String, pageNumber: Int) =
        RetrofitInstance.api.getHeadlines(countryCode, pageNumber)

    suspend fun searchNews(searchQuery: String, pageNumber: Int) =
        RetrofitInstance.api.searchForNews(searchQuery, pageNumber)

    suspend fun upsert(article: Article) = db.getArticleDao().insert(article)

    fun getFavourites() = db.getArticleDao().getAllArticles()

    suspend fun deleteArt(article: Article) = db.getArticleDao().deleteArticle(article)

}