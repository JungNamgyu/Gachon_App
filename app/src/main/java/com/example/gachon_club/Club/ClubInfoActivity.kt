package com.example.gachon_club.Club

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentPagerAdapter
import com.example.gachon_club.R
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.gachon_club.Account.Model.User
import com.example.gachon_club.Club.Adapter.BoardRecyclerAdapter
import com.example.gachon_club.Club.Adapter.CalendarRecyclerAdapter
import com.example.gachon_club.Club.Model.Board
import com.example.gachon_club.Club.Model.Club
import com.example.gachon_club.Network.ServiceControl
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.activity_club_info.*
import kotlinx.android.synthetic.main.fragment_calendar.*
import kotlinx.android.synthetic.main.fragment_notice.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.collections.ArrayList

class ClubInfoActivity : AppCompatActivity() {

    private var fragmentPagerAdapter: FragmentPagerAdapter? = null

    var user:User ?= null
    var msg:CharSequence ?= null
    var Bbody:Club ?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_club_info)

        val adapter = ViewPagerAdapter(supportFragmentManager)
        adapter.addFragment(NoticeFragment(), "공지사항")
        adapter.addFragment(CalendarFragment(), "일정")
        viewPager.adapter = adapter



        loadClub(intent.getLongExtra("id", 0))

        tab_layout.setupWithViewPager(viewPager)

        user = intent.getParcelableExtra<User>("user")

        tab_layout.addOnTabSelectedListener(object:TabLayout.OnTabSelectedListener {
            override fun onTabReselected(p0: TabLayout.Tab?) {}
            override fun onTabUnselected(p0: TabLayout.Tab?) {
            }
            override fun onTabSelected(tab: TabLayout.Tab?) {
                if (tab == null)
                    btn_notice_edit.hide()
                else {
                    if (tab.position == 0) {
                        if(user != null)
                            if (text_Title.text == user!!.club && user!!.position == "동아리 회장")
                                btn_notice_edit.show()
                            else
                                btn_notice_edit.hide()
                        else
                            btn_notice_edit.hide()
                    }
                    else{
                        calendarView?.setOnDateChangeListener { view, year, month, dayOfMonth ->
                            if(month < 9){
                                text_Calendar.text = (""+year+"년 "+ "0" + (month+1)+"월 일정")
                                msg = (year.toString() + "0" + (month+1).toString())
                                loadCalendars(Bbody!!.name, msg as String)
                            }
                            else{
                                text_Calendar.text = (""+year+"년 "+(month+1)+"월 일정")
                                msg = (year.toString() + (month+1).toString())
                                loadCalendars(Bbody!!.name, msg as String)
                            }
                        }
                        btn_notice_edit.hide()
                    }
                }
            }
        })
    }

    class ViewPagerAdapter(manager: FragmentManager) : FragmentPagerAdapter(manager){

        private  val fragmentList : MutableList<Fragment> = ArrayList()
        private  val titleList : MutableList<String> = ArrayList()

        override fun getItem(position: Int): Fragment {
            return fragmentList[position]
        }

        override fun getCount(): Int {
            return fragmentList.size
        }

        fun addFragment(fragment: Fragment, title:String){
            fragmentList.add(fragment)
            titleList.add(title)
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return titleList[position]
        }

    }

    private fun setAdapter(boardList: ArrayList<Board>){
        val mAdapter = BoardRecyclerAdapter(boardList,this) { it ->
            val intent = Intent(applicationContext, ClubNotice::class.java)
            intent.putExtra("id", it?._id)
            intent.putExtra("user", user)
            startActivityForResult(intent, 100)
        }

        val manager = LinearLayoutManager(this)
        manager.reverseLayout = true
        manager.stackFromEnd = true

        board_recycler_view.adapter = mAdapter
        board_recycler_view.layoutManager = manager

    }

    private fun bsetAdapter(boardList: ArrayList<Board>){
        val bAdapter = CalendarRecyclerAdapter(boardList,this) { it ->
            val intent = Intent(applicationContext, ClubNotice::class.java)
            intent.putExtra("id", it?._id)
            intent.putExtra("user", user)
            startActivityForResult(intent, 100)
        }

        calendar_recycler_view.adapter = bAdapter
        calendar_recycler_view.layoutManager = LinearLayoutManager(this)
    }

    private fun loadClub(id: Long) {
        val retrofitService = ServiceControl.getInstance()
        retrofitService?.getClub(id)?.enqueue(object: Callback<Club> {
            override fun onResponse(call: Call<Club>, response: Response<Club>) {
                if (response.isSuccessful) {
                    val body = response.body()
                    Bbody = response.body()
                    text_Title.text = body!!.name
                    loadBoards(body!!.name)
                    if(user != null)
                        if (body!!.name == user!!.club && user!!.position == "동아리 회장")
                            btn_notice_edit.show()
                        else
                            btn_notice_edit.hide()
                    else
                        btn_notice_edit.hide()
                    btn_notice_edit.setOnClickListener {
                        val intent = Intent(applicationContext, EditNotice::class.java)
                        intent.putExtra("club", body?.name)
                        intent.putExtra("user_name", user?.name)
                        startActivityForResult(intent, 100)
                    }
                }
            }
            override fun onFailure(call: Call<Club>, t: Throwable) {
                Log.d("this is error",t.message)
            }
        })
    }

    private fun loadBoards(club: String) {
        val retrofitService = ServiceControl.getInstance()
        retrofitService?.getAllBoards(club)?.enqueue(object: Callback<List<Board>> {
            override fun onResponse(call: Call<List<Board>>, response: Response<List<Board>>) {
                if (response.isSuccessful) {
                    val body = response.body()
                    body?.let {
                        setAdapter(it as ArrayList<Board>)
                    }
                }
            }
            override fun onFailure(call: Call<List<Board>>, t: Throwable) {
                Log.d("this is error",t.message)
            }
        })
    }


    private fun loadCalendars(club: String, date: String) {
        val retrofitService = ServiceControl.getInstance()
        retrofitService?.getAllCalendarBoards(club, date)?.enqueue(object: Callback<List<Board>> {
            override fun onResponse(call: Call<List<Board>>, response: Response<List<Board>>) {
                if (response.isSuccessful) {
                    val body = response.body()
                    body?.let {
                        bsetAdapter(it as ArrayList<Board>)
                    }
                }
            }
            override fun onFailure(call: Call<List<Board>>, t: Throwable) {
                Log.d("this is error",t.message)
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                100 -> {
                    loadClub(intent.getLongExtra("id", 0))
                }
            }
        }
    }
}