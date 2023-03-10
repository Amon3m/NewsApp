package com.m3.newsapp

import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.core.view.get
import com.google.android.material.tabs.TabLayout
import com.m3.islami2.base.BaseActivity
import com.m3.newsapp.api.ApiManger
import com.m3.newsapp.api.Constance
import com.m3.newsapp.api.model.ArticlesItem
import com.m3.newsapp.api.model.NewsResponse
import com.m3.newsapp.api.model.SourceResponse
import com.m3.newsapp.api.model.SourcesItem
import com.m3.newsapp.databinding.ActivityHomeBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.Serializable

class HomeActivity : BaseActivity() ,TabLayout.OnTabSelectedListener{
    lateinit var binding: ActivityHomeBinding
    lateinit var adapter: NewsAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        getSources()
        setUpViews()

    }

    private fun setUpViews() {
    adapter= NewsAdapter(null)
        binding.recyclerview.adapter=adapter
    }

    private fun getSources() {
        ApiManger.getApis().getNewsSources(Constance.apiKey,"en","us")
            .enqueue(object : Callback<SourceResponse>{
            override fun onResponse(
                call: Call<SourceResponse>,
                response: Response<SourceResponse>
            ) {
                binding.progressBar.visibility= View.GONE

                if(response.isSuccessful){
                    showSourceIntablayout(response.body()?.sources)

                }
                else{
                    showDialoge(message = response.body()?.message?:"",
                    posActionName = getString(R.string.ok)
                    , posAction = DialogInterface.OnClickListener { dialog, which ->
                      //  call.enqueue(this)
                            call.clone().enqueue(this)
                        dialog.dismiss()
                    })}
            }

            override fun onFailure(call: Call<SourceResponse>, t: Throwable) {

                showDialoge(message = t.localizedMessage as String,
                    posActionName = getString(R.string.retry)
                    , posAction = DialogInterface.OnClickListener { dialog, which ->
                        call.clone().enqueue(this)
                        dialog.dismiss()
                    })
            }

        })
    }

    private fun showSourceIntablayout(sources: List<SourcesItem?>?) {

        sources?.forEach{item->

            val tab=binding.tablayot.newTab()
            tab.setTag(item)
            tab.text = item?.name
            binding.tablayot.addTab(tab)
        }
        binding.tablayot.addOnTabSelectedListener(this)
        binding.tablayot.getTabAt(0)?.select()

    }


    override fun onTabSelected(tab: TabLayout.Tab?) {
        val item=tab?.tag as SourcesItem
        getNews(item.id)
        newsSearch(item)
    }

    private fun getNews(sourceID: String?) {

        adapter.changeData(null)
        binding.progressBar.visibility=View.VISIBLE

        ApiManger.getApis().getNews(Constance.apiKey,"en","",sourceID?:"")
            .enqueue(object :Callback<NewsResponse>{
                override fun onResponse(
                    call: Call<NewsResponse>,
                    response: Response<NewsResponse>
                ) {
                    if(response.isSuccessful){
                        ShowNewsInRecyclerView(response.body()?.articles)
                        binding.progressBar.visibility=View.GONE

                    }
                    else{
                        binding.progressBar.visibility=View.GONE

                        showDialoge(message = response.body()?.message?:"",
                            posActionName = getString(R.string.ok)
                            , posAction = DialogInterface.OnClickListener { dialog, which ->
                                //  call.enqueue(this)
                                call.clone().enqueue(this)
                                dialog.dismiss()
                            })}
                }

                override fun onFailure(call: Call<NewsResponse>, t: Throwable) {
                    showDialoge(message = t.localizedMessage as String,
                        posActionName = getString(R.string.retry)
                        , posAction = DialogInterface.OnClickListener { dialog, which ->
                            call.clone().enqueue(this)
                            dialog.dismiss()
                        })
                }
            })
    }

    private fun newsSearch(item:SourcesItem) {

        var sourceID=item.id
        if (sourceID==null){
            sourceID="abc-news"
        }

        binding.searchButton.setOnClickListener {


        adapter.changeData(null)
        binding.progressBar.visibility=View.VISIBLE
            val keyWord=binding.textSearch.text.toString()
        ApiManger.getApis().getNews(Constance.apiKey,"en",keyWord,sourceID?:"")
            .enqueue(object :Callback<NewsResponse>{
                override fun onResponse(
                    call: Call<NewsResponse>,
                    response: Response<NewsResponse>
                ) {
                    if(response.isSuccessful){
                        ShowNewsInRecyclerView(response.body()?.articles)
                        binding.progressBar.visibility=View.GONE

                    }
                    else{
                        binding.progressBar.visibility=View.GONE

                        showDialoge(message = response.body()?.message?:"",
                            posActionName = getString(R.string.ok)
                            , posAction = DialogInterface.OnClickListener { dialog, which ->
                                //  call.enqueue(this)
                                call.clone().enqueue(this)
                                dialog.dismiss()
                            })}
                }

                override fun onFailure(call: Call<NewsResponse>, t: Throwable) {
                    showDialoge(message = t.localizedMessage as String,
                        posActionName = getString(R.string.retry)
                        , posAction = DialogInterface.OnClickListener { dialog, which ->
                            call.clone().enqueue(this)
                            dialog.dismiss()
                        })
                }
            })

    }
    }


    //,,,,,,,,,,,,,,
    override fun onTabUnselected(tab: TabLayout.Tab?) {

    }

    override fun onTabReselected(tab: TabLayout.Tab?) {
        val item=tab?.tag as SourcesItem
        getNews(item.id)
        newsSearch(item)

    }

    fun ShowNewsInRecyclerView(newslist: List<ArticlesItem?>?) {
        adapter.changeData(newslist)
        adapter.onItemClickListener=object :NewsAdapter.OnItemClickListener{


            override fun onItemClick(position: Int, newsItem: ArticlesItem?) {
                val intent = Intent(this@HomeActivity, NewsDetailsActivity::class.java)
                intent.putExtra(Constance.ITEM_EXTRA,newsItem)
//                intent.putExtra(Constance.CONTENT,newsItem?.content)
//                intent.putExtra(Constance.AUTHOR,newsItem?.author)
                startActivity(intent)
            }
        }
    }


}


