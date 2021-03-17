package com.app.miliwili.src.exercise;

import com.app.miliwili.src.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class ExerciseProvider {
    private final ExerciseSelectRepository exerciseSelectRepository;
    private final UserRepository userRepository;


}
