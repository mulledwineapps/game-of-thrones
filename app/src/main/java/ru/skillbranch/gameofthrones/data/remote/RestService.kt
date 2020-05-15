package ru.skillbranch.gameofthrones.data.remote

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import ru.skillbranch.gameofthrones.data.remote.res.CharacterRes
import ru.skillbranch.gameofthrones.data.remote.res.HouseRes

// 42:52 мастер-класса
// в ретрофит 2.6 можно использовать suspend функции как результат выполнения нашего запроса,
// т.е. это функции, которые можно обрабатывать внутри наших корутин, что позволяет писать код
// в более привычном синхронном представлении, хотя на самом деле он является асинхронным
interface RestService {
    @GET("houses?pagesSize=50")
    suspend fun houses(@Query("page") page: Int = 1): List<HouseRes>

    @GET("characters/{id}")
    suspend fun character(@Path("id") characterId: String): CharacterRes

    @GET("houses")
    suspend fun houseByName(@Query("name") name: String) : List<HouseRes>
}