package com.example.laboratorio4

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView

class HistorialAdapter(private val context: Context) : BaseAdapter() {
    private var entries: List<HistorialEntry> = emptyList()

    fun updateList(newEntries: List<HistorialEntry>) {
        entries = newEntries
        notifyDataSetChanged()
    }

    override fun getCount(): Int = entries.size

    override fun getItem(position: Int): HistorialEntry = entries[position]

    override fun getItemId(position: Int): Long = entries[position].id

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val entry = getItem(position)
        val view = convertView ?: LayoutInflater.from(context)
            .inflate(android.R.layout.simple_list_item_2, parent, false)

        val text1 = view.findViewById<TextView>(android.R.id.text1)
        val text2 = view.findViewById<TextView>(android.R.id.text2)

        text1.text = "${entry.field.replaceFirstChar { it.uppercase() }}: ${entry.oldValue} → ${entry.newValue}"
        text2.text = entry.timestamp

        return view
    }
}