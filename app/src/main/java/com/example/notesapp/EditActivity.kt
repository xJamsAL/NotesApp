package com.example.notesapp

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.notesapp.db.MyDbMAnager
import com.example.notesapp.db.MyIntentConstance
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


class EditActivity : AppCompatActivity() {
    private val myDbManager = MyDbMAnager(this)
    private val imageRequestCode = 10
    private var isEditState = false
    private var id = 0
    private var tempImageUri = "empty"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_edit)
        getMyIntents()


    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val ivImage: ImageView = findViewById(R.id.ivImage)
        if (resultCode == Activity.RESULT_OK && requestCode == imageRequestCode) {
            data?.data?.let { uri ->
                contentResolver.takePersistableUriPermission(
                    uri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
                ivImage.setImageURI(uri)
                tempImageUri = uri.toString()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        myDbManager.openDB()
    }

    override fun onDestroy() {
        super.onDestroy()
        myDbManager.closeDb()
    }

    fun onClickAddImage(view: View) {
        val fbAddImage: FloatingActionButton = findViewById(R.id.fbAddimage)
        val myImageLayout: ConstraintLayout = findViewById(R.id.myImageLayout)
        myImageLayout.visibility = View.VISIBLE
        fbAddImage.visibility = View.GONE
    }

    fun OnClickDeleteImg(view: View) {

        val fbAddImage: FloatingActionButton = findViewById(R.id.fbAddimage)
        val myImageLayout: ConstraintLayout = findViewById(R.id.myImageLayout)
        myImageLayout.visibility = View.GONE
        fbAddImage.visibility = View.VISIBLE
        tempImageUri = "empty"
    }

    fun OnClickChooseImage(view: View) {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.type = "image/*"
        intent.flags =
            Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION
        startActivityForResult(intent, imageRequestCode)
    }

    fun OnClickSave(view: View) {
        val edTitul = findViewById<EditText>(R.id.edTitul)
        val edDesc = findViewById<EditText>(R.id.edDesc)
        val myTitle = edTitul.text.toString()
        val myDesc = edDesc.text.toString()

        if (myTitle != "" && myDesc != "") {

            CoroutineScope(Dispatchers.Main).launch {   if (isEditState) {
                myDbManager.updateitem(myTitle, myDesc, tempImageUri, id, getCurrentTime())
            } else {
                myDbManager.insertTodb(myTitle, myDesc, tempImageUri, getCurrentTime())
            }
                finish()
            }
        }
    }

    fun onClickEditEnable(view: View) {
        val edTitular = findViewById<EditText>(R.id.edTitul)
        val edDesc = findViewById<EditText>(R.id.edDesc)
        val fbEdit = findViewById<FloatingActionButton>(R.id.fbEdit)
        val imDelete = findViewById<ImageButton>(R.id.imDelete)
        val imEdit = findViewById<ImageButton>(R.id.imEdit)
        val fbAddimage: FloatingActionButton = findViewById(R.id.fbAddimage)

        edDesc.isEnabled = true
        edTitular.isEnabled = true
        fbEdit.visibility = View.GONE
        fbAddimage.visibility = View.VISIBLE
        if (tempImageUri=="empty") return
        imEdit.visibility =View.VISIBLE
        imDelete.visibility = View.VISIBLE

    }

    private fun getMyIntents() {
        val edTitular = findViewById<EditText>(R.id.edTitul)
        val edDesc = findViewById<EditText>(R.id.edDesc)
        val fbEdit = findViewById<FloatingActionButton>(R.id.fbEdit)
        fbEdit.visibility = View.GONE
        val i = intent
        if (i != null) {

            if (i.getStringExtra(MyIntentConstance.I_TITLE_KEY) != null) {

                val fbAddImage: FloatingActionButton = findViewById(R.id.fbAddimage)
                fbAddImage.visibility = View.GONE

                isEditState = true
                edTitular.isEnabled = false
                edDesc.isEnabled = false
                fbEdit.visibility = View.VISIBLE
                edTitular.setText(i.getStringExtra(MyIntentConstance.I_TITLE_KEY))
                edDesc.setText(i.getStringExtra(MyIntentConstance.I_DESC_KEY))
                id = i.getIntExtra(MyIntentConstance.I_ID_KEY, 0)

                if (i.getStringExtra(MyIntentConstance.I_URI_KEY) != "empty") {
                    val myImageLayout: ConstraintLayout = findViewById(R.id.myImageLayout)
                    val ivImage: ImageView = findViewById(R.id.ivImage)
                    val imdelete: ImageView = findViewById(R.id.imDelete)
                    val imedit: ImageView = findViewById(R.id.imEdit)
                    myImageLayout.visibility = View.VISIBLE
                    tempImageUri = i.getStringExtra(MyIntentConstance.I_URI_KEY)!!
                    ivImage.setImageURI(Uri.parse(tempImageUri))
                    imdelete.visibility = View.GONE
                    imedit.visibility = View.GONE
                }
            }
        }
    }
    private fun getCurrentTime(): String {
        val time = Calendar.getInstance().time
        val formater = SimpleDateFormat("dd-MM-yy kk:mm", Locale.getDefault())
        return formater.format(time)
    }


}