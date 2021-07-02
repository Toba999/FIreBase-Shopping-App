package com.example.android.oshoppingapp.firestore

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import com.example.android.oshoppingapp.activities.*
import com.example.android.oshoppingapp.fragments.HomeFragment
import com.example.android.oshoppingapp.fragments.ProductsFragment
import com.example.android.oshoppingapp.fragments.ProfileFragment
import com.example.android.oshoppingapp.models.CartItem
import com.example.android.oshoppingapp.models.Product
import com.example.android.oshoppingapp.models.User
import com.example.android.oshoppingapp.utils.Constants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class FireStoreClass{

    private val mFireStore = FirebaseFirestore.getInstance()

    fun registerUser(activity: RegistryActivity, userInfo: User) {

        // The "users" is collection name. If the collection is already created then it will not create the same one again.
        mFireStore.collection(Constants.USERS)
            // Document ID for users fields. Here the document it is the User ID.
            .document(userInfo.id)
            // Here the userInfo are Field and the SetOption is set to merge. It is for if we wants to merge later on instead of replacing the fields.
            .set(userInfo, SetOptions.merge())
            .addOnSuccessListener {

                // Here call a function of base activity for transferring the result to it.
                activity.userRegistrationSuccess()
            }
            .addOnFailureListener { e ->
                activity.hideProgressDialog()
                Log.e(
                    activity.javaClass.simpleName,
                    "Error while registering the user.",
                    e
                )
            }
    }


    fun getCurrentUserID(): String {
        // An Instance of currentUser using FirebaseAuth
        val currentUser = FirebaseAuth.getInstance().currentUser

        // A variable to assign the currentUserId if it is not null or else it will be blank.
        var currentUserID = ""
        if (currentUser != null) {
            currentUserID = currentUser.uid
        }

        return currentUserID
    }


    fun getUserDetails(lifecycleOwner: LifecycleOwner) {

        // Here we pass the collection name from which we wants the data.
        mFireStore.collection(Constants.USERS)
            // The document id to get the Fields of user.
            .document(getCurrentUserID())
            .get()
            .addOnSuccessListener { document ->
                Log.i(lifecycleOwner.javaClass.simpleName, document.toString())
                // Here we have received the document snapshot which is converted into the User Data model object.
                val user = document.toObject(User::class.java)!!

                val context = if (lifecycleOwner is Activity){
                   ( lifecycleOwner as Activity)
                }else{
                    (lifecycleOwner as Fragment).requireActivity()
                }
                val sharedPreferences=context.getSharedPreferences(
                    Constants.MYSHOPPAL_PREFERENCES,
                    Context.MODE_PRIVATE
                )
                val editor:SharedPreferences.Editor=sharedPreferences.edit()
                //Key:logged_in_username
                editor.putString(
                    Constants.LOGGED_IN_USERNAME,
                    "${user.firstName} ${user.lastName}"
                )
                editor.apply()
                // START
                when (lifecycleOwner) {
                    is LoginActivity -> {
                        // Call a function of base activity for transferring the result to it.
                        lifecycleOwner.userLoggedInSuccess(user)
                    }
                    is SettingsActivity -> {
                        lifecycleOwner.userDetailsSuccess(user)
                    }
                    is ProfileFragment ->{
                        lifecycleOwner.userDetailsSuccess(user)
                    }

                }

            }
            .addOnFailureListener { e ->
                // Hide the progress dialog if there is any error. And print the error in log.
                when (lifecycleOwner) {
                    is LoginActivity -> {
                        lifecycleOwner.hideProgressDialog()
                    }
                    is SettingsActivity -> {
                        lifecycleOwner.hideProgressDialog()
                    }
                    is ProfileFragment ->{
                        lifecycleOwner.hideProgressDialog()
                    }

                }
                Log.e(lifecycleOwner.javaClass.simpleName,
                    "Error while getting user details.", e)
            }
    }

    fun updateUserProfileData(activity: Activity, userHashMap: HashMap<String, Any>) {
        // Collection Name
        mFireStore.collection(Constants.USERS)
            // Document ID against which the data to be updated. Here the document id is the current logged in user id.
            .document(getCurrentUserID())
            // A HashMap of fields which are to be updated.
            .update(userHashMap)
            .addOnSuccessListener {
                // Notify the success result.
                when (activity) {
                    is UserProfileActivity -> {
                        // Call a function of base activity for transferring the result to it.
                        activity.userProfileUpdateSuccess()
                    }
                }
                // END
            }
            .addOnFailureListener { e ->
                when (activity) {
                    is UserProfileActivity -> {
                        // Hide the progress dialog if there is any error. And print the error in log.
                        activity.hideProgressDialog()
                    }
                }
                Log.e(
                    activity.javaClass.simpleName,
                    "Error while updating the user details.", e)
            }
    }

    fun uploadImageToCloudStorage(activity: Activity, imageFileURI: Uri?, imageType: String) {
        //getting the storage reference
        val sRef: StorageReference = FirebaseStorage.getInstance().reference.child(
            imageType + System.currentTimeMillis() + "."
                    + Constants.getFileExtension(activity, imageFileURI))
        //adding the file to reference
        sRef.putFile(imageFileURI!!)
            .addOnSuccessListener { taskSnapshot ->
                // The image upload is success
                Log.e("Firebase Image URL",
                    taskSnapshot.metadata!!.reference!!.downloadUrl.toString()
                )
                // Get the downloadable url from the task snapshot
                taskSnapshot.metadata!!.reference!!.downloadUrl
                    .addOnSuccessListener { uri ->
                        Log.e("Downloadable Image URL", uri.toString())
                        // Here call a function of base activity for transferring the result to it.
                        when (activity) {
                            is UserProfileActivity -> {
                                activity.imageUploadSuccess(uri.toString())
                            }
                            is AddProductActivity -> {
                                activity.imageUploadSuccess(uri.toString())
                            }
                            is EditProductActivity -> {
                                activity.imageUploadSuccess(uri.toString())
                            }
                        }
                    }
            }
            .addOnFailureListener { exception ->
                // Hide the progress dialog if there is any error. And print the error in log.
                when (activity) {
                    is UserProfileActivity -> {
                        activity.hideProgressDialog()
                    }
                    is AddProductActivity -> {
                        activity.hideProgressDialog()
                    }
                    is EditProductActivity -> {
                        activity.hideProgressDialog()
                    }
                }
                Log.e(
                    activity.javaClass.simpleName,
                    exception.message,
                    exception
                )
            }
    }

    fun uploadProductDetails(activity: AddProductActivity, productInfo: Product) {

        mFireStore.collection(Constants.PRODUCTS)
                .document()
                // Here the userInfo are Field and the SetOption is set to merge. It is for if we wants to merge
                .set(productInfo, SetOptions.merge())
                .addOnSuccessListener {

                    // Here call a function of base activity for transferring the result to it.
                    activity.productUploadSuccess()
                }
                .addOnFailureListener { e ->

                    activity.hideProgressDialog()

                    Log.e(activity.javaClass.simpleName,
                            "Error while uploading the product details.", e)
                }
    }

    fun updateProductDetails(activity: EditProductActivity,  productHashMap: HashMap<String, Any>,productId: String) {

        mFireStore.collection(Constants.PRODUCTS)
                .document(productId)
                // Here the userInfo are Field and the SetOption is set to merge. It is for if we wants to merge
                .update(productHashMap)
                .addOnSuccessListener {
                    // Here call a function of base activity for transferring the result to it.
                    activity.productUpdateSuccess()
                }
                .addOnFailureListener { e ->

                    activity.hideProgressDialog()

                    Log.e(activity.javaClass.simpleName,
                            "Error while updating the product details.", e)
                }
    }

    fun deleteProduct(fragment: ProductsFragment,productId:String){
        mFireStore.collection(Constants.PRODUCTS)
                .document(productId)
                .delete()
                .addOnSuccessListener {

                    fragment.productDeleteSuccess()

                }.addOnFailureListener {
                    e ->
                    fragment.hideProgressDialog()
                    Log.e(fragment.requireActivity().javaClass.simpleName,
                            "error while deleting the product", e)
                }
    }

    fun getProductsList(fragment: Fragment) {
        // The collection name for PRODUCTS
        mFireStore.collection(Constants.PRODUCTS)
            .whereEqualTo(Constants.USER_ID, getCurrentUserID())
            .get() // Will get the documents snapshots.
            .addOnSuccessListener { document ->

                // Here we get the list of boards in the form of documents.
                Log.e("Products List", document.documents.toString())

                // Here we have created a new instance for Products ArrayList.
                val productsList: ArrayList<Product> = ArrayList()

                // A for loop as per the list of documents to convert them into Products ArrayList.
                for (i in document.documents) {

                    val product = i.toObject(Product::class.java)
                    product!!.product_id = i.id

                    productsList.add(product)
                }

                when (fragment) {
                    is ProductsFragment -> {
                        fragment.successProductsListFromFireStore(productsList)
                    }
                }
            }
            .addOnFailureListener { e ->
                // Hide the progress dialog if there is any error based on the base class instance.
                when (fragment) {
                    is ProductsFragment -> {
                        fragment.hideProgressDialog()
                    }
                }
                Log.e("Get Product List", "Error while getting product list.", e)
            }
    }

    fun getHomeItemsList(fragment: HomeFragment) {
        // The collection name for PRODUCTS
        mFireStore.collection(Constants.PRODUCTS)
                .get() // Will get the documents snapshots.
                .addOnSuccessListener { document ->

                    // Here we get the list of boards in the form of documents.
                    Log.e(fragment.javaClass.simpleName, document.documents.toString())

                    // Here we have created a new instance for Products ArrayList.
                    val productsList: ArrayList<Product> = ArrayList()

                    // A for loop as per the list of documents to convert them into Products ArrayList.
                    for (i in document.documents) {

                        val product = i.toObject(Product::class.java)!!
                        product.product_id = i.id
                        productsList.add(product)
                    }

                    // Pass the success result to the base fragment.
                    fragment.successHomeItemsList(productsList)
                }
                .addOnFailureListener { e ->
                    // Hide the progress dialog if there is any error which getting the dashboard items list.
                    fragment.hideProgressDialog()
                    Log.e(fragment.javaClass.simpleName, "Error while getting dashboard items list.", e)
                }
    }

    fun getProductDetails(activity: Activity, productId: String) {
        // The collection name for PRODUCTS
        mFireStore.collection(Constants.PRODUCTS)
            .document(productId)
            .get() // Will get the document snapshots.
            .addOnSuccessListener { document ->

                // Here we get the product details in the form of document.
                Log.e(activity.javaClass.simpleName, document.toString())

                // Convert the snapshot to the object of Product data model class.
                val product = document.toObject(Product::class.java)!!

                when (activity) {
                    is ProductDetailsActivity -> {
                        activity.productDetailsSuccess(product)
                    }
                    is EditProductActivity -> {
                        activity.productDetailsSuccess(product)
                    }
                }
            }
            .addOnFailureListener { e ->

                // Hide the progress dialog if there is an error.
                when (activity) {
                    is ProductDetailsActivity -> {
                        activity.hideProgressDialog()
                    }
                    is EditProductActivity -> {
                        activity.hideProgressDialog()
                    }
                }

                Log.e(activity.javaClass.simpleName, "Error while getting the product details.", e)
            }
    }

    fun addCartItems(activity: ProductDetailsActivity, addToCart: CartItem) {
        mFireStore.collection(Constants.CART_ITEMS)
            .document()
            // Here the userInfo are Field and the SetOption is set to merge. It is for if we wants to merge
            .set(addToCart, SetOptions.merge())
            .addOnSuccessListener {
                // Here call a function of base activity for transferring the result to it.
                activity.addToCartSuccess()
            }
            .addOnFailureListener { e ->
                activity.hideProgressDialog()
                Log.e(activity.javaClass.simpleName,
                    "Error while creating the document for cart item.", e)
            }
    }

}