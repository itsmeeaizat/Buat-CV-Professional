package com.example.data.local

import androidx.room.TypeConverter
import com.example.data.model.CvStyleConfig
import com.example.data.model.CvTemplateType
import com.example.data.model.Education
import com.example.data.model.PersonalInfo
import com.example.data.model.ProjectItem
import com.example.data.model.SkillItem
import com.example.data.model.WorkExperience
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

class Converters {
    private val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()

    @TypeConverter
    fun fromTemplateType(value: CvTemplateType): String = value.name

    @TypeConverter
    fun toTemplateType(value: String): CvTemplateType = try {
        CvTemplateType.valueOf(value)
    } catch (e: Exception) {
        CvTemplateType.PROFESSIONAL
    }

    @TypeConverter
    fun fromPersonalInfo(info: PersonalInfo): String = moshi.adapter(PersonalInfo::class.java).toJson(info)

    @TypeConverter
    fun toPersonalInfo(json: String): PersonalInfo = try {
        moshi.adapter(PersonalInfo::class.java).fromJson(json) ?: PersonalInfo()
    } catch (e: Exception) {
        PersonalInfo()
    }

    @TypeConverter
    fun fromExperiences(list: List<WorkExperience>): String {
        val type = Types.newParameterizedType(List::class.java, WorkExperience::class.java)
        return moshi.adapter<List<WorkExperience>>(type).toJson(list)
    }

    @TypeConverter
    fun toExperiences(json: String): List<WorkExperience> = try {
        val type = Types.newParameterizedType(List::class.java, WorkExperience::class.java)
        moshi.adapter<List<WorkExperience>>(type).fromJson(json) ?: emptyList()
    } catch (e: Exception) {
        emptyList()
    }

    @TypeConverter
    fun fromEducations(list: List<Education>): String {
        val type = Types.newParameterizedType(List::class.java, Education::class.java)
        return moshi.adapter<List<Education>>(type).toJson(list)
    }

    @TypeConverter
    fun toEducations(json: String): List<Education> = try {
        val type = Types.newParameterizedType(List::class.java, Education::class.java)
        moshi.adapter<List<Education>>(type).fromJson(json) ?: emptyList()
    } catch (e: Exception) {
        emptyList()
    }

    @TypeConverter
    fun fromSkills(list: List<SkillItem>): String {
        val type = Types.newParameterizedType(List::class.java, SkillItem::class.java)
        return moshi.adapter<List<SkillItem>>(type).toJson(list)
    }

    @TypeConverter
    fun toSkills(json: String): List<SkillItem> = try {
        val type = Types.newParameterizedType(List::class.java, SkillItem::class.java)
        moshi.adapter<List<SkillItem>>(type).fromJson(json) ?: emptyList()
    } catch (e: Exception) {
        emptyList()
    }

    @TypeConverter
    fun fromProjects(list: List<ProjectItem>): String {
        val type = Types.newParameterizedType(List::class.java, ProjectItem::class.java)
        return moshi.adapter<List<ProjectItem>>(type).toJson(list)
    }

    @TypeConverter
    fun toProjects(json: String): List<ProjectItem> = try {
        val type = Types.newParameterizedType(List::class.java, ProjectItem::class.java)
        moshi.adapter<List<ProjectItem>>(type).fromJson(json) ?: emptyList()
    } catch (e: Exception) {
        emptyList()
    }

    @TypeConverter
    fun fromStyleConfig(config: CvStyleConfig): String = moshi.adapter(CvStyleConfig::class.java).toJson(config)

    @TypeConverter
    fun toStyleConfig(json: String): CvStyleConfig = try {
        moshi.adapter(CvStyleConfig::class.java).fromJson(json) ?: CvStyleConfig()
    } catch (e: Exception) {
        CvStyleConfig()
    }
}
