package uz.shahbozbek.breakingnews.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import uz.shahbozbek.breakingnews.R
import uz.shahbozbek.breakingnews.models.Article

class NewsAdapter : RecyclerView.Adapter<NewsAdapter.ArticleViewHolder>() {

    inner class ArticleViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {

        lateinit var articleImage: ImageView
        lateinit var articleSource: TextView
        lateinit var articleTitle: TextView
        lateinit var articleDescription: TextView
        lateinit var articleDataTime: TextView

    }

    private val differCallback = object : DiffUtil.ItemCallback<Article>() {
        override fun areItemsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem.url == newItem.url
        }

        override fun areContentsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem == newItem
        }

    }

    val differ = AsyncListDiffer(this, differCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleViewHolder {
        return ArticleViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_news, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    private var onItemClickListener: ((Article) -> Unit)? = null

    override fun onBindViewHolder(holder: ArticleViewHolder, position: Int) {
        val article = differ.currentList[position]

        holder.articleImage = holder.itemView.findViewById(R.id.articleImage)
        holder.articleDataTime = holder.itemView.findViewById(R.id.articleDateTime)
        holder.articleTitle = holder.itemView.findViewById(R.id.articleTitle)
        holder.articleSource = holder.itemView.findViewById(R.id.articleSource)
        holder.articleDescription = holder.itemView.findViewById(R.id.articleDescription)

        holder.itemView.apply {
            Glide.with(this).load(article.urlToImage).into(holder.articleImage)
            holder.articleTitle.text = article.title
            holder.articleDataTime.text = article.publishedAt
            holder.articleSource.text = article.source?.name
            holder.articleDescription.text = article.description

            setOnClickListener {
                onItemClickListener?.let {
                    it(article)
                }
            }
        }

    }

    fun setOnItemClickListener(listener: (Article) -> Unit) {
        onItemClickListener = listener
    }
}