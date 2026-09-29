package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.local.dao.CommentDao
import com.example.data.local.dao.CreatorDao
import com.example.data.local.dao.LiveStreamDao
import com.example.data.local.dao.MessageDao
import com.example.data.local.dao.UserDao
import com.example.data.local.dao.VideoDao
import com.example.data.local.entity.CommentEntity
import com.example.data.local.entity.ConversationEntity
import com.example.data.local.entity.CreatorBalanceEntity
import com.example.data.local.entity.LiveStreamEntity
import com.example.data.local.entity.MessageEntity
import com.example.data.local.entity.TransactionEntity
import com.example.data.local.entity.UserEntity
import com.example.data.local.entity.VideoEntity

@Database(
    entities = [
        UserEntity::class,
        VideoEntity::class,
        CommentEntity::class,
        ConversationEntity::class,
        MessageEntity::class,
        LiveStreamEntity::class,
        CreatorBalanceEntity::class,
        TransactionEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class GoalDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun videoDao(): VideoDao
    abstract fun commentDao(): CommentDao
    abstract fun messageDao(): MessageDao
    abstract fun liveStreamDao(): LiveStreamDao
    abstract fun creatorDao(): CreatorDao

    companion object {
        @Volatile
        private var INSTANCE: GoalDatabase? = null

        fun getInstance(context: Context): GoalDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    GoalDatabase::class.java,
                    "goal_platform.db"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
