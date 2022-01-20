package com.fdhasna21.latihancrud_dailyagenda.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.latihancrud_dailyagenda.databinding.ItemDiaryBinding
import com.fdhasna21.latihancrud_dailyagenda.model.DiaryModel

class DiaryAdapter(private val diaryList: ArrayList<DiaryModel>)
    : RecyclerView.Adapter<DiaryAdapter.ViewHolder>() {
    inner class ViewHolder(val binding : ItemDiaryBinding) : RecyclerView.ViewHolder(binding.root){}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemDiaryBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = diaryList.get(position)
        holder.binding.apply {
            diarylayoutDate.text = item.date
            diarylayoutTime.text = item.time
            diarylayoutTitle.text = item.title
            diarylayoutContent.text = item.content
            diarylayoutContainer.setOnClickListener {
                onItemClickListener.onItemClicked(position, item)
            }
        }

    }

    override fun getItemCount(): Int {
        return diaryList.size
    }

    private lateinit var onItemClickListener : OnItemClickListener
    fun setOnItemClickListener(onItemClickListener: OnItemClickListener){
        onItemClickListener.let {
            this.onItemClickListener = onItemClickListener
        }
    }

    interface OnItemClickListener{
        fun onItemClicked(position: Int, item: DiaryModel)
    }

}
