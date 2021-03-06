package com.example.gachon_club.Club

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.gachon_club.Club.Model.Board
import com.example.gachon_club.Network.ServiceControl
import com.example.gachon_club.R
import kotlinx.android.synthetic.main.activity_modify_notice.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class NoticeModify:AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_modify_notice)

        val board:Board = intent.getParcelableExtra<Board>("boardInfo")

        edit_title.setText(board.title)
        edit_notice_calendar.setText(board.name)
        edit_contnent.setText(board.content)


        btn_notice_modify.setOnClickListener{

            val TITLE = edit_title.text.toString()
            val CONTENT = edit_contnent.text.toString()
            val CALENDAR = edit_notice_calendar.text.toString()

            if((!TITLE.isNullOrBlank()) && (!CONTENT.isNullOrBlank()) && (!CALENDAR.isNullOrBlank())){
                val board1 = Board(
                    board._id,
                    board.club,
                    edit_title.text.toString(),
                    edit_contnent.text.toString(),
                    intent.getStringExtra("club"),
                    null,
                    edit_notice_calendar.text.toString()
                )
                modifyData(board1)
                finish()
            }
            else {
                Toast.makeText(this, "빠짐없이 입력해주세요", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun modifyData(board: Board) {
        val retrofitService = ServiceControl.getInstance()
        retrofitService?.modifyBoard(board)?.enqueue(object: Callback<Board> {
            override fun onResponse(call: Call<Board>, response: Response<Board>) {
                if (response.isSuccessful) {
                    val body = response.body()
                    body?.let {
                        Toast.makeText(applicationContext, "공지사항 수정 완료", Toast.LENGTH_LONG).show()
                        val intent = Intent()
                        intent.putExtra("board", it)
                        setResult(Activity.RESULT_OK, intent)
                        finish()
                    }
                }

            }
            override fun onFailure(call: Call<Board>, t: Throwable) {
                Toast.makeText(applicationContext, "공지사항 수정 실패", Toast.LENGTH_LONG).show()
            }
        })
    }


}