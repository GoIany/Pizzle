package com.yjh.pizzle.Record

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.yjh.pizzle.R
import kotlinx.android.synthetic.main.record_rc_item.view.*

class RcAdapter(private var items: MutableList<PlayRecord>): RecyclerView.Adapter<RcAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var itemView = LayoutInflater.from(parent.context).inflate(R.layout.record_rc_item, parent,false)
        return ViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var item = items[position]
        holder.apply {
            bind(item)
        }
    }

    class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        private var view = v
        fun bind(item: PlayRecord){
            var min = if (item.recordSec/60/10 <1) "0"+(item.recordSec/60).toString() else (item.recordSec/60).toString()
            var sec = if (item.recordSec%60/10 <1) "0"+(item.recordSec%60).toString() else (item.recordSec%60).toString()
            view.rankingTv.text = (position+1).toString()+"."
            view.recordViewName.text = item.recordName
            view.recordViewSec.text = "$min : $sec"
        }
    }


}