package ru.bakhelovs.cryptoapp.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import ru.bakhelovs.cryptoapp.R
import ru.bakhelovs.cryptoapp.pojo.CoinPriceInfo

class CoinInfoAdapter(private val context: Context) : RecyclerView.Adapter<CoinInfoAdapter.CoinViewHolder>() {

    var coinInfoList: List<CoinPriceInfo> = listOf()
        // каждый раз когда будем присваивать новое значение, будет обновляться RV
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    var onCoinClickListener: OnCoinClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CoinViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_coin_info, parent, false)
        return CoinViewHolder(view)
    }

    override fun onBindViewHolder(holder: CoinViewHolder, position: Int) {
        val coin = coinInfoList[position]
        with (holder) {
            val symbolsTemplate = context.resources.getString(R.string.symbols_template)
            val lastUpdateTemplate = context.resources.getString(R.string.last_update_template)
            tvSymbols.text = String.format(symbolsTemplate, coin.fromSymbol, coin.toSymbol)
            tvPrice.text = coin.price.toString()
            tvLastUpdate.text = String.format(lastUpdateTemplate, coin.getFormattedTime())
            Picasso.get().load(coin.getFullImageUrl()).into(ivLogoCoin)
            itemView.setOnClickListener {
                onCoinClickListener?.onCoinClick(coin)
            }
        }
    }

    override fun getItemCount() = coinInfoList.size

    inner class CoinViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val ivLogoCoin: ImageView = itemView.findViewById(R.id.ivLogoCoin)
        val tvSymbols: TextView = itemView.findViewById(R.id.tvSymbols)
        val tvPrice: TextView = itemView.findViewById(R.id.tvPrice)
        val tvLastUpdate: TextView = itemView.findViewById(R.id.tvLastUpdate)
    }

    interface OnCoinClickListener {
        fun onCoinClick(coinPriceInfo: CoinPriceInfo)
    }
}
