package com.example.karldivad.gitme

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        button.setOnClickListener{
            val intent = Intent(this@MainActivity, GetRepositories::class.java)
            intent.putExtra("user_name", editText.text.toString())
            startActivity(intent)
        }

    }
}