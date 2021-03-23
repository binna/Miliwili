package com.app.miliwili.src.calendar;

import com.app.miliwili.src.calendar.models.ToDoList;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ToDoListRepository extends CrudRepository<ToDoList, Long> {;}