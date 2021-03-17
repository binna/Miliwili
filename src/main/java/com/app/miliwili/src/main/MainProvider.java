package com.app.miliwili.src.main;

import com.app.miliwili.config.BaseException;
import com.app.miliwili.src.main.model.GetEndDayRes;
import com.app.miliwili.src.user.UserProvider;
import com.app.miliwili.src.user.dto.GetAbnormalUserEndDate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;

import static com.app.miliwili.config.BaseResponseStatus.*;


@RequiredArgsConstructor
@Service
public class MainProvider {
    private final UserProvider userProvider;

}
