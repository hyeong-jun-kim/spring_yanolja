package com.yanolja.repository.user

class UserSql {
	public static final String INSERT = """
			INSERT INTO user (name, profileImgUrl, email, password, phoneNumber, deleteYN)
			values (:name, :profileImgUrl, :email, :password, :phoneNumber, :deleteYN)
			""";
	public static final String SELECT = """
			SELECT * from user where userId = :userId
			""";

	public static final String UPDATE = """
			UPDATE user SET name = :name, profileImgUrl = :profileImgUrl, email = :email,
			password = :password, phoneNumber = :phoneNumber WHERE userId = :userId
""";
	public static final String DELETE = """
			UPDATE user SET deleteYN = 'Y' WHERE userId = :userId
""";
}