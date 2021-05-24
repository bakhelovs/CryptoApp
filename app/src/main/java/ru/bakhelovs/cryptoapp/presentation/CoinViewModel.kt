package ru.bakhelovs.cryptoapp.presentation

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.google.gson.Gson
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import ru.bakhelovs.cryptoapp.api.ApiFactory
import ru.bakhelovs.cryptoapp.database.AppDatabase
import ru.bakhelovs.cryptoapp.pojo.CoinPriceInfo
import ru.bakhelovs.cryptoapp.pojo.CoinPriceInfoRawData
import java.util.concurrent.TimeUnit

class CoinViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getInstance(application)
    private val compositeDisposable = CompositeDisposable()

    val priceList = db.coinPriceInfoDao().getPriceList()

    fun getDetailInfo(fSym: String): LiveData<CoinPriceInfo> {
        return db.coinPriceInfoDao().getPriceInfoAboutCoin(fSym)
    }

    init {
        loadData()
    }

    private fun loadData() {
        val disposable = ApiFactory.apiService.getTopCoinsInfo(limit = 50)
            // нам нужны только имена объектов(криптовалют),
            // поэтому мы мапаем Datum и на выходе мы получаем коллекцию строк
            // и соединяем их в одну строчку
            .map { it.data?.map { it.coinInfo?.name }?.joinToString(",") }
            // метод для загрузки всей информации о криптовалютах
            //TODO убрать toString!
            .flatMap { ApiFactory.apiService.getFullPriceList(fSyms = it.toString()) }
                // преобразовываем полученную rawData в List<CoinPriceInfo>
            .map { getPriceListFromRawData(it) }
                // частота запроса
            .delaySubscription(15,TimeUnit.SECONDS)
                // метод для бесконечной загрузки, чтобы данные всегда были актуальны,
            // а не только при заново открытым приложении. Работает до первой ошибки(напр., отклчение интернета)
            .repeat()
                // повторить в случае ошибки
            .retry()
                // говорим все это делать в другом потоке
            .subscribeOn(Schedulers.io())
            .subscribe({
                // добавляем объекты в db
                db.coinPriceInfoDao().insertPriceList(it)
                Log.d("TEST_OF_LOADING_DATA", "Success: $it")
            }, {
                Log.d("TEST_OF_LOADING_DATA", "Failure: ${it.message}")
            })
        compositeDisposable.add(disposable)
    }

    // парсим Json
    // на вход приходит объект coinPriceInfoRawData, который содержит Json объект
    private fun getPriceListFromRawData(
        coinPriceInfoRawData: CoinPriceInfoRawData
    ): List<CoinPriceInfo> {
        val result = ArrayList<CoinPriceInfo>()
        val jsonObject = coinPriceInfoRawData.coinPriceInfoJsonObject ?: return result
        // берем у объекта все ключи( в данном случае это BTC, ETH и т.д.)
        val coinKeySet = jsonObject.keySet()
        // проходимся по ключам и получаем вложенные объекты
        for (coinKey in coinKeySet) {
            val currencyJson = jsonObject.getAsJsonObject(coinKey)
            // получаем ключи из вложенного Json(в данном случае USD)
            val currencyKeySet = currencyJson.keySet()
            // проходимся по ключам(USD) и получаем Json объект
            for (currencyKey in currencyKeySet) {
                // конвертируем его с помощью библиотеки Gson в нужный нам объект CoinPriceInfo
                val priceInfo = Gson().fromJson(
                    currencyJson.getAsJsonObject(currencyKey),
                    CoinPriceInfo::class.java
                )
                // добавляем полученный объект в нашу коллекцию result
                result.add(priceInfo)
            }
        }
        // после прохождения по всем ключам, возвращаем коллекцию result
        return result
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.dispose()
    }
}