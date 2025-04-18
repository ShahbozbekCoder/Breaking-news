package uz.shahbozbek.breakingnews.db

import androidx.room.TypeConverter
import uz.shahbozbek.breakingnews.models.Source

class Converters {

    @TypeConverter
    fun fromSource(source: Source): String? {
        return source.name
    }

    @TypeConverter
    fun toSource(name: String): Source{
        return Source(name, name)
    }

}