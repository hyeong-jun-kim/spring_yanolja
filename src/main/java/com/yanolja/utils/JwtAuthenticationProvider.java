package com.yanolja.utils;

import com.yanolja.configuration.DefaultException;
import com.yanolja.configuration.ResponseMessage;
import com.yanolja.configuration.StatusCode;
import com.yanolja.repository.owner.OwnerRepository;
import com.yanolja.repository.room.RoomRepository;
import com.yanolja.service.RoomContentService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

@Component
@EnableAutoConfiguration
public class JwtAuthenticationProvider {
    @Autowired
    RoomContentService roomContentService;
    @Autowired
    RoomRepository roomRepository;
    @Autowired
    OwnerRepository ownerRepository;
    private String secretKey = "secret";

    private long tokenValidTime = 1000L * 60 * 60;

    // JWT 토큰 생성
    public String createToken(String userPk) {
        Claims claims = Jwts.claims().setSubject(userPk); // JWT payload 에 저장되는 정보단위
        Date now = new Date();
        return Jwts.builder()
                .setClaims(claims) // 정보 저장
                .setIssuedAt(now) // 토큰 발행 시간 정보
                .setExpiration(new Date(now.getTime() + tokenValidTime)) // set Expire Time
                .signWith(SignatureAlgorithm.HS256, secretKey)  // 사용할 암호화 알고리즘과
                // signature 에 들어갈 secret값 세팅
                .compact();
    }

    // 토큰에서 회원 정보 추출
    public String getJwtEmail(HttpServletRequest request) throws DefaultException {
        String token = resolveToken(request);
        String email;
        // 토큰이 비었을 때 예외 처리
        if(token == null || token.length() == 0){
            throw new DefaultException(StatusCode.JWT_ERROR, ResponseMessage.EMPTY_JWT);
        }
        try{
            email = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody().getSubject();
        }catch(Exception e){
            throw new DefaultException(StatusCode.JWT_ERROR, ResponseMessage.INVALID_JWT);
        }
        return email;
    }

    // Request의 Header에서 token 값을 가져옵니다. "X-AUTH-TOKEN" : "TOKEN값'
    public String resolveToken(HttpServletRequest request) {
        String token = null;
        String auth_token = request.getHeader("X-AUTH-TOKEN");
        if(auth_token != null){
            token = auth_token;
        }
        return token;
    }

    // 토큰의 유효성 + 만료일자 확인
    public boolean validateToken(String jwtToken) {
        try {
            Jws<Claims> claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(jwtToken);
            return !claims.getBody().getExpiration().before(new Date());
        } catch (Exception e) {
            return false;
        }
    }
    // Owner jwt 토큰 체크 메서드
    public void setOwnerJwtTokenCheck(int roomContentId, HttpServletRequest request) throws DefaultException {
        int roomId = roomContentService.findRoomIdByRoomContentId(roomContentId);
        int ownerId = roomRepository.getOwnerIdByRoomId(roomId);
        // ownerId로 email 가져오기
        String ownerEmail = ownerRepository.getEmailById(ownerId);
        // jwt에서 email 추출
        String jwtEmail = getJwtEmail(request);
        // jwt validation
        if(!jwtEmail.equals(ownerEmail) || jwtEmail == null){
            throw new DefaultException(StatusCode.JWT_ERROR, ResponseMessage.INVALID_JWT);
        }
    }
}
