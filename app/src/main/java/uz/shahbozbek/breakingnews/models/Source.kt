package uz.shahbozbek.breakingnews.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Source(
    val id: String? = null,
    val name: String? = null
): Parcelable