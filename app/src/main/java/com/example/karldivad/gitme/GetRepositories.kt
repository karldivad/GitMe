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


    val BASE_URL = "https://api.github.com/graphql"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_get_repositories)
        val bundle :Bundle ?=intent.extras
        val user_name = bundle!!.getString("user_name")

        val client = setupApollo()

        client.query(FindQuery
                .builder()
                .user_name(user_name.toString())
                .build())
                .enqueue(object : ApolloCall.Callback<FindQuery.Data>() {
                    override fun onFailure(e: ApolloException) {
                        Log.d("ss",e.message.toString())

                    }

                    override fun onResponse(response: Response<FindQuery.Data>) {
                        Log.d("ss"," " + response.data()?.user())
                        runOnUiThread {
                            /*progress_bar.visibility = View.GONE
                            name_text_view.text = String.format(getString(R.string.name_text),
                                    response.data()?.repository()?.name())
                            description_text_view.text = String.format(getString(R.string.description_text),
                                    response.data()?.repository()?.description())
                            forks_text_view.text = String.format(getString(R.string.fork_count_text),
                                    response.data()?.repository()?.forkCount().toString())
                            url_text_view.text = String.format(getString(R.string.url_count_text),
                                    response.data()?.repository()?.url().toString())*/
                            editText3.setText(String.format(getString(R.string.abc_capital_on),
                                    response.data()?.user()?.repositories()?.edges()?.last()?.node()?.url().toString()))

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
                            , "Bearer " + "<my-code-here>")
                    chain.proceed(builder.build())
                }
                .build()
        return ApolloClient.builder()
                .serverUrl(BASE_URL)
                .okHttpClient(okHttp)
                .build()
    }
}