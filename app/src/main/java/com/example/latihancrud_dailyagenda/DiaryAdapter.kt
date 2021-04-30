package com.example.latihancrud_dailyagenda

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import org.w3c.dom.Text


class DiaryAdapter(val arrayList: ArrayList<DiaryModel>, val context: Context)
    : RecyclerView.Adapter<DiaryAdapter.ViewHolder>() {
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val layoutAdapt = itemView.findViewById<View>(R.id.diarylayout_container)
        val dateAdapt = itemView.findViewById<TextView>(R.id.diarylayout_date)
        val timeAdapt = itemView.findViewById<TextView>(R.id.diarylayout_time)
        val titleAdapt = itemView.findViewById<TextView>(R.id.diarylayout_title)
        val contentAdapt = itemView.findViewById<TextView>(R.id.diarylayout_content)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_diary, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = arrayList.get(position)
        holder.dateAdapt.text = item.date
        holder.timeAdapt.text = item.time
        holder.titleAdapt.text = item.title
        holder.contentAdapt.text = item.content

        holder.itemView.setOnClickListener{
            if(context is MainActivity){
                val context = holder.itemView.context
                val intent = Intent(context, EditDiary::class.java)
                intent.putExtra("editID", item.id)
                intent.putExtra("editDate", item.date)
                intent.putExtra("editTime", item.time)
                intent.putExtra("editTitle", item.title)
                intent.putExtra("editContent", item.content)
                context.startActivity(intent)
            }
        }
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }
}
