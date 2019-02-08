package com.example.karldivad.gitme

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.View
import com.apollographql.apollo.ApolloCall
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.Github.UsersQuery
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.exception.ApolloException
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_main.*
import okhttp3.OkHttpClient

class MainActivity : AppCompatActivity() {

    val users: ArrayList<User> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val context = this

        UserRecyclerV.layoutManager = LinearLayoutManager(this)

        val client = setupApollo()

        button.setOnClickListener{
            progressBar2.visibility = View.VISIBLE
            client.query(UsersQuery
                    .builder()
                    .queryText(editText.text.toString())
                    .build())
                    .enqueue(object : ApolloCall.Callback<UsersQuery.Data>() {
                        override fun onFailure(e: ApolloException) {
                            progressBar2.visibility = View.GONE
                            Log.d("Error Message: ", e.message.toString())
                            Log.d("Trace Error: ", e.printStackTrace().toString())
                        }

                        override fun onResponse(response: Response<UsersQuery.Data>) {
                            Log.d("OK Message: ", response.data().toString())
                            runOnUiThread {
                                progressBar2.visibility = View.GONE

                                val queryResult = response.data()?.search()?.edges()

                                queryResult!!.forEach {
                                    var nodeString = it.node()
                                    users.add(HelperJSON.getUser(HelperJSON.toJsonString(nodeString)))
                                }
                                UserRecyclerV.adapter = UserAdapter(users, context, {partItem : User -> partItemClicked(partItem)})
                            }
                        }
                    })
        }

    }

    private fun setupApollo(): ApolloClient {
        val okHttp = OkHttpClient
                .Builder()
                .addInterceptor { chain ->
                    val original = chain.request()
                    val builder = original.newBuilder().method(original.method(),
                            original.body())
                    builder.addHeader("Authorization"
                            , "Bearer " + BuildConfig.GITHUB_TOKEN)
                    chain.proceed(builder.build())
                }
                .build()
        return ApolloClient.builder()
                .serverUrl("https://api.github.com/graphql")
                .okHttpClient(okHttp)
                .build()
    }

    object HelperJSON {
        private var gson: Gson = Gson()
        fun getUser(jsonString: String): User {
            Log.e("JSONHelper ", "Enter: " + jsonString)
            return gson.fromJson(jsonString, User::class.java)
        }

        fun toJsonString(simpleObject: Any?): String = gson.toJson(simpleObject)
    }

    private fun partItemClicked(partItem : User) {
        val intent = Intent(this@MainActivity, GetRepositories::class.java)
        intent.putExtra("user_name", partItem.login)
        startActivity(intent)
    }
}
