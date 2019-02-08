package com.example.karldivad.gitme

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import com.apollographql.apollo.ApolloCall
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.Github.FindQuery
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.exception.ApolloException
import com.apollographql.apollo.rx2.Rx2Apollo

import kotlinx.android.synthetic.main.activity_get_repositories.*
import kotlinx.android.synthetic.main.activity_main.*
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit
import javax.annotation.Nonnull

class GetRepositories : AppCompatActivity() {

    val repositories: ArrayList<Repository> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_get_repositories)
        val username:String = intent.getStringExtra("user_name")
        val context = this

        RepositoryRecyclerV.layoutManager = LinearLayoutManager(this)


        val client = setupApollo()

        client.query(FindQuery
                .builder()
                .user_name(username)
                .build())
                .enqueue(object : ApolloCall.Callback<FindQuery.Data>() {
                    override fun onFailure(e: ApolloException) {
                        Log.d("Error Message: ", e.message.toString())
                        Log.d("Trace Error: ", e.printStackTrace().toString())
                    }

                    override fun onResponse(response: Response<FindQuery.Data>) {
                        runOnUiThread {
                            progressBar.visibility = View.GONE

                            val repos = response.data()?.user()?.repositories()?.edges()
                            repos!!.forEach {
                                repositories.add(
                                        Repository(
                                                name = it.node()!!.name(),
                                                url = it.node()!!.url(),
                                                description = it.node()!!.description().toString(),
                                                PR_count = it.node()!!.pullRequests().totalCount()
                                                )
                                )
                            }
                            RepositoryRecyclerV.adapter = RepositoryAdapter(repositories, context)
                        }
                    }
                })
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
}