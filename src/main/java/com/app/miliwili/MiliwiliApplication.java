package com.app.miliwili;

import com.app.miliwili.src.calendar.ScheduleRepository;
import com.app.miliwili.src.calendar.models.Schedule;
import com.app.miliwili.src.calendar.models.ScheduleDate;
import com.app.miliwili.src.user.LeaveRepository;
import com.app.miliwili.src.user.UserRepository;
import com.app.miliwili.src.user.models.AbnormalPromotionState;
import com.app.miliwili.src.user.models.Leave;
import com.app.miliwili.src.user.models.NormalPromotionState;
import com.app.miliwili.src.user.models.User;
import lombok.AllArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ConcurrentTaskScheduler;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

@AllArgsConstructor
@EnableJpaAuditing
@EnableScheduling
@SpringBootApplication
public class MiliwiliApplication implements CommandLineRunner {
    private final UserRepository userRepository;
    private final LeaveRepository leaveRepository;
    private final ScheduleRepository scheduleRepository;

    public static void main(String[] args) {
        SpringApplication.run(MiliwiliApplication.class, args);
    }

    @Bean
    public TaskScheduler taskScheduler() {
        return new ConcurrentTaskScheduler();
    }

    @Override
    public void run(String... args) throws Exception {
        /**
         * 회원 데이터
         */
        User userKaKao1 = User.builder()
                .name("이원성").stateIdx(1).serveType("육군").socialType("K").socialId("test1")
                .startDate(LocalDate.parse("2020-01-01" , DateTimeFormatter.ISO_DATE))
                .endDate(LocalDate.parse("2022-12-31" , DateTimeFormatter.ISO_DATE))
                .build();
        NormalPromotionState normalPromotionStateKaKao1 = NormalPromotionState.builder()
                .firstDate(LocalDate.parse("2020-10-31" , DateTimeFormatter.ISO_DATE))
                .secondDate(LocalDate.parse("2021-12-31" , DateTimeFormatter.ISO_DATE))
                .thirdDate(LocalDate.parse("2022-10-31" , DateTimeFormatter.ISO_DATE))
                .user(userKaKao1)
                .build();
        userKaKao1.setNormalPromotionState(normalPromotionStateKaKao1);

        User userKaKao2 = User.builder()
                .name("이두성").stateIdx(1).serveType("육군").socialType("K").socialId("test1")
                .startDate(LocalDate.parse("2020-01-01" , DateTimeFormatter.ISO_DATE))
                .endDate(LocalDate.parse("2022-12-31" , DateTimeFormatter.ISO_DATE))
                .build();
        NormalPromotionState normalPromotionStateKaKao2 = NormalPromotionState.builder()
                .firstDate(LocalDate.parse("2020-10-31" , DateTimeFormatter.ISO_DATE))
                .secondDate(LocalDate.parse("2020-12-31" , DateTimeFormatter.ISO_DATE))
                .thirdDate(LocalDate.parse("2022-10-31" , DateTimeFormatter.ISO_DATE))
                .user(userKaKao2)
                .build();
        userKaKao2.setNormalPromotionState(normalPromotionStateKaKao2);

        User userKaKao3 = User.builder()
                .name("이삼성").stateIdx(1).serveType("육군").socialType("K").socialId("test1")
                .startDate(LocalDate.parse("2020-01-01" , DateTimeFormatter.ISO_DATE))
                .endDate(LocalDate.parse("2022-12-31" , DateTimeFormatter.ISO_DATE))
                .build();
        NormalPromotionState normalPromotionStateKaKao3 = NormalPromotionState.builder()
                .firstDate(LocalDate.parse("2020-10-31" , DateTimeFormatter.ISO_DATE))
                .secondDate(LocalDate.parse("2021-12-31" , DateTimeFormatter.ISO_DATE))
                .thirdDate(LocalDate.parse("2022-10-31" , DateTimeFormatter.ISO_DATE))
                .user(userKaKao3)
                .build();
        userKaKao3.setNormalPromotionState(normalPromotionStateKaKao3);

        User userKaKao4 = User.builder()
                .name("이사성").stateIdx(2).serveType("육군").socialType("K").socialId("test1")
                .startDate(LocalDate.parse("2020-01-01" , DateTimeFormatter.ISO_DATE))
                .endDate(LocalDate.parse("2022-12-31" , DateTimeFormatter.ISO_DATE))
                .build();
        AbnormalPromotionState abnormalPromotionState = AbnormalPromotionState.builder()
                .proDate(LocalDate.parse("2021-12-31", DateTimeFormatter.ISO_DATE))
                .user(userKaKao4)
                .build();
        userKaKao4.setAbnormalPromotionState(abnormalPromotionState);
        User userKaKao5 = User.builder()
                .name("이오성").stateIdx(1).serveType("육군").socialType("K").socialId("test1")
                .startDate(LocalDate.parse("2020-01-01" , DateTimeFormatter.ISO_DATE))
                .endDate(LocalDate.parse("2022-12-31" , DateTimeFormatter.ISO_DATE))
                .build();
        NormalPromotionState normalPromotionStateKaKao5 = NormalPromotionState.builder()
                .firstDate(LocalDate.parse("2020-10-31" , DateTimeFormatter.ISO_DATE))
                .secondDate(LocalDate.parse("2021-01-31" , DateTimeFormatter.ISO_DATE))
                .thirdDate(LocalDate.parse("2021-03-10" , DateTimeFormatter.ISO_DATE))
                .user(userKaKao5)
                .build();
        userKaKao5.setNormalPromotionState(normalPromotionStateKaKao5);

        final List<User> userList =
                Arrays.asList(userKaKao1, userKaKao2, userKaKao3, userKaKao4, userKaKao5);
        userRepository.saveAll(userList);

        /**
         * 휴가 더미데이터
         */
        Leave leave1 = Leave.builder().title("1차정기휴가").user(userKaKao1).total(8).build();
        Leave leave2 = Leave.builder().title("2차정기휴가").user(userKaKao1).total(8).build();
        Leave leave3 = Leave.builder().title("3차정기휴가").user(userKaKao1).total(8).build();

        final List<Leave> leaveList = Arrays.asList(leave1, leave2, leave3);
        leaveRepository.saveAll(leaveList);

        /**
         * 스케줄 더미데이터
         */
        Schedule schedule1 = Schedule.builder()
                .color("빨강").distinction("정기휴가").title("휴가날 뭐할지 딱 정함ㅋ")
                .leave(leave1).user(userKaKao1)
                .build();
        ScheduleDate date = ScheduleDate.builder()
                .date(LocalDate.parse("2021-12-31", DateTimeFormatter.ISO_DATE))
                .schedule(schedule1)
                .build();
        schedule1.setScheduleDates(Arrays.asList(date));

        final List<Schedule> scheduleList = Arrays.asList(schedule1);
        scheduleRepository.saveAll(scheduleList);
    }
}