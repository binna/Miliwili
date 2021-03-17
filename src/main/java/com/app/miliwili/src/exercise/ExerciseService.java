package com.app.miliwili.src.exercise;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class ExerciseService {
    private final ExerciseRepository exerciseRepository;
    private final ExerciseProvider exerciseProvider;
}
