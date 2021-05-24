package ru.bakhelovs.cryptoapp.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import ru.bakhelovs.cryptoapp.R
import ru.bakhelovs.cryptoapp.databinding.ItemCoinInfoBinding
import ru.bakhelovs.cryptoapp.pojo.CoinPriceInfo

class CoinInfoAdapter(private val context: Context) :
    RecyclerView.Adapter<CoinInfoAdapter.CoinViewHolder>() {

    var coinInfoList: List<CoinPriceInfo> = listOf()
        // каждый раз когда будем присваивать новое значение, будет обновляться RV
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    var onCoinClickListener: OnCoinClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CoinViewHolder {
        val itemBinding =
            ItemCoinInfoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CoinViewHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: CoinViewHolder, position: Int) {
        val coin = coinInfoList[position]
        holder.bind(coin)
        holder.itemView.setOnClickListener {
            onCoinClickListener?.onCoinClick(coin)
        }
    }

    override fun getItemCount() = coinInfoList.size

    inner class CoinViewHolder(private val itemBinding: ItemCoinInfoBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        fun bind(coin: CoinPriceInfo) {
            val symbolsTemplate = context.resources.getString(R.string.symbols_template)
            val lastUpdateTemplate = context.resources.getString(R.string.last_update_template)
            itemBinding.tvSymbols.text =
                String.format(symbolsTemplate, coin.fromSymbol, coin.toSymbol)
            itemBinding.tvPrice.text = coin.price.toString()
            itemBinding.tvLastUpdate.text =
                String.format(lastUpdateTemplate, coin.getFormattedTime())
            Picasso.get().load(coin.getFullImageUrl()).into(itemBinding.ivLogoCoin)
        }
    }

    interface OnCoinClickListener {
        fun onCoinClick(coinPriceInfo: CoinPriceInfo)
    }
}
