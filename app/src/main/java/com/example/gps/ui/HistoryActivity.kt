package com.example.gps.ui

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gps.R
import com.example.gps.dao.MyDataBase

class HistoryActivity : AppCompatActivity() {
    private lateinit var toolbar: Toolbar
    private lateinit var rcy: RecyclerView

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        toolbar = findViewById(R.id.mToolBar)
        rcy = findViewById(R.id.rcy)
        setSupportActionBar(toolbar)
        val mng = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        val list = MyDataBase.getInstance(this).movementDao().getAllMovementData()
        list.reverse()
        val adapter =
            HistoryAdapter(list)
        val direction = DividerItemDecoration(this, LinearLayoutManager.VERTICAL)
        rcy.addItemDecoration(direction)
        rcy.layoutManager = mng
        rcy.adapter = adapter
    }
}