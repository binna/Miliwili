package com.app.miliwili.src.calendar;

import com.app.miliwili.src.calendar.models.ToDoList;
import com.app.miliwili.src.calendar.dto.WorkReq;
import com.app.miliwili.src.calendar.dto.WorkRes;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class CalendarProvider {




    /**
     * List<ToDoList> -> List<WorkRes> 변경
     * @param List<WorkReq> parameters
     * @return List<ToDoList>
     * @Auther shine
     */
    public List<ToDoList> retrieveToDoList(List<WorkReq> parameters) {
        List<ToDoList> toDoLists = null;

        for(WorkReq toDoList : parameters) {
            ToDoList newToDoList = ToDoList.builder()
                    .content(toDoList.getContent())
                    .build();
            toDoLists.add(newToDoList);
        }
        return toDoLists;
    }

    /**
     * List<ToDoList> -> List<WorkRes> 변경
     * @param List<ToDoList> parameters
     * @return List<WorkRes>
     * @Auther shine
     */
    public List<WorkRes> retrieveWorkRes(List<ToDoList> parameters) {
        if(parameters == null) return null;

        return parameters.stream().map(toDoList -> {
            return new WorkRes(toDoList.getContent(), toDoList.getProcessingStatus());
        }).collect(Collectors.toList());
    }





}