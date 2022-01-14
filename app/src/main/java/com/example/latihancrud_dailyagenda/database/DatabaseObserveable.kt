package com.example.latihancrud_dailyagenda.database

interface DatabaseObserveable {
    fun regisisterDatabaseObserver(databaseObserver : DatabaseObserver)
    fun removeDatabaseObserver(databaseObserver: DatabaseObserver)
    fun notifyDatabaseChanged()
}

interface DatabaseObserver{
    fun onDatabaseChanged()
}