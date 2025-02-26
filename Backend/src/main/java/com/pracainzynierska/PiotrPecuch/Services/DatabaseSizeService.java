package com.pracainzynierska.PiotrPecuch.Services;


import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class DatabaseSizeService {

    private final JdbcTemplate jdbcTemplate;

    public DatabaseSizeService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public String getDatabaseSize(){
        String sql = "SELECT ROUND(SUM(data_length + index_length) / 1024 / 1024, 2)\n" +
                "FROM information_schema.tables\n" +
                "WHERE table_schema = 'PracaInzynierska'\n" +
                "GROUP BY table_schema";

        return jdbcTemplate.queryForObject(sql, String.class);
    }
}
