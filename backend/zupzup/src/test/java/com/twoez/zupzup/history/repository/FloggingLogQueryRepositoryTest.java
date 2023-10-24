package com.twoez.zupzup.history.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.twoez.zupzup.history.domain.FloggingLog;
import com.twoez.zupzup.member.domain.AuthProvider;
import com.twoez.zupzup.member.domain.Member;
import com.twoez.zupzup.member.domain.OAuth;
import com.twoez.zupzup.member.domain.Role;
import com.twoez.zupzup.member.repository.MemberRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class FloggingLogQueryRepositoryTest {

    @Autowired
    FloggingLogQueryRepository floggingLogQueryRepository;
    @Autowired
    FloggingLogRepository floggingLogRepository;
    @Autowired
    MemberRepository memberRepository;
    Member member;

    @BeforeEach
    void initObjects() {
        this.member = memberRepository.save(Member.builder()
                .oAuth(new OAuth(AuthProvider.GOOGLE, "1234567"))
                .name("zupzup")
                .gender("F")
                .birthYear(2002)
                .height(160)
                .weight(50)
                .coin(24L)
                .isDeleted(false)
                .role(List.of(Role.ROLE_USER))
                .build());
    }

    @Test
    @DisplayName("기간 내의 플로깅 로그들을 조회한다.")
    void searchInPeriod() {
        LocalDateTime now = LocalDateTime.now();
        FloggingLog floggingLog = floggingLogRepository.save(FloggingLog.builder()
                .startDateTime(now)
                .endDateTime(now.plusHours(1))
                .distance(12345)
                .gatheredTrash(24)
                .coin(240L)
                .calories(240)
                .member(member)
                .routeImageUrl("https://image.com")
                .isDeleted(false)
                .build());

        LocalDateTime startDate = now.minusDays(1);
        LocalDateTime endDate = now.plusDays(2);
        List<FloggingLog> floggingLogs = floggingLogQueryRepository.findByBetweenStartDateAndEndDate(
                startDate,
                endDate,
                member.getId());

        assertThat(floggingLogs).hasSize(1);
        assertThat(floggingLogs.get(0).getStartDateTime()).isAfter(startDate);
        assertThat(floggingLogs.get(0).getEndDateTime()).isBefore(endDate);
    }

    @Test
    @DisplayName("특정 일의 플로깅 로그들을 가져온다.")
    void searchByDate() {
        LocalDateTime now = LocalDateTime.now();
        FloggingLog floggingLog = floggingLogRepository.save(FloggingLog.builder()
                .startDateTime(now)
                .endDateTime(now.plusDays(2))
                .distance(12345)
                .gatheredTrash(24)
                .coin(240L)
                .calories(240)
                .member(member)
                .routeImageUrl("https://image.com")
                .isDeleted(false)
                .build());

        LocalDate date = now.toLocalDate();
        List<FloggingLog> floggingLogs = floggingLogQueryRepository.findByDate(date,
                member.getId());

        assertThat(floggingLogs).hasSize(1);
        assertThat(floggingLogs.get(0).getStartDateTime().toLocalDate()).isEqualTo(date);
    }

    @Test
    @DisplayName("최근 플로깅 로그를 조회한다..")
    void searchRecent() {
        LocalDateTime now = LocalDateTime.now();
        FloggingLog floggingLog = floggingLogRepository.save(FloggingLog.builder()
                .startDateTime(now)
                .endDateTime(now)
                .distance(12345)
                .gatheredTrash(24)
                .coin(240L)
                .calories(240)
                .member(member)
                .routeImageUrl("https://image.com")
                .isDeleted(false)
                .build());

        Optional<FloggingLog> findFloggingLog = floggingLogQueryRepository
                .findOneOrderByDateDesc(member.getId());

        assertThat(findFloggingLog).isNotEmpty();
    }
}