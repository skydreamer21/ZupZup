package com.twoez.zupzup.member.service;


import com.twoez.zupzup.config.security.exception.InvalidAuthorizationTokenException;
import com.twoez.zupzup.config.security.jwt.AuthorizationToken;
import com.twoez.zupzup.config.security.jwt.JwtProvider;
import com.twoez.zupzup.config.security.jwt.RefreshToken;
import com.twoez.zupzup.global.exception.HttpExceptionCode;
import com.twoez.zupzup.global.util.Assertion;
import com.twoez.zupzup.member.controller.dto.MemberHealthModifyRequest;
import com.twoez.zupzup.member.controller.dto.MemberHealthRegisterRequest;
import com.twoez.zupzup.member.controller.dto.ReissueTokenRequest;
import com.twoez.zupzup.member.domain.AuthUser;
import com.twoez.zupzup.member.domain.Gender;
import com.twoez.zupzup.member.domain.Member;
import com.twoez.zupzup.member.exception.AlreadyRegisteredMemberException;
import com.twoez.zupzup.member.exception.MemberQueryException;
import com.twoez.zupzup.member.repository.MemberQueryRepository;
import com.twoez.zupzup.member.repository.MemberSpringDataRepository;
import com.twoez.zupzup.member.repository.redis.RefreshTokenRedisRepository;
import com.twoez.zupzup.pet.domain.Pet;
import com.twoez.zupzup.pet.repository.PetRepository;
import com.twoez.zupzup.plogginglog.domain.TotalPloggingLog;
import com.twoez.zupzup.plogginglog.repository.TotalPloggingLogRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberService {

    private final JwtProvider jwtProvider;
    private final MemberQueryRepository memberQueryRepository;
    private final MemberSpringDataRepository memberSpringDataRepository;
    private final RefreshTokenRedisRepository refreshTokenRedisRepository;
    private final PetRepository petRepository;
    private final TotalPloggingLogRepository totalPloggingLogRepository;

    @Transactional
    public Member save(AuthUser authUser) {

        Member member = memberSpringDataRepository.save(authUser.toNewMember());
        petRepository.save(Pet.init(member));
        totalPloggingLogRepository.save(TotalPloggingLog.init(member));

        return member;
    }

    public Optional<Member> findMemberByOauth(AuthUser authUser) {
        return memberQueryRepository.findByOauth(authUser.getOauth());
    }

    public AuthorizationToken issueAuthorizationToken(Long memberId) {
        AuthorizationToken authorizationToken = jwtProvider.createAuthorizationToken(memberId);
        saveRefreshToken(memberId, authorizationToken);
        return authorizationToken;
    }

    private void saveRefreshToken(Long memberId, AuthorizationToken authorizationToken) {
        refreshTokenRedisRepository.save(RefreshToken.from(memberId, authorizationToken));
    }

    // TODO : Transaction 처리
    public AuthorizationToken reIssueAuthorizationToken(
            Long memberId, ReissueTokenRequest reissueTokenRequest) {
        RefreshToken refreshToken =
                refreshTokenRedisRepository
                        .findRefreshTokenByMemberId(String.valueOf(memberId))
                        .orElseThrow(
                                () ->
                                        new InvalidAuthorizationTokenException(
                                                HttpExceptionCode.REFRESH_TOKEN_NOT_FOUND));

        Assertion.with(refreshToken)
                .setValidation((token) -> token.isSameToken(reissueTokenRequest.refreshToken()))
                .validateOrThrow(
                        () ->
                                new InvalidAuthorizationTokenException(
                                        HttpExceptionCode.INVALID_REFRESH_TOKEN));

        refreshTokenRedisRepository.delete(refreshToken);
        return issueAuthorizationToken(memberId);
    }

    public void logout(Long memberId) {
        removeRefreshTokenByMemberId(memberId);
    }

    private void removeRefreshTokenByMemberId(Long memberId) {
        RefreshToken refreshToken =
                refreshTokenRedisRepository
                        .findRefreshTokenByMemberId(String.valueOf(memberId))
                        .orElseThrow(
                                () ->
                                        new InvalidAuthorizationTokenException(
                                                HttpExceptionCode.REFRESH_TOKEN_NOT_FOUND));

        refreshTokenRedisRepository.delete(refreshToken);
    }

    public boolean hasValidRefreshToken(Long memberId) {
        return refreshTokenRedisRepository
                .findRefreshTokenByMemberId(String.valueOf(memberId))
                .isPresent();
    }

    @Transactional
    public void modifyMemberHealth(MemberHealthRegisterRequest memberHealthRegisterRequest) {
        // TODO : DB에 2번 접근하는 문제 해결하기
        Member memberRequestRegister = findById(memberHealthRegisterRequest.memberId());

        // 이전에 가입하며 최초 헬스정보를 입력한 멤버는 다시 이 요청을 보낼 수 없다.
        Assertion.with(memberRequestRegister)
                .setValidation(Member::isNewMember)
                .validateOrThrow(AlreadyRegisteredMemberException::new);
        
        modifyHealth(
                memberHealthRegisterRequest.memberId(),
                memberHealthRegisterRequest.birthYear(),
                memberHealthRegisterRequest.gender(),
                memberHealthRegisterRequest.height(),
                memberHealthRegisterRequest.weight());
    }

    @Transactional
    public void modifyMemberHealth(
            Long memberId, MemberHealthModifyRequest memberHealthRegisterRequest) {
        modifyHealth(
                memberId,
                memberHealthRegisterRequest.birthYear(),
                memberHealthRegisterRequest.gender(),
                memberHealthRegisterRequest.height(),
                memberHealthRegisterRequest.weight());
    }

    private void modifyHealth(
            Long memberId, Integer birthYear, Gender gender, Integer height, Integer weight) {
        findById(memberId).updateHealthInfo(birthYear, gender, height, weight);
    }

    public Member findById(Long memberId) {
        log.info("findById Service - memberId : {}", memberId);
        return memberSpringDataRepository
                .findMemberByIsDeletedIsFalseAndIdEquals(memberId)
                .orElseThrow(() -> new MemberQueryException(HttpExceptionCode.MEMBER_NOT_FOUND));
    }

    public Member validateMember(Long memberId) {
        return findById(memberId);
    }
}
