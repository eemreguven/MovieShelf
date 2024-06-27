package com.mrguven.movieshelf.data.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "movie_table")
data class MovieEntity(
    @PrimaryKey val id: Int,
    @ColumnInfo val posterPath: String,
    @ColumnInfo val title: String,
    @ColumnInfo var popularity: Double,
    @ColumnInfo val releaseDate: String,
    @ColumnInfo var voteAverage: Double,
    @ColumnInfo var isFavorite: Boolean,
    @ColumnInfo var isInWatchList: Boolean
)