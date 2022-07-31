package com.bignerdranch.android.nerdlauncher


import android.content.Intent
import android.content.pm.ResolveInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

const val TAG = "Activities"
class NerdLauncherActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nerd_launcher)
        recyclerView = findViewById(R.id.app_recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this)
        setupAdapter()
    }

    private fun setupAdapter() {
        val startupIntent =
            Intent(Intent.ACTION_MAIN).apply {
                addCategory(Intent.CATEGORY_LAUNCHER)
            }
        val activities =
            packageManager.queryIntentActivities(startupIntent, 0)
        Log.i(TAG, "Found ${activities.size} activities")
        activities.sortWith { a, b ->
            String.CASE_INSENSITIVE_ORDER
                .compare(
                    a.loadLabel(packageManager).toString(),
                    b.loadLabel(packageManager).toString()
                )
        }
        recyclerView.adapter = ActivityAdapter(activities)
    }
    private class ActivityHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener{
        private val nameTextView = itemView.findViewById(R.id.textView)  as TextView
        private val imageView = itemView.findViewById(R.id.imageView)  as ImageView
        private lateinit var resolveInfo: ResolveInfo

        fun bindActivity(resolveInfo: ResolveInfo) {
            this.resolveInfo = resolveInfo
            val packageManager = itemView.context.packageManager
            val appName = resolveInfo.loadLabel(packageManager).toString()
            val appIcon = resolveInfo.loadIcon(packageManager)
            imageView.setImageDrawable(appIcon)
            nameTextView.text = appName
            Log.i(TAG, "Set $appName")
        }

        init {
            nameTextView.setOnClickListener(this)
        }
        override fun onClick(view: View) {
            val activityInfo = resolveInfo.activityInfo
            val intent = Intent(Intent.ACTION_MAIN).apply {
                    setClassName(activityInfo.applicationInfo.packageName,
                        activityInfo.name)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
            val context = view.context
            context.startActivity(intent)
        }
    }
    private class ActivityAdapter(val activities : List<ResolveInfo>) : RecyclerView.Adapter<ActivityHolder>(){
        override fun onCreateViewHolder(container: ViewGroup, viewType: Int): ActivityHolder {
            val layoutInflater = LayoutInflater.from(container.context)
          val view = layoutInflater.inflate(R.layout.list_item,container,false)
            return ActivityHolder(view)
        }

        override fun onBindViewHolder(holder: ActivityHolder, position: Int) {
        val resolveInfo = activities[position]
        holder.bindActivity(resolveInfo)
        }

        override fun getItemCount(): Int {
           return activities.size
        }


    }
}