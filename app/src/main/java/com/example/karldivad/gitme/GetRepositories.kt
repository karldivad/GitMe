package com.example.karldivad.gitme

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.apollographql.apollo.ApolloCall
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.Github.FindQuery
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.exception.ApolloException
import com.apollographql.apollo.rx2.Rx2Apollo
import com.example.karldivad.gitme.R.styleable.View
import kotlinx.android.synthetic.main.activity_get_repositories.*
import kotlinx.android.synthetic.main.activity_main.*
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit
import javax.annotation.Nonnull

class GetRepositories : AppCompatActivity() {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_get_repositories)
        val username:String = intent.getStringExtra("user_name")

        val client = setupApollo()

        client.query(FindQuery
                .builder()
                .user_name(username)
                .build())
                .enqueue(object : ApolloCall.Callback<FindQuery.Data>() {
                    override fun onFailure(e: ApolloException) {
                        Log.d("ss",e.message.toString())
                        Log.d("ss",e.printStackTrace().toString())
                    }

                    override fun onResponse(response: Response<FindQuery.Data>) {
                        Log.d("ssdd"," " + response.data()?.user())
                        runOnUiThread {
                            /*progress_bar.visibility = View.GONE*/
                            textView3.setText(response.data()?.user()?.repositories().toString())

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