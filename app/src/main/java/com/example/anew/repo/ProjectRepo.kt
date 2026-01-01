package com.example.anew.repo

import android.util.Log
import com.example.anew.model.Team
import com.example.anew.model.User
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

//in Home
class ProjectRepo {
    private val db : FirebaseFirestore = FirebaseFirestore.getInstance()

    suspend fun createProject(team: Team){
        try {
            db.collection("projects").document(team.id).set(team).await()
            Log.d("project", "createProject success")
        }catch (e: Exception){
            Log.d("project", "createProject failed")
        }
    }

    suspend fun getProjectsData(uid: String): MutableList<Team>{
        val projectList= mutableListOf<Team>()
        try {
            val snapshot = db.collection("projects")
                .whereArrayContains("members",uid)
                .get()
                .await()
            for(document in snapshot){
                val project = document.toObject(Team::class.java)
                projectList.add(project)
            }
        }
        catch (e: Exception){
            Log.d("project", "getProject failed")
        }

        return projectList
    }

    suspend fun getProjectFromId(id: String): Team{
        try {
            val snapshot = db.collection("projects")
                .document(id).get().await()
            return snapshot.toObject(Team::class.java)!!
        }catch (e:Exception){
            Log.d("project", "getProject failed")
            throw e
        }
    }

}