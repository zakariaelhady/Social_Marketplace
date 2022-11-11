package com.example.krilia.utils

class Constant {
    companion object {
        const val url="http://192.168.1.133:8000/api/"
        const val login=url+"login"
        const val register=url+"register"
        const val home=url+"home"
        const val Products=url+"product/items"
        const val createProduct=url+"products"
        const val deleteProduct=url+"products/{id}"
        const val updateProduct=url+"products/{id}"
        const val productAvailability=url+"product/availability"
        const val changeAvailability=url+"product/changeAvailability"
        const val storage=url+"storage/"
        const val storage2=url+"storage"
        const val conversations=url+"conversations/get"
        const val addmessage=url+"add/message"
        const val deletemessage=url+"conversations/delete"
        const val profile=url+"profile/{id}"
        const val updateProfile=url+"profile/{id}"
        const val categories=url+"product/categories"
        const val likerOrSavedProducts=url+"FavouriteSaved"
        const val reviews=url+"reviews"
        const val add_Comment=url+"reviews/add"
        const val deleteComment=url+"reviews/delete"
        const val messages=url+"get/messages"
        const val productDetails=url+"products/{id}"
        const val likeproduct=url+"LikeProduct"
        const val saveproduct=url+"SaveProduct"
        const val getLikeOrSave=url+"getLikeOrSave"
    }
}