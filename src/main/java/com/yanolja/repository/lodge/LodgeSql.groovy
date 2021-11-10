package com.yanolja.repository.lodge

class LodgeSql {
    public static final String INSERT = """
			INSERT INTO lodge (roomContentId, checkIn, checkOut, price, deleteYN)
			values (:roomContentId, :checkIn, :checkOut, :price, :deleteYN)
			""";
    public static final String SELECT = """
			SELECT * from lodge where lodgeId = :lodgeId
			""";

    public static final String UPDATE = """
			UPDATE lodge SET checkIn = :checkIn, checkOut = :checkOut, price = :price,
			checkIn = :checkIn, checkOut = :checkOut, price = :price WHERE lodgeId = :lodgeId
            """;
    public static final String DELETE = """
			UPDATE lodge SET deleteYN = 'Y' WHERE lodgeId = :lodgeId
            """;
}