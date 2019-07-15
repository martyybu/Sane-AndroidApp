package com.example.tp.sane.API;

import com.example.tp.sane.Models.ChatGetResponse;
import com.example.tp.sane.Models.DefaultResponse;
import com.example.tp.sane.Models.EntryResponse;
import com.example.tp.sane.Models.EntryTextGetResponse;
import com.example.tp.sane.Models.LoginResponse;
import com.example.tp.sane.Models.MessageGetResponse;
import com.example.tp.sane.Models.ProfileGetResponse;
import com.example.tp.sane.Models.TherapistGetResponse;
import com.example.tp.sane.Models.TherapistResponse;
import com.example.tp.sane.Models.UsernameGetResponse;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface API {

    //Creates a user in the DB
    @FormUrlEncoded
    @POST("createuser")
    Call<DefaultResponse> createUser(
            @Field("username") String username,
            @Field("password") String password,
            @Field("email") String email
    );

    //Creates a therapist in the DB
    @FormUrlEncoded
    @POST("createtherapist")
    Call<DefaultResponse> createTherapist(
            @Field("username") String username,
            @Field("password") String password,
            @Field("email") String email,
            @Field("name") String name,
            @Field("surname") String surname,
            @Field("DoB") String DoB,
            @Field("phoneNumber") String phoneNumber,
            @Field("discipline") String discipline,
            @Field("valid") int valid
    );

    //Validates whether credentials are correct
    @FormUrlEncoded
    @POST("userlogin")
    Call<LoginResponse> userLogin(
            @Field("username") String username,
            @Field("password") String password
    );

    //Gets the logged in therapist with ID
    @FormUrlEncoded
    @POST("gettherapist")
    Call<TherapistGetResponse> getTherapist(
            @Field("ID") int ID
    );

    //Gets the profile with ID
    @FormUrlEncoded
    @POST("getprofile")
    Call<ProfileGetResponse> getProfile(
            @Field("ID") int ID
    );

    //Gets all therapists (for search fragment)
    @GET("getalltherapists")
    Call<TherapistResponse> getTherapists();

    //Gets entries with user ID
    @FormUrlEncoded
    @POST("getentries")
    Call<EntryResponse> getEntries(
            @Field("ID") int ID
    );

    //Updates the profile image and about section
    @FormUrlEncoded
    @POST("updateprofile")
    Call<DefaultResponse> updateProfile(
            @Field("ID") int ID,
            @Field("image") String image,
            @Field("about") String about
    );

    //Gets the full entry using ID and title
    @FormUrlEncoded
    @POST("getfullentry")
    Call<EntryTextGetResponse> getFullEntry(
            @Field("ID") int ID,
            @Field("title") String title
    );

    //Adds an entry to the diary
    @FormUrlEncoded
    @POST("addentry")
    Call<DefaultResponse> addEntry(
            @Field("ID") int ID,
            @Field("title") String title,
            @Field("text") String text
    );

    //Gets chats using ID
    @FormUrlEncoded
    @POST("getchats")
    Call<ChatGetResponse> getChats(
            @Field("ID") int ID
    );

    //Adds a chat when send message is pressed
    @FormUrlEncoded
    @POST("addchat")
    Call<DefaultResponse> createChat(
            @Field("UserID") int UserID,
            @Field("TherapistID") int TherapistID
    );

    //Gets user usename using ID
    @FormUrlEncoded
    @POST("getuserusername")
    Call<UsernameGetResponse> getUserUsername(
            @Field("ID") int ID
    );

    //Gets messages using Chat ID
    @FormUrlEncoded
    @POST("getmessages")
    Call<MessageGetResponse> getMessages(
            @Field("Chat_ID") int Chat_ID
    );

    //Sends a message
    @FormUrlEncoded
    @POST("sendmessage")
    Call<DefaultResponse> sendMessage(
            @Field("ID") int ID,
            @Field("Chat_ID") int Chat_ID,
            @Field("message") String message
    );
}