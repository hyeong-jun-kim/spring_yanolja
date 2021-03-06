package com.yanolja.repository.user;

import com.yanolja.domain.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;

@Slf4j
@Repository
public class UserRepository {
private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	UserSql userSql;
	public UserRepository(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
		this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
	}
	// User 레코드 추가
	public User.RegisterRes insert(User.RegisterReq user) {
		KeyHolder keyHolder = new GeneratedKeyHolder();
		SqlParameterSource parameterSource = new MapSqlParameterSource("name", user.getName())
				.addValue("nickName", user.getNickName())
				.addValue("profileImgUrl", user.getProfileImgUrl())
				.addValue("email", user.getEmail())
				.addValue("password", user.getPassword())
				.addValue("phoneNumber", user.getPhoneNumber())
				.addValue("deleteYN", "N");
		int affectedRows = namedParameterJdbcTemplate.update(userSql.INSERT, parameterSource, keyHolder);
		return User.RegisterRes.builder().name(user.getName()).build();
	}
	// UserId를 사용해 레코드 업데이트
	public Integer updateById(User.PatchReq user) {
		String qry = UserSql.UPDATE;
		SqlParameterSource parameterSource = new MapSqlParameterSource("userId", user.getUserId())
				.addValue("nickName", user.getNickName());
		return namedParameterJdbcTemplate.update(qry, parameterSource);
	}
	// UserId를 사용해 User 레코드 제거
	public Integer deleteById(Integer id) {
		SqlParameterSource parameterSource = new MapSqlParameterSource("userId", id);
		return namedParameterJdbcTemplate.update(UserSql.DELETE, parameterSource);
	}
	// deleteYN N -> Y
	public User.Info findById(Integer id) {
		SqlParameterSource parameterSource = new MapSqlParameterSource("userId", id);
		return namedParameterJdbcTemplate.queryForObject(UserSql.SELECT, parameterSource,
				new UserMapper());
	}
	// EMAIL로 User.Info 전달
	public User.Info findByEmail(String email) {
		SqlParameterSource parameterSource = new MapSqlParameterSource("email", email);
		try {
			return namedParameterJdbcTemplate.queryForObject(UserSql.FIND_BY_EMAIL, parameterSource, new UserMapper());
		} catch (EmptyResultDataAccessException e) {
			// EmptyResultDataAccessException 예외 발생시 null 리턴
			return null;
		}

	}
	// NICKNAME으로 User.Info 전달
	public User.Info findByNickName(String nickName){
		SqlParameterSource parameterSource = new MapSqlParameterSource("nickName", nickName);
		try {
			return namedParameterJdbcTemplate.queryForObject(UserSql.FIND_BY_NICKNAME, parameterSource, new UserMapper());
		} catch (EmptyResultDataAccessException e){
			return null;
		}
	}

	// UserId로 Email 전달
	public String getEmailById(Integer Id){
		SqlParameterSource parameterSource = new MapSqlParameterSource("userId", Id);
		try {
			return namedParameterJdbcTemplate.queryForObject(UserSql.GET_EMAIL, parameterSource,
					(rs, rowNum) -> {
						String email = rs.getString("email");
						return email;
					});
		}catch(EmptyResultDataAccessException e){
			return null;
		}
	}
	// queryForObject 수행시 User 리턴해주기 위한 클래스
	private static final class UserMapper implements RowMapper<User.Info>{
		@Override
		public User.Info mapRow(ResultSet rs, int rowNum) throws SQLException {
			User.Info user = new User.Info();
			user.setUserId(rs.getInt("userId"));
			user.setNickName(rs.getString("nickName"));
			user.setEmail(rs.getString("email"));
			user.setName(rs.getString("name"));
			user.setPassword(rs.getString("password"));
			user.setPhoneNumber(rs.getString("phoneNumber"));
			user.setDeleteYN(rs.getString("deleteYN"));
			user.setProfileImgUrl(rs.getString("profileImgUrl"));
			return user;
		}
	}
}
