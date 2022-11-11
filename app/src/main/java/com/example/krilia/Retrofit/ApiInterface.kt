package com.example.krilia.Retrofit

import com.example.krilia.models.*
import com.example.krilia.utils.Constant
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import java.math.BigInteger

interface ApiInterface {
    @POST(Constant.login)
    @FormUrlEncoded
    fun login(@Field("email") email: String,
              @Field("password") password: String): Call<LoginData>


    @POST(Constant.register)
    @Multipart
    fun register(@PartMap partMap: MutableMap<String, RequestBody>,
                @Part image: MultipartBody.Part? = null ): Call<RegisterData>

    @GET(Constant.home)
    fun getProducts() : Call<ProductModel>


    @POST(Constant.Products)
    @FormUrlEncoded
    fun getAllProduct(
        @Header("Authorization") token: String,
        @Field("userId") userId: Any,

        @Field("allStatus") allStatus: Boolean,
        @Field("available") available: Boolean,
        @Field("notAvailable") notAvailable: Boolean,

        @Field("allPrices") allPrices: Boolean,
        @Field("firstPrice") firstPrice: Boolean,
        @Field("secondPrice") secondPrice: Boolean,
        @Field("thirdPrice") thirdPrice: Boolean,
        @Field("fourthPrice") fourthPrice: Boolean,
        @Field("morePrice") morePrice: Boolean,

        @Field("latest") latest: Boolean,
        @Field("favourited") favourited: Boolean,
        @Field("lessprice") lessprice: Boolean,

        @Field("chosenCategory") chosenCategory: String,
    ): Call<ProductItem>

    @POST(Constant.createProduct)
    @Multipart
    fun createProduct(@Header("Authorization") token: String,
                      @PartMap partMap: MutableMap<String, RequestBody>,
                 @Part image : List<MultipartBody.Part> ): Call<createProductData>

    @POST(Constant.updateProduct)
    @Multipart
    fun updateProduct(@Path("id") id: Int,
                      @PartMap partMap: MutableMap<String, RequestBody>,
                      @Part image : List<MultipartBody.Part>?=null) : Call<Any>

    @DELETE(Constant.deleteProduct)
    fun deleteProduct(@Path("id") id: Int) : Call<Any>

    @POST(Constant.productAvailability)
    @FormUrlEncoded
    fun getAvailability(@Field("productId") productId: Int): Call<productAvailability>

    @POST(Constant.changeAvailability)
    @FormUrlEncoded
    fun changeAvailability(@Field("productId") productId: Int,@Field("availability") avail: String): Call<Any>

    @POST(Constant.conversations)
    fun getConversations(@Header("Authorization") token: String) : Call<Conversation>

    @POST(Constant.addmessage)
    @FormUrlEncoded
    fun addMessage(@Header("Authorization") token: String,
                   @Field("message") message: String,
                   @Field("product_id") product_id: Int,
                   @Field("receiver_id") receiver_id: Int,) : Call<Any>

    @POST(Constant.deletemessage)
    @FormUrlEncoded
    fun deleteMessage(@Header("Authorization") token: String,
                      @Field("convId") id: Int) : Call<Any>

    @GET(Constant.profile)
    fun getProfile(@Path("id") id: String) : Call<Profile>

    @POST(Constant.updateProfile)
    @Multipart
    fun updateProfile(@Path("id") id: String,
                      @PartMap partMap: MutableMap<String, RequestBody>,
                      @Part image: MultipartBody.Part? = null) : Call<Any>

    @POST(Constant.categories)
    fun getCategories(): Call<Categories>

    @POST(Constant.likerOrSavedProducts)
    @FormUrlEncoded
    fun getLikedOrSavedProducts(@Header("Authorization") token: String,
                                @Field("favourites") favourites: Int,
                                @Field("saved") saved: Int
    ): Call<ProductLiKedOrSaveddata>

    @POST(Constant.getLikeOrSave)
    @FormUrlEncoded
    fun getLikeOrSave(@Header("Authorization") token: String,
                      @Field("product_id") product_id: Int
    ): Call<LikedOrSaved>

    @POST(Constant.likeproduct)
    @FormUrlEncoded
    fun addLike(@Header("Authorization") token: String,
                @Field("product_id") product_id: Int): Call<UInt>

    @POST(Constant.saveproduct)
    @FormUrlEncoded
    fun addSave(@Header("Authorization") token: String,
                @Field("product_id") product_id: Int): Call<UInt>

    @POST(Constant.reviews)
    @FormUrlEncoded
    fun getComments(@Field("product_id") product_id: String
    ): Call<Comments>

    @POST(Constant.add_Comment)
    @FormUrlEncoded
    fun add_Comment(@Header("Authorization") token: String,
                   @Field("product_id") product_id: String,
                   @Field("review") review: String): Call<Any>

    @POST(Constant.deleteComment)
    @FormUrlEncoded
    fun deleteComment(@Field("commentId") commentId: String): Call<Any>


    @POST(Constant.messages)
    @FormUrlEncoded
    fun getMessages(@Header("Authorization") token: String,@Field("product_id") product_id: String
    ): Call<Messages>

    @GET(Constant.productDetails)
    fun getProductDetails(@Path("id") id: String) : Call<ProductDetails>

    companion object {

        var BASE_URL = Constant.url

        fun create() : ApiInterface {

            val retrofit = Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(BASE_URL)
                .build()
            return retrofit.create(ApiInterface::class.java)

        }
    }

}