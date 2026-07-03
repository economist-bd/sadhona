package com.example.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [AdviceEntity::class, CommentEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun adviceDao(): AdviceDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "sadhana_database"
                )
                    .addCallback(AppDatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class AppDatabaseCallback(
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch(Dispatchers.IO) {
                    populateDatabase(database.adviceDao())
                }
            }
        }

        suspend fun populateDatabase(adviceDao: AdviceDao) {
            val initialAdvices = listOf(
                // 1. মনোযোগ ও ফোকাস
                AdviceEntity(
                    text = "পোমোডোরো টেকনিক ব্যবহার করো: ২৫ মিনিট সম্পূর্ণ মনোযোগ দিয়ে পড়ো, তারপর ৫ মিনিটের একটি ছোট বিরতি নাও। এতে তোমার ব্রেন সতেজ থাকবে এবং দীর্ঘসময় মনোযোগ ধরে রাখা সহজ হবে।",
                    category = "মনোযোগ ও ফোকাস",
                    author = "সাদনা কোচ",
                    likesCount = 245
                ),
                AdviceEntity(
                    text = "পড়াশোনা শুরু করার আগে তোমার পড়ার টেবিলটি পরিষ্কার করো। অগোছালো পরিবেশ মনের অজান্তেই মনোযোগ বিক্ষিপ্ত করে। একটি পরিষ্কার খাতা, কলম এবং প্রয়োজনীয় বই ছাড়া টেবিলে বাড়তি কিছু রেখো না।",
                    category = "মনোযোগ ও ফোকাস",
                    author = "সংগৃহীত",
                    likesCount = 189
                ),
                AdviceEntity(
                    text = "মনোযোগ কোনো জাদু নয়, এটি একটি দক্ষতা। প্রতিদিন অন্তত ১০ মিনিট শান্ত হয়ে বসে শ্বাস-প্রশ্বাসের ব্যায়াম (মেডিটেশন) করো। এটি ব্রেনের ফোকাস এরিয়াকে শক্তিশালী করে।",
                    category = "মনোযোগ ও ফোকাস",
                    author = "সাদনা কোচ",
                    likesCount = 312
                ),
                AdviceEntity(
                    text = "পড়ার সময় কোনো কঠিন টপিক আসলে হাল ছেড়ে দিও না। সেটিকে ছোট ছোট অংশে ভাগ করো। একবারে পুরো অধ্যায় না পড়ে, একটি নির্দিষ্ট অনুচ্ছেদ বা গাণিতিক সমস্যা সমাধান করার লক্ষ্য স্থির করো।",
                    category = "মনোযোগ ও ফোকাস",
                    author = "মনোবিজ্ঞানী টিম",
                    likesCount = 156
                ),

                // 2. সময় ও রুটিন
                AdviceEntity(
                    text = "সবচেয়ে কঠিন বিষয়টি দিনের শুরুতেই পড়ার অভ্যাস করো (Eat That Frog)। সকালে আমাদের ব্রেন সবচেয়ে বেশি সক্রিয় এবং এনার্জেটিক থাকে, ফলে কঠিন পড়াগুলো সহজেই মাথায় ঢোকে।",
                    category = "সময় ও রুটিন",
                    author = "সাদনা কোচ",
                    likesCount = 420
                ),
                AdviceEntity(
                    text = "পরের দিন কী পড়বে, তা আগের দিন রাতেই ডায়েরিতে লিখে রাখো (To-Do List)। লক্ষ্য নির্দিষ্ট থাকলে সকালে ঘুম থেকে উঠে সময় নষ্ট করার কোনো সুযোগ থাকে না।",
                    category = "সময় ও রুটিন",
                    author = "সংগৃহীত",
                    likesCount = 278
                ),
                AdviceEntity(
                    text = "পড়াশোনায় ধারাবাহিকতা (Consistency) রক্ষা করা জরুরি। প্রতিদিন ১০ ঘণ্টা পড়ার চেয়ে দৈনিক ৩-৪ ঘণ্টা নিয়মিত পড়া অনেক বেশি কার্যকর ও দীর্ঘস্থায়ী হয়।",
                    category = "সময় ও রুটিন",
                    author = "শিক্ষা গবেষক",
                    likesCount = 389
                ),
                AdviceEntity(
                    text = "একটি বাস্তবসম্মত রুটিন তৈরি করো। রুটিনে পড়াশোনার পাশাপাশি পর্যাপ্ত ঘুম, খাওয়া এবং বিনোদনের জন্য নির্দিষ্ট সময় রাখো। অবাস্তব কঠিন রুটিন কখনোই ধরে রাখা যায় না।",
                    category = "সময় ও রুটিন",
                    author = "সাদনা কোচ",
                    likesCount = 203
                ),

                // 3. পরীক্ষার প্রস্তুতি
                AdviceEntity(
                    text = "পরীক্ষার আগের রাতে নতুন কোনো টপিক পড়তে যেও না। যা আগে পড়েছ, তা-ই রিভিশন করো। নতুন কিছু পড়ার চেষ্টা করলে আগের পড়াও গুলিয়ে যেতে পারে এবং মানসিক চাপ বাড়বে।",
                    category = "পরীক্ষার প্রস্তুতি",
                    author = "পরীক্ষা বিশেষজ্ঞ",
                    likesCount = 512
                ),
                AdviceEntity(
                    text = "বিগত ৫ বছরের প্রশ্ন সমাধান (Active Recall) করো। এটি তোমাকে পরীক্ষার প্রশ্নের ধরন বুঝতে এবং পরীক্ষার হলের সময় ব্যবস্থাপনায় দারুণ সাহায্য করবে।",
                    category = "পরীক্ষার প্রস্তুতি",
                    author = "সাদনা কোচ",
                    likesCount = 467
                ),
                AdviceEntity(
                    text = "পরীক্ষার হলে তাড়াহুড়ো না করে প্রথম ১০ মিনিট শান্ত মাথায় প্রশ্নপত্রটি ভালোভাবে পড়ে নাও। যে উত্তরগুলো সবচেয়ে ভালো পারো, সেগুলো দিয়েই লেখা শুরু করো।",
                    category = "পরীক্ষার প্রস্তুতি",
                    author = "সাদনা কোচ",
                    likesCount = 345
                ),
                AdviceEntity(
                    text = "শুধুমাত্র মুখস্থ করার চেয়ে লিখে পড়ার অভ্যাস করো। একবার লেখা দশবার পড়ার চেয়েও বেশি কার্যকর, যা পরীক্ষার খাতায় উত্তর নির্ভুলভাবে লিখতে সাহায্য করে।",
                    category = "পরীক্ষার প্রস্তুতি",
                    author = "সংগৃহীত",
                    likesCount = 299
                ),

                // 4. ডিজিটাল আসক্তি
                AdviceEntity(
                    text = "পড়ার টেবিলে মোবাইল ফোন একদম নিষিদ্ধ করো। ফোন সাইলেন্ট বা ভাইব্রেট মোডে রাখলেও মনোযোগ বিঘ্নিত হয়। ফোনটি অন্য ঘরে বা ড্রয়ারে বন্ধ করে লক করে রাখো।",
                    category = "ডিজিটাল আসক্তি",
                    author = "ডিজিটাল ডিটক্স টিম",
                    likesCount = 678
                ),
                AdviceEntity(
                    text = "সোশ্যাল মিডিয়া ব্যবহারে নিজেকে সীমাবদ্ধ করো। পড়াশোনার অ্যাপ ছাড়া অন্যান্য সব বিনোদনমূলক অ্যাপের নোটিফিকেশন বন্ধ করে দাও। পড়াশোনার পর পুরস্কার হিসেবে ফোন ব্যবহার করতে পারো।",
                    category = "ডিজিটাল আসক্তি",
                    author = "সাদনা কোচ",
                    likesCount = 521
                ),
                AdviceEntity(
                    text = "ফোন থেকে দূরে থাকার জন্য স্টাডি ট্র্যাকার বা ফরেস্ট (Forest) এর মতো অ্যাপ ব্যবহার করতে পারো, যা তোমাকে পড়ার সময় স্ক্রিন লক করে রাখতে উদ্বুদ্ধ করবে।",
                    category = "ডিজিটাল আসক্তি",
                    author = "অ্যাপ রিভিউয়ার",
                    likesCount = 188
                ),
                AdviceEntity(
                    text = "ডিজিটাল ডিটক্স: সপ্তাহে অন্তত একটি দিন পড়াশোনার পর সম্পূর্ণ অফলাইনে কাটাও। বই পড়া, বাগান করা বা বন্ধুদের সাথে সশরীরে আড্ডা দিয়ে মনকে চাঙ্গা করো।",
                    category = "ডিজিটাল আসক্তি",
                    author = "সাদনা কোচ",
                    likesCount = 275
                ),

                // 5. ব্যর্থতা ও অনুপ্রেরণা
                AdviceEntity(
                    text = "ব্যর্থতা মানেই জীবনের শেষ নয়, বরং এটি শেখার একটি নতুন সুযোগ। পরীক্ষায় খারাপ রেজাল্ট হলে ভেঙে না পড়ে ভুলগুলো চিহ্নিত করো এবং পরবর্তী সময়ে তা শুধরে নেওয়ার চেষ্টা করো।",
                    category = "ব্যর্থতা ও অনুপ্রেরণা",
                    author = "মোটিভেটর এক্স",
                    likesCount = 789
                ),
                AdviceEntity(
                    text = "অন্যের সফলতার সাথে নিজের তুলনা বন্ধ করো। প্রতিটি মানুষের শেখার গতি এবং প্রতিভা আলাদা। তুমি শুধু গতকালের নিজের চেয়ে আজকের নিজেকে একটু উন্নত করার চেষ্টা করো।",
                    category = "ব্যর্থতা ও অনুপ্রেরণা",
                    author = "সাদনা কোচ",
                    likesCount = 632
                ),
                AdviceEntity(
                    text = "বড় লক্ষ্য অর্জনে ছোট ছোট ভুল হবেই। ভুল থেকেই প্রকৃত জ্ঞান লাভ হয়। বিজ্ঞানী টমাস আলভা এডিসন বৈদ্যুতিক বাল্ব আবিষ্কারের আগে হাজারবার ব্যর্থ হয়েছিলেন কিন্তু হাল ছাড়েননি।",
                    category = "ব্যর্থতা ও অনুপ্রেরণা",
                    author = "সংগৃহীত",
                    likesCount = 491
                ),
                AdviceEntity(
                    text = "দৃঢ় সংকল্প এবং কঠোর পরিশ্রমের কোনো বিকল্প নেই। আজ যে কষ্ট করছ, তার ফল ভবিষ্যতে অবশ্যই পাবে। ধৈর্য ধরো এবং নিজের ক্ষমতার ওপর বিশ্বাস রাখো।",
                    category = "ব্যর্থতা ও অনুপ্রেরণা",
                    author = "সাদনা কোচ",
                    likesCount = 554
                ),

                // 6. স্বাস্থ্য ও মানসিকতা
                AdviceEntity(
                    text = "পড়াশোনায় ভালো পারফর্ম করতে হলে পর্যাপ্ত ঘুম অপরিহার্য। প্রতিদিন অন্তত ৭-৮ ঘণ্টা গভীর ঘুম ব্রেনের স্মৃতিশক্তি উন্নত করতে এবং পড়া দীর্ঘসময় মনে রাখতে সাহায্য করে।",
                    category = "স্বাস্থ্য ও মানসিকতা",
                    author = "ডাক্তার রাজীব",
                    likesCount = 411
                ),
                AdviceEntity(
                    text = "পড়ার ফাঁকে ফাঁকে পর্যাপ্ত পানি পান করো। ব্রেন ডিহাইড্রেটেড বা পানিশূন্য হলে অলসতা ও মাথাধরা দেখা দেয়, যা মনোযোগের চরম ক্ষতি করে।",
                    category = "স্বাস্থ্য ও মানসিকতা",
                    author = "সাদনা কোচ",
                    likesCount = 289
                ),
                AdviceEntity(
                    text = "নেতিবাচক চিন্তা ও সঙ্গ এড়িয়ে চলো। যারা তোমাকে নিরুৎসাহিত করে বা ছোট করে কথা বলে, তাদের থেকে দূরে থাকো। ইতিবাচক বন্ধুদের সাথে সময় কাটাও।",
                    category = "স্বাস্থ্য ও মানসিকতা",
                    author = "সাদনা কোচ",
                    likesCount = 376
                ),
                AdviceEntity(
                    text = "সুস্থ শরীরে সুস্থ মন থাকে। প্রতিদিন অন্তত ২০-৩০ মিনিট হালকা ব্যায়াম বা হাঁটাহাঁটি করো। এটি শরীরে রক্ত সঞ্চালন বাড়ায় এবং এন্ডোরফিন হরমোন ক্ষরণ করে মন ভালো রাখে।",
                    category = "স্বাস্থ্য ও মানসিকতা",
                    author = "ফিটনেস এক্সপার্ট",
                    likesCount = 312
                )
            )
            adviceDao.insertAllAdvices(initialAdvices)
        }
    }
}
