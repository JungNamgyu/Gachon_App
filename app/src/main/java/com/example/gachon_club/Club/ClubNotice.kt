package com.example.gachon_club.Club

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.gachon_club.Account.Model.User
import com.example.gachon_club.Club.Model.Board
import com.example.gachon_club.Network.ServiceControl
import com.example.gachon_club.R
import kotlinx.android.synthetic.main.activity_edit_notice.*
import kotlinx.android.synthetic.main.activity_notice.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ClubNotice : AppCompatActivity() {

    var board:Board ?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notice)
        val id = intent.getLongExtra("id", 0)
        loadData(id)
        btn_notice_delete.setOnClickListener {
            if(id != null){
                val retrofitService = ServiceControl.getInstance()
                retrofitService?.deleteBoard(id)?.enqueue(object: Callback<Boolean> {
                    override fun onResponse(call: Call<Boolean>, response: Response<Boolean>) {
                        if (response.isSuccessful) {
                            Toast.makeText(applicationContext, "공지사항이 삭제 되었습니다.", Toast.LENGTH_LONG).show()
                        }
                    }
                    override fun onFailure(call: Call<Boolean>, t: Throwable) {
                    }
                })
            }
            else {
                Toast.makeText(this, "Deleted", Toast.LENGTH_LONG).show()
            }
            val intent = Intent()
            setResult(Activity.RESULT_OK, intent)
            finish()
        }

        btn_notice_configuration.setOnClickListener {
            val intent = Intent(this, NoticeModify::class.java)
            intent.putExtra("boardInfo", board)
            intent.putExtra("club", text_name.text)
            startActivityForResult(intent, 100)
        }

    }

    private fun loadData(id:Long) {
        val retrofitService = ServiceControl.getInstance()
        retrofitService?.getBoard(id)?.enqueue(object: Callback<Board> {
            override fun onResponse(call: Call<Board>, response: Response<Board>) {
                if (response.isSuccessful) {
                    val body = response.body()
                    body?.let {
                        val user = intent.getParcelableExtra<User>("user")
                        if(user != null)
                            if (body.name == user!!.name && user!!.position == "동아리 회장") {
                                btn_notice_configuration.show()
                                btn_notice_delete.show()
                            }
                            else {
                                btn_notice_configuration.hide()
                                btn_notice_delete.hide()
                            }
                        else {
                            btn_notice_configuration.hide()
                            btn_notice_delete.hide()
                        }
                        board = body
                        text_Title.text = body.title
                        text_name.text = body.name
                        text_Boardcontent.text = body.content
                    }
                }
            }
            override fun onFailure(call: Call<Board>, t: Throwable) {
                Log.d("this is error",t.message)
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                100 -> {
                    board = data!!.getParcelableExtra<Board>("boardInfo")
                    text_Title.text = board?.title
                    text_name.text = board?.name
                    text_Boardcontent.text = board?.content
                }
            }
        }
    }


}