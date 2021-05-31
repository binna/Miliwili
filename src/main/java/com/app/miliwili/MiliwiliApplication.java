package com.app.miliwili;

import com.app.miliwili.src.calendar.PlanRepository;
import com.app.miliwili.src.emotionRecord.EmotionRecordRepository;
import com.app.miliwili.src.emotionRecord.models.EmotionRecord;
import com.app.miliwili.src.user.VacationRepository;
import com.app.miliwili.src.user.UserRepository;
import com.app.miliwili.src.user.models.AbnormalPromotionState;
import com.app.miliwili.src.user.models.NormalPromotionState;
import com.app.miliwili.src.user.models.UserInfo;
import com.app.miliwili.src.user.models.Vacation;
import lombok.AllArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ConcurrentTaskScheduler;

import javax.annotation.PostConstruct;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

@AllArgsConstructor
@EnableJpaAuditing
@EnableScheduling
@SpringBootApplication
public class MiliwiliApplication implements CommandLineRunner {
    private final UserRepository userRepository;
    private final EmotionRecordRepository emotionRecordRepository;
    private final VacationRepository vacationRepository;
    private final PlanRepository scheduleRepository;

    @PostConstruct
    public void started() {
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Seoul"));
    }

    public static void main(String[] args) {
        SpringApplication.run(MiliwiliApplication.class, args);
    }

    @Bean
    public TaskScheduler taskScheduler() {
        return new ConcurrentTaskScheduler();
    }

    @Override
    public void run(String... args) throws Exception {

//        /**
//         * 회원 데이터
//         */
        UserInfo userKaKao1 = UserInfo.builder()
                .birthday(LocalDate.parse("2020-01-01", DateTimeFormatter.ISO_DATE))
                .name("이원성").stateIdx(1).serveType("육군").socialType("K").socialId("test1")
                .startDate(LocalDate.parse("2020-01-01" , DateTimeFormatter.ISO_DATE))
                .endDate(LocalDate.parse("2022-12-31" , DateTimeFormatter.ISO_DATE))
                .build();
        NormalPromotionState normalPromotionStateKaKao1 = NormalPromotionState.builder()
                .firstDate(LocalDate.parse("2020-10-31" , DateTimeFormatter.ISO_DATE))
                .secondDate(LocalDate.parse("2021-12-31" , DateTimeFormatter.ISO_DATE))
                .thirdDate(LocalDate.parse("2022-10-31" , DateTimeFormatter.ISO_DATE))
                .userInfo(userKaKao1)
                .build();
        userKaKao1.setNormalPromotionState(normalPromotionStateKaKao1);

        UserInfo userKaKao2 = UserInfo.builder()
                .birthday(LocalDate.parse("2020-01-01", DateTimeFormatter.ISO_DATE))
                .name("이두성").stateIdx(1).serveType("육군").socialType("K").socialId("test1")
                .startDate(LocalDate.parse("2020-01-01" , DateTimeFormatter.ISO_DATE))
                .endDate(LocalDate.parse("2022-12-31" , DateTimeFormatter.ISO_DATE))
                .build();
        NormalPromotionState normalPromotionStateKaKao2 = NormalPromotionState.builder()
                .firstDate(LocalDate.parse("2020-10-31" , DateTimeFormatter.ISO_DATE))
                .secondDate(LocalDate.parse("2020-12-31" , DateTimeFormatter.ISO_DATE))
                .thirdDate(LocalDate.parse("2022-10-31" , DateTimeFormatter.ISO_DATE))
                .userInfo(userKaKao2)
                .build();
        userKaKao2.setNormalPromotionState(normalPromotionStateKaKao2);

        UserInfo userKaKao3 = UserInfo.builder()
                .birthday(LocalDate.parse("2020-01-01", DateTimeFormatter.ISO_DATE))
                .name("이삼성").stateIdx(1).serveType("육군").socialType("K").socialId("test1")
                .startDate(LocalDate.parse("2020-01-01" , DateTimeFormatter.ISO_DATE))
                .endDate(LocalDate.parse("2022-12-31" , DateTimeFormatter.ISO_DATE))
                .build();
        NormalPromotionState normalPromotionStateKaKao3 = NormalPromotionState.builder()
                .firstDate(LocalDate.parse("2020-10-31" , DateTimeFormatter.ISO_DATE))
                .secondDate(LocalDate.parse("2021-12-31" , DateTimeFormatter.ISO_DATE))
                .thirdDate(LocalDate.parse("2022-10-31" , DateTimeFormatter.ISO_DATE))
                .userInfo(userKaKao3)
                .build();
        userKaKao3.setNormalPromotionState(normalPromotionStateKaKao3);

        UserInfo userKaKao4 = UserInfo.builder()
                .birthday(LocalDate.parse("2020-01-01", DateTimeFormatter.ISO_DATE))
                .name("이사성").stateIdx(2).serveType("육군").socialType("K").socialId("test1")
                .startDate(LocalDate.parse("2020-01-01" , DateTimeFormatter.ISO_DATE))
                .endDate(LocalDate.parse("2022-12-31" , DateTimeFormatter.ISO_DATE))
                .build();
        AbnormalPromotionState abnormalPromotionState = AbnormalPromotionState.builder()
                .proDate(LocalDate.parse("2021-12-31", DateTimeFormatter.ISO_DATE))
                .userInfo(userKaKao4)
                .build();
        userKaKao4.setAbnormalPromotionState(abnormalPromotionState);
        UserInfo userKaKao5 = UserInfo.builder()
                .birthday(LocalDate.parse("2020-01-01", DateTimeFormatter.ISO_DATE))
                .name("이오성").stateIdx(1).serveType("육군").socialType("K").socialId("test1")
                .startDate(LocalDate.parse("2020-01-01" , DateTimeFormatter.ISO_DATE))
                .endDate(LocalDate.parse("2022-12-31" , DateTimeFormatter.ISO_DATE))
                .build();
        NormalPromotionState normalPromotionStateKaKao5 = NormalPromotionState.builder()
                .firstDate(LocalDate.parse("2020-10-31" , DateTimeFormatter.ISO_DATE))
                .secondDate(LocalDate.parse("2021-01-31" , DateTimeFormatter.ISO_DATE))
                .thirdDate(LocalDate.parse("2021-03-10" , DateTimeFormatter.ISO_DATE))
                .userInfo(userKaKao5)
                .build();
        userKaKao5.setNormalPromotionState(normalPromotionStateKaKao5);

        final List<UserInfo> userList =
                Arrays.asList(userKaKao1, userKaKao2, userKaKao3, userKaKao4, userKaKao5);
        userRepository.saveAll(userList);

        Vacation vacation1 = Vacation.builder().title("정기휴가").userInfo(userKaKao1).totalDays(24).build();
        Vacation vacation2 = Vacation.builder().title("포상휴가").userInfo(userKaKao1).totalDays(15).build();
        Vacation vacation3 = Vacation.builder().title("기타휴가").userInfo(userKaKao1).totalDays(0).build();
        final List<Vacation> vacationList = Arrays.asList(vacation1, vacation2, vacation3);
        vacationRepository.saveAll(vacationList);

        EmotionRecord emotionRecord1 = EmotionRecord.builder()
                .date(LocalDate.parse("2021-03-01", DateTimeFormatter.ISO_DATE))
                .content("test1").emoticon(1).userInfo(userKaKao1).build();
        EmotionRecord emotionRecord2 = EmotionRecord.builder()
                .date(LocalDate.parse("2021-03-10", DateTimeFormatter.ISO_DATE))
                .content("test1").emoticon(2).userInfo(userKaKao1).build();
        EmotionRecord emotionRecord3 = EmotionRecord.builder()
                .date(LocalDate.parse("2021-03-15", DateTimeFormatter.ISO_DATE))
                .content("test1").emoticon(3).userInfo(userKaKao1).build();
        EmotionRecord emotionRecord4 = EmotionRecord.builder()
                .date(LocalDate.parse("2021-03-20", DateTimeFormatter.ISO_DATE))
                .content("test1").emoticon(4).userInfo(userKaKao1).build();
        EmotionRecord emotionRecord5 = EmotionRecord.builder()
                .date(LocalDate.parse("2021-03-25", DateTimeFormatter.ISO_DATE))
                .content("test1").emoticon(5).userInfo(userKaKao1).build();
        EmotionRecord emotionRecord6 = EmotionRecord.builder()
                .date(LocalDate.parse("2021-03-31", DateTimeFormatter.ISO_DATE))
                .content("test1").emoticon(6).userInfo(userKaKao1).build();
        EmotionRecord emotionRecord7 = EmotionRecord.builder()
                .date(LocalDate.parse("2021-04-01", DateTimeFormatter.ISO_DATE))
                .content("test1").emoticon(7).userInfo(userKaKao1).build();
        EmotionRecord emotionRecord8 = EmotionRecord.builder()
                .date(LocalDate.parse("2021-01-01", DateTimeFormatter.ISO_DATE))
                .content("test1").emoticon(8).userInfo(userKaKao1).build();
        final List<EmotionRecord> emotionRecordList
                = Arrays.asList(emotionRecord1, emotionRecord2, emotionRecord3, emotionRecord4,
                                emotionRecord5, emotionRecord6, emotionRecord7, emotionRecord8);
        emotionRecordRepository.saveAll(emotionRecordList);
    }
}