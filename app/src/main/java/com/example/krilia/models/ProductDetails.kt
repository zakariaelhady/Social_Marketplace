package com.example.krilia.models

data class ProductDetails(
    val data: PDetails
)

data class PDetails(
    val product: Product,
    val user: User,
    val total: Int,
    val auth: Boolean,
    val commentCount: Int,
    val similar_products: List<Product>
)

//"product":{"id":1,"title":"LED Double Sided Pocket Mirror for Makeup","description":"LED Double Sided Pocket Mirror for Makeup","price":19,"status":"available","categorie":"Beauty & Personal Care \/ Health & Medical","favourites":1,"user_id":1,"created_at":"2022-09-07T18:00:53.000000Z","updated_at":"2022-09-12T20:58:45.000000Z","images":["products_img\/1\/PdIHHGkI0RTjBOstlWaiuMs3FP1wDJWbTtXR7KhT-1662573653.jpg"]},
//"user":{"id":1,"name":"Zakaria El hady","email":"zakariaelhady2017@gmail.com","email_verified_at":null,"image":"profile_img\/IMG_20220828_172044-1662572161.jpg","location":"Bir rami est.Kenitra","country":"Morocco","phone":"0665847511","created_at":"2022-09-07T17:36:02.000000Z","updated_at":"2022-09-07T17:36:02.000000Z"},
//"total":4,
//"auth":false,
//"commentCount":0,
//"similar_products":[{"id":2,"title":"Hair Stylist","description":"\u0627\u0644\u0645\u0634\u0637 \u0627\u0644\u062d\u0631\u0627\u0631\u064a \u0644\u062a\u0633\u0631\u064a\u062d \u0648 \u062a\u0646\u0639\u064a\u0645 \u0627\u0644\u0634\u0639\u0631\r\nhttps:\/\/getoffer.shop\/offer?ref_id=9QrOvzld2g","price":30,"status":"available","categorie":"Beauty & Personal Care \/ Health & Medical","favourites":2,"user_id":1,"created_at":"2022-09-07T18:05:21.000000Z","updated_at":"2022-09-07T18:20:51.000000Z","images":["products_img\/2\/1-1662573921.jpg","products_img\/2\/2-1662573922.jpg","products_img\/2\/3-1662573922.jpg","products_img\/2\/4-1662573922.jpg"]}]},"message":""}