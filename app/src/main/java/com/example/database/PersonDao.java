package com.example.database;

import androidx.room.*;

import java.util.List;

@Dao
public interface PersonDao {

    @Insert
    void insert(Person person);

    @Update
    void update(Person person);

    @Delete
    void delete(Person person);

    @Query("SELECT * FROM person")
    List<Person> getAllPersons();
}
