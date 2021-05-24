package ru.bakhelovs.cryptoapp

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import ru.bakhelovs.cryptoapp.adapters.CoinInfoAdapter
import ru.bakhelovs.cryptoapp.databinding.ActivityCoinPriceListBinding
import ru.bakhelovs.cryptoapp.pojo.CoinPriceInfo
import ru.bakhelovs.cryptoapp.presentation.CoinViewModel

class CoinPriceListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCoinPriceListBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCoinPriceListBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        val coinViewModel: CoinViewModel by viewModels()

        val adapter = CoinInfoAdapter(this)
        binding.rvCoinPriceList.adapter = adapter
        adapter.onCoinClickListener = object : CoinInfoAdapter.OnCoinClickListener {
            override fun onCoinClick(coinPriceInfo: CoinPriceInfo) {
                val intent = CoinDetailActivity.newIntent(
                    this@CoinPriceListActivity,
                    coinPriceInfo.fromSymbol
                )
                startActivity(intent)
            }
        }

        coinViewModel.priceList.observe(this, {
            adapter.coinInfoList = it
        })
    }
}