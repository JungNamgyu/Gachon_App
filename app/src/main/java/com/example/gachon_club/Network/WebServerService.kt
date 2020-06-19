package com.example.gachon_club.Network

import com.example.gachon_club.Account.Model.User
import com.example.gachon_club.Club.Model.Board
import com.example.gachon_club.Club.Model.Club
import retrofit2.Call
import retrofit2.http.*


interface WebServerService {

    ///////////////////////////////////////////////////////////////////
    // All User
    @GET("user/getAll")
    fun getAllUsers(): Call<List<User>>

    // User
    @GET("user/get/{id}")
    fun getUser(@Path("id")id: String): Call<User>

    // User Signup
    @POST("user/add")
    fun addUser(@Body body:User): Call<User>

    // User Modified
    @PUT("user/edit")
    fun modifyUser(@Body body:User): Call<User>

    // User Delete
    @DELETE("user/delete/{id}")
    fun deleteUser(@Path("id")id: String): Call<Boolean>

    ///////////////////////////////////////////////////////////////////
    // All Club
    @GET("club/getAll")
    fun getAllClubs(): Call<List<Club>>

    // Club
    @GET("club/get/{id}")
    fun getClub(@Path("id") id: Long): Call<Club>

    ///////////////////////////////////////////////////////////////////
    // All Board
    @GET("board/getAll")
    fun getAllBoards(): Call<List<Board>>

    // Board
    @GET("board/get/{id}")
    fun getBoard(@Path("id") id: Long): Call<Board>

    // Board Create
    @POST("board/add")
    fun addBoard(@Body body:Board): Call<Board>
}